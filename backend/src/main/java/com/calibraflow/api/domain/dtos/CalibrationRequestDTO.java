package com.calibraflow.api.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CalibrationRequestDTO(
        @NotNull(message = "A data da calibração é obrigatória") LocalDate calibrationDate,
        @NotNull(message = "A data da próxima calibração é obrigatória") LocalDate nextCalibrationDate,
        @NotBlank(message = "O número do certificado é obrigatório") String certificateNumber,
        @NotBlank(message = "O laboratório é obrigatório") String laboratory,
        @NotNull(message = "O ID do instrumento é obrigatório") Long instrumentId
) {}