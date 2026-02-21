package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.entities.Movement;
import com.calibraflow.api.domain.repositories.MovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovementService {

    private final MovementRepository movementRepository;

    @Transactional(readOnly = true)
    public Page<Movement> findAll(Pageable pageable) {
        return movementRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Movement> findById(Long id) {
        return movementRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<Movement> findByInstrument(Long instrumentId, Pageable pageable) {
        return movementRepository.findByInstrumentIdOrderByMovementDateDesc(instrumentId, pageable);
    }

    @Transactional
    public Movement save(Movement movement) {
        return movementRepository.save(movement);
    }
}