package com.fourclover.clobee.card.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
한 페이지에 보여줄 데이터 리스트
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardPageListDTO {
    private Long cardInfoId;
    private int cardRank;
    private String cardName;
    private String cardBrand;
    private int cardDomesticAnnualFee;
    private int cardGlobalAnnualFee;
    private int cardType;
    private String cardImageUrl;
}
