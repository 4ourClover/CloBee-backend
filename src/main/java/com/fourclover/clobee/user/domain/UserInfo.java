package com.fourclover.clobee.user.domain;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private Long userId;

    @NotNull
    private Integer userLoginType;   // 101=카카오, 102=이메일

    // OAuth2 토큰 저장용 (필요 없으면 null 허용)
    private String userAccessToken;
    private String userRefreshToken;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String userEmail;

    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하입니다.")
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String userPassword;

    @Size(max = 14, message = "닉네임은 15자 미만으로 입력해주세요.")
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String userNickname;

    //@NotNull(message = "생년월일을 입력해주세요.")
    private LocalDate userBirthday;

    @Pattern(regexp = "^\\d{10,11}$",
            message = "핸드폰 번호가 올바르지 않습니다.")
    @NotBlank(message = "핸드폰 번호는 필수 입력값입니다.")
    private String userPhone;

    // Redis 검증 뒤 Controller 에서 true 세팅
    private Boolean userPhoneVerified = false;

    // 개인정보 이용 동의 체크
    @NotNull(message = "개인정보 이용 동의가 필요합니다.")
    private Boolean userAgreedPrivacy = false;

    // 탈퇴 여부
    private Boolean isDeleted = false;

    // 가입 시 자동 생성
    private String userInvitationCode;

    // 친구의 초대 코드 입력
    private String friendInvitationCode;

    // timestamp
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
