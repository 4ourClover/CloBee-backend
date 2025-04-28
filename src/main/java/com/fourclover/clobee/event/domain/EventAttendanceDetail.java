package com.fourclover.clobee.event.domain;

import lombok.Getter;

import java.sql.Date;

@Getter
public class EventAttendanceDetail {
    private Long event_attendance_id;
    private Long event_info_id;
    private Long user_id;
    private Date created_at;
}
