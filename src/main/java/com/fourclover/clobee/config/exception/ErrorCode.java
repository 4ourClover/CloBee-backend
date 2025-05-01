package com.fourclover.clobee.config.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 400 Bad Request: 오늘은 이미 참여한 경우
    ALREADY_PARTICIPATED(HttpStatus.BAD_REQUEST, "오늘은 이미 참여하셨습니다."),
    // 400 Bad Request: 3단계에서 이미 쿠폰 받은 경우
    COUPON_ALREADY_RECEIVED(HttpStatus.BAD_REQUEST, "이미 쿠폰을 지급받으셨습니다."),
    // 400 Bad Request: 시도 횟수 모두 소진된 경우
    ATTEMPTS_EXHAUSTED(HttpStatus.BAD_REQUEST, "횟수를 모두 소진하였습니다."),
    // 409 : 출석체크 중복
    CONFLICT_USER_ATTENDANCE(HttpStatus.CONFLICT, "이미 출석체크를 하였습니다."),

    // 500 상태코드
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다.");

    private HttpStatus httpStatus;
    private String errorMessage;
}
