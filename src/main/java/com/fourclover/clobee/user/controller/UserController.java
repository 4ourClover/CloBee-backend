package com.fourclover.clobee.user.controller;

import com.fourclover.clobee.user.domain.UserInfo;
import com.fourclover.clobee.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // SMS 코드 발송
    @PostMapping("/sendPhoneCode")
    public ResponseEntity<Void> sendPhoneCode(@RequestParam("user_phone") String userPhone) {
        userService.sendPhoneVerificationCode(userPhone);
        return ResponseEntity.ok().build();
    }

    // SMS 코드 검증
    @PostMapping("/verifyPhoneCode")
    public ResponseEntity<Void> verifyPhoneCode(@RequestParam("user_phone") String userPhone,
                                                @RequestParam String code) {
        userService.verifyPhoneCode(userPhone, code);
        return ResponseEntity.ok().build();
    }

    // 이메일 회원가입
    @PostMapping("/signup/email")
    public ResponseEntity<Void> signup(@Valid @RequestBody UserInfo dto) {
        userService.registerEmailUser(dto);
        return ResponseEntity.ok().build();
    }

    // 카카오 로그인
    @GetMapping("/login/kakao")
    public ResponseEntity<UserInfo> kakaoLoginSuccess(@AuthenticationPrincipal OAuth2User oauthUser) {
        UserInfo user = userService.loginWithKakao(oauthUser);
        return ResponseEntity.ok(user);
    }

    // 카카오 회원가입 (프론트에서 닉네임·동의 폼 제출)
    @PostMapping("/signup/kakao")
    public ResponseEntity<Void> kakaoSignup(
            @RequestParam String nickname,
            @RequestParam Boolean agreedPrivacy,
            @AuthenticationPrincipal OAuth2User oauthUser
    ) {
        String email = ((Map<String,Object>)oauthUser.getAttribute("kakao_account")).get("email").toString();
        userService.registerKakaoUser(email, nickname, agreedPrivacy);
        return ResponseEntity.ok().build();
    }
}
