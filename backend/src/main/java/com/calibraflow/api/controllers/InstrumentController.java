package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.repositories.InstrumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instruments")
public class InstrumentController {

    @Autowired
    private InstrumentRepository instrumentRepository;

    @GetMapping
    public List<Instrument> findAll() {
        return instrumentRepository.findAll();
    }

    @PostMapping
    public Instrument create(@RequestBody Instrument instrument) {
        return instrumentRepository.save(instrument);
    }
}