package com.micro.user_service.service;

import com.micro.user_service.dto.UserDTO;

public interface AuthService {
    String login(UserDTO userDTO);
}
