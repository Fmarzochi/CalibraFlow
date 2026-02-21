package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.dtos.CalibrationRequestDTO;
import com.calibraflow.api.domain.dtos.CalibrationResponseDTO;
import com.calibraflow.api.domain.dtos.UpcomingCalibrationDTO;
import com.calibraflow.api.domain.entities.Calibration;
import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.repositories.InstrumentRepository;
import com.calibraflow.api.domain.services.CalibrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/calibrations")
@RequiredArgsConstructor
public class CalibrationController {

    private final CalibrationService calibrationService;
    private final InstrumentRepository instrumentRepository;

    @GetMapping
    public ResponseEntity<Page<CalibrationResponseDTO>> findAll(@PageableDefault(size = 10, sort = "calibrationDate") Pageable pageable) {
        Page<CalibrationResponseDTO> calibrations = calibrationService.findAll(pageable).map(CalibrationResponseDTO::new);
        return ResponseEntity.ok(calibrations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalibrationResponseDTO> findById(@PathVariable Long id) {
        return calibrationService.findById(id)
                .map(calibration -> ResponseEntity.ok(new CalibrationResponseDTO(calibration)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/instrument/{instrumentId}")
    public ResponseEntity<Page<CalibrationResponseDTO>> findByInstrument(
            @PathVariable Long instrumentId,
            @PageableDefault(size = 10, sort = "calibrationDate") Pageable pageable) {
        Page<CalibrationResponseDTO> calibrations = calibrationService.findByInstrument(instrumentId, pageable).map(CalibrationResponseDTO::new);
        return ResponseEntity.ok(calibrations);
    }

    @PostMapping
    public ResponseEntity<CalibrationResponseDTO> create(@Valid @RequestBody CalibrationRequestDTO dto) {
        Instrument instrument = instrumentRepository.findById(dto.instrumentId())
                .orElseThrow(() -> new IllegalArgumentException("Instrumento não encontrado"));

        Calibration calibration = Calibration.builder()
                .calibrationDate(dto.calibrationDate())
                .nextCalibrationDate(dto.nextCalibrationDate())
                .certificateNumber(dto.certificateNumber())
                .laboratory(dto.laboratory())
                .instrument(instrument)
                .build();

        Calibration savedCalibration = calibrationService.save(calibration);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CalibrationResponseDTO(savedCalibration));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<UpcomingCalibrationDTO>> getUpcomingCalibrations(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<Calibration> calibrations = calibrationService.findUpcomingCalibrations(start, end);
        List<UpcomingCalibrationDTO> dtos = calibrations.stream()
                .map(UpcomingCalibrationDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}