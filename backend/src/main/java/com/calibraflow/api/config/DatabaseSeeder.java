package com.calibraflow.api.config;

import com.calibraflow.api.domain.entities.Category;
import com.calibraflow.api.domain.repositories.CategoryRepository;
import com.calibraflow.api.domain.services.MigrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final MigrationService migrationService;

    @Override
    public void run(String... args) throws Exception {
        seedCategories();
        seedDataFromCsv();
    }

    private void seedCategories() {
        if (categoryRepository.count() == 0) {
            List<Category> categories = List.of(
                    Category.builder().name("Pressão").calibrationIntervalDays(365).description("Manômetros e Transmissores").build(),
                    Category.builder().name("Temperatura").calibrationIntervalDays(365).description("Termômetros e PT-100").build(),
                    Category.builder().name("Dimensional").calibrationIntervalDays(180).description("Paquímetros e Micrômetros").build(),
                    Category.builder().name("Massa").calibrationIntervalDays(365).description("Balanças").build()
            );
            categoryRepository.saveAll(categories);
        }
    }

    private void seedDataFromCsv() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new ClassPathResource("data/planilha_mestre.csv").getInputStream()))) {

            List<String[]> rows = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                rows.add(line.split(","));
            }

            if (!rows.isEmpty()) {
                migrationService.importFromCsv(rows);
            }

        } catch (Exception e) {
            System.err.println("Erro ao carregar arquivo de migração: " + e.getMessage());
        }
    }
}