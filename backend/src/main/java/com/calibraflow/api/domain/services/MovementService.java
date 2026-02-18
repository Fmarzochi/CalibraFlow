package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.entities.Movement;
import com.calibraflow.api.domain.repositories.MovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovementService {

    private final MovementRepository movementRepository;

    @Transactional(readOnly = true)
    public List<Movement> findAll() {
        return movementRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Movement> findById(UUID id) {
        return movementRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Movement> findByInstrument(UUID instrumentId) {
        return movementRepository.findByInstrumentIdOrderByMovementDateDesc(instrumentId);
    }

    @Transactional
    public Movement save(Movement movement) {
        return movementRepository.save(movement);
    }
}