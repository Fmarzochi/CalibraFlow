package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.entities.Calibration;
import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.repositories.CalibrationRepository;
import com.calibraflow.api.domain.repositories.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CalibrationService {

    private final CalibrationRepository calibrationRepository;
    private final InstrumentRepository instrumentRepository;

    @Transactional
    public Calibration registerCalibration(UUID instrumentId, Calibration calibration) {
        Instrument instrument = instrumentRepository.findById(instrumentId)
                .orElseThrow(() -> new RuntimeException("Instrumento não encontrado"));

        if (!instrument.isActive()) {
            throw new RuntimeException("Não é possível registrar calibração para um instrumento inativo");
        }

        calibration.setInstrument(instrument);
        return calibrationRepository.save(calibration);
    }

    public List<Calibration> getHistoryByInstrument(UUID instrumentId) {
        return calibrationRepository.findByInstrumentId(instrumentId);
    }
}