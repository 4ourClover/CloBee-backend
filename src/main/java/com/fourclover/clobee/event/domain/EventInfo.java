package com.fourclover.clobee.event.domain;

import lombok.Getter;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
public class EventInfo {
    private Long event_info_id;
    private String event_title;
    private String event_desc;
    private int event_type_cd;
    private Date event_start_day;
    private Date event_end_day;
    private int event_status_cd;
    private String event_card_url;
    private String event_card_corp;
    private String event_qr;
    private Boolean is_deleted;
    private Timestamp created_at;
    private Timestamp updated_at;
}
