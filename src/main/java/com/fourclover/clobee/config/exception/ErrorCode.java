package com.fourclover.clobee.config.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // UserService 관련
    EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    PHONE_VERIFICATION_REQUIRED(HttpStatus.BAD_REQUEST, "전화번호 인증이 필요합니다."),
    PHONE_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "인증 코드가 만료되었거나 존재하지 않습니다."),
    PHONE_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "인증 코드가 일치하지 않습니다."),
    KAKAO_EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "카카오 계정에 이메일이 존재하지 않습니다."),
    PRIVACY_NOT_AGREED(HttpStatus.BAD_REQUEST, "개인정보 이용 동의가 필요합니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "입력값이 유효하지 않습니다."),
    KAKAO_NEED_SIGNUP(HttpStatus.BAD_REQUEST, "카카오 회원가입이 필요합니다."),

    // EventService 관련
    // 400 Bad Request: 오늘은 이미 참여한 경우
    ALREADY_PARTICIPATED(HttpStatus.BAD_REQUEST, "오늘은 이미 참여하셨습니다."),
    // 400 Bad Request: 3단계에서 이미 쿠폰 받은 경우
    COUPON_ALREADY_RECEIVED(HttpStatus.BAD_REQUEST, "이미 쿠폰을 지급받으셨습니다."),
    // 400 Bad Request: 시도 횟수 모두 소진된 경우
    ATTEMPTS_EXHAUSTED(HttpStatus.BAD_REQUEST, "횟수를 모두 소진하였습니다."),
    // 400 Bad Request: 이벤트 정보 찾을 수 없을 경우
    EVENT_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "이벤트 정보를 찾을 수 없습니다."),
    // 400 Bad Request: 사용자의 게임 정보 찾을 수 없을 경우
    GAME_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "게임 정보를 찾을 수 없습니다."),
    // 500 상태코드
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다.");

    private HttpStatus httpStatus;
    private String errorMessage;
}
