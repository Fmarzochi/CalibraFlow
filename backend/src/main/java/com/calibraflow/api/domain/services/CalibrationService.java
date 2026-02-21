package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.entities.Calibration;
import com.calibraflow.api.domain.repositories.CalibrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<Calibration> findAll(Pageable pageable) {
        return calibrationRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Calibration> findById(Long id) {
        return calibrationRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<Calibration> findByInstrument(Long instrumentId, Pageable pageable) {
        return calibrationRepository.findByInstrumentIdOrderByCalibrationDateDesc(instrumentId, pageable);
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