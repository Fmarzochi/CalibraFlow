package com.calibraflow.api.infrastructure.seeder;

import com.calibraflow.api.domain.entities.Periodicity;
import com.calibraflow.api.domain.repositories.PeriodicityRepository;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
public class PeriodicitySeeder implements CommandLineRunner {

    private final PeriodicityRepository periodicityRepository;

    public PeriodicitySeeder(PeriodicityRepository periodicityRepository) {
        this.periodicityRepository = periodicityRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (periodicityRepository.count() > 0) {
            return;
        }

        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();

        try (CSVReader reader = new CSVReaderBuilder(
                new InputStreamReader(new ClassPathResource("periodicities.csv").getInputStream(), StandardCharsets.UTF_8))
                .withSkipLines(3)
                .withCSVParser(parser)
                .build()) {

            String[] line;
            List<Periodicity> periodicities = new ArrayList<>();
            int count = 0;

            while ((line = reader.readNext()) != null) {
                if (line.length >= 3 && !line[0].trim().isEmpty()) {
                    String name = line[0].trim();
                    String daysStr = line[2].trim();

                    if (!daysStr.equalsIgnoreCase("Indeterminado") && !daysStr.isEmpty()) {
                        try {
                            Integer days = Integer.parseInt(daysStr);
                            periodicities.add(new Periodicity(name, days));
                            count++;
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }

            periodicityRepository.saveAll(periodicities);
            System.out.println(">>> CalibraFlow: Regras de periodicidade importadas com sucesso via OpenCSV! Total: " + count);

        } catch (Exception e) {
            System.out.println("Erro ao importar periodicidades: " + e.getMessage());
        }
    }
}