package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.entities.Periodicity;
import com.calibraflow.api.domain.services.PeriodicityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/periodicities")
@RequiredArgsConstructor
public class PeriodicityController {

    private final PeriodicityService periodicityService;

    @GetMapping
    public ResponseEntity<Page<Periodicity>> findAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(periodicityService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Periodicity> findById(@PathVariable Long id) {
        return periodicityService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}