package com.micro.user_service.service.impl;

import com.micro.user_service.dto.UserDTO;
import com.micro.user_service.persistence.entity.User;
import com.micro.user_service.persistence.repository.UserRepository;
import com.micro.user_service.service.AuthService;
import com.micro.user_service.service.UserService;
import com.micro.user_service.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String login(UserDTO userDTO) {
        Optional<User> userOpt = userRepository.findByUsername(userDTO.getUsername());
        if (userOpt.isPresent()) {
            if(passwordEncoder.matches(userDTO.getPassword(), userOpt.get().getPassword())) {
                return jwtUtil.generateToken(userOpt.get().getUsername(),"user");
            }
        }
        throw new RuntimeException();
    }
}
