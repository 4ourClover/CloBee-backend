package com.fourclover.clobee.card.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCardListDTO {
    private Long cardInfoId;
    private int cardRank;
    private String cardName;
    private String cardBrand;
    private int cardDomesticAnnualFee;
    private int cardGlobalAnnualFee;
    private int cardType;
    private String cardImageUrl;
    private Long userId;
}
