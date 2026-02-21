package com.calibraflow.api.domain.dtos;

import com.calibraflow.api.domain.entities.User;
import java.util.Set;
import java.util.stream.Collectors;

public record UserResponseDTO(Long id, String username, Set<String> roles) {
    public UserResponseDTO(User user) {
        this(
                user.getId(),
                user.getUsername(),
                user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet())
        );
    }
}