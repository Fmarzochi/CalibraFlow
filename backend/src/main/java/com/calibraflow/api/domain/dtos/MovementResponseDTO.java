package com.calibraflow.api.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementResponseDTO {
    private Long id;
    private String instrumentTag;
    private String originName;
    private String destinationName;
    private String reason;
    private String movedByName;
    private OffsetDateTime movementDate;
}