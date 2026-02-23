package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.dtos.InstrumentRequestDTO;
import com.calibraflow.api.domain.dtos.InstrumentResponseDTO;
import com.calibraflow.api.domain.entities.*;
import com.calibraflow.api.domain.entities.enums.InstrumentStatus;
import com.calibraflow.api.domain.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final PeriodicityRepository periodicityRepository;

    @Transactional
    public InstrumentResponseDTO create(InstrumentRequestDTO dto, User loggedUser) {
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada."));

        Location location = locationRepository.findById(dto.locationId())
                .orElseThrow(() -> new EntityNotFoundException("Localização não encontrada."));

        Periodicity periodicity = null;
        if (dto.periodicityId() != null) {
            periodicity = periodicityRepository.findById(dto.periodicityId()).orElse(null);
        }

        Instrument instrument = Instrument.builder()
                .tag(dto.tag())
                .name(dto.name())
                .serialNumber(dto.serialNumber())
                .manufacturer(dto.manufacturer())
                .model(dto.model())
                .tolerance(dto.tolerance())
                .category(category)
                .location(location)
                .periodicity(periodicity)
                .tenant(loggedUser.getTenant())
                .status(InstrumentStatus.ATIVO)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        instrument = instrumentRepository.save(instrument);
        return mapToResponseDTO(instrument);
    }

    @Transactional
    public InstrumentResponseDTO update(Long id, InstrumentRequestDTO dto, User loggedUser) {
        Instrument instrument = instrumentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Instrumento não encontrado."));

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada."));

        Location location = locationRepository.findById(dto.locationId())
                .orElseThrow(() -> new EntityNotFoundException("Localização não encontrada."));

        Periodicity periodicity = null;
        if (dto.periodicityId() != null) {
            periodicity = periodicityRepository.findById(dto.periodicityId()).orElse(null);
        }

        instrument.setTag(dto.tag());
        instrument.setName(dto.name());
        instrument.setSerialNumber(dto.serialNumber());
        instrument.setManufacturer(dto.manufacturer());
        instrument.setModel(dto.model());
        instrument.setTolerance(dto.tolerance());
        instrument.setCategory(category);
        instrument.setLocation(location);
        instrument.setPeriodicity(periodicity);

        instrument = instrumentRepository.save(instrument);
        return mapToResponseDTO(instrument);
    }

    public Page<InstrumentResponseDTO> findAll(Pageable pageable) {
        return instrumentRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    public InstrumentResponseDTO findById(Long id) {
        Instrument instrument = instrumentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Instrumento não encontrado."));
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
                instrument.getTolerance(),
                instrument.getCategory() != null ? instrument.getCategory().getName() : null,
                instrument.getLocation() != null ? instrument.getLocation().getName() : null,
                instrument.getPeriodicity() != null ? instrument.getPeriodicity().getDays() : null,
                instrument.getStatus(),
                instrument.isActive(),
                instrument.getCreatedAt()
        );
    }
}