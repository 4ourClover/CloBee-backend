package com.fourclover.clobee.user.service;

import com.fourclover.clobee.config.SmsConfig;
import com.fourclover.clobee.config.exception.ApiException;
import com.fourclover.clobee.config.exception.ErrorCode;
import com.fourclover.clobee.token.service.JwtService;
import com.fourclover.clobee.user.domain.UserInfo;
import com.fourclover.clobee.user.domain.request.LoginRequest;
import com.fourclover.clobee.user.domain.request.RefreshRequest;
import com.fourclover.clobee.user.domain.request.TempPasswordRequest;
import com.fourclover.clobee.user.domain.response.FindEmailResponse;
import com.fourclover.clobee.user.domain.response.TokenResponse;
import com.fourclover.clobee.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Validator;
import net.nurigo.sdk.message.exception.NurigoEmptyResponseException;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.exception.NurigoUnknownException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final Validator validator;
    private final DefaultMessageService messageService;
    private final SmsConfig smsConfig;
    private BCryptPasswordEncoder encoder;
    private final JwtService jwtService;

    public UserServiceImpl(
            UserRepository userRepository,
            RedisTemplate<String, String> redisTemplate,
            Validator validator,
            DefaultMessageService messageService,
            SmsConfig smsConfig,
            BCryptPasswordEncoder encoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.validator = validator;
        this.messageService = messageService;
        this.smsConfig = smsConfig;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @Override
    public void sendPhoneVerificationCode(String phone) {
        // 6자리 랜덤 코드 생성
        String code = String.format("%06d", new Random().nextInt(900_000) + 100_000);

        // Redis에 5분 만료로 저장
        redisTemplate.opsForValue()
                .set("SMS_CODE:" + phone, code, Duration.ofMinutes(5));

        // COOL SMS 메시지 생성 및 전송
        Message message = new Message();
        message.setFrom(smsConfig.getSenderPhone());
        message.setTo(phone);
        message.setText("인증번호 [" + code + "]를 입력해주세요.");

        try {
            messageService.send(message);
        } catch (NurigoMessageNotReceivedException | NurigoEmptyResponseException | NurigoUnknownException e) {
            throw new ApiException(ErrorCode.SMS_SEND_FAILURE);
        }
    }

    @Override
    public void verifyPhoneCode(String phone, String code) {
        String saved = redisTemplate.opsForValue().get("SMS_CODE:" + phone);
        if (saved == null) {
            throw new ApiException(ErrorCode.PHONE_CODE_EXPIRED);
        }
        if (!saved.equals(code)) {
            throw new ApiException(ErrorCode.PHONE_CODE_MISMATCH);
        }
        // 성공 시엔 register 단계에서 phoneVerified=true 로 처리
    }

    @Value("${app.skip-phone-verification:true}")
    private boolean skipPhoneVerification;

    @Override
    public void registerEmailUser(UserInfo dto) {
        // UserInfo에 지정된 @NotBlank, @Email, @Size 등 검사 수행
        var violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ApiException(ErrorCode.VALIDATION_FAILED);
        }
        // 이메일 중복 확인
        Optional<UserInfo> exist = Optional.ofNullable(userRepository.findByEmail(dto.getUserEmail()));
        if (exist.isPresent()) {
            throw new ApiException(ErrorCode.EMAIL_DUPLICATION);
        }
        if (userRepository.findByPhone(dto.getUserPhone()) != null) {
            throw new ApiException(ErrorCode.PHONE_DUPLICATION);
        }
        // 전화번호 인증 확인
        if (!skipPhoneVerification) {
            String codeInRedis = redisTemplate.opsForValue().get("SMS_CODE:" + dto.getUserPhone());
            if (codeInRedis == null) {
                throw new ApiException(ErrorCode.PHONE_VERIFICATION_REQUIRED);
            }
        }

        // 개인정보 동의 여부 확인
        if (Boolean.FALSE.equals(dto.getUserAgreedPrivacy())) {
            throw new ApiException(ErrorCode.PRIVACY_NOT_AGREED);
        }

        dto.setUserPassword(encoder.encode(dto.getUserPassword())); // 비밀번호 암호화
        dto.setUserInvitationCode(UUID.randomUUID().toString().substring(0,10));
        dto.setUserPhoneVerified(true);
        dto.setUserLoginType(102);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        userRepository.insertUser(dto);
    }

    // 임시 비밀번호 생성
    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i <= 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    // 임시 비밀번호 전송
    @Override
    public void sendTemporaryPassword(TempPasswordRequest request) {
        String phone = request.phone();
        String tempPassword = generateTempPassword();
        UserInfo userInfo = userRepository.findByPhone(phone);
        if (userInfo == null)
            throw new ApiException(ErrorCode.USER_NOT_FOUND);

        // 카카오 유저는 제외
        if (userInfo.getUserLoginType() == 101)
            throw new ApiException(ErrorCode.USER_NOT_FOUND);

        Message message = new Message();
        message.setFrom(smsConfig.getSenderPhone());
        message.setTo(phone);
        message.setText("임시 비밀번호는 [" + tempPassword + "]입니다.");

        // Redis에 5분 만료로 저장
        redisTemplate.opsForValue()
                .set("TEMP_PASSWORD:" + userInfo.getUserEmail(), tempPassword, Duration.ofMinutes(5));

        try {
            messageService.send(message);
        } catch (NurigoMessageNotReceivedException | NurigoEmptyResponseException | NurigoUnknownException e) {
            throw new ApiException(ErrorCode.SMS_SEND_FAILURE);
        }
    }

    @Override
    public TokenResponse login(LoginRequest dto, HttpServletRequest request) {
        String codeInRedis = redisTemplate.opsForValue().get("TEMP_PASSWORD:" + dto.email());
        UserInfo user = userRepository.findByEmail(dto.email());
        if (!encoder.matches(dto.password(), user.getUserPassword()) && !dto.password().equals(codeInRedis)) {
            throw new ApiException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        String accessToken = jwtService.generateAccessToken(user.getUserId());
        String refreshToken = jwtService.generateRefreshToken(user.getUserId());

        user.setUserAccessToken(accessToken);
        user.setUserRefreshToken(refreshToken);
        userRepository.updateUser(user);

        HttpSession session = request.getSession(true);
        session.setAttribute("accessToken", accessToken);
        session.setAttribute("refreshToken", refreshToken);
        return new TokenResponse(
                accessToken, refreshToken
        );
    }

    @Override
    public TokenResponse refresh(RefreshRequest dto, HttpServletRequest request) {
        UserInfo user = userRepository.findById(jwtService.extractUserId(dto.refreshToken()));

        String accessToken = jwtService.generateAccessToken(user.getUserId());
        String refreshToken = jwtService.generateRefreshToken(user.getUserId());

        user.setUserAccessToken(accessToken);
        user.setUserRefreshToken(refreshToken);
        userRepository.updateUser(user);

        HttpSession session = request.getSession(true);
        session.setAttribute("accessToken", accessToken);
        session.setAttribute("refreshToken", refreshToken);

        return new TokenResponse(
                accessToken, refreshToken
        );
    }

    @Override
    public TokenResponse loginWithKakao(OAuth2User oauth2User) {
        try {
            String email = extractEmail(oauth2User);
            UserInfo user = userRepository.findByEmail(email);
            if (user == null) {
                // 가입 안내
                throw new ApiException(ErrorCode.KAKAO_NEED_SIGNUP);

            }
            String accessToken = jwtService.generateAccessToken(user.getUserId());
            String refreshToken = jwtService.generateRefreshToken(user.getUserId());

            user.setUserAccessToken(accessToken);
            user.setUserRefreshToken(refreshToken);
            userRepository.updateUser(user);

            return new TokenResponse(accessToken, refreshToken);
        } catch (ApiException e) {
            throw new ApiException(ErrorCode.KAKAO_NEED_SIGNUP);
        }
    }

    @Override
    public FindEmailResponse findEmail(String phone) {
        UserInfo user = userRepository.findByPhone(phone);
        if (user == null) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }
        return new FindEmailResponse(user.getUserEmail());
    }

    @Override
    public void registerKakaoUser(String email, String nickname, Boolean agreedPrivacy) {
        if (userRepository.findByEmail(email) != null) {
            throw new ApiException(ErrorCode.EMAIL_DUPLICATION);
        }
        UserInfo dto = new UserInfo();
        dto.setUserLoginType(101);              // 카카오 가입
        dto.setUserEmail(email);
        dto.setUserNickname(nickname);
        dto.setUserAgreedPrivacy(agreedPrivacy);
        dto.setUserPhoneVerified(false);
        dto.setUserInvitationCode(UUID.randomUUID().toString().substring(0,10));
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        userRepository.insertUser(dto);
    }

    @Override
    public Object kakaoLoginSuccess(HttpServletResponse response, @AuthenticationPrincipal OAuth2User oauthUser)  {
        try {
            if (oauthUser == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "OAuth2User is null"));
            }

            // 로그인 시도
            TokenResponse tokenResponse = this.loginWithKakao(oauthUser);

            // 헤더에 토큰 삽입
            setTokenToCookie(response, tokenResponse);

            // 프론트 리다이렉션
            response.sendRedirect("http://localhost:3000/kakao/callback");

            return ResponseEntity.ok(tokenResponse);

        } catch (ApiException e) {
            if (e.getErrorCode() == ErrorCode.KAKAO_NEED_SIGNUP) {
                String email = extractEmail(oauthUser);
                String nickname = extractNickname(oauthUser);

                this.registerKakaoUser(email, nickname, true);

                TokenResponse tokenResponse = this.loginWithKakao(oauthUser);

                // 헤더에 토큰 삽입
                setTokenToCookie(response, tokenResponse);

                // 자동 로그인 상태로 변경
                try {
                    response.sendRedirect("http://localhost:3000/kakao/callback");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                return ResponseEntity.ok().build();
            }

            // 그 외 API 예외
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getErrorCode().name());
            error.put("message", e.getErrorCode().getErrorMessage());
            return ResponseEntity.badRequest().body(error);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "UNKNOWN_ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @Override
    public UserInfo authedUserInfo(Authentication authentication) {
        // 컨트롤러에서 체크하지만 추가 안전 장치로 여기서도 체크
        if (authentication == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        try {
            Object principal = authentication.getPrincipal();
            String email = null;

            if (principal instanceof UserDetails) {
                email = ((UserDetails) principal).getUsername();
            } else if (principal instanceof OAuth2User) {
                email = ((OAuth2User) principal).getAttribute("email");
            } else {
                throw new ApiException(ErrorCode.AUTHENTICATION_FAILED);
            }

            UserInfo userInfo = userRepository.findByEmail(email);
            if (userInfo == null) {
                throw new ApiException(ErrorCode.USER_NOT_FOUND);
            }

            return userInfo;
        } catch (NullPointerException e) {
            // getPrincipal() 또는 다른 메서드에서 NPE가 발생할 경우
            throw new ApiException(ErrorCode.AUTHENTICATION_FAILED);
        } catch (ClassCastException e) {
            // 타입 변환 실패 시
            throw new ApiException(ErrorCode.AUTHENTICATION_FAILED);
        }
    }

    private String extractEmail(OAuth2User oauth2User) {
        Map<String,Object> acc = oauth2User.getAttribute("kakao_account");
        String email = acc != null ? (String)acc.get("email") : null;
        if (email == null) {
            throw new ApiException(ErrorCode.KAKAO_EMAIL_NOT_FOUND);
        }
        return email;
    }

    private String extractNickname(OAuth2User oauth2User) {
        Map<String, Object> kakaoAccount = oauth2User.getAttribute("kakao_account");
        Map<String, Object> profile = kakaoAccount != null ? (Map<String, Object>) kakaoAccount.get("profile") : null;
        String nickname = profile != null ? (String) profile.get("nickname") : null;
        if (nickname == null) {
            throw new ApiException(ErrorCode.KAKAO_NICKNAME_NOT_FOUND);
        }
        return nickname;
    }

    private void setTokenToCookie(HttpServletResponse response, TokenResponse token) {
        Cookie accessTokenCookie = new Cookie("accessToken", token.access());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true); // HTTPS에서만 전송
        accessTokenCookie.setPath("/"); // 루트 경로에만 쿠키 전달
        accessTokenCookie.setMaxAge(60 * 60 * 24 * 7); // 7일 설정


        Cookie refreshTokenCookie = new Cookie("refreshToken", token.refresh());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 30);

        // 쿠키 응답에 추가
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

    @Override
    public boolean checkEmailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public boolean checkPhoneExists(String phone) {
        return userRepository.findByPhone(phone) != null;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 세션에서 토큰 가져오기
        HttpSession session = request.getSession(false);
        if (session != null) {
            // 액세스 토큰 및 리프레시 토큰 무효화
            String accessToken = (String) session.getAttribute("accessToken");
            String refreshToken = (String) session.getAttribute("refreshToken");

            if (accessToken != null) {
                // Redis에 저장된 토큰을 블랙리스트에 추가하거나 삭제
                // 실제 토큰 만료시간만큼 유지
                try {
                    Long userId = jwtService.extractUserId(accessToken);
                    UserInfo user = userRepository.findById(userId);
                    if (user != null) {
                        user.setUserAccessToken(null);
                        user.setUserRefreshToken(null);
                        userRepository.updateUser(user);
                    }
                } catch (Exception e) {
                    // 토큰이 이미 만료되었거나 유효하지 않은 경우 무시
                }
            }

            // 세션 무효화
            session.invalidate();
        }

        // 쿠키 삭제
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName()) || "refreshToken".equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0); // 쿠키 즉시 만료
                    response.addCookie(cookie);
                }
            }
        }

        // 명시적으로 새 쿠키를 만들어 덮어씌우기
        Cookie accessTokenCookie = new Cookie("accessToken", "");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);

        Cookie refreshTokenCookie = new Cookie("refreshToken", "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

}
