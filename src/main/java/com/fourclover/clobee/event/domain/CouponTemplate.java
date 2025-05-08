package com.fourclover.clobee.event.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class CouponTemplate {
    private Long templateId;
    private String eventTypeCd;
    private String couponName;
    private String couponDesc;
    private String couponTitle;
    private Integer couponAmount;
    private LocalDate couponValidDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
