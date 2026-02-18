package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.entities.Movement;
import com.calibraflow.api.domain.services.MovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller refatorado para gestão de movimentações e auditoria utilizando a camada de serviço.
 */
@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
public class MovementController {

    private final MovementService movementService;

    /**
     * Lista todas as movimentações registradas no sistema via Service.
     */
    @GetMapping
    public ResponseEntity<List<Movement>> findAll() {
        return ResponseEntity.ok(movementService.findAll());
    }

    /**
     * Busca uma movimentação específica pelo seu ID (UUID) com tratamento de erro 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Movement> findById(@PathVariable UUID id) {
        return movementService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retorna o histórico completo de movimentações de um instrumento específico.
     */
    @GetMapping("/instrument/{instrumentId}")
    public ResponseEntity<List<Movement>> findByInstrument(@PathVariable UUID instrumentId) {
        return ResponseEntity.ok(movementService.findByInstrument(instrumentId));
    }

    /**
     * Registra uma nova movimentação de instrumento através do Service.
     */
    @PostMapping
    public ResponseEntity<Movement> create(@RequestBody Movement movement) {
        Movement savedMovement = movementService.save(movement);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMovement);
    }
}