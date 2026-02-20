package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.entities.Location;
import com.calibraflow.api.domain.services.InstrumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instruments")
@RequiredArgsConstructor
public class InstrumentController {

    private final InstrumentService instrumentService;

    @GetMapping
    public ResponseEntity<List<Instrument>> findAll() {
        return ResponseEntity.ok(instrumentService.findAllActive());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Instrument>> findAllIncludingDeleted() {
        return ResponseEntity.ok(instrumentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Instrument> findById(@PathVariable Long id) {
        return instrumentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Instrument> create(@RequestBody Instrument instrument) {
        return ResponseEntity.ok(instrumentService.save(instrument));
    }

    @PutMapping("/{id}/location")
    public ResponseEntity<Instrument> updateLocation(
            @PathVariable Long id,
            @RequestBody Location newLocation,
            @RequestParam Long userId,
            @RequestParam String reason) {

        return instrumentService.updateLocation(id, newLocation, userId, reason)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam(required = false) String reason) {
        return instrumentService.softDelete(id, userId, reason)
                .map(instrument -> ResponseEntity.noContent().<Void>build())
                .orElse(ResponseEntity.notFound().build());
    }
}