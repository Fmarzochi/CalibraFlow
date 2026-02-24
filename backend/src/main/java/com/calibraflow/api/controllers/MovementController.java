package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.dtos.MovementRequestDTO;
import com.calibraflow.api.domain.dtos.MovementResponseDTO;
import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.entities.Location;
import com.calibraflow.api.domain.entities.Movement;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.repositories.InstrumentRepository;
import com.calibraflow.api.domain.repositories.LocationRepository;
import com.calibraflow.api.domain.services.MovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
public class MovementController {

    private final MovementService movementService;
    private final InstrumentRepository instrumentRepository;
    private final LocationRepository locationRepository;

    @GetMapping
    public ResponseEntity<Page<MovementResponseDTO>> findAll(@PageableDefault(size = 10, sort = "movementDate") Pageable pageable) {
        Page<MovementResponseDTO> movements = movementService.findAll(pageable).map(MovementResponseDTO::new);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovementResponseDTO> findById(@PathVariable Long id) {
        return movementService.findById(id)
                .map(movement -> ResponseEntity.ok(new MovementResponseDTO(movement)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/instrument/{instrumentId}")
    public ResponseEntity<Page<MovementResponseDTO>> findByInstrument(
            @PathVariable Long instrumentId,
            @PageableDefault(size = 10, sort = "movementDate") Pageable pageable) {
        Page<MovementResponseDTO> movements = movementService.findByInstrument(instrumentId, pageable).map(MovementResponseDTO::new);
        return ResponseEntity.ok(movements);
    }

    @PostMapping
    public ResponseEntity<MovementResponseDTO> create(
            @Valid @RequestBody MovementRequestDTO dto,
            @AuthenticationPrincipal User user) {

        Instrument instrument = instrumentRepository.findById(dto.instrumentId())
                .orElseThrow(() -> new IllegalArgumentException("Instrumento não encontrado"));

        Location origin = dto.originId() != null ? locationRepository.findById(dto.originId()).orElse(null) : null;
        Location destination = dto.destinationId() != null ? locationRepository.findById(dto.destinationId()).orElse(null) : null;

        Movement movement = Movement.builder()
                .reason(dto.reason())
                .instrument(instrument)
                .origin(origin)
                .destination(destination)
                .user(user)
                .movementDate(OffsetDateTime.now())
                .build();

        Movement savedMovement = movementService.save(movement);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MovementResponseDTO(savedMovement));
    }
}