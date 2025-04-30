package com.fourclover.clobee.card.service;

import com.fourclover.clobee.card.domain.CardBenefitDetail;
import com.fourclover.clobee.card.domain.CardPageDTO;
import com.fourclover.clobee.card.domain.CardPageListDTO;
import com.fourclover.clobee.card.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    // 전체 카드 불러오기
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

        List<CardPageListDTO> cards = cardRepository.allCardPaging(cardType, offset, size);
        int totalCount = cardRepository.allCardCount(cardType);
        return new CardPageDTO(cards, totalCount);

    }
    
    // 카드 상세 보기(전체, 내 카드 공통?)
    public List<CardBenefitDetail> getCardBenefitDetail(Long cardInfoId) {

        return cardRepository.cardBenefit(cardInfoId);
    }

}
