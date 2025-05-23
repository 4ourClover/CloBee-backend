package com.fourclover.clobee.user.service;

import com.fourclover.clobee.config.SmsConfig;
import com.fourclover.clobee.config.exception.ApiException;
import com.fourclover.clobee.config.exception.ErrorCode;
import com.fourclover.clobee.event.domain.EventFriendsDetail;
import com.fourclover.clobee.event.repository.EventRepository;
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
    private final EventRepository eventRepository;
    private BCryptPasswordEncoder encoder;
    private final JwtService jwtService;

    public UserServiceImpl(
            UserRepository userRepository,
            RedisTemplate<String, String> redisTemplate,
            Validator validator,
            DefaultMessageService messageService,
            SmsConfig smsConfig,
            BCryptPasswordEncoder encoder,
            JwtService jwtService, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.validator = validator;
        this.messageService = messageService;
        this.smsConfig = smsConfig;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.eventRepository = eventRepository;
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

        // 친구 초대 코드 입력 확인
        if (dto.getFriendInvitationCode() != null && !dto.getFriendInvitationCode().trim().isEmpty()) {
            Long friendUserId = userRepository.findUserIdByInvitationCode(dto.getFriendInvitationCode());
            if (friendUserId == null) {
                throw new IllegalArgumentException("유효하지 않은 초대 코드입니다.");
            }
            else {
                // userId가 event_friends_detail에 존재 하는 지 확인
                Long eventFriendsId = userRepository.findEventFriendByUserId(friendUserId);
                EventFriendsDetail eventFriendsDetail;

                // 존재 하지 않는다면 insert, 존재 한다면 update
                if(eventFriendsId == null) {
                    userRepository.insertEventFriendsDetail(friendUserId);
                    eventFriendsDetail = userRepository.getEventFriendsDetail(friendUserId);
                }
                else {
                    eventFriendsDetail = userRepository.getEventFriendsDetail(friendUserId);

                    if(eventFriendsDetail.getEventFriendsRouletteCountLimit() < 10) {
                        userRepository.plusRouletteCountLimit(eventFriendsDetail.getEventFriendsId());
                    }
                }

                // 로그 기록
                userRepository.insertEventFriendLog(eventFriendsDetail.getEventFriendsId(), friendUserId, dto.getUserId());
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
        Object principal = authentication.getPrincipal();
        String email = null;

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = ((OAuth2User) principal).getAttribute("email");
        } else {
            throw new IllegalStateException(principal.getClass() + "은 올바르지 않습니다.");
        }

        UserInfo userInfo = userRepository.findByEmail(email);
        if (userInfo == null) {
            throw new IllegalArgumentException(email + "로 등록된 이메일 정보를 찾을수 없습니다.");
        }

        return userInfo;
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
}
