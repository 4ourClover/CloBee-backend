package com.fourclover.clobee.event.domain;

import lombok.Getter;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
public class EventInfo {
    private Long eventInfoId;
    private String eventTitle;
    private String eventDesc;
    private int eventTypeCd;
    private Date eventStartDay;
    private Date eventEndDay;
    private int eventStatusCd;
    private String eventCardUrl;
    private String eventCardCorp;
    private String eventQr;
    private Boolean isDeleted;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
