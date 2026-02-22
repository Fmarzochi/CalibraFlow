package com.calibraflow.api.domain.services.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String store(MultipartFile file, Long tenantId);

    Resource loadAsResource(String storagePath);

    void delete(String storagePath);
}