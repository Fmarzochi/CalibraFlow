package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.services.MigrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/migration")
@RequiredArgsConstructor
public class MigrationController {

    private final MigrationService migrationService;

    @PostMapping("/import")
    public ResponseEntity<String> triggerMigration(@RequestParam String fileName) {
        try {
            List<String[]> rows = new ArrayList<>();
            String path = "src/main/resources/" + fileName;

            try (BufferedReader br = new BufferedReader(new FileReader(path))) {
                String line;
                while ((line = br.readLine()) != null) {
                    rows.add(line.split(","));
                }
            }

            migrationService.importFromCsv(rows);
            return ResponseEntity.ok("Migração concluída com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro na migração: " + e.getMessage());
        }
    }
}