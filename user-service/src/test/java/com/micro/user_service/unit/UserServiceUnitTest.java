package com.micro.user_service.unit;

import com.micro.user_service.dto.UserDTO;
import com.micro.user_service.persistence.entity.User;
import com.micro.user_service.persistence.repository.UserRepository;
import com.micro.user_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void create_shouldSaveUserWithEncodedPassword() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("plainPassword");

        String encodedPassword = "encodedPassword123";

        when(passwordEncoder.encode("plainPassword")).thenReturn(encodedPassword);

        User savedUser = User.builder()
                .Id(1L)
                .username("testuser")
                .password(encodedPassword)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.create(userDTO);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(encodedPassword, result.getPassword());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();

        assertEquals("testuser", capturedUser.getUsername());
        assertEquals(encodedPassword, capturedUser.getPassword());

        verify(passwordEncoder).encode("plainPassword");
    }

}
