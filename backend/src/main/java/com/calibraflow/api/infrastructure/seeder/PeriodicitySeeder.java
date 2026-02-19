package com.calibraflow.api.infrastructure.seeder;

import com.calibraflow.api.domain.entities.Periodicity;
import com.calibraflow.api.domain.repositories.PeriodicityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
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
    public void run(String... args) throws Exception {
        if (periodicityRepository.count() > 0) {
            return;
        }

        String path = "src/main/resources/periodicities.csv";
        List<Periodicity> periodicities = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;

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
        } catch (Exception e) {
            System.out.println("Erro ao carregar periodicidades: " + e.getMessage());
        }
    }
}