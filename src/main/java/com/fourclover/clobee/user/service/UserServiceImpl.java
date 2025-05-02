package com.fourclover.clobee.user.service;

import com.fourclover.clobee.config.exception.ApiException;
import com.fourclover.clobee.config.exception.ErrorCode;
import com.fourclover.clobee.user.domain.UserInfo;
import com.fourclover.clobee.user.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository; // MyBatis 매퍼 인터페이스로, DB의 user_info 테이블에 접근해 사용자 조회·등록
    private final RedisTemplate<String, String> redisTemplate; // Redis에 SMS 인증 코드를 저장·조회하기 위해 사용
    private final WebClient webClient; // SMS API 호출을 비동기 방식으로 수행하기 위한 Spring WebFlux 클라이언트
    private final Validator validator; // @Valid 외에 직접 DTO 제약조건(Validation) 검사를 수행할 때 사용

    // SMS 전송용 외부 API의 URL 및 인증키를 @Value로 주입
    @Value("${sms.api.url}")
    private String smsApiUrl;
    @Value("${sms.api.key}")
    private String smsApiKey;
    @Value("${sms.api.secret}")
    private String smsApiSecret;

    public UserServiceImpl(
            UserRepository userRepository,
            RedisTemplate<String, String> redisTemplate,
            WebClient.Builder webClientBuilder,
            Validator validator)
    {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.webClient = webClientBuilder.build();
        this.validator = validator;
    }

    @Override
    public void sendPhoneVerificationCode(String phone) {

        // 인증코드를 6자리 숫자(000000 ~ 999999) 중 랜덤으로 생성
        String code = String.format("%06d", new Random().nextInt(1_000_000));

        // SMS API 호출
        Map<String, Object> payload = Map.of(
                "apiKey", smsApiKey,
                "apiSecret", smsApiSecret,
                "to", phone,
                "text", "인증번호: " + code
        );

        // WebClient를 통해 SMS 공급자 API에 POST 요청을 보내 인증 코드를 전송
        webClient.post()
                .uri(smsApiUrl)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // .block()을 사용해 동기 호출로 처리(결과를 기다림).

        // Redis에 5분 TTL 로 저장
        redisTemplate.opsForValue()
                .set("SMS_CODE:" + phone, code, Duration.ofMinutes(5));
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
        // 전화번호 인증 확인
        String codeInRedis = redisTemplate.opsForValue().get("SMS_CODE:" + dto.getUserPhone());
        if (codeInRedis == null) {
            throw new ApiException(ErrorCode.PHONE_VERIFICATION_REQUIRED);
        }
        // 개인정보 동의 여부 확인
        if (Boolean.FALSE.equals(dto.getUserAgreedPrivacy())) {
            throw new ApiException(ErrorCode.PRIVACY_NOT_AGREED);
        }

        dto.setUserPhoneVerified(true);
        dto.setUserLoginType(102);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        userRepository.insertUser(dto);
    }

    @Override
    public UserInfo loginWithKakao(OAuth2User oauth2User) {
        String email = extractEmail(oauth2User);
        UserInfo user = userRepository.findByEmail(email);
        if (user == null) {
            // 가입 안내
            throw new ApiException(ErrorCode.KAKAO_NEED_SIGNUP);
        }
        return user;
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

    private String extractEmail(OAuth2User oauth2User) {
        Map<String,Object> acc = oauth2User.getAttribute("kakao_account");
        String email = acc != null ? (String)acc.get("email") : null;
        if (email == null) {
            throw new ApiException(ErrorCode.KAKAO_EMAIL_NOT_FOUND);
        }
        return email;
    }
}
