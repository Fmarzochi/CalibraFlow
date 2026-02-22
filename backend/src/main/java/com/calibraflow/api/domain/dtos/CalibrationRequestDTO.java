package com.calibraflow.api.application.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CalibrationRequestDTO(

        @NotNull(message = "O ID do instrumento e obrigatorio")
        Long instrumentId,

        @NotNull(message = "A data da calibracao e obrigatoria")
        LocalDate calibrationDate,

        LocalDate nextCalibrationDate,

        @NotBlank(message = "O numero do certificado e obrigatorio")
        String certificateNumber,

        @NotNull(message = "O status de aprovacao e obrigatorio")
        Boolean approved,

        String observations
) {}