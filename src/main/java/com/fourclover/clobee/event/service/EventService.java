package com.fourclover.clobee.event.service;

import com.fourclover.clobee.event.domain.EventAttendanceDetail;
import com.fourclover.clobee.event.domain.EventFindingCloverDetail;
import com.fourclover.clobee.event.domain.EventInfo;

import java.util.List;

public interface EventService {
    List<EventAttendanceDetail> getTotalAttend(long userId);
    Long addAttend(EventAttendanceDetail attendanceDetail);

    List<EventInfo> getCardEvents();

    EventFindingCloverDetail startCloverGame(Long userId, boolean invitedByFriend);
    EventFindingCloverDetail processCloverAttempt(Long userId, boolean success);
    EventFindingCloverDetail getCloverStatus(Long userId);
}
