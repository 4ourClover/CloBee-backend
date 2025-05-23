package com.fourclover.clobee.card.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
카드 데이터 리스트
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardListDTO {
    private Long userCardId;
    private Long cardInfoId;
    private int cardRank;
    private String cardName;
    private String cardBrand;
    private String cardBrandName;
    private int cardDomesticAnnualFee;
    private int cardGlobalAnnualFee;
    private int cardType;
    private String cardImageUrl;
}
