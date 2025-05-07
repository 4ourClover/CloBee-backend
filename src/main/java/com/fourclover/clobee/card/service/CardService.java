package com.fourclover.clobee.card.service;

import com.fourclover.clobee.card.domain.*;

import java.util.List;

public interface CardService {
    CardPageDTO getCardPage(String type, int page, int size);

    List<CardBenefitDetail> getCardBenefitDetail(Long cardInfoId);

    String getCardBrandUrlAndIncreaseApplyViews(Long cardInfoId, int cardBrand);

    void addUserCard(Long userId, Long cardInfoId, Integer userCardType);

    List<UserCardListDTO> getMyCardList(Long userId);

    List<CardListDTO> searchCard(String cardName);

    void addPerformance(UserCardPerformanceDetail detail);

    UserCardPerformanceDetail getPerformance(Long userCardId, int year, int month);
}