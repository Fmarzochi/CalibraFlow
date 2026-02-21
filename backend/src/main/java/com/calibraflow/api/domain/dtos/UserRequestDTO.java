package com.calibraflow.api.domain.dtos;

import com.calibraflow.api.domain.entities.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UserRequestDTO(
        @NotBlank(message = "O nome de usuário é obrigatório") String username,
        @NotBlank(message = "A senha é obrigatória") String password,
        @NotEmpty(message = "Pelo menos uma role é obrigatória") Set<Role> roles
) {
}