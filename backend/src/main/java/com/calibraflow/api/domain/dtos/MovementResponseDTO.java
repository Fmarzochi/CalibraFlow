package com.calibraflow.api.domain.dtos;

import com.calibraflow.api.domain.entities.Movement;
import java.time.LocalDateTime;

public record MovementResponseDTO(
        Long id,
        LocalDateTime movementDate,
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
                movement.getMovedBy() != null ? movement.getMovedBy().getUsername() : null
        );
    }
}