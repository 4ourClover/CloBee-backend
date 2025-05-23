package com.fourclover.clobee.user.controller;

import com.fourclover.clobee.config.exception.ApiException;
import com.fourclover.clobee.config.exception.ErrorCode;
import com.fourclover.clobee.user.domain.UserInfo;
import com.fourclover.clobee.user.domain.request.LoginRequest;
import com.fourclover.clobee.user.domain.request.RefreshRequest;
import com.fourclover.clobee.user.domain.request.TempPasswordRequest;
import com.fourclover.clobee.user.domain.response.FindEmailResponse;
import com.fourclover.clobee.user.domain.response.TokenResponse;
import com.fourclover.clobee.user.repository.UserRepository;
import com.fourclover.clobee.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
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

    // 임시 비밀번호 발급
    @PostMapping("/temp-password")
    public ResponseEntity<Void> sendTemporaryPassword(@RequestBody TempPasswordRequest request) {
        userService.sendTemporaryPassword(request);
        return ResponseEntity.ok().build();
    }

    // 이메일 회원가입
    @PostMapping("/signup/email")
    public ResponseEntity<Void> signup(@Valid @RequestBody UserInfo dto) {
        userService.registerEmailUser(dto);
        return ResponseEntity.ok().build();
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest dto, HttpServletRequest request) {
        return ResponseEntity.ok(userService.login(dto, request));
    }

    // 토큰 리프레쉬
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest dto, HttpServletRequest request) {
        return ResponseEntity.ok(userService.refresh(dto, request));
    }

    // 카카오 로그인
    @GetMapping("/login/kakao")
    public Object kakaoLoginSuccess(HttpServletResponse response, @AuthenticationPrincipal OAuth2User oauthUser) throws IOException {
        userService.kakaoLoginSuccess(response, oauthUser);
        return ResponseEntity.ok().build();
    }

    // 카카오 회원가입 (프론트에서 닉네임·동의 폼 제출)
    @PostMapping("/signup/kakao")
    public ResponseEntity<Void> kakaoSignup(
            @RequestParam String email,
            @RequestParam String nickname,
            @RequestParam Boolean agreedPrivacy
    ) {
        userService.registerKakaoUser(email, nickname, agreedPrivacy);
        return ResponseEntity.ok().build();
    }

    // 이메일 찾기
    @GetMapping("/find-email")
    public ResponseEntity<FindEmailResponse> findEmail(@RequestParam String phone) {
        return ResponseEntity.ok(userService.findEmail(phone));
    }

    // 에러 페이지 추가
    @GetMapping("/error")
    public ResponseEntity<Map<String, String>> error(@RequestParam(required = false) String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message != null ? message : "An error occurred");
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfo> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        UserInfo userInfo = userService.authedUserInfo(authentication);
        userInfo.setUserPassword(null);
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam String email) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", userService.checkEmailExists(email));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-phone")
    public ResponseEntity<Map<String, Boolean>> checkPhoneExists(@RequestParam String phone) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", userService.checkPhoneExists(phone));
        return ResponseEntity.ok(response);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        userService.logout(request, response);
        return ResponseEntity.ok().build();
    }
}
