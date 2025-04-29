package com.micro.user_service.controller;

import com.micro.user_service.dto.UserDTO;
import com.micro.user_service.persistence.entity.User;
import com.micro.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test")
    private ResponseEntity<Boolean> test() {
        return ResponseEntity.ok(true);
    }

    @PostMapping("/create")
    private ResponseEntity<User> create(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.create(userDTO));
    }
}
