package com.calibraflow.api.infrastructure.seeder;

import com.calibraflow.api.domain.entities.Category;
import com.calibraflow.api.domain.repositories.CategoryRepository;
import com.calibraflow.api.domain.services.MigrationService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final MigrationService migrationService;

    @Override
    public void run(String... args) throws Exception {
        seedCategories();
        importCsvData();
    }

    private void seedCategories() {
        if (categoryRepository.count() == 0) {
            Category c1 = new Category(null, "Pressão", 365, "Instrumentos de medição de pressão e vácuo");
            Category c2 = new Category(null, "Temperatura", 365, "Termômetros, termopares e controladores");
            Category c3 = new Category(null, "Dimensional", 730, "Paquímetros, micrômetros e réguas");
            Category c4 = new Category(null, "Massa", 365, "Balanças analíticas e industriais");
            Category c5 = new Category(null, "Volume", 365, "Vidrarias e medidores de vazão");
            categoryRepository.saveAll(Arrays.asList(c1, c2, c3, c4, c5));
        }
    }

    private void importCsvData() {
        try (CSVReader reader = new CSVReader(new InputStreamReader(new ClassPathResource("planilha_mestre.csv").getInputStream()))) {
            List<String[]> rows = reader.readAll();
            migrationService.importFromCsv(rows);
            System.out.println("Carga inicial finalizada com sucesso.");
        } catch (IOException | CsvException e) {
            System.err.println("Erro ao processar carga inicial: " + e.getMessage());
        }
    }
}