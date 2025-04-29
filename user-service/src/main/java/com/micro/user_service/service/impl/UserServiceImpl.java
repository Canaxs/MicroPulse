package com.micro.user_service.service.impl;

import com.micro.user_service.dto.UserDTO;
import com.micro.user_service.persistence.entity.User;
import com.micro.user_service.persistence.repository.UserRepository;
import com.micro.user_service.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User create(UserDTO userDTO) {
        return userRepository.save(User.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .build());
    }
}
