package com.calibraflow.api.domain.dtos;

import com.calibraflow.api.domain.entities.enums.UserPermission;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UserUpdatePermissionsDTO(
        @NotNull(message = "A lista de permissoes e obrigatoria")
        Set<UserPermission> permissions
) {}