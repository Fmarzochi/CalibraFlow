package com.calibraflow.api.infrastructure.seeder;

import com.calibraflow.api.domain.services.MigrationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final MigrationService migrationService;

    public DatabaseSeeder(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @Override
    public void run(String... args) throws Exception {
        String csvFile = "src/main/resources/instruments.csv";
        String csvSplitBy = ",";
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;

            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);
                rows.add(data);
            }

            if (!rows.isEmpty()) {
                migrationService.importFromCsv(rows);
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler CSV: " + e.getMessage());
        }
    }
}