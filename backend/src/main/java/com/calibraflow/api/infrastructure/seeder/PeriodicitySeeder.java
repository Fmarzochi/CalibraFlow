package com.calibraflow.api.infrastructure.seeder;

import com.calibraflow.api.domain.entities.Periodicity;
import com.calibraflow.api.domain.repositories.PeriodicityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
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

        try {
            ClassPathResource resource = new ClassPathResource("periodicities.csv");

            try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                List<Periodicity> periodicities = new ArrayList<>();

                br.readLine();
                br.readLine();
                br.readLine();

                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");

                    if (data.length >= 3) {
                        String name = data[0].trim();
                        String daysStr = data[2].trim();

                        if (!daysStr.equalsIgnoreCase("Indeterminado") && !daysStr.isEmpty()) {
                            try {
                                Integer days = Integer.parseInt(daysStr);
                                periodicities.add(new Periodicity(name, days));
                            } catch (NumberFormatException e) {
                                continue;
                            }
                        }
                    }
                }

                periodicityRepository.saveAll(periodicities);
                System.out.println(">>> CalibraFlow: Regras de periodicidade importadas com sucesso!");
            }
        } catch (Exception e) {
            System.out.println("Erro ao importar periodicidades: " + e.getMessage());
        }
    }
}