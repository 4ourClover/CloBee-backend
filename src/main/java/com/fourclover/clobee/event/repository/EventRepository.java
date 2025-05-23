package com.fourclover.clobee.event.repository;

import com.fourclover.clobee.event.domain.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface EventRepository {
    // 카드사 이벤트
    List<EventInfo> getEventInfo(Map params);

    // 출석 이벤트
    List<String> getTotalAttend(Map params);
    Long addAttendDay(EventAttendanceDetail attendanceDetail);

    // 클로버 찾기 이벤트
    EventFindingCloverDetail selectCloverDetailByUserId(@Param("userId") Long userId);
    List<EventFindingCloverDetail> selectCloverDetailByUserList();
    int insertCloverDetail(EventFindingCloverDetail detail);
    int updateCloverDetail(EventFindingCloverDetail detail);

    // findClover용 event_info 한 건 조회 (진행중인)
    EventInfo selectEventInfoByTypeCd(int eventTypeCd);

    // 특정 이벤트 유형의 쿠폰 템플릿 조회
    List<CouponTemplate> selectCouponTemplatesByEventType(int eventTypeCd);

    // 쿠폰 발급용 insert 메서드
    int insertCouponInfo(CouponInfo couponInfo);

    // 친구 초대 이벤트
    Integer findUserIdByInvitationCode(String invitationCode);

    boolean existsByInvitedUser(int invitedUserId);

    void insertEventFriendLog(EventFriends eventFriends);

    Integer getRouletteCount(@Param("eventInfoId") int eventInfoId,
                             @Param("userId") int userId);

    void insertEventFriendDetail(@Param("eventInfoId") int eventInfoId,
                                 @Param("userId") int userId);

    void increaseRouletteCount(@Param("eventInfoId") int eventInfoId,
                               @Param("userId") int userId);

    void decreaseRouletteCount(@Param("eventInfoId") int eventInfoId,
                               @Param("userId") int userId);

    String getInvitationCodeByUserId(int userId);

    CouponTemplate pickRandomCoupon(int eventTypeCd);

    boolean existsUserCoupon(@Param("userId") int userId,
                             @Param("templateId") int templateId);

    void insertCoupon(@Param("userId") int userId,
                      @Param("templateId") int templateId);
}
