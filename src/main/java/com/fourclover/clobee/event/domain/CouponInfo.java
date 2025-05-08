package com.fourclover.clobee.event.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CouponInfo {
    private Long couponInfoId;
    private Long userId;
    private Long templateId;
    private Boolean couponUsedYn;
    private Boolean couponDoneYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
