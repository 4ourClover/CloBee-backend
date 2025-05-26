package com.fourclover.clobee.card.service;

import com.fourclover.clobee.card.domain.*;
import com.fourclover.clobee.card.repository.CardRepository;
import com.fourclover.clobee.config.exception.ApiException;
import com.fourclover.clobee.config.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;


    // 전체 카드 불러오기
    @Override
    public CardPageDTO getCardPage(String type, int page, int size) {

        int offset = (page -1) * size;
        int cardType = 0;

        switch (type) {
            case "credit":
                cardType = 401;
                break;
            case "check":
                cardType = 402;
                break;
            default:
                throw new ApiException(ErrorCode.INVALID_CARD_TYPE);
        }

        List<CardListDTO> cards = cardRepository.allCardPaging(cardType, offset, size);
        int totalCount = cardRepository.allCardCount(cardType);
        return new CardPageDTO(cards, totalCount);
    }

    // 카드 상세 보기(전체, 내 카드 공통?)
    @Override
    public List<CardBenefitDetail> getCardBenefitDetail(Long cardInfoId) {
        List<CardBenefitDetail> benefit = cardRepository.getCardBenefit(cardInfoId);
        if (benefit == null || benefit.isEmpty()) {
            throw new ApiException(ErrorCode.CARD_NOT_FOUND);
        }
        return benefit;
    }

    // 카드사 url 가져오기(카드 신청하기)
    // 카드 신청하기 버튼 클릭 시 card_apply_views 증가
    @Override
    @Transactional  // 트랜잭션
    public String getCardBrandUrlAndIncreaseApplyViews(Long cardInfoId, int cardBrand) {
        cardRepository.updateApplyViews(cardInfoId);
        String url = cardRepository.getCardBrandURL(cardBrand);

        // 카드 브랜드 url이 없는 경우 redirection 오류
        if (url == null) {
            throw new ApiException(ErrorCode.CARD_BRAND_URL_NOT_FOUND);
        }

        return url;
    }

    // 내 카드 추가하기
    @Override
    public void addUserCard(Long userId, Long cardInfoId, Integer userCardType) {
        CardListDTO card = cardRepository.findByCardInfoId(cardInfoId);
        if (card == null) {
            throw new ApiException(ErrorCode.CARD_NOT_FOUND);
        }

        // 이미 등록한 카드일 경우 (중복 등록)
        boolean alreadyExists = cardRepository.existsUserCard(userId, cardInfoId);
        if (alreadyExists) {
            throw new ApiException(ErrorCode.CARD_ALREADY_REGISTERED);
        }

        UserCardDetail detail = UserCardDetail.builder()
                .userId(userId)
                .cardInfoId(cardInfoId)
                .build();

        cardRepository.insertUserCard(detail);
    }

    // 내 카드 조회하기
    @Override
    public List<CardListDTO> getMyCardList(Long userId) {
        if (userId == null) {
            throw new ApiException(ErrorCode.INVALID_USER);
        }
        return cardRepository.getMyCard(userId);
        // userId == null if 처리
    }

    // 카드 검색
    @Override
    public List<CardListDTO> searchCard(String cardName) {
        // 검색어 없을 시
        if (cardName == null || cardName.trim().isEmpty()) {
            throw new ApiException(ErrorCode.EMPTY_SEARCH_KEYWORD);
        }

        return cardRepository.searchCard("%" + cardName + "%");
    }

    // 카드 실적 추가 및 업데이트
    @Transactional
    @Override
    public void addPerformance(UserCardPerformanceDetail detail) {
        int updated = cardRepository.updateMonthlyPerformance(detail);
        if (updated == 0) {
            cardRepository.insertMonthlyPerformance(detail);
        }
    }

    // 카드 실적 조회
    @Override
    public UserCardPerformanceDetail getPerformance(Long userCardId, int year, int month) {
        return cardRepository.getPerformance(userCardId, year, month);
    }

    // 내 카드 삭제하기
    @Override
    public void deleteUserCard(Long userId, Long cardInfoId) {
        cardRepository.deleteUserCard(userId, cardInfoId);
    }

    @Override
    public Long getCardId(String cardName) {
        try{
            System.out.println("Service에서 처리할 카드명: " + cardName);
            return cardRepository.findCardIdByName(cardName.trim());
        }catch (Exception e) {
            System.err.println("데이터베이스 조회 오류: " + e.getMessage());
            throw new RuntimeException("카드 조회 중 오류가 발생했습니다.", e);
        }


    }

    // 카드 혜택을 지도로
    @Override
    public List<CardBenefitDetail> getCardBenefitsSortedByDiscount(int userId, String store) {
        List<CardBenefitDetail> rawData = cardRepository.selectCardBenefitsByUserIdAndStore(userId, store);

        return rawData.stream().map(data -> {
                    String cardName = data.getCardName();
                    String benefitDesc = data.getCardBenefitDesc();
                    String discountRaw = data.getCardBenefitDiscntPrice();

                    int discountAmount = parseToDiscountAmount(discountRaw, store);

                    data.setDiscountPrice(discountAmount);
                    return data;
                }).sorted(Comparator.comparingInt(CardBenefitDetail::getDiscountPrice).reversed())
                .collect(Collectors.toList());

    }

    // 매장별 카드 혜택 높은 카드 추천
    public List<CardBenefitDetail> getRecommendedCards(String store) {
        List<CardBenefitDetail> rawList = cardRepository.selectRecommendedCardsByStore(store);

        return rawList.stream()
                .peek(dto -> {
                    int discount = parseToDiscountAmount(dto.getCardBenefitDiscntPrice(), store);
                    dto.setDiscountPrice(discount);
                    System.out.println("💳 " + dto.getCardName() + " / 원문: " + dto.getCardBenefitDiscntPrice() + " → 할인 금액: " + discount);
                })
                .filter(dto -> dto.getDiscountPrice() > 0)
                .sorted(Comparator
                        .comparingInt(CardBenefitDetail::getDiscountPrice).reversed()  // 할인 금액 내림차순
                        .thenComparingInt(CardBenefitDetail::getCardRank))  // card_rank 오름차순 (1등급이 가장 좋음)
                .limit(3)
                .collect(Collectors.toList());
    }


    // 원 단위와 % 단위를 비교하기 위한 코드
    private int parseToDiscountAmount(String priceStr, String storeName) {
        try {
            priceStr = priceStr.trim();

            // 한글 숫자 표현 변환
            priceStr = priceStr.replace("천", "000")
                    .replace("백", "00")
                    .replace("만", "0000");

            // 범위 표현 처리 (예: 30~50% → 최대값 사용)
            if (priceStr.contains("~")) {
                String[] range = priceStr.split("~");
                if (range.length == 2) {
                    // 최대값 사용
                    priceStr = range[1].trim();
                }
            }

            // "최대" 표현 처리
            if (priceStr.contains("최대")) {
                priceStr = priceStr.replace("최대", "").trim();

                // "최대 10%" 형태 처리
                if (priceStr.endsWith("%")) {
                    double percent = Double.parseDouble(priceStr.replace("%", "").trim());
                    if (storeName.contains("CGV") || storeName.contains("메가박스") ||
                            storeName.contains("롯데시네마") || storeName.contains("영화")) {
                        return (int) (14000 * (percent / 100.0));
                    }
                    return (int) (10000 * (percent / 100.0));
                }

                // "최대 8천포인트" 형태는 0으로 처리 (포인트는 할인이 아님)
                if (priceStr.contains("포인트")) {
                    return 0;
                }
            }

            // 쿠폰인 경우 0 처리
            if (priceStr.contains("쿠폰")) {
                return 0;
            }

            // 퍼센트 할인
            if (priceStr.endsWith("%")) {
                double percent = Double.parseDouble(priceStr.replace("%", "").trim());

                // 영화관 관련 매장은 영화 티켓 기준 가격 적용
                if (storeName.contains("CGV") || storeName.contains("메가박스") ||
                        storeName.contains("롯데시네마") || storeName.contains("영화")) {
                    return (int) (14000 * (percent / 100.0));
                }

                // 기본 10,000원 기준
                return (int) (10000 * (percent / 100.0));
            }

            // 금액 할인
            if (priceStr.contains("원")) {
                return Integer.parseInt(priceStr.replaceAll("[^0-9]", ""));
            }

            return 0;

        } catch (Exception e) {
            System.err.println("할인 금액 파싱 오류: " + priceStr + " - " + e.getMessage());
            return 0;
        }
    }

}
