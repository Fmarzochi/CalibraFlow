package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.dtos.InstrumentRequestDTO;
import com.calibraflow.api.domain.dtos.InstrumentResponseDTO;
import com.calibraflow.api.domain.entities.Location;
import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.services.InstrumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/instruments")
@RequiredArgsConstructor
public class InstrumentController {

    private final InstrumentService instrumentService;

    @GetMapping
    public ResponseEntity<List<InstrumentResponseDTO>> findAll() {
        List<InstrumentResponseDTO> instruments = instrumentService.findAllActive()
                .stream()
                .map(InstrumentResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(instruments);
    }

    @GetMapping("/all")
    public ResponseEntity<List<InstrumentResponseDTO>> findAllIncludingDeleted() {
        List<InstrumentResponseDTO> instruments = instrumentService.findAll()
                .stream()
                .map(InstrumentResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(instruments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstrumentResponseDTO> findById(@PathVariable Long id) {
        return instrumentService.findById(id)
                .map(instrument -> ResponseEntity.ok(new InstrumentResponseDTO(instrument)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InstrumentResponseDTO> create(@Valid @RequestBody InstrumentRequestDTO dto) {
        Instrument savedInstrument = instrumentService.createFromDTO(dto);
        return ResponseEntity.ok(new InstrumentResponseDTO(savedInstrument));
    }

    @PutMapping("/{id}/location")
    public ResponseEntity<InstrumentResponseDTO> updateLocation(
            @PathVariable Long id,
            @RequestBody Location newLocation,
            @RequestParam Long userId,
            @RequestParam String reason) {

        return instrumentService.updateLocation(id, newLocation, userId, reason)
                .map(instrument -> ResponseEntity.ok(new InstrumentResponseDTO(instrument)))
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
