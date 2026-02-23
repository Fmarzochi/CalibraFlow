package com.calibraflow.api.application.controllers;

import com.calibraflow.api.domain.dtos.InstrumentRequestDTO;
import com.calibraflow.api.application.dtos.InstrumentResponseDTO;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.services.InstrumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/instruments")
@RequiredArgsConstructor
public class InstrumentController {

    private final InstrumentService instrumentService;

    @PostMapping
    public ResponseEntity<InstrumentResponseDTO> create(
            @RequestBody @Valid InstrumentRequestDTO dto,
            @AuthenticationPrincipal User loggedUser) {

        InstrumentResponseDTO response = instrumentService.create(dto, loggedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<InstrumentResponseDTO>> findAll(
            @PageableDefault(size = 20, sort = "tag") Pageable pageable) {

        Page<InstrumentResponseDTO> response = instrumentService.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstrumentResponseDTO> findById(@PathVariable Long id) {
        InstrumentResponseDTO response = instrumentService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstrumentResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid InstrumentRequestDTO dto,
            @AuthenticationPrincipal User loggedUser) {

        InstrumentResponseDTO response = instrumentService.update(id, dto, loggedUser);
        return ResponseEntity.ok(response);
    }
}