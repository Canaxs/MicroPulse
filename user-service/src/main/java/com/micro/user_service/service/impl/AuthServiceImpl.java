package com.micro.user_service.service.impl;

import com.micro.user_service.dto.LogEvent;
import com.micro.user_service.dto.UserDTO;
import com.micro.user_service.persistence.entity.User;
import com.micro.user_service.persistence.repository.UserRepository;
import com.micro.user_service.service.AuthService;
import com.micro.user_service.service.LogProducerService;
import com.micro.user_service.service.UserService;
import com.micro.user_service.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LogProducerService logProducerService;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, LogProducerService logProducerService, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.logProducerService = logProducerService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String login(UserDTO userDTO) {
        Optional<User> userOpt = userRepository.findByUsername(userDTO.getUsername());
        if (userOpt.isPresent()) {
            if(passwordEncoder.matches(userDTO.getPassword(), userOpt.get().getPassword())) {
                HttpStatus status = HttpStatus.OK;
                logProducerService.sendLog(LogEvent.builder()
                        .message("User: "+userDTO.getUsername())
                        .serviceName("user-service")
                        .serviceURL("/auth/login/")
                        .statusCode(status.value() + " " + status.getReasonPhrase())
                        .logDate(LocalDateTime.now())
                        .build());
                return jwtUtil.generateToken(userOpt.get().getUsername(),"user");
            }
        }
        HttpStatus status = HttpStatus.BAD_REQUEST;
        logProducerService.sendLog(LogEvent.builder()
                .message("User: "+userDTO.getUsername())
                .serviceName("user-service")
                .serviceURL("/auth/login/")
                .statusCode(status.value() + " " + status.getReasonPhrase())
                .logDate(LocalDateTime.now())
                .build());
        throw new RuntimeException();
    }
}
