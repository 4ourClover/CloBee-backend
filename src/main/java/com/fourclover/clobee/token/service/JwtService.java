package com.fourclover.clobee.token.service;

import com.fourclover.clobee.config.exception.ApiException;
import com.fourclover.clobee.config.exception.ErrorCode;
import com.fourclover.clobee.token.domain.TokenType;
import com.fourclover.clobee.token.domain.TokenProperties;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

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
        try {
            if (findType(token) != tokenType) throw new ApiException(ErrorCode.FAKE_TOKEN);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        } catch (JwtException e) {
            throw new RuntimeException("Invalid token" + e);
        }
    }

    public Long extractUserId(String token) {
        Number idNum = (Number) createClaims(token).get("id");
        return idNum.longValue();
    }

    private TokenType findType(String token) {
        return TokenType.toTokenType(createClaims(token).get("category").toString());
    }

    private Claims createClaims(String token) {
        return Jwts.parser().verifyWith(tokenProperties.secretKey()).build().parseSignedClaims(token).getPayload();
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

