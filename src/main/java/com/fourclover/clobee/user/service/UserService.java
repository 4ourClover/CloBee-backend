package com.fourclover.clobee.user.service;

import com.fourclover.clobee.user.domain.UserInfo;
import com.fourclover.clobee.user.domain.request.LoginRequest;
import com.fourclover.clobee.user.domain.request.RefreshRequest;
import com.fourclover.clobee.user.domain.request.TempPasswordRequest;
import com.fourclover.clobee.user.domain.response.FindEmailResponse;
import com.fourclover.clobee.user.domain.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface UserService {
    void sendPhoneVerificationCode(String phone);
    void verifyPhoneCode(String phone, String code);
    void registerEmailUser(UserInfo dto);
    void registerKakaoUser(String email, String nickname, Boolean agreedPrivacy);

    void sendTemporaryPassword(TempPasswordRequest request);
    FindEmailResponse findEmail(String phone);
    TokenResponse login(LoginRequest dto, HttpServletRequest request);
    TokenResponse refresh(RefreshRequest dto, HttpServletRequest request);
    TokenResponse loginWithKakao(OAuth2User oauth2User);
    Object kakaoLoginSuccess(HttpServletResponse response, OAuth2User oauthUser);
    UserInfo authedUserInfo(Authentication authentication);

    // 새로 추가된 메서드: 이메일 중복 체크
    boolean checkEmailExists(String email);

    // 새로 추가된 메서드: 전화번호 중복 체크
    boolean checkPhoneExists(String phone);

    // 로그아웃 메서드 추가
    void logout(HttpServletRequest request, HttpServletResponse response);
}
