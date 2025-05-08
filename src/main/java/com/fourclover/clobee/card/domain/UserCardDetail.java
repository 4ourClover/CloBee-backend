package com.fourclover.clobee.card.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCardDetail {
    private Long userCardId;
    private Long userId;
    private Long cardInfoId;
    private Integer userCardType;
}


