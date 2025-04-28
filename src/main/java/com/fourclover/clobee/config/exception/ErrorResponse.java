package com.fourclover.clobee.config.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private String errorMessage;
    private int status;

    private ErrorResponse(final ErrorCode errorCode) {
        this.errorMessage = errorCode.getErrorMessage();
        this.status = errorCode.getHttpStatus().value();
    }

    public static ErrorResponse of(final ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }
}
