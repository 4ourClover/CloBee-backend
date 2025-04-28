package com.fourclover.clobee.event.service;

import com.fourclover.clobee.event.domain.EventAttendanceDetail;
import com.fourclover.clobee.event.domain.EventInfo;

import java.util.List;

public interface EventService {
    List<EventAttendanceDetail> getTotalAttend(long userId);
    List<EventInfo> getCardEvents();
}
