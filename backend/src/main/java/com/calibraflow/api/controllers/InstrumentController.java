package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.repositories.InstrumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instruments")
public class InstrumentController {

    // Eu utilizo a injeção de dependência para acessar o repositório de instrumentos
    @Autowired
    private InstrumentRepository instrumentRepository;

    // Eu exponho o método GET para listar todos os instrumentos cadastrados
    @GetMapping
    public List<Instrument> findAll() {
        return instrumentRepository.findAll();
    }

    // Eu exponho o método POST para permitir o cadastro de novos instrumentos
    @PostMapping
    public Instrument create(@RequestBody Instrument instrument) {
        return instrumentRepository.save(instrument);
    }
}