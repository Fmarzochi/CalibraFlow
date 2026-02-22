package com.calibraflow.api.domain.services.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String store(MultipartFile file, Long tenantId);

    void delete(String storagePath);
}