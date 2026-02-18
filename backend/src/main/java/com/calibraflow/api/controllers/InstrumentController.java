package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.repositories.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/instruments")
@RequiredArgsConstructor
public class InstrumentController {

    private final InstrumentRepository instrumentRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Instrument> create(@RequestBody Instrument instrument) {
        return ResponseEntity.status(HttpStatus.CREATED).body(instrumentRepository.save(instrument));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'AUDITOR')")
    public ResponseEntity<List<Instrument>> listAll() {
        return ResponseEntity.ok(instrumentRepository.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'AUDITOR')")
    public ResponseEntity<Instrument> getById(@PathVariable UUID id) {
        return instrumentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> softDelete(@PathVariable UUID id) {
        return instrumentRepository.findById(id)
                .map(instrument -> {
                    instrument.setActive(false);
                    instrumentRepository.save(instrument);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}