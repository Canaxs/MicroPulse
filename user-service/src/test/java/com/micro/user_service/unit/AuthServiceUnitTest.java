package com.micro.user_service.unit;

import com.micro.user_service.dto.UserDTO;
import com.micro.user_service.persistence.entity.User;
import com.micro.user_service.persistence.repository.UserRepository;
import com.micro.user_service.service.impl.AuthServiceImpl;
import com.micro.user_service.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("rawPassword");
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("rawPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPassword", "rawPassword")).thenReturn(true);
        when(jwtUtil.generateToken("testuser", "user", "1")).thenReturn("mocked-jwt-token");

        String token = authService.login(userDTO);

        assertEquals("mocked-jwt-token", token);
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("rawPassword", "rawPassword");
        verify(jwtUtil).generateToken("testuser", "user", "1");
    }

    @Test
    void login_shouldThrowException_whenUserNotFound() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("notfound");
        userDTO.setPassword("any");

        when(userRepository.findByUsername("notfound")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(userDTO));
    }

    @Test
    void login_shouldThrowException_whenPasswordDoesNotMatch() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("wrongPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(userDTO));
    }


}
