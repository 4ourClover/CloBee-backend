package com.fourclover.clobee.card.domain;

import lombok.*;

import java.sql.Timestamp;

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
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
