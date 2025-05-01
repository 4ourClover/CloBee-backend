package com.fourclover.clobee.card.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCardDetail {
    private Long userId;
    private Long cardInfoId;
    private Integer userCardType;
    private Integer userCardBrand;
    private String userCardNumber;
    private Integer userCardCvc;
    private String userCardName;
    private String userCardExpiration;
}


