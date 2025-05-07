package com.fourclover.clobee.card.domain;

import lombok.*;

// 카드 실적 가져오기 위해 userCardId 추가한 DTO
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCardListDTO {
    private Long userCardId;
    private Long cardInfoId;
    private int cardRank;
    private String cardName;
    private String cardBrand;
    private int cardDomesticAnnualFee;
    private int cardGlobalAnnualFee;
    private int cardType;
    private String cardImageUrl;
}
