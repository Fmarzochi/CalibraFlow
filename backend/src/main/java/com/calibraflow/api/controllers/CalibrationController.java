package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.entities.Calibration;
import com.calibraflow.api.domain.services.CalibrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/calibrations")
public class CalibrationController {

    @Autowired
    private CalibrationService calibrationService;

    @PostMapping("/instrument/{instrumentId}")
    public ResponseEntity<Calibration> create(@PathVariable UUID instrumentId, @RequestBody Calibration calibration) {
        Calibration newCalibration = calibrationService.registerCalibration(instrumentId, calibration);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCalibration);
    }
}