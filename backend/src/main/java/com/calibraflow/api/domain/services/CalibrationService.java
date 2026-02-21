package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.entities.Calibration;
import com.calibraflow.api.domain.repositories.CalibrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalibrationService {

    private final CalibrationRepository calibrationRepository;

    @Transactional(readOnly = true)
    public List<Calibration> findAll() {
        return calibrationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Calibration> findById(Long id) {
        return calibrationRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Calibration> findByInstrument(Long instrumentId) {
        return calibrationRepository.findByInstrumentIdOrderByCalibrationDateDesc(instrumentId);
    }

    @Transactional
    public Calibration save(Calibration calibration) {
        return calibrationRepository.save(calibration);
    }

    @Transactional(readOnly = true)
    public List<Calibration> findUpcomingCalibrations(LocalDate start, LocalDate end) {
        return calibrationRepository.findByNextCalibrationDateBetween(start, end);
    }
}
