package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.entities.Periodicity;
import com.calibraflow.api.domain.repositories.PeriodicityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PeriodicityService {

    private final PeriodicityRepository periodicityRepository;

    public List<Periodicity> findAll() {
        return periodicityRepository.findAll();
    }

    public Optional<Periodicity> findById(Long id) {
        return periodicityRepository.findById(id);
    }

    public Optional<Periodicity> findByInstrumentName(String instrumentName) {
        return periodicityRepository.findByInstrumentName(instrumentName);
    }

    @Transactional
    public Periodicity save(Periodicity periodicity) {
        return periodicityRepository.save(periodicity);
    }
}