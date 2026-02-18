package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.entities.Calibration;
import com.calibraflow.api.domain.services.CalibrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller padronizado para gestão do ciclo de vida de calibrações.
 */
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
    public ResponseEntity<Calibration> findById(@PathVariable UUID id) {
        return calibrationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/instrument/{instrumentId}")
    public ResponseEntity<List<Calibration>> findByInstrument(@PathVariable UUID instrumentId) {
        return ResponseEntity.ok(calibrationService.findByInstrument(instrumentId));
    }

    @PostMapping
    public ResponseEntity<Calibration> create(@RequestBody Calibration calibration) {
        Calibration savedCalibration = calibrationService.save(calibration);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCalibration);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        calibrationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}