package com.calibraflow.api.domain.services.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {

    private final Path rootLocation;

    public LocalStorageService(@Value("${api.storage.local.dir:uploads}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Nao foi possivel inicializar o diretorio de armazenamento base.", e);
        }
    }

    @Override
    public String store(MultipartFile file, Long tenantId) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Falha ao armazenar arquivo vazio.");
            }

            Path tenantFolder = this.rootLocation.resolve(String.valueOf(tenantId));
            Files.createDirectories(tenantFolder);

            String originalExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String uniqueFileName = UUID.randomUUID().toString() + "." + originalExtension;

            Path targetLocation = tenantFolder.resolve(uniqueFileName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return tenantId + "/" + uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("Falha ao armazenar o arquivo no disco local.", e);
        }
    }

    @Override
    public void delete(String storagePath) {
        try {
            Path fileToDelete = this.rootLocation.resolve(storagePath).normalize();
            Files.deleteIfExists(fileToDelete);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao deletar o arquivo do disco local.", e);
        }
    }
}