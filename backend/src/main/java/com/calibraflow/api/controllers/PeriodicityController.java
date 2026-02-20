package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.entities.Periodicity;
import com.calibraflow.api.services.PeriodicityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/periodicities")
@RequiredArgsConstructor
public class PeriodicityController {

    private final PeriodicityService periodicityService;

    @GetMapping
    public ResponseEntity<List<Periodicity>> findAll() {
        return ResponseEntity.ok(periodicityService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Periodicity> findById(@PathVariable UUID id) {
        return periodicityService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
