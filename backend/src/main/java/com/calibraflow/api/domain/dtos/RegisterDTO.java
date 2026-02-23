package com.calibraflow.api.domain.dtos;

import com.calibraflow.api.domain.entities.enums.UserRole;

public record RegisterDTO(
        String name,
        String email,
        String password,
        String cpf,
        UserRole role
) {}