package com.fourclover.clobee.card.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardBenefitDetail {
    private int cardBenefitId;
    private Long cardInfoId;
    private int cardBenefitCategory;
    private String cardBenefitStore;
    private String cardBenefitTitle;
    private String cardBenefitDesc;         // 혜택 상세 설명 text
    private double cardBenefitDiscntRate;   // 할인율
    private double cardBenefitDiscntPrice;  // 할인 금액
    private String cardBenefitCondition;    // 혜택 조건
}
