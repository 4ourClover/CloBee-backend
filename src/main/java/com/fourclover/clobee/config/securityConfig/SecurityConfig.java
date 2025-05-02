package com.fourclover.clobee.config.securityConfig;

import com.fourclover.clobee.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                HttpMethod.POST,"/user/signup/email",
                                "/user/sendPhoneCode",
                                "/user/verifyPhoneCode",
                                "/login**",
                                "/oauth2/**",
                                "/user/signup/kakao",      // 카카오 회원가입 엔드포인트
                                "/user/login/kakao"        // 카카오 로그인 엔드포인트
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        // OAuth2 인증 성공 후 redirect 할 URL을 컨트롤러 매핑에 맞춰 변경
                        .defaultSuccessUrl("/user/login/kakao", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oauth2UserService())
                        )
                );
        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return request -> {
            OAuth2User oauth2User = delegate.loadUser(request);
            // 로그인 시 등록된 유저만 로그인 처리, 없으면 ApiException 던져서 프론트가 /user/signup/kakao로 유도
            userService.loginWithKakao(oauth2User);
            return oauth2User;
        };
    }
}
