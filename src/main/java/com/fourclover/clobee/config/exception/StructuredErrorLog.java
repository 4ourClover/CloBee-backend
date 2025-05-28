package com.fourclover.clobee.config.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class StructuredErrorLog {
    private final String code;
    private final String name;      // 예: "US-01-"
    private final String message;   // 예: "올바르지 않은 전화번호 형식입니다"
    private final int httpStatus;   // 예: 400
    private final String uri;
    private final String userId;
    private final String stackTrace;
}
