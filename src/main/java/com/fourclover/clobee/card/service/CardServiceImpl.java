package com.fourclover.clobee.card.service;

import com.fourclover.clobee.card.domain.CardBenefitDetail;
import com.fourclover.clobee.card.domain.CardListDTO;
import com.fourclover.clobee.card.domain.CardPageDTO;
import com.fourclover.clobee.card.domain.UserCardDetail;
import com.fourclover.clobee.card.repository.CardRepository;
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
                // 나중에 Exception 설정
                break;
        }

        List<CardListDTO> cards = cardRepository.allCardPaging(cardType, offset, size);
        int totalCount = cardRepository.allCardCount(cardType);
        return new CardPageDTO(cards, totalCount);

    }

    // 카드 상세 보기(전체, 내 카드 공통?)
    @Override
    public List<CardBenefitDetail> getCardBenefitDetail(Long cardInfoId) {
        return cardRepository.getCardBenefit(cardInfoId);
    }

    // 카드사 url 가져오기(카드 신청하기)
    // 카드 신청하기 버튼 클릭 시 card_apply_views 증가
    @Override
    @Transactional  // 트랜잭션
    public String getCardBrandUrlAndIncreaseApplyViews(Long cardInfoId, int cardBrand) {
        cardRepository.updateApplyViews(cardInfoId);
        return cardRepository.getCardBrandURL(cardBrand);
       // 추후 에러코드 추가
    }

    // 내 카드 추가하기
    @Override
    public void addUserCard(Long userId, Long cardInfoId) {
        CardListDTO card = cardRepository.findByCardInfoId(cardInfoId);
                //.orElseThrow(() -> new ApiException(ErrorCode.CARD_NOT_FOUND));

        UserCardDetail detail = UserCardDetail.builder()
                .userId(userId)
                .cardInfoId(cardInfoId)
                .build();

        cardRepository.insertUserCard(detail);
    }

    // 내 카드 조회하기
    @Override
    public List<CardListDTO> getMyCardList(Long userId) {
        return cardRepository.getMyCard(userId);
    }

    // 카드 검색
    @Override
    public List<CardListDTO> searchCard(String cardName) {
        return cardRepository.searchCard("%" + cardName + "%");
    }
}
