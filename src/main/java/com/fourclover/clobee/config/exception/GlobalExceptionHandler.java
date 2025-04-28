package com.fourclover.clobee.config.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice // 전역적으로 예외처리 가능
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<Object> apiException(ApiException e) {
        return makeExceptionResponse(e.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleException(Exception e) {
        log.error("handleException : {}", e.getMessage());
        return makeExceptionResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> makeExceptionResponse(ErrorCode errorCode) {
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatus()));
    }
}
