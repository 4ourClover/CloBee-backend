package com.fourclover.clobee.card.domain;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCardPerformanceDetail {
    private Long performanceId;
    private Long userCardId;
    private int year;
    private int month;
    private int monthlyAmount;
}
