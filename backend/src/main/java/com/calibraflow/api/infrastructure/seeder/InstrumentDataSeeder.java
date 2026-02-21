package com.calibraflow.api.infrastructure.seeder;

import com.calibraflow.api.domain.entities.Category;
import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.entities.Location;
import com.calibraflow.api.domain.entities.Patrimony;
import com.calibraflow.api.domain.entities.Periodicity;
import com.calibraflow.api.domain.repositories.CategoryRepository;
import com.calibraflow.api.domain.repositories.InstrumentRepository;
import com.calibraflow.api.domain.repositories.LocationRepository;
import com.calibraflow.api.domain.repositories.PeriodicityRepository;
import com.calibraflow.api.tools.DataFixer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Order(2)
@RequiredArgsConstructor
public class InstrumentDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InstrumentDataSeeder.class);
    private static final Path CLEAN_CSV_PATH = Paths.get("src/main/resources/instrumentos_limpos.csv");

    private final InstrumentRepository instrumentRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final PeriodicityRepository periodicityRepository;
    private final DataFixer dataFixer;

    @Override
    @Transactional
    public void run(String... args) {
        if (instrumentRepository.count() > 0) {
            log.info("Banco de dados já possui instrumentos. Seeder ignorado.");
            return;
        }

        if (!Files.exists(CLEAN_CSV_PATH)) {
            log.info("Arquivo de dados limpos não encontrado. Acionando ETL...");
            Path result = dataFixer.process();
            if (result == null || !Files.exists(CLEAN_CSV_PATH)) {
                log.error("Processo ETL falhou. Abortando seeder.");
                return;
            }
        }

        List<Instrument> instrumentsToSave = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(CLEAN_CSV_PATH, StandardCharsets.UTF_8)) {
            String line;
            int count = 0;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length <= 3) continue;

                String tag = data[0];
                String name = data[1];

                if (tag.isEmpty() || name.isEmpty()) continue;

                Category category = findOrCreateCategory(name);
                Location location = findOrCreateLocation(data[2]);
                Optional<Periodicity> periodicity = periodicityRepository.findByInstrumentName(name);

                Patrimony patrimony = new Patrimony();
                patrimony.setPatrimonyCode(data[3]);
                patrimony.setTag(tag);

                Instrument instrument = Instrument.builder()
                        .tag(tag)
                        .name(name)
                        .category(category)
                        .location(location)
                        .patrimony(patrimony)
                        .periodicity(periodicity.orElse(null))
                        .manufacturer(data.length > 4 ? data[4] : "")
                        .model(data.length > 5 ? data[5] : "")
                        .serialNumber(data.length > 6 ? data[6] : "")
                        .range(data.length > 7 ? data[7] : "")
                        .tolerance(data.length > 8 ? data[8] : "")
                        .active(true)
                        .deleted(false)
                        .build();

                instrumentsToSave.add(instrument);
                count++;
            }

            instrumentRepository.saveAll(instrumentsToSave);
            log.info("Seeder finalizado. {} instrumentos inseridos em lote.", count);

        } catch (Exception e) {
            log.error("Erro crítico durante a execução do seeder: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Category findOrCreateCategory(String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.save(Category.builder().name(name).build()));
    }

    private Location findOrCreateLocation(String name) {
        String finalName = (name == null || name.isEmpty()) ? "Não Informado" : name;
        return locationRepository.findByName(finalName)
                .orElseGet(() -> locationRepository.save(Location.builder().name(finalName).active(true).build()));
    }
}