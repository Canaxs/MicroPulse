package com.micro.user_service.service;

import com.micro.user_service.dto.UserDTO;
import com.micro.user_service.persistence.entity.User;

public interface UserService {
    User create(UserDTO userDTO);
}
