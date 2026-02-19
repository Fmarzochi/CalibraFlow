package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.entities.Location;
import com.calibraflow.api.domain.entities.Movement;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.repositories.InstrumentRepository;
import com.calibraflow.api.domain.repositories.MovementRepository;
import com.calibraflow.api.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;
    private final MovementRepository movementRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Instrument> findAll() {
        return instrumentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Instrument> findById(UUID id) {
        return instrumentRepository.findById(id);
    }

    @Transactional
    public Instrument save(Instrument instrument) {
        return instrumentRepository.save(instrument);
    }

    @Transactional
    public Instrument updateLocation(UUID instrumentId, Location newLocation, Long userId, String reason) {
        Instrument instrument = instrumentRepository.findById(instrumentId)
                .orElseThrow(() -> new RuntimeException("Instrumento nao encontrado"));

        Location oldLocation = instrument.getLocation();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

        Movement movement = new Movement();
        movement.setInstrument(instrument);
        movement.setOrigin(oldLocation);
        movement.setDestination(newLocation);
        movement.setMovedBy(user);
        movement.setMovementDate(LocalDateTime.now());
        movement.setReason(reason);

        movementRepository.save(movement);

        instrument.setLocation(newLocation);
        return instrumentRepository.save(instrument);
    }

    @Transactional
    public Optional<Instrument> softDelete(UUID id) {
        return instrumentRepository.findById(id)
                .map(instrument -> {
                    instrument.setActive(false);
                    return instrumentRepository.save(instrument);
                });
    }
}