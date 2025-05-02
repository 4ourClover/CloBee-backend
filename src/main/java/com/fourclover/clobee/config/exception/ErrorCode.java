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
    ALREADY_PARTICIPATED(HttpStatus.BAD_REQUEST, "오늘은 이미 참여하셨습니다."),
    COUPON_ALREADY_RECEIVED(HttpStatus.BAD_REQUEST, "이미 쿠폰을 지급받으셨습니다."),
    ATTEMPTS_EXHAUSTED(HttpStatus.BAD_REQUEST, "횟수를 모두 소진하였습니다."),
    CONFLICT_USER_ATTENDANCE(HttpStatus.CONFLICT, "이미 출석체크를 하였습니다."),
    EVENT_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "이벤트 정보를 찾을 수 없습니다."),
    GAME_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "게임 정보를 찾을 수 없습니다."),

    // BatchService 관련
    SECRET_KEY_NOT_FOUND(HttpStatus.NOT_FOUND, "허가되지 않은 API 요청입니다."),

    // 500 상태코드
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다."),

    // Card
    // "credit" 또는 "check" 외 잘못된 type이 입력된 경우
    INVALID_CARD_TYPE(HttpStatus.BAD_REQUEST, "올바르지 않은 카드 타입입니다."),
    // cardInfoId가 존재하지 않을 경우
    CARD_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카드입니다."),
    // 카드 브랜드 url이 null 일 경우
    CARD_BRAND_URL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카드 브랜드의 URL이 존재하지 않습니다."),
    // 사용자 id가 없을 경우
    INVALID_USER(HttpStatus.BAD_REQUEST, "유효하지 않은 사용자입니다."),
    // 카드 추가 시 이미 등록한 카드일 경우
    CARD_ALREADY_REGISTERED(HttpStatus.CONFLICT, "이미 등록된 카드입니다."),
    // 카드 검색 시 검색어를 입력하지 않은 경우
    EMPTY_SEARCH_KEYWORD(HttpStatus.BAD_REQUEST, "검색어를 입력해주세요.");

    private HttpStatus httpStatus;
    private String errorMessage;
}
