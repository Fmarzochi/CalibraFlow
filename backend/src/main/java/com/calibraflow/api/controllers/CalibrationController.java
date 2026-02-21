package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.dtos.UpcomingCalibrationDTO;
import com.calibraflow.api.domain.entities.Calibration;
import com.calibraflow.api.domain.services.CalibrationService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public ResponseEntity<List<Calibration>> findAll() {
        return ResponseEntity.ok(calibrationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Calibration> findById(@PathVariable Long id) {
        return calibrationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/instrument/{instrumentId}")
    public ResponseEntity<List<Calibration>> findByInstrument(@PathVariable Long instrumentId) {
        return ResponseEntity.ok(calibrationService.findByInstrument(instrumentId));
    }

    @PostMapping
    public ResponseEntity<Calibration> create(@RequestBody Calibration calibration) {
        Calibration savedCalibration = calibrationService.save(calibration);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCalibration);
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
