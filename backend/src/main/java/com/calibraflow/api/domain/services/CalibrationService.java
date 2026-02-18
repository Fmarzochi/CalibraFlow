package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.entities.Calibration;
import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.repositories.CalibrationRepository;
import com.calibraflow.api.domain.repositories.InstrumentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class CalibrationService {

    @Autowired
    private CalibrationRepository calibrationRepository;

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Transactional
    public Calibration registerCalibration(UUID instrumentId, Calibration calibrationData) {
        Instrument instrument = instrumentRepository.findById(instrumentId)
                .orElseThrow(() -> new EntityNotFoundException("Instrumento não encontrado com ID: " + instrumentId));

        if (!instrument.getActive()) {
            throw new IllegalStateException("Não é possível calibrar um instrumento inativo.");
        }

        // Regra de Negócio: Calcula o vencimento baseado na periodicidade da categoria
        int intervalDays = instrument.getCategory().getCalibrationIntervalDays();
        LocalDate nextDate = calibrationData.getCalibrationDate().plusDays(intervalDays);

        calibrationData.setNextCalibrationDate(nextDate);
        calibrationData.setInstrument(instrument);

        return calibrationRepository.save(calibrationData);
    }
}