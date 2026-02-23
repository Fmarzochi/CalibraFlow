package com.calibraflow.api.domain.dtos;

import com.calibraflow.api.domain.entities.enums.InstrumentStatus;
import java.time.LocalDateTime;

public record InstrumentResponseDTO(
        Long id,
        String tag,
        String name,
        String serialNumber,
        String manufacturer,
        String model,
        String tolerance,
        String categoryName,
        String locationName,
        Integer periodicityDays,
        InstrumentStatus status,
        boolean active,
        LocalDateTime createdAt
) {}