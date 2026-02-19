package com.calibraflow.api.infrastructure.seeder;

import com.calibraflow.api.domain.entities.*;
import com.calibraflow.api.domain.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
@Order(2)
public class DatabaseSeeder implements CommandLineRunner {

    private final InstrumentRepository instrumentRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final PatrimonyRepository patrimonyRepository;

    public DatabaseSeeder(InstrumentRepository instrumentRepository,
                          CategoryRepository categoryRepository,
                          LocationRepository locationRepository,
                          PatrimonyRepository patrimonyRepository) {
        this.instrumentRepository = instrumentRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.patrimonyRepository = patrimonyRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (instrumentRepository.count() > 0) {
            return;
        }

        try {
            ClassPathResource resource = new ClassPathResource("instruments.csv");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                boolean isFirstLine = true;

                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }

                    String[] data = line.split(",");
                    if (data.length >= 4) {
                        String tag = data[0].trim();
                        String description = data[1].trim();
                        String serial = data[2].trim();
                        String locationName = data[3].trim();

                        // SOLUÇÃO: Busca se o patrimônio já existe no banco
                        Patrimony patrimony = patrimonyRepository.findByPatrimonyCode(tag)
                                .orElseGet(() -> {
                                    Patrimony newP = new Patrimony();
                                    newP.setPatrimonyCode(tag);
                                    newP.setTag(tag);
                                    return patrimonyRepository.save(newP);
                                });

                        Location location = locationRepository.findByName(locationName)
                                .orElseGet(() -> locationRepository.save(new Location(null, locationName, "Importado", true)));

                        Category category = categoryRepository.findAll().stream().findFirst().orElse(null);

                        Instrument instrument = new Instrument();
                        instrument.setName(description);
                        instrument.setSerialNumber(serial);
                        instrument.setPatrimony(patrimony); // Aqui usamos a entidade gerenciada pelo Hibernate
                        instrument.setLocation(location);
                        instrument.setCategory(category);
                        instrument.setActive(true);

                        instrumentRepository.save(instrument);
                    }
                }
                System.out.println(">>> CalibraFlow: Migração finalizada com sucesso!");
            }
        } catch (Exception e) {
            System.out.println("Erro na migração de instrumentos: " + e.getMessage());
        }
    }
}