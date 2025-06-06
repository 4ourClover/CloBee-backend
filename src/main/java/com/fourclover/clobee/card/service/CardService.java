package com.fourclover.clobee.card.service;

import com.fourclover.clobee.card.domain.CardBenefitDetail;
import com.fourclover.clobee.card.domain.CardListDTO;
import com.fourclover.clobee.card.domain.CardPageDTO;
import com.fourclover.clobee.card.domain.UserCardPerformanceDetail;

import java.util.List;
import java.util.Map;

public interface CardService {
    CardPageDTO getCardPage(String type, int page, int size);

    List<CardBenefitDetail> getCardBenefitDetail(Long cardInfoId);

    String getCardBrandUrlAndIncreaseApplyViews(Long cardInfoId, int cardBrand);

    void addUserCard(Long userId, Long cardInfoId, Integer userCardType);

    List<CardListDTO> getMyCardList(Long userId);

    List<CardListDTO> searchCard(String cardName);

    void addPerformance(UserCardPerformanceDetail detail);

    UserCardPerformanceDetail getPerformance(Long userCardId, int year, int month);

    void deleteUserCard(Long userId, Long cardInfoId);

    List<String> getBenefitStoresByUserId(Long userId);

    Map<String, List<String>> getCardBrandByUserId(Long userId);

    List<CardBenefitDetail> getCardBenefitsSortedByDiscount(int userId, String store);

    List<CardBenefitDetail> getRecommendedCards(String store);
}