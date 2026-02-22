package com.calibraflow.api.application.dtos;

import java.time.OffsetDateTime;

public record CertificateResponseDTO(
        Long id,
        String originalFileName,
        String contentType,
        Long fileSize,
        OffsetDateTime uploadedAt,
        String uploadedByName
) {}