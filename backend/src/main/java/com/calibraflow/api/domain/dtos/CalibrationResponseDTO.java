package com.calibraflow.api.domain.dtos;

import java.time.LocalDate;

public record CalibrationResponseDTO(
        Long id,
        String instrumentTag,
        String instrumentName,
        LocalDate calibrationDate,
        LocalDate nextCalibrationDate,
        String certificateNumber,
        boolean approved,
        String observations
) {}