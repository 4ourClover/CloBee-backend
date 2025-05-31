package com.fourclover.clobee.config.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // UserService 관련
    // 회원 가입
    INVALID_PHONE_FORMAT("US-01", HttpStatus.BAD_REQUEST, "올바르지 않은 전화번호 형식입니다"),
    PHONE_DUPLICATION("US-01", HttpStatus.BAD_REQUEST, "이미 사용 중인 전화번호입니다."),
    EMAIL_DUPLICATION("US-01", HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    PRIVACY_NOT_AGREED("US-01", HttpStatus.BAD_REQUEST, "개인정보 이용 동의가 필요합니다."),
    VALIDATION_FAILED("US-01", HttpStatus.BAD_REQUEST, "입력값이 유효하지 않습니다."),
    // 카카오
    KAKAO_EMAIL_NOT_FOUND("US-02", HttpStatus.BAD_REQUEST, "카카오 계정에 이메일이 존재하지 않습니다."),
    KAKAO_NEED_SIGNUP("US-02", HttpStatus.BAD_REQUEST, "카카오 회원가입이 필요합니다."),
    KAKAO_NICKNAME_NOT_FOUND("US-02", HttpStatus.BAD_REQUEST, "카카오 유저 닉네임을 찾을수 없습니다."),
    // 휴대폰 인증
    USER_NOT_FOUND("US-03", HttpStatus.BAD_REQUEST, "유저를 찾을 수 없습니다."),
    PHONE_VERIFICATION_REQUIRED("US-03", HttpStatus.BAD_REQUEST, "전화번호 인증이 필요합니다."),
    SMS_SEND_FAILURE("US-03", HttpStatus.BAD_REQUEST,"SMS 전송 실패"),
    PHONE_CODE_EXPIRED("US-03", HttpStatus.BAD_REQUEST, "인증 코드가 만료되었거나 존재하지 않습니다."),
    PHONE_CODE_MISMATCH("US-03", HttpStatus.BAD_REQUEST, "인증 코드가 일치하지 않습니다."),

    // EventService 관련
    // 클로버 찾기
    ALREADY_PARTICIPATED("EV-01", HttpStatus.BAD_REQUEST, "오늘은 이미 참여하셨습니다."),
    COUPON_ALREADY_RECEIVED("EV-01", HttpStatus.BAD_REQUEST, "이미 쿠폰을 지급받으셨습니다."),
    ATTEMPTS_EXHAUSTED("EV-01", HttpStatus.BAD_REQUEST, "횟수를 모두 소진하였습니다."),
    EVENT_INFO_NOT_FOUND("EV-01", HttpStatus.NOT_FOUND, "이벤트 정보를 찾을 수 없습니다."),
    GAME_INFO_NOT_FOUND("EV-01", HttpStatus.NOT_FOUND, "게임 정보를 찾을 수 없습니다."),
    // 출석체크
    CONFLICT_USER_ATTENDANCE("EV-02", HttpStatus.CONFLICT, "이미 출석체크를 하였습니다."),

    // BatchService 관련
    SECRET_KEY_NOT_FOUND("BA-01", HttpStatus.NOT_FOUND, "허가되지 않은 API 요청입니다."),

    // 500 상태코드
    INTERNAL_SERVER_ERROR("CO-01",HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다."),

    // Card
    // "credit" 또는 "check" 외 잘못된 type이 입력된 경우
    INVALID_CARD_TYPE("CA-03", HttpStatus.BAD_REQUEST, "올바르지 않은 카드 타입입니다."),
    // cardInfoId가 존재하지 않을 경우
    CARD_NOT_FOUND("CA-01", HttpStatus.NOT_FOUND, "존재하지 않는 카드입니다."),
    // DB에 해당 카드 정보가 없을 때
    CARD_NOT_FOUND_MAP("CA-04", HttpStatus.NOT_FOUND, "존재하지 않는 카드입니다."),
    // 카드 브랜드 url이 null 일 경우
    CARD_BRAND_URL_NOT_FOUND("CA-04", HttpStatus.NOT_FOUND, "해당 카드 브랜드의 URL이 존재하지 않습니다."),
    // 사용자 id가 없을 경우
    INVALID_USER("CA-04", HttpStatus.BAD_REQUEST, "유효하지 않은 사용자입니다."),
    // 카드 추가 시 이미 등록한 카드일 경우
    CARD_ALREADY_REGISTERED("CA-02", HttpStatus.CONFLICT, "이미 등록된 카드입니다."),
    // 카드 검색 시 검색어를 입력하지 않은 경우
    EMPTY_SEARCH_KEYWORD("CA-01", HttpStatus.BAD_REQUEST, "검색어를 입력해주세요."),
    // 해당 store가 존재하지 않는 경우
    EMPTY_SEARCH_STORE("CA-04", HttpStatus.BAD_REQUEST, "검색어를 입력해주세요."),
    // 카드 실적 조회 결과가 null 일 경우
    PERFORMANCE_NOT_FOUND("CA-02", HttpStatus.NOT_FOUND, "해당 카드 실적 정보를 찾을 수 없습니다."),
    // 받아온 혜택 정보가 비었을 경우
    RECOMMENDATION_NOT_AVAILABLE("CA-04", HttpStatus.NOT_FOUND, "추천 가능한 카드가 없습니다."),
    // 카드 신청하기 중복 요청 되었을 경우
    DUPLICATE_REQUEST("CA-04", HttpStatus.TOO_MANY_REQUESTS, "중복 요청이 감지되었습니다."),

    // 인증/인가 관련
    // 토큰
    UNKNOWN_TOKEN_TYPE("AU-01", HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),
    FAKE_TOKEN("AU-01", HttpStatus.CONFLICT, "변조된 토큰입니다."),
    // 로그인 인증
    PASSWORD_NOT_MATCH("AU-02", HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED("AU-02", HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    AUTHENTICATION_FAILED("AU-02", HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),
    ;

    private String code;
    private HttpStatus httpStatus;
    private String errorMessage;
}
