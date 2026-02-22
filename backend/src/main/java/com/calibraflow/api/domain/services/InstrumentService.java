package com.calibraflow.api.domain.services;

import com.calibraflow.api.application.dtos.InstrumentRequestDTO;
import com.calibraflow.api.application.dtos.InstrumentResponseDTO;
import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.repositories.InstrumentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;

    @Transactional
    public InstrumentResponseDTO create(InstrumentRequestDTO dto, User loggedUser) {
        if (instrumentRepository.existsByTagAndTenant(dto.tag(), loggedUser.getTenant())) {
            throw new IllegalArgumentException("Ja existe um instrumento com esta TAG cadastrado na sua empresa.");
        }

        Instrument instrument = new Instrument();
        instrument.setTenant(loggedUser.getTenant());
        instrument.setTag(dto.tag());
        instrument.setName(dto.name());
        instrument.setSerialNumber(dto.serialNumber());
        instrument.setManufacturer(dto.manufacturer());
        instrument.setModel(dto.model());
        instrument.setLocation(dto.location());
        instrument.setTolerance(dto.tolerance());

        instrumentRepository.save(instrument);

        return mapToResponseDTO(instrument);
    }

    @Transactional(readOnly = true)
    public Page<InstrumentResponseDTO> findAll(Pageable pageable) {
        return instrumentRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    @Transactional(readOnly = true)
    public InstrumentResponseDTO findById(Long id) {
        Instrument instrument = instrumentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Instrumento nao encontrado."));
        return mapToResponseDTO(instrument);
    }

    @Transactional
    public InstrumentResponseDTO update(Long id, InstrumentRequestDTO dto, User loggedUser) {
        Instrument instrument = instrumentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Instrumento nao encontrado."));

        if (!instrument.getTag().equals(dto.tag()) && instrumentRepository.existsByTagAndTenant(dto.tag(), loggedUser.getTenant())) {
            throw new IllegalArgumentException("Ja existe outro instrumento com esta TAG cadastrado na sua empresa.");
        }

        instrument.setTag(dto.tag());
        instrument.setName(dto.name());
        instrument.setSerialNumber(dto.serialNumber());
        instrument.setManufacturer(dto.manufacturer());
        instrument.setModel(dto.model());
        instrument.setLocation(dto.location());
        instrument.setTolerance(dto.tolerance());

        instrumentRepository.save(instrument);

        return mapToResponseDTO(instrument);
    }

    private InstrumentResponseDTO mapToResponseDTO(Instrument instrument) {
        return new InstrumentResponseDTO(
                instrument.getId(),
                instrument.getTag(),
                instrument.getName(),
                instrument.getSerialNumber(),
                instrument.getManufacturer(),
                instrument.getModel(),
                instrument.getLocation(),
                instrument.getTolerance(),
                instrument.getStatus()
        );
    }
}