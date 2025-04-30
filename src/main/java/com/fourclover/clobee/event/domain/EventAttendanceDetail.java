package com.fourclover.clobee.event.domain;

import lombok.Builder;
import lombok.Getter;

import java.sql.Date;

@Getter
@Builder
public class EventAttendanceDetail {
    private Long eventAttendanceId;
    private Long eventInfoId;
    private Long userId;
    private Date createAt;
}
