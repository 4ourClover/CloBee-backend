package com.fourclover.clobee.user.service;

import com.fourclover.clobee.user.domain.UserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface UserService {
    void sendPhoneVerificationCode(String phone);
    void verifyPhoneCode(String phone, String code);
    void registerEmailUser(UserInfo dto);
    void registerKakaoUser(String email, String nickname, Boolean agreedPrivacy);
    UserInfo loginWithKakao(OAuth2User oauth2User);
}
