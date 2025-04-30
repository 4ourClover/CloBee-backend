package com.fourclover.clobee.event.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventFindingCloverDetail {
    private Long event_finding_clover_id;
    private Long event_info_id;
    private Long user_id;
    private Boolean event_finding_clover_participation_status; //금일 참여 여부(00시 초기화)
    private Integer event_finding_clover_current_stage; //현재 단계(1=쉬움, 2=보통, 3=어려움)
    private Boolean event_finding_clover_receive_coupon; //쿠폰 수령 여부
    private Integer event_finding_clover_attempts_left; //시도 횟수
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
