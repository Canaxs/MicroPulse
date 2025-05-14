package com.micro.user_service.integration.service;

import com.micro.user_service.dto.UserDTO;
import com.micro.user_service.persistence.entity.User;
import com.micro.user_service.persistence.repository.UserRepository;
import com.micro.user_service.service.AuthService;
import com.micro.user_service.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .username("meric")
                .password(passwordEncoder.encode("12345"))
                .build();

        userRepository.save(user);
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("meric");
        userDTO.setPassword("12345");

        String token = authService.login(userDTO);

        assertNotNull(token);
        assertTrue(token.startsWith("ey"));
        System.out.println("JWT Token: " + token);
    }

    @Test
    void login_shouldThrowException_whenPasswordIsWrong() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("meric");
        userDTO.setPassword("wrong-password");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(userDTO);
        });

        assertEquals("Invalid credentials", exception.getMessage());
    }
}
