package com.fourclover.clobee.user.repository;

import com.fourclover.clobee.user.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserRepository {
    Optional<UserInfo> findByEmail(String userEmail);
}