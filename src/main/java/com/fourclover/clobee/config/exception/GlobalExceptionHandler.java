package com.fourclover.clobee.config.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<Object> apiException(ApiException e, HttpServletRequest request) {
        return makeExceptionResponse(e.getErrorCode(), request, e);
    }

    @ExceptionHandler({
            ExpiredJwtException.class,
            UnsupportedJwtException.class,
            MalformedJwtException.class,
            SignatureException.class
    })
    protected ResponseEntity<Object> handleJwtException(Exception e, HttpServletRequest request) {
        return makeExceptionResponse(ErrorCode.FAKE_TOKEN, request, e);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        if (e.getMessage() != null &&
                (e.getMessage().contains("JWT") || e.getMessage().contains("token") || e.getMessage().contains("CharSequence"))) {
            return makeExceptionResponse(ErrorCode.UNKNOWN_TOKEN_TYPE, request, e);
        }
        return makeExceptionResponse(ErrorCode.INTERNAL_SERVER_ERROR, request, e);
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<Object> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        if (e instanceof BadCredentialsException) {
            return makeExceptionResponse(ErrorCode.PASSWORD_NOT_MATCH, request, e);
        }
        return makeExceptionResponse(ErrorCode.UNAUTHORIZED, request, e);
    }

    protected ResponseEntity<Object> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        boolean isAuthenticationRelated = Arrays.stream(e.getStackTrace())
                .anyMatch(el -> el.getClassName().contains("Authentication")
                        || el.getClassName().contains("Security")
                        || el.getClassName().contains("Jwt")
                        || el.getClassName().contains("User"));

        ErrorCode errorCode = isAuthenticationRelated ? ErrorCode.UNAUTHORIZED : ErrorCode.INTERNAL_SERVER_ERROR;
        return makeExceptionResponse(errorCode, request, e);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String userId = getUserId();

        String stackTrace = Arrays.stream(e.getStackTrace())
                .limit(5)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));

        StructuredErrorLog structuredLog = new StructuredErrorLog(
                "CO-01",
                "요청 파라미터 타입이 올바르지 않습니다: " + e.getName(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                userId,
                stackTrace
        );

        log.error("파라미터 타입 예외 발생: {}", structuredLog);

        return new ResponseEntity<>(
                new ErrorResponse(400, "CO-01", "요청 파라미터 타입이 올바르지 않습니다."),
                HttpStatus.BAD_REQUEST
        );
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            org.springframework.http.HttpStatusCode status,
            WebRequest request
    ) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request.resolveReference("request");
        String userId = getUserId();

        String stackTrace = Arrays.stream(ex.getStackTrace())
                .limit(5)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));

        StructuredErrorLog structuredLog = new StructuredErrorLog(
                "CO-00-04",
                "필수 파라미터 누락: " + ex.getParameterName(),
                status.value(),
                httpServletRequest != null ? httpServletRequest.getRequestURI() : "unknown",
                userId,
                stackTrace
        );

        log.error("필수 파라미터 누락 예외 발생: {}", structuredLog);

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                "CO-00-04",
                "필수 파라미터 '" + ex.getParameterName() + "'가 누락되었습니다."
        );

        return new ResponseEntity<>(errorResponse, status);
    }


    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleException(Exception e, HttpServletRequest request) {
        return makeExceptionResponse(ErrorCode.INTERNAL_SERVER_ERROR, request, e);
    }

    private ResponseEntity<Object> makeExceptionResponse(ErrorCode errorCode, HttpServletRequest request, Exception e) {
        String userId = getUserId();

        String stackTrace = Arrays.stream(e.getStackTrace())
                .limit(5)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));

        StructuredErrorLog structuredLog = new StructuredErrorLog(
                errorCode.name(),
                errorCode.getErrorMessage(),
                errorCode.getHttpStatus().value(),
                request.getRequestURI(),
                userId,
                stackTrace
        );

        log.error("예외 발생: {}", structuredLog);

        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatus()));
    }

    private String getUserId() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception ignored) {
            return "anonymous";
        }
    }
}
