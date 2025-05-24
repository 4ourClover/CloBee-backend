package com.fourclover.clobee.token.service;

import com.fourclover.clobee.config.exception.ApiException;
import com.fourclover.clobee.config.exception.ErrorCode;
import com.fourclover.clobee.token.domain.TokenType;
import com.fourclover.clobee.token.domain.TokenProperties;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final TokenProperties tokenProperties;
    private final Long expiration;
    private final Long refreshExpiration;

    public JwtService(TokenProperties tokenProperties) {
        this.tokenProperties = tokenProperties;
        this.expiration = tokenProperties.getAccess();
        this.refreshExpiration = tokenProperties.getRefresh();
    }

    public String generateAccessToken(Long userId) {
        return generateToken(TokenType.ACCESS_TOKEN, userId, expiration);
    }

    public String generateRefreshToken(Long userId) {
        return generateToken(TokenType.REFRESH_TOKEN, userId, refreshExpiration);
    }

    private String generateToken(TokenType tokenType, Long userId, long expiry) {
        Date now = new Date();
        return Jwts.builder()
                .claim("id", userId)
                .claim("category", tokenType.getValue())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiry))
                .signWith(tokenProperties.secretKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateToken(String token, TokenType tokenType) {
        // 토큰이 null이거나 빈 문자열인 경우 바로 false 반환
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            if (findType(token) != tokenType) throw new ApiException(ErrorCode.FAKE_TOKEN);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        } catch (JwtException | ApiException e) {
            log.debug("Invalid token: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.debug("Unexpected error during token validation: {}", e.getMessage());
            return false;
        }
    }

    public Long extractUserId(String token) {
        // 토큰이 null이거나 빈 문자열인 경우 예외 발생
        if (token == null || token.trim().isEmpty()) {
            log.debug("Attempt to extract userId from null or empty token");
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        try {
            Claims claims = createClaims(token);
            Number idNum = (Number) claims.get("id");
            return idNum.longValue();
        } catch (JwtException e) {
            log.debug("Failed to extract userId: {}", e.getMessage());
            throw new ApiException(ErrorCode.UNKNOWN_TOKEN_TYPE);
        }
    }

    private TokenType findType(String token) {
        // 토큰이 null이거나 빈 문자열인 경우 예외 발생
        if (token == null || token.trim().isEmpty()) {
            log.debug("Attempt to find type from null or empty token");
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        try {
            Object category = createClaims(token).get("category");
            if (category == null) {
                log.debug("Token category is null");
                throw new ApiException(ErrorCode.UNKNOWN_TOKEN_TYPE);
            }
            return TokenType.toTokenType(category.toString());
        } catch (JwtException e) {
            log.debug("Failed to find token type: {}", e.getMessage());
            throw new ApiException(ErrorCode.UNKNOWN_TOKEN_TYPE);
        }
    }

    private Claims createClaims(String token) {
        // 토큰이 null이거나 빈 문자열인 경우 예외 발생
        if (token == null || token.trim().isEmpty()) {
            log.debug("Attempt to create claims from null or empty token");
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        try {
            return Jwts.parser()
                    .verifyWith(tokenProperties.secretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            log.debug("JWT parsing error: {}", e.getMessage());
            throw new ApiException(ErrorCode.UNKNOWN_TOKEN_TYPE);
        }
    }

    private String getTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}