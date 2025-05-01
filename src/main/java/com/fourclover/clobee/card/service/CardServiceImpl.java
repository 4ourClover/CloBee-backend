package com.fourclover.clobee.card.service;

import com.fourclover.clobee.card.domain.CardBenefitDetail;
import com.fourclover.clobee.card.domain.CardListDTO;
import com.fourclover.clobee.card.domain.CardPageDTO;
import com.fourclover.clobee.card.domain.UserCardDetail;
import com.fourclover.clobee.card.repository.CardRepository;
import com.fourclover.clobee.config.exception.ApiException;
import com.fourclover.clobee.config.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public void addUserCard(Long userId, Long cardInfoId) {
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
}
