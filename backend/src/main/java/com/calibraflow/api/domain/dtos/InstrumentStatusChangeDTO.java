package com.calibraflow.api.application.dtos;

import com.calibraflow.api.domain.entities.enums.InstrumentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InstrumentStatusChangeDTO(

        @NotNull(message = "O novo status e obrigatorio")
        InstrumentStatus newStatus,

        @NotBlank(message = "A justificativa e obrigatoria para fins de auditoria ISO")
        String justification

) {}