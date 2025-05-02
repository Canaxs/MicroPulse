package com.micro.user_service.service.impl;

import com.micro.user_service.dto.LogEvent;
import com.micro.user_service.dto.UserDTO;
import com.micro.user_service.persistence.entity.User;
import com.micro.user_service.persistence.repository.UserRepository;
import com.micro.user_service.service.LogProducerService;
import com.micro.user_service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final LogProducerService logProducerService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, LogProducerService logProducerService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.logProducerService = logProducerService;
    }

    @Override
    public User create(UserDTO userDTO) {
        try {
            User user = userRepository.save(User.builder()
                    .username(userDTO.getUsername())
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .build());
            HttpStatus status = HttpStatus.CREATED;
            logProducerService.sendLog(LogEvent.builder()
                            .message("Username: "+userDTO.getUsername())
                            .serviceName("user-service")
                            .serviceURL("/user/create/")
                            .statusCode(status.value() + " " + status.getReasonPhrase())
                            .logDate(LocalDateTime.now())
                    .build());
            return user;
        }
        catch (Exception e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            logProducerService.sendLog(LogEvent.builder()
                    .message("Username: "+userDTO.getUsername())
                    .serviceName("user-service")
                    .serviceURL("/user/create/")
                    .statusCode(status.value() + " " + status.getReasonPhrase())
                    .logDate(LocalDateTime.now())
                    .build());
            throw new RuntimeException();
        }
    }
}
