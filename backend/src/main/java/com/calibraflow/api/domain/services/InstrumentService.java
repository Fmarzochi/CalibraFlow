package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.dtos.InstrumentRequestDTO;
import com.calibraflow.api.domain.entities.Category;
import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.entities.Location;
import com.calibraflow.api.domain.entities.Movement;
import com.calibraflow.api.domain.entities.Periodicity;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.repositories.CategoryRepository;
import com.calibraflow.api.domain.repositories.InstrumentRepository;
import com.calibraflow.api.domain.repositories.LocationRepository;
import com.calibraflow.api.domain.repositories.MovementRepository;
import com.calibraflow.api.domain.repositories.PeriodicityRepository;
import com.calibraflow.api.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;
    private final MovementRepository movementRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final PeriodicityRepository periodicityRepository;

    @Transactional(readOnly = true)
    public List<Instrument> findAllActive() {
        return instrumentRepository.findAllActive();
    }

    @Transactional(readOnly = true)
    public List<Instrument> findAll() {
        return instrumentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Instrument> findById(Long id) {
        return instrumentRepository.findActiveById(id);
    }

    @Transactional
    public Instrument save(Instrument instrument) {
        return instrumentRepository.save(instrument);
    }

    @Transactional
    public Instrument createFromDTO(InstrumentRequestDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new IllegalArgumentException("Localização não encontrada"));
        Periodicity periodicity = null;
        if (dto.getPeriodicityId() != null) {
            periodicity = periodicityRepository.findById(dto.getPeriodicityId())
                    .orElseThrow(() -> new IllegalArgumentException("Periodicidade não encontrada"));
        }

        Instrument instrument = Instrument.builder()
                .tag(dto.getTag())
                .name(dto.getName())
                .manufacturer(dto.getManufacturer())
                .model(dto.getModel())
                .serialNumber(dto.getSerialNumber())
                .range(dto.getRange())
                .tolerance(dto.getTolerance())
                .resolution(dto.getResolution())
                .patrimonyCode(dto.getPatrimonyCode())
                .category(category)
                .location(location)
                .periodicity(periodicity)
                .active(true)
                .deleted(false)
                .build();

        return instrumentRepository.save(instrument);
    }

    @Transactional
    public Optional<Instrument> softDelete(Long id, Long userId, String reason) {
        return instrumentRepository.findById(id)
                .map(instrument -> {
                    instrument.setDeleted(true);
                    instrument.setActive(false);
                    instrument.setDeletedAt(LocalDateTime.now());

                    Instrument savedInstrument = instrumentRepository.save(instrument);

                    registrarMovimento(savedInstrument, instrument.getLocation(), null, userId,
                            reason != null && !reason.isEmpty() ? reason : "Exclusão Lógica via Sistema");

                    return savedInstrument;
                });
    }

    @Transactional
    public Optional<Instrument> softDelete(Long id, User user, String reason) {
        return softDelete(id, user.getId(), reason);
    }

    @Transactional
    public Optional<Instrument> updateLocation(Long id, Location newLocation, Long userId, String reason) {
        return instrumentRepository.findById(id)
                .map(instrument -> {
                    Location oldLocation = instrument.getLocation();
                    instrument.setLocation(newLocation);

                    Instrument savedInstrument = instrumentRepository.save(instrument);

                    registrarMovimento(savedInstrument, oldLocation, newLocation, userId, reason);

                    return savedInstrument;
                });
    }

    @Transactional
    public Optional<Instrument> updateLocation(Long id, Location newLocation, User user, String reason) {
        return updateLocation(id, newLocation, user.getId(), reason);
    }

    private void registrarMovimento(Instrument instrument, Location origin, Location destination, Long userId, String reason) {
        if (userId == null) {
            throw new IllegalArgumentException("O ID do usuário é obrigatório para registrar a movimentação.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado para auditoria."));

        Movement movement = new Movement();
        movement.setInstrument(instrument);
        movement.setOrigin(origin);
        movement.setDestination(destination);
        movement.setMovedBy(user);
        movement.setReason(reason);
        movement.setMovementDate(LocalDateTime.now());

        movementRepository.save(movement);
    }
}