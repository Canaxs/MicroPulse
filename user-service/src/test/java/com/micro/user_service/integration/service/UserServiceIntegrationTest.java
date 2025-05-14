package com.micro.user_service.integration.service;

import com.micro.user_service.dto.UserDTO;
import com.micro.user_service.persistence.entity.User;
import com.micro.user_service.persistence.repository.UserRepository;
import com.micro.user_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void create_shouldSaveUserToDatabase_whenValidUserDTOGiven() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("mero");
        userDTO.setPassword("12345");

        User savedUser = userService.create(userDTO);

        assertNotNull(savedUser.getId(), "The registered user ID must not be null");
        assertEquals("mero", savedUser.getUsername());
        assertTrue(passwordEncoder.matches("12345", savedUser.getPassword()), "Check if the password is encoded correctly with the encoder");

        User userFromDb = userRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(userFromDb, "There must be a user in the database");
    }
}
