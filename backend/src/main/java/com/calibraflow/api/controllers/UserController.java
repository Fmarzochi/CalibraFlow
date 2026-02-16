package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userRepository.save(user);
    }
}