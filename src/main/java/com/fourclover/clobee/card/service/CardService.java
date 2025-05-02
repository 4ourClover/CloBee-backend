package com.fourclover.clobee.card.service;

import com.fourclover.clobee.card.domain.CardBenefitDetail;
import com.fourclover.clobee.card.domain.CardListDTO;
import com.fourclover.clobee.card.domain.CardPageDTO;

import java.util.List;

public interface CardService {
    CardPageDTO getCardPage(String type, int page, int size);

    List<CardBenefitDetail> getCardBenefitDetail(Long cardInfoId);

    String getCardBrandUrlAndIncreaseApplyViews(Long cardInfoId, int cardBrand);

    void addUserCard(Long userId, Long cardInfoId);

    List<CardListDTO> getMyCardList(Long userId);

    List<CardListDTO> searchCard(String cardName);
}