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
    private String cardBenefitStore;
    private String cardBenefitDiscntPrice;
    private String cardBenefitDesc;         // 혜택 상세 설명 text
    private String cardBenefitCondition;    // 혜택 조건
    private String cardName;
    private int discountPrice;  // 할인 퍼센트를 금액으로 환산
    private int cardRank;
    private int cardBrand;
    private String cardImageUrl;

}
