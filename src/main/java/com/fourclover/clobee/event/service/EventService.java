package com.fourclover.clobee.event.service;

import com.fourclover.clobee.event.domain.EventAttendanceDetail;
import com.fourclover.clobee.event.domain.EventCloverCollectionDetail;
import com.fourclover.clobee.event.domain.EventFindingCloverDetail;
import com.fourclover.clobee.event.domain.EventInfo;

import java.util.List;

public interface EventService {
    // 출석 이벤트
    List<String> getTotalAttend(long userId, String month);
    Long addAttend(EventAttendanceDetail eventAttendanceDetail);

    // 카드사 이벤트
    List<EventInfo> getCardEvents(Long userId, int pageSize, int pageNumber);

    // 클로버 만들기 이벤트
    EventCloverCollectionDetail selectCloverCollection(Long userId);

    void initCloverGame();
    EventFindingCloverDetail startCloverGame(Long userId, boolean invitedByFriend);
    EventFindingCloverDetail processCloverAttempt(Long userId, boolean success);
    EventFindingCloverDetail getCloverStatus(Long userId);

}
