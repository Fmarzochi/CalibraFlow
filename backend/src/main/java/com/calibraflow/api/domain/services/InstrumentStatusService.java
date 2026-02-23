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

@Service
@RequiredArgsConstructor
public class InstrumentStatusService {

    private final InstrumentRepository instrumentRepository;
    private final InstrumentStatusHistoryRepository historyRepository;

    @Transactional
    public void changeStatus(Long instrumentId, InstrumentRequestDTO.InstrumentStatusChangeDTO dto, User loggedUser, String sourceIp) {
        Instrument instrument = instrumentRepository.findById(instrumentId)
                .orElseThrow(() -> new IllegalArgumentException("Instrumento nao encontrado no sistema."));

        validateTransition(instrument.getStatus(), dto.status());

        InstrumentStatus previousStatus = instrument.getStatus();
        instrument.setStatus(dto.status());
        instrumentRepository.save(instrument);

        saveHistory(instrument, previousStatus, dto.status(), loggedUser, sourceIp, dto.justification());
    }

    @Transactional
    public void updateStatusFromCalibration(Instrument instrument, boolean approved, User loggedUser) {
        InstrumentStatus previousStatus = instrument.getStatus();
        InstrumentStatus newStatus = approved ? InstrumentStatus.ATIVO : InstrumentStatus.REPROVADO;

        instrument.setStatus(newStatus);
        instrumentRepository.save(instrument);

        saveHistory(instrument, previousStatus, newStatus, loggedUser, "SISTEMA_CALIBRACAO", "Status alterado automaticamente após registo de calibração.");
    }

    private void validateTransition(InstrumentStatus current, InstrumentStatus next) {
        if (current == next) {
            throw new IllegalArgumentException("O instrumento já se encontra no status " + next);
        }
        if (next == InstrumentStatus.EM_CALIBRACAO && current != InstrumentStatus.ATIVO && current != InstrumentStatus.VENCIDO) {
            throw new IllegalArgumentException("Um instrumento só pode entrar EM_CALIBRACAO se estiver ATIVO ou VENCIDO.");
        }
        if (next == InstrumentStatus.ATIVO && current == InstrumentStatus.REPROVADO) {
            throw new IllegalArgumentException("Um instrumento REPROVADO não pode ser ativado manualmente. É necessário registar uma nova calibração aprovada.");
        }
    }

    private void saveHistory(Instrument instrument, InstrumentStatus previousStatus, InstrumentStatus newStatus, User user, String ip, String justification) {
        InstrumentStatusHistory history = new InstrumentStatusHistory();
        history.setInstrument(instrument);
        history.setTenant(instrument.getTenant());
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setResponsibleId(user.getId());
        history.setResponsibleFullName(user.getName());
        history.setResponsibleCpf(user.getCpf());
        history.setSourceIp(ip);
        history.setJustification(justification);
        historyRepository.save(history);
    }
}