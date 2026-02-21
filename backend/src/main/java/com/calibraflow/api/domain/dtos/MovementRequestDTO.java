package com.calibraflow.api.domain.dtos;

import jakarta.validation.constraints.NotNull;

public record MovementRequestDTO(
        String reason,
        @NotNull(message = "O ID do instrumento é obrigatório") Long instrumentId,
        Long originId,
        Long destinationId
) {}