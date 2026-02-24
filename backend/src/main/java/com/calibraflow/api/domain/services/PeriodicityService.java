package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.entities.Periodicity;
import com.calibraflow.api.domain.repositories.PeriodicityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PeriodicityService {

    private final PeriodicityRepository periodicityRepository;

    @Transactional(readOnly = true)
    public Page<Periodicity> findAll(Pageable pageable) {
        return periodicityRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Periodicity> findById(Long id) {
        return periodicityRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Periodicity> findByInstrumentName(String instrumentName) {
        return periodicityRepository.findByCategoryName(instrumentName);
    }

    @Transactional
    public Periodicity save(Periodicity periodicity) {
        return periodicityRepository.save(periodicity);
    }
}