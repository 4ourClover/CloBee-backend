package com.fourclover.clobee.user.repository;

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
}