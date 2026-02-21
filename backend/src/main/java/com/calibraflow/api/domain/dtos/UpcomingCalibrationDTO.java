package com.calibraflow.api.domain.dtos;

import com.calibraflow.api.domain.entities.Calibration;
import com.calibraflow.api.domain.entities.Instrument;
import java.time.LocalDate;

public record UpcomingCalibrationDTO(
    Long calibrationId,
    Long instrumentId,
    String instrumentTag,
    String instrumentName,
    String patrimonyCode,
    LocalDate calibrationDate,
    LocalDate nextCalibrationDate,
    String laboratory,
    String certificateNumber
) {
    public UpcomingCalibrationDTO(Calibration calibration) {
        this(
            calibration.getId(),
            calibration.getInstrument().getId(),
            calibration.getInstrument().getTag(),
            calibration.getInstrument().getName(),
            calibration.getInstrument().getPatrimonyCode(),
            calibration.getCalibrationDate(),
            calibration.getNextCalibrationDate(),
            calibration.getLaboratory(),
            calibration.getCertificateNumber()
        );
    }
}
