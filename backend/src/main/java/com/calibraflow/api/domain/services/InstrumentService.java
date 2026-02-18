package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.repositories.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;

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
    public Optional<Instrument> softDelete(UUID id) {
        return instrumentRepository.findById(id)
                .map(instrument -> {
                    instrument.setActive(false);
                    return instrumentRepository.save(instrument);
                });
    }
}