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
