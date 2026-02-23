package com.calibraflow.api.domain.dtos;

import java.time.LocalDate;

public record UpcomingCalibrationDTO(
        Long instrumentId,
        String instrumentTag,
        String instrumentName,
        LocalDate nextCalibrationDate
) {}