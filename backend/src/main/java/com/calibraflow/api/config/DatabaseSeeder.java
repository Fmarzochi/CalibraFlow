package com.calibraflow.api.infrastructure.seeder;

import com.calibraflow.api.domain.entities.Category;
import com.calibraflow.api.domain.repositories.CategoryRepository;
import com.calibraflow.api.domain.services.MigrationService;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final MigrationService migrationService;

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            categoryRepository.save(Category.builder().name("Pressão").calibrationIntervalDays(365).build());
            categoryRepository.save(Category.builder().name("Temperatura").calibrationIntervalDays(365).build());
            categoryRepository.save(Category.builder().name("Dimensional").calibrationIntervalDays(365).build());
            categoryRepository.save(Category.builder().name("Elétrica").calibrationIntervalDays(365).build());
            categoryRepository.save(Category.builder().name("Massa").calibrationIntervalDays(365).build());
        }

        try {
            ClassPathResource resource = new ClassPathResource("data/planilha_mestre.csv");
            if (resource.exists()) {
                System.out.println(">>> SEEDER: Lendo arquivo em " + resource.getPath());
                try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
                    List<String[]> rows = reader.readAll();
                    System.out.println(">>> SEEDER: Linhas encontradas no CSV: " + rows.size());
                    migrationService.importFromCsv(rows);
                }
            } else {
                System.err.println(">>> SEEDER: Arquivo planilha_mestre.csv não encontrado!");
            }
        } catch (Exception e) {
            System.err.println(">>> SEEDER: Erro ao ler CSV: " + e.getMessage());
        }

        System.out.println(">>> CalibraFlow: Banco de dados reiniciado com sucesso!");
    }
}