package com.fourclover.clobee.event.repository;

import com.fourclover.clobee.event.domain.EventAttendanceDetail;
import com.fourclover.clobee.event.domain.EventFindingCloverDetail;
import com.fourclover.clobee.event.domain.EventInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface EventRepository {
    // 전체 이벤트 중 특정 이벤트 내역 불러오기
    List<EventInfo> getEventInfo(int comCodeId);

    // 출석 이벤트
    List<String> getTotalAttend(Map params);
    Long addAttendDay(EventAttendanceDetail attendanceDetail);

    // 클로버 찾기 이벤트
    EventFindingCloverDetail selectCloverDetailByUserId(@Param("userId") Long userId);
    int insertCloverDetail(EventFindingCloverDetail detail);
    int updateCloverDetail(EventFindingCloverDetail detail);

    // findClover용 event_info 한 건 조회 (진행중인)
    EventInfo selectEventInfoByTypeCd(int eventTypeCd);
}
