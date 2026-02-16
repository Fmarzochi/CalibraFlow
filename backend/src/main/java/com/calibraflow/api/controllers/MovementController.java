package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.entities.Movement;
import com.calibraflow.api.domain.repositories.MovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movements")
public class MovementController {

    @Autowired
    private MovementRepository movementRepository;

    @GetMapping
    public List<Movement> findAll() {
        return movementRepository.findAll();
    }

    @PostMapping
    public Movement create(@RequestBody Movement movement) {
        return movementRepository.save(movement);
    }
}