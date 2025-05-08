package com.fourclover.clobee.token.service;

import com.fourclover.clobee.user.domain.UserInfo;
import com.fourclover.clobee.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = userRepository.findByEmail(username);

        if (userInfo == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        return new User(
                userInfo.getUserEmail(),
                userInfo.getUserPassword(),
                new ArrayList<>()
        );
    }

    // 사용자 ID로 사용자 조회하는 메서드
    public UserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException {
        // DB에서 사용자 ID로 사용자 조회
        Optional<UserInfo> optionalUserInfo = Optional.ofNullable(userRepository.findById(userId));

        // Optional을 사용하여 값이 없을 경우 예외를 던짐
        UserInfo userInfo = optionalUserInfo
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        if (userInfo.getUserEmail() == null || userInfo.getUserEmail().isBlank()) {
            throw new IllegalArgumentException("User email is null or blank for userId: " + userId);
        }

        return new User(
                userInfo.getUserEmail(),
                "password",
                new ArrayList<>()
        );
    }
}
