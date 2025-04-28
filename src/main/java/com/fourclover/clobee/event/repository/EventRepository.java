package com.fourclover.clobee.event.repository;

import com.fourclover.clobee.event.domain.EventAttendanceDetail;
import com.fourclover.clobee.event.domain.EventInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EventRepository {
    // 출석 이벤트
    List<EventAttendanceDetail> getTotalAttend(long userId);
    Long addAttendDay(EventAttendanceDetail attendanceDetail);

    // 카드 이벤트
    List<EventInfo> getCardEvents();
}
