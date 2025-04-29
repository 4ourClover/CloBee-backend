package com.fourclover.clobee.card.service;

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

}
