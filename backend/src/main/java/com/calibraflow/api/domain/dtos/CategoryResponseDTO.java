package com.calibraflow.api.application.dtos;

import com.calibraflow.api.domain.entities.enums.UserPermission;
import com.calibraflow.api.domain.entities.enums.UserRole;

import java.util.Set;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        String cpf,
        UserRole role,
        boolean enabled,
        Set<UserPermission> permissions
) {}