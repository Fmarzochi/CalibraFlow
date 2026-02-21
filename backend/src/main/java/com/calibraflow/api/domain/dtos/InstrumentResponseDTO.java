package com.calibraflow.api.domain.dtos;

import com.calibraflow.api.domain.entities.Instrument;

public record InstrumentResponseDTO(
        Long id,
        String tag,
        String name,
        String manufacturer,
        String model,
        String serialNumber,
        String range,
        String tolerance,
        String resolution,
        String categoryName,
        String locationName,
        String patrimonyCode,
        String periodicityName,
        boolean active
) {
    public InstrumentResponseDTO(Instrument instrument) {
        this(
                instrument.getId(),
                instrument.getTag(),
                instrument.getName(),
                instrument.getManufacturer(),
                instrument.getModel(),
                instrument.getSerialNumber(),
                instrument.getRange(),
                instrument.getTolerance(),
                instrument.getResolution(),
                instrument.getCategory() != null ? instrument.getCategory().getName() : null,
                instrument.getLocation() != null ? instrument.getLocation().getName() : null,
                instrument.getPatrimony() != null ? instrument.getPatrimony().getPatrimonyCode() : null,
                instrument.getPeriodicity() != null ? instrument.getPeriodicity().getInstrumentName() : null,
                instrument.isActive()
        );
    }
}