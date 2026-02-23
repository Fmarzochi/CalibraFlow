package com.calibraflow.api.domain.dtos;

import com.calibraflow.api.domain.entities.enums.InstrumentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InstrumentRequestDTO(
        @NotBlank(message = "A TAG do instrumento é obrigatória")
        @Size(max = 100, message = "A TAG deve ter no máximo 100 caracteres")
        String tag,

        @NotBlank(message = "O nome do instrumento é obrigatório")
        @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
        String name,

        String serialNumber,
        String manufacturer,
        String model,
        String tolerance,

        @NotNull(message = "O ID da categoria é obrigatório")
        Long categoryId,

        @NotNull(message = "O ID da localização é obrigatório")
        Long locationId,

        Long periodicityId
) {
        public record InstrumentStatusChangeDTO(
                @NotNull(message = "O status é obrigatório")
                InstrumentStatus status,
                String justification
        ) {}
}