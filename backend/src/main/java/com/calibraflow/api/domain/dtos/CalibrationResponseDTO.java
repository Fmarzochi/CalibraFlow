package com.calibraflow.api.domain.dtos;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record CalibrationResponseDTO(
        Long id,
        Long instrumentId,
        String instrumentTag,
        LocalDate calibrationDate,
        LocalDate nextCalibrationDate,
        String certificateNumber,
        boolean approved,
        String observations,
        OffsetDateTime createdAt,
        String createdByName
) {}
