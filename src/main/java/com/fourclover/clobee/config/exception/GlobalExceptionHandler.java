package com.fourclover.clobee.config.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice // 전역적으로 예외처리 가능
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<Object> apiException(ApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("ApiException : {} - {}", errorCode.name(), errorCode.getErrorMessage());
        return makeExceptionResponse(errorCode);
    }

    // JWT 관련 예외 처리 추가
    @ExceptionHandler({
            ExpiredJwtException.class,
            UnsupportedJwtException.class,
            MalformedJwtException.class,
            SignatureException.class
    })
    protected ResponseEntity<Object> handleJwtException(Exception e) {
        log.error("JWT Exception : {}", e.getMessage());
        return makeExceptionResponse(ErrorCode.FAKE_TOKEN);
    }

    // IllegalArgumentException 처리 추가 - JWT 파싱 실패 시 주로 발생
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException : {}", e.getMessage());

        // JWT 관련 오류인 경우
        if (e.getMessage() != null && (
                e.getMessage().contains("JWT") ||
                        e.getMessage().contains("token") ||
                        e.getMessage().contains("CharSequence cannot be null or empty")
        )) {
            return makeExceptionResponse(ErrorCode.UNKNOWN_TOKEN_TYPE);
        }

        return makeExceptionResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    // 인증 관련 예외 처리
    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<Object> handleAuthenticationException(AuthenticationException e) {
        log.error("AuthenticationException : {}", e.getMessage());

        if (e instanceof BadCredentialsException) {
            return makeExceptionResponse(ErrorCode.PASSWORD_NOT_MATCH);
        }

        return makeExceptionResponse(ErrorCode.UNAUTHORIZED);
    }

    // NullPointerException 처리
    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<Object> handleNullPointerException(NullPointerException e) {
        log.error("NullPointerException : {}", e.getMessage());

        // Authentication이 null인 경우가 많음
        boolean isAuthenticationRelated = false;
        for (StackTraceElement element : e.getStackTrace()) {
            if (element.getClassName().contains("Authentication") ||
                    element.getClassName().contains("Security") ||
                    element.getClassName().contains("Jwt") ||
                    element.getClassName().contains("User")) {
                isAuthenticationRelated = true;
                log.error("Authentication related NPE at: {}", element);
                break;
            }
        }

        return makeExceptionResponse(
                isAuthenticationRelated ? ErrorCode.UNAUTHORIZED : ErrorCode.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleException(Exception e) {
        log.error("handleException : {}", e.getMessage());
        // 전체 스택 트레이스 로깅
        log.error("Exception stack trace:", e);
        return makeExceptionResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> makeExceptionResponse(ErrorCode errorCode) {
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatus()));
    }
}