package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.entities.Calibration;
import com.calibraflow.api.domain.entities.Certificate;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.repositories.CalibrationRepository;
import com.calibraflow.api.domain.repositories.CertificateRepository;
import com.calibraflow.api.domain.services.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final CalibrationRepository calibrationRepository;
    private final StorageService storageService;

    @Transactional
    public Certificate uploadCertificate(Long calibrationId, MultipartFile file, User loggedUser) {
        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new IllegalArgumentException("Apenas arquivos PDF sao aceitos para certificados ISO.");
        }

        Calibration calibration = calibrationRepository.findById(calibrationId)
                .orElseThrow(() -> new IllegalArgumentException("Calibracao nao encontrada no sistema."));

        String storagePath = storageService.store(file, loggedUser.getTenant().getId());

        Certificate certificate = new Certificate();
        certificate.setTenant(loggedUser.getTenant());
        certificate.setCalibration(calibration);
        certificate.setOriginalFileName(file.getOriginalFilename());
        certificate.setStoragePath(storagePath);
        certificate.setContentType(file.getContentType());
        certificate.setFileSize(file.getSize());
        certificate.setUploadedById(loggedUser.getId());
        certificate.setUploadedByName(loggedUser.getName());

        return certificateRepository.save(certificate);
    }

    @Transactional(readOnly = true)
    public Resource downloadCertificate(Long certificateId, User loggedUser) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new IllegalArgumentException("Certificado nao encontrado no sistema."));

        if (!certificate.getTenant().getId().equals(loggedUser.getTenant().getId())) {
            throw new IllegalArgumentException("Acesso negado. O certificado pertence a outra corporacao.");
        }

        return storageService.loadAsResource(certificate.getStoragePath());
    }

    @Transactional(readOnly = true)
    public Certificate getCertificateInfo(Long certificateId, User loggedUser) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new IllegalArgumentException("Certificado nao encontrado no sistema."));

        if (!certificate.getTenant().getId().equals(loggedUser.getTenant().getId())) {
            throw new IllegalArgumentException("Acesso negado. O certificado pertence a outra corporacao.");
        }

        return certificate;
    }
}