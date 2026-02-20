package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.entities.Movement;
import com.calibraflow.api.domain.services.MovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
public class MovementController {

    private final MovementService movementService;

    @GetMapping
    public ResponseEntity<List<Movement>> findAll() {
        return ResponseEntity.ok(movementService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movement> findById(@PathVariable Long id) {
        return movementService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/instrument/{instrumentId}")
    public ResponseEntity<List<Movement>> findByInstrument(@PathVariable Long instrumentId) {
        return ResponseEntity.ok(movementService.findByInstrument(instrumentId));
    }

    @PostMapping
    public ResponseEntity<Movement> create(@RequestBody Movement movement) {
        Movement savedMovement = movementService.save(movement);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMovement);
    }
}