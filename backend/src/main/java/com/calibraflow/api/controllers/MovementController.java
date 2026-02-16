package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.entities.Movement;
import com.calibraflow.api.domain.repositories.MovementRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/movements")
public class MovementController {

    private final MovementRepository movementRepository;

    public MovementController(MovementRepository movementRepository) {
        this.movementRepository = movementRepository;
    }

    @GetMapping
    public List<Movement> findAll() {
        return movementRepository.findAll();
    }

    @PostMapping
    public Movement create(@RequestBody Movement movement) {
        return movementRepository.save(movement);
    }

    @GetMapping("/{id}")
    public Movement findById(@PathVariable UUID id) {
        return movementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));
    }
}