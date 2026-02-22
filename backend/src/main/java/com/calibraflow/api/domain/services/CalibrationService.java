package com.calibraflow.api.domain.services;

import com.calibraflow.api.application.dtos.CalibrationRequestDTO;
import com.calibraflow.api.application.dtos.CalibrationResponseDTO;
import com.calibraflow.api.domain.entities.Calibration;
import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.entities.enums.InstrumentStatus;
import com.calibraflow.api.domain.repositories.CalibrationRepository;
import com.calibraflow.api.domain.repositories.InstrumentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalibrationService {

    private final CalibrationRepository calibrationRepository;
    private final InstrumentRepository instrumentRepository;

    @Transactional
    public CalibrationResponseDTO create(CalibrationRequestDTO dto, User loggedUser) {
        Instrument instrument = instrumentRepository.findById(dto.instrumentId())
                .orElseThrow(() -> new EntityNotFoundException("Instrumento nao encontrado no sistema."));

        Calibration calibration = new Calibration();
        calibration.setTenant(loggedUser.getTenant());
        calibration.setInstrument(instrument);
        calibration.setCalibrationDate(dto.calibrationDate());
        calibration.setNextCalibrationDate(dto.nextCalibrationDate());
        calibration.setCertificateNumber(dto.certificateNumber());
        calibration.setApproved(dto.approved());
        calibration.setObservations(dto.observations());
        calibration.setCreatedById(loggedUser.getId());
        calibration.setCreatedByName(loggedUser.getName());

        calibrationRepository.save(calibration);

        if (Boolean.TRUE.equals(dto.approved())) {
            instrument.setStatus(InstrumentStatus.ATIVO);
            instrumentRepository.save(instrument);
        } else {
            instrument.setStatus(InstrumentStatus.QUARENTENA);
            instrumentRepository.save(instrument);
        }

        return mapToResponseDTO(calibration);
    }

    @Transactional(readOnly = true)
    public Page<CalibrationResponseDTO> findAllByInstrument(Long instrumentId, Pageable pageable) {
        return calibrationRepository.findByInstrumentId(instrumentId, pageable)
                .map(this::mapToResponseDTO);
    }

    @Transactional(readOnly = true)
    public CalibrationResponseDTO findById(Long id) {
        Calibration calibration = calibrationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro de calibracao nao encontrado."));
        return mapToResponseDTO(calibration);
    }

    private CalibrationResponseDTO mapToResponseDTO(Calibration calibration) {
        return new CalibrationResponseDTO(
                calibration.getId(),
                calibration.getInstrument().getId(),
                calibration.getInstrument().getTag(),
                calibration.getCalibrationDate(),
                calibration.getNextCalibrationDate(),
                calibration.getCertificateNumber(),
                calibration.isApproved(),
                calibration.getObservations(),
                calibration.getCreatedAt(),
                calibration.getCreatedByName()
        );
    }
}