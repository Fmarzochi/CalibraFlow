package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.dtos.CalibrationResponseDTO;
import com.calibraflow.api.domain.entities.Calibration;
import com.calibraflow.api.domain.repositories.CalibrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalibrationService {

    private final CalibrationRepository calibrationRepository;

    @Transactional(readOnly = true)
    public Page<CalibrationResponseDTO> findAll(Pageable pageable) {
        return calibrationRepository.findAll(pageable).map(c -> new CalibrationResponseDTO(
                c.getId(),
                c.getInstrument().getTag(),
                c.getInstrument().getName(),
                c.getCalibrationDate(),
                c.getNextCalibrationDate(),
                c.getCertificateNumber(),
                c.isApproved(),
                c.getObservations()
        ));
    }

    @Transactional(readOnly = true)
    public List<Calibration> findUpcomingCalibrations(LocalDate start, LocalDate end) {
        return calibrationRepository.findUpcomingCalibrations(start, end);
    }
}