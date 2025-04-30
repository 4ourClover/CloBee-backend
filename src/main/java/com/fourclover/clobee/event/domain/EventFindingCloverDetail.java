package com.fourclover.clobee.event.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventFindingCloverDetail {
    private Long eventFindingCloverId;
    private Long eventInfoId;
    private Long userId;
    // 금일 참여 여부(00시 초기화)
    private Boolean eventFindingCloverParticipationStatus;
    // 현재 단계(1=쉬움, 2=보통, 3=어려움)
    private Integer eventFindingCloverCurrentStage;
    // 쿠폰 수령 여부
    private Boolean eventFindingCloverReceiveCoupon;
    // 시도 횟수
    private Integer eventFindingCloverAttemptsLeft;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
