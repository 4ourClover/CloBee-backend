package com.fourclover.clobee.user.service;

import com.fourclover.clobee.config.SmsConfig;
import com.fourclover.clobee.config.exception.ApiException;
import com.fourclover.clobee.config.exception.ErrorCode;
import com.fourclover.clobee.user.domain.UserInfo;
import com.fourclover.clobee.user.repository.UserRepository;
import jakarta.validation.Validator;
import net.nurigo.sdk.message.exception.NurigoEmptyResponseException;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.exception.NurigoUnknownException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final Validator validator;
    private final DefaultMessageService messageService;
    private final SmsConfig smsConfig;



    public UserServiceImpl(
            UserRepository userRepository,
            RedisTemplate<String, String> redisTemplate,
            Validator validator,
            DefaultMessageService messageService,
            SmsConfig smsConfig
    ) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.validator = validator;
        this.messageService = messageService;
        this.smsConfig = smsConfig;
    }

    @Override
    public void sendPhoneVerificationCode(String phone) {
        // 1) 6자리 랜덤 코드 생성
        String code = String.format("%06d", new Random().nextInt(900_000) + 100_000);

        // 2) Redis에 5분 만료로 저장
        redisTemplate.opsForValue()
                .set("SMS_CODE:" + phone, code, Duration.ofMinutes(5));

        // 3) COOL SMS 메시지 생성 및 전송
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
        // 전화번호 인증 확인
//        String codeInRedis = redisTemplate.opsForValue().get("SMS_CODE:" + dto.getUserPhone());
//        if (codeInRedis == null) {
//            throw new ApiException(ErrorCode.PHONE_VERIFICATION_REQUIRED);
//        }

        // 전화번호 인증 스킵
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
