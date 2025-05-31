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
import java.util.Map;
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
        UserCardPerformanceDetail result = cardRepository.getPerformance(userCardId, year, month);
        if (result == null) {
            throw new ApiException(ErrorCode.PERFORMANCE_NOT_FOUND);
        }
        return result;
    }

    // 내 카드 삭제하기
    @Override
    public void deleteUserCard(Long userId, Long cardInfoId) {
        cardRepository.deleteUserCard(userId, cardInfoId);
    }

    @Override
    public List<String> getBenefitStoresByUserId(Long userId) {
        try {
            List<String> benefitStores = cardRepository.findBenefitStoresByUserId(userId);

//            if (benefitStores == null || benefitStores.isEmpty()) {
//                logger.info("사용자 혜택 매장 없음 - userId: {}", userId);
//            }

            //logger.info("사용자 혜택 매장 조회 완료 - userId: {}, 매장 수: {}", userId, benefitStores.size());
            //logger.debug("혜택 매장 목록: {}", benefitStores);

            return benefitStores;
        } catch (Exception e) {
            //logger.error("사용자 혜택 매장 조회 실패 - userId: {}", userId, e);
            throw new RuntimeException("혜택 매장 조회 중 오류가 발생했습니다.", e);
        }


    }

    @Override
    public Map<String, List<String>> getCardBrandByUserId(Long userId) {
        List<BenefitStoreDTO> benefitStores = cardRepository.findCardBrandByUserId(userId);

        System.out.println(benefitStores);

        return benefitStores.stream()
                .collect(Collectors.groupingBy(
                        store -> (String) store.getBrand(),  // 브랜드명으로 그룹화
                        Collectors.mapping(
                                store -> (String) store.getCardBenefitStore(),
                                Collectors.toList()
                        )
                ));
    }

    // 카드 혜택을 지도로
    @Override
    public List<CardBenefitDetail> getCardBenefitsSortedByDiscount(int userId, String store) {
        if (userId <= 0 || store == null || store.trim().isEmpty()) {
            throw new ApiException(ErrorCode.INVALID_USER);
        }

        List<CardBenefitDetail> rawData = cardRepository.selectCardBenefitsByUserIdAndStore(userId, store);
        if (rawData == null || rawData.isEmpty()) {
            throw new ApiException(ErrorCode.CARD_NOT_FOUND_MAP);
        }

        return rawData.stream().map(data -> {
                    int discountAmount = parseToDiscountAmount(data.getCardBenefitDiscntPrice(), store);
                    data.setDiscountPrice(discountAmount);
                    return data;
                }).sorted(Comparator.comparingInt(CardBenefitDetail::getDiscountPrice).reversed())
                .collect(Collectors.toList());
    }

    // 매장별 카드 혜택 높은 카드 추천
    @Override
    public List<CardBenefitDetail> getRecommendedCards(String store) {
        if (store == null || store.trim().isEmpty()) {
            throw new ApiException(ErrorCode.EMPTY_SEARCH_STORE);
        }

        List<CardBenefitDetail> rawList = cardRepository.selectRecommendedCardsByStore(store);
        if (rawList == null || rawList.isEmpty()) {
            throw new ApiException(ErrorCode.RECOMMENDATION_NOT_AVAILABLE);
        }

        return rawList.stream()
                .peek(dto -> dto.setDiscountPrice(parseToDiscountAmount(dto.getCardBenefitDiscntPrice(), store)))
                .filter(dto -> dto.getDiscountPrice() > 0)
                .sorted(Comparator.comparingInt(CardBenefitDetail::getDiscountPrice).reversed()
                        .thenComparingInt(CardBenefitDetail::getCardRank))
                .limit(3)
                .collect(Collectors.toList());
    }


    // 원 단위와 % 단위를 비교하기 위한 코드
    private int parseToDiscountAmount(String priceStr, String storeName) {
        try {
            priceStr = priceStr.trim().replace("천", "000").replace("백", "00").replace("만", "0000");
            if (priceStr.contains("~")) {
                String[] range = priceStr.split("~");
                if (range.length == 2) {
                    priceStr = range[1].trim();
                }
            }
            if (priceStr.contains("최대")) {
                priceStr = priceStr.replace("최대", "").trim();
                if (priceStr.endsWith("%")) {
                    double percent = Double.parseDouble(priceStr.replace("%", "").trim());
                    return storeName.contains("CGV") || storeName.contains("메가박스") || storeName.contains("롯데시네마") || storeName.contains("영화")
                            ? (int) (14000 * (percent / 100.0))
                            : (int) (10000 * (percent / 100.0));
                }
                if (priceStr.contains("포인트")) return 0;
            }
            if (priceStr.contains("쿠폰")) return 0;
            if (priceStr.endsWith("%")) {
                double percent = Double.parseDouble(priceStr.replace("%", "").trim());
                return storeName.contains("CGV") || storeName.contains("메가박스") || storeName.contains("롯데시네마") || storeName.contains("영화")
                        ? (int) (14000 * (percent / 100.0))
                        : (int) (10000 * (percent / 100.0));
            }
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
