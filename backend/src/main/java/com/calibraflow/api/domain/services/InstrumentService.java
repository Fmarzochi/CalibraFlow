package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.entities.Location;
import com.calibraflow.api.domain.repositories.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;

    @Transactional(readOnly = true)
    public List<Instrument> findAllActive() {
        return instrumentRepository.findAllActive();
    }

    @Transactional(readOnly = true)
    public List<Instrument> findAll() {
        return instrumentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Instrument> findById(UUID id) {
        return instrumentRepository.findActiveById(id);
    }

    @Transactional
    public Instrument save(Instrument instrument) {
        return instrumentRepository.save(instrument);
    }

    @Transactional
    public Optional<Instrument> softDelete(UUID id) {
        return instrumentRepository.findById(id)
                .map(instrument -> {
                    instrument.setDeleted(true);
                    instrument.setActive(false);
                    instrument.setDeletedAt(LocalDateTime.now());
                    return instrumentRepository.save(instrument);
                });
    }

    @Transactional
    public Optional<Instrument> updateLocation(UUID id, Location newLocation, Long userId, String reason) {
        return instrumentRepository.findById(id)
                .map(instrument -> {
                    instrument.setLocation(newLocation);
                    return instrumentRepository.save(instrument);
                });
    }
}