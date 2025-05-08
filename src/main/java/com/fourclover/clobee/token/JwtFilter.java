package com.fourclover.clobee.token;

import com.fourclover.clobee.token.service.UserDetailsServiceImpl;
import com.fourclover.clobee.token.domain.TokenType;
import com.fourclover.clobee.token.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/user/signup/email")
                || path.equals("/user/sendPhoneCode")
                || path.equals("/user/verifyPhoneCode")
                || path.equals("/user/login")
                || path.equals("/user/refresh")
                || path.startsWith("/oauth2/")
                || path.startsWith("/login")
                || path.startsWith("/error")
                || path.equals("/user/signup/kakao")
                || path.equals("/user/login/kakao")
                || path.startsWith("/swagger-ui/")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources/")
                || path.startsWith("/webjars/")
                || path.startsWith("/configuration/");
    }

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain)
            throws ServletException, IOException {

        Optional<String> accessTokenOpt = getCookieValue(request, "accessToken");

        if (accessTokenOpt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = accessTokenOpt.get();

        if (accessToken.isBlank() || accessToken.equals("undefined") ) {
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtService.validateToken(accessToken, TokenType.ACCESS_TOKEN)) {
            Long userId = jwtService.extractUserId(accessToken);
            UserDetails userDetails = userDetailsService.loadUserByUserId(userId);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(cookieName)) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }
}

