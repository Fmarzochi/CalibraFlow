package com.calibraflow.api.application.controllers;

import com.calibraflow.api.application.dtos.CertificateResponseDTO;
import com.calibraflow.api.domain.entities.Certificate;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.services.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/calibrations")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping(value = "/{calibrationId}/certificates", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CertificateResponseDTO> upload(
            @PathVariable Long calibrationId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User loggedUser) {

        Certificate certificate = certificateService.uploadCertificate(calibrationId, file, loggedUser);

        CertificateResponseDTO responseDTO = new CertificateResponseDTO(
                certificate.getId(),
                certificate.getOriginalFileName(),
                certificate.getContentType(),
                certificate.getFileSize(),
                certificate.getUploadedAt(),
                certificate.getUploadedByName()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
}