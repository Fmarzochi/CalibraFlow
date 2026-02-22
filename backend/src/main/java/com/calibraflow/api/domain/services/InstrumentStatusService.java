package com.calibraflow.api.domain.services;

import com.calibraflow.api.application.dtos.InstrumentStatusChangeDTO;
import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.entities.InstrumentStatusHistory;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.entities.enums.InstrumentStatus;
import com.calibraflow.api.domain.repositories.InstrumentRepository;
import com.calibraflow.api.domain.repositories.InstrumentStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InstrumentStatusService {

    private final InstrumentRepository instrumentRepository;
    private final InstrumentStatusHistoryRepository historyRepository;

    @Transactional
    public void changeStatus(Long instrumentId, InstrumentStatusChangeDTO dto, User loggedUser, String sourceIp) {
        Instrument instrument = instrumentRepository.findById(instrumentId)
                .orElseThrow(() -> new IllegalArgumentException("Instrumento nao encontrado no sistema."));

        InstrumentStatus previousStatus = instrument.getStatus();

        instrument.setStatus(dto.newStatus());
        instrumentRepository.save(instrument);

        InstrumentStatusHistory history = new InstrumentStatusHistory();
        history.setInstrument(instrument);
        history.setTenant(instrument.getTenant());
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(dto.newStatus());
        history.setResponsibleId(loggedUser.getId());
        history.setResponsibleFullName(loggedUser.getName());
        history.setResponsibleCpf(loggedUser.getCpf());
        history.setSourceIp(sourceIp);
        history.setJustification(dto.justification());

        historyRepository.save(history);
    }
}