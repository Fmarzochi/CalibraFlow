package com.calibraflow.api.domain.dtos;

import com.calibraflow.api.domain.entities.Calibration;
import java.time.LocalDate;

public record CalibrationResponseDTO(
        Long id,
        LocalDate calibrationDate,
        LocalDate nextCalibrationDate,
        String certificateNumber,
        String laboratory,
        Long instrumentId,
        String instrumentName,
        String instrumentTag
) {
    public CalibrationResponseDTO(Calibration calibration) {
        this(
                calibration.getId(),
                calibration.getCalibrationDate(),
                calibration.getNextCalibrationDate(),
                calibration.getCertificateNumber(),
                calibration.getLaboratory(),
                calibration.getInstrument() != null ? calibration.getInstrument().getId() : null,
                calibration.getInstrument() != null ? calibration.getInstrument().getName() : null,
                calibration.getInstrument() != null ? calibration.getInstrument().getTag() : null
        );
    }
}