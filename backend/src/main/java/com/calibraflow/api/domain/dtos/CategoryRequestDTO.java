package com.calibraflow.api.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequestDTO(
        @NotBlank(message = "O nome da categoria é obrigatório") String name,
        @NotNull(message = "A validade em dias é obrigatória") Integer validityDays,
        String description
) {}