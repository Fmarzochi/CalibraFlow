package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.dtos.CalibrationRequestDTO;
import com.calibraflow.api.domain.dtos.CalibrationResponseDTO;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.services.CalibrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calibrations")
@RequiredArgsConstructor
public class CalibrationController {

    private final CalibrationService calibrationService;

    @PostMapping
    public ResponseEntity<CalibrationResponseDTO> create(
            @RequestBody @Valid CalibrationRequestDTO dto,
            @AuthenticationPrincipal User loggedUser) {

        CalibrationResponseDTO response = calibrationService.create(dto, loggedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/instrument/{instrumentId}")
    public ResponseEntity<Page<CalibrationResponseDTO>> findAllByInstrument(
            @PathVariable Long instrumentId,
            @PageableDefault(size = 20, sort = "calibrationDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<CalibrationResponseDTO> response = calibrationService.findAllByInstrument(instrumentId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalibrationResponseDTO> findById(@PathVariable Long id) {
        CalibrationResponseDTO response = calibrationService.findById(id);
        return ResponseEntity.ok(response);
    }
}