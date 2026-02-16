package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.entities.Movement;
import com.calibraflow.api.domain.repositories.MovementRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller para gestão de movimentações e auditoria de instrumentos.
 */
@RestController
@RequestMapping("/api/movements")
public class MovementController {

    private final MovementRepository movementRepository;

    public MovementController(MovementRepository movementRepository) {
        this.movementRepository = movementRepository;
    }

    /**
     * Lista todas as movimentações registradas no sistema.
     */
    @GetMapping
    public List<Movement> findAll() {
        return movementRepository.findAll();
    }

    /**
     * Registra uma nova movimentação de instrumento.
     */
    @PostMapping
    public Movement create(@RequestBody Movement movement) {
        return movementRepository.save(movement);
    }

    /**
     * Busca uma movimentação específica pelo seu ID (UUID).
     */
    @GetMapping("/{id}")
    public Movement findById(@PathVariable UUID id) {
        return movementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));
    }

    /**
     * Retorna o histórico completo de movimentações de um instrumento específico.
     * Ordenado pela data de movimentação mais recente.
     */
    @GetMapping("/instrument/{instrumentId}")
    public List<Movement> findByInstrument(@PathVariable UUID instrumentId) {
        return movementRepository.findByInstrumentIdOrderByMovementDateDesc(instrumentId);
    }
}