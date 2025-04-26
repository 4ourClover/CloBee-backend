package com.fourclover.clobee.user.domain;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class UserInfo {
    private Long user_id;
    private String username;
    private String email;
    private Timestamp created_at;
    private Timestamp updated_at;
}
