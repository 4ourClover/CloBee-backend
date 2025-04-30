package com.fourclover.clobee.user.service;

import com.fourclover.clobee.user.repository.UserRepository;
import com.fourclover.clobee.user.domain.UserInfo;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    @Override
//    public UserInfo getUserByEmail(String email) {
//        return userRepository.findByEmail(email).orElseThrow();
//    }
}
