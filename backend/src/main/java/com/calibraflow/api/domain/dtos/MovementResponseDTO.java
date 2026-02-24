package com.calibraflow.api.domain.dtos;

import com.calibraflow.api.domain.entities.Movement;
import java.time.OffsetDateTime;

public record MovementResponseDTO(
        Long id,
        OffsetDateTime movementDate,
        String reason,
        Long instrumentId,
        String instrumentTag,
        String originName,
        String destinationName,
        String userName
) {
    public MovementResponseDTO(Movement movement) {
        this(
                movement.getId(),
                movement.getMovementDate(),
                movement.getReason(),
                movement.getInstrument() != null ? movement.getInstrument().getId() : null,
                movement.getInstrument() != null ? movement.getInstrument().getTag() : null,
                movement.getOrigin() != null ? movement.getOrigin().getName() : null,
                movement.getDestination() != null ? movement.getDestination().getName() : null,
                movement.getUser() != null ? movement.getUser().getUsername() : null
        );
    }
}