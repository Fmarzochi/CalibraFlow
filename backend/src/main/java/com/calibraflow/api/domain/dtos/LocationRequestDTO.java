package com.calibraflow.api.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LocationRequestDTO(
        @NotBlank(message = "O nome da localização é obrigatório") String name,
        String description,
        @NotNull(message = "O status da localização é obrigatório") Boolean active
) {}