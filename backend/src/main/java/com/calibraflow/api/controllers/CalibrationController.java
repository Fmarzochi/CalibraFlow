package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.dtos.CalibrationResponseDTO;
import com.calibraflow.api.domain.services.CalibrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calibrations")
@RequiredArgsConstructor
public class CalibrationController {

    private final CalibrationService calibrationService;

    @GetMapping
    public ResponseEntity<Page<CalibrationResponseDTO>> findAll(@PageableDefault(size = 20) Pageable pageable) {
        Page<CalibrationResponseDTO> response = calibrationService.findAll(pageable);
        return ResponseEntity.ok(response);
    }
}