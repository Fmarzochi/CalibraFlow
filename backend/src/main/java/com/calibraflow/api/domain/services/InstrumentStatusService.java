package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.dtos.InstrumentRequestDTO;
import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.entities.InstrumentStatusHistory;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.entities.enums.InstrumentStatus;
import com.calibraflow.api.domain.repositories.InstrumentRepository;
import com.calibraflow.api.domain.repositories.InstrumentStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class InstrumentStatusService {

    private final InstrumentRepository instrumentRepository;
    private final InstrumentStatusHistoryRepository historyRepository;

    @Transactional
    public void changeStatus(Long instrumentId, InstrumentRequestDTO.InstrumentStatusChangeDTO dto, User loggedUser, String sourceIp) {
        Instrument instrument = instrumentRepository.findById(instrumentId)
                .orElseThrow(() -> new RuntimeException("Instrumento não encontrado"));

        InstrumentStatus previousStatus = instrument.getStatus();
        InstrumentStatus newStatus = dto.status();

        instrument.setStatus(newStatus);
        instrumentRepository.save(instrument);

        InstrumentStatusHistory history = InstrumentStatusHistory.builder()
                .tenant(instrument.getTenant())
                .instrument(instrument)
                .previousStatus(previousStatus)
                .status(newStatus)
                .justification(dto.justification())
                .sourceIp(sourceIp)
                .changedAt(OffsetDateTime.now())
                .responsibleId(loggedUser.getId())
                .responsibleFullName(loggedUser.getName())
                .responsibleCpf(loggedUser.getCpf())
                .build();

        historyRepository.save(history);
    }
}