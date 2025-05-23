package com.fourclover.clobee.user.repository;

import com.fourclover.clobee.event.domain.EventFriendsDetail;
import com.fourclover.clobee.user.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRepository {

    // 이메일로 가입된 사용자가 있는지 조회
    UserInfo findByEmail(@Param("userEmail") String email);

    // 신규 사용자 INSERT
    void insertUser(UserInfo user);

    // 카카오 회원가입 완료 후 프로필 업데이트
    void updateUser(UserInfo user);

    UserInfo findById(@Param("userId") Long id);

    UserInfo findByPhone(@Param("userPhone") String phone);

    // 초대 코드 유효성 검증
    Long findUserIdByInvitationCode(String invitationCode);

    // userId가 event friends detail 테이블에 존재하는지 확인
    Long findEventFriendByUserId(Long userId);

    // event_friends_detail 테이블에 insert
    void insertEventFriendsDetail(Long userId);

    // 이벤트 DTO 가져와서 총 룰렛 횟수 10 이상인지 확인
    EventFriendsDetail getEventFriendsDetail(Long userId);

    // 룰렛 횟수 추가 업데이트
    void plusRouletteCountLimit(Long eventFriendsId);

    // 친구 초대 로그에 추가
    void insertEventFriendLog(Long eventFriendsId, Long friendUserId, Long userId);

}