package com.calibraflow.api.application.dtos;

import com.calibraflow.api.domain.entities.enums.InstrumentStatus;

public record InstrumentResponseDTO(
        Long id,
        String tag,
        String name,
        String serialNumber,
        String manufacturer,
        String model,
        String location,
        String tolerance,
        InstrumentStatus status
) {}