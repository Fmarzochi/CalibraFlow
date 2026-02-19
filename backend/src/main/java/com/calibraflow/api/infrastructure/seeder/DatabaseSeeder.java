package com.calibraflow.api.infrastructure.seeder;

import com.calibraflow.api.domain.entities.*;
import com.calibraflow.api.domain.repositories.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final InstrumentRepository instrumentRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final PatrimonyRepository patrimonyRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        InputStream is = getClass().getResourceAsStream("/instruments.csv");
        if (is == null) return;

        try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReaderBuilder(reader).build()) {

            String[] line;
            int count = 0;
            while ((line = csvReader.readNext()) != null) {
                try {
                    if (line.length < 5 || line[0].trim().isEmpty() || line[0].contains("Descrição")) {
                        continue;
                    }

                    String name = line[0].trim();
                    String patrimonyCode = line[3].trim();
                    String tag = line[4].trim();
                    String serial = line[5].trim();
                    String locName = (line.length > 11) ? line[11].trim() : "PADRAO";

                    if (patrimonyRepository.findByPatrimonyCode(patrimonyCode).isPresent()) {
                        continue;
                    }

                    Category category = categoryRepository.findByName(name)
                            .orElseGet(() -> categoryRepository.save(new Category(UUID.randomUUID(), name, 365, "Auto")));

                    Location location = locationRepository.findByName(locName)
                            .orElseGet(() -> locationRepository.save(new Location(UUID.randomUUID(), locName, "Local", true)));

                    Patrimony patrimony = new Patrimony(UUID.randomUUID(), patrimonyCode, tag);

                    Instrument instrument = new Instrument();
                    instrument.setId(UUID.randomUUID());
                    instrument.setName(name);
                    instrument.setSerialNumber(serial);
                    instrument.setCategory(category);
                    instrument.setLocation(location);
                    instrument.setPatrimony(patrimony);

                    instrumentRepository.save(instrument);
                    count++;
                } catch (Exception e) {
                    log.error("Erro ao processar linha: {}", e.getMessage());
                }
            }
            if (count > 0) {
                log.info("Processamento concluído. {} novos instrumentos inseridos.", count);
            }
        }
    }
}