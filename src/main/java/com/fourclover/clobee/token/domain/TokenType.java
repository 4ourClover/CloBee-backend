package com.fourclover.clobee.token.domain;

import com.fourclover.clobee.config.exception.ApiException;
import com.fourclover.clobee.config.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum TokenType {
    ACCESS_TOKEN("access"),
    REFRESH_TOKEN("refresh");

    private final String value;

    TokenType(String value) {
        this.value = value;
    }

    public static TokenType toTokenType(String string) {
        for (TokenType type : TokenType.values()) {
            if (type.getValue().equals(string)) {
                return type;
            }
        }
        throw new ApiException(ErrorCode.UNKNOWN_TOKEN_TYPE);
    }
}

