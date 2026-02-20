package com.calibraflow.api.infrastructure.seeder;

import com.calibraflow.api.domain.entities.*;
import com.calibraflow.api.domain.repositories.*;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class InstrumentDataSeeder implements CommandLineRunner {

    private final InstrumentRepository instrumentRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final PatrimonyRepository patrimonyRepository;
    private final CalibrationRepository calibrationRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (instrumentRepository.count() > 0) {
            log.info("Tabela de instrumentos já populada. Ignorando seeder.");
            return;
        }

        InputStream is = getClass().getResourceAsStream("/instrumentos.csv");
        if (is == null) {
            log.error("Arquivo instrumentos.csv não encontrado no diretório resources.");
            return;
        }

        CSVParser parser = new CSVParserBuilder().withSeparator(',').build();

        try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReaderBuilder(reader)
                     .withSkipLines(1)
                     .withCSVParser(parser)
                     .build()) {

            String[] line;
            int count = 0;

            while ((line = csvReader.readNext()) != null) {
                if (line.length < 12 || line[0].trim().isEmpty()) {
                    continue;
                }

                String patrimonyCode = getValidString(line[3]);
                if (patrimonyCode == null || patrimonyRepository.findByPatrimonyCode(patrimonyCode).isPresent()) {
                    continue;
                }

                try {
                    String name = getValidString(line[0]);
                    String tag = getValidString(line[4]);
                    String serial = getValidString(line[5]);
                    String locName = getValidString(line[11]);
                    
                    if (locName == null) {
                        locName = "ALMOXARIFADO";
                    }

                    Category category = categoryRepository.findByName(name)
                            .orElseGet(() -> categoryRepository.save(new Category(UUID.randomUUID(), name, 365, "Importação Automática")));

                    Location location = locationRepository.findByName(locName)
                            .orElseGet(() -> locationRepository.save(new Location(UUID.randomUUID(), locName, "Local Padrão", true)));

                    Patrimony patrimony = new Patrimony(UUID.randomUUID(), patrimonyCode, tag);

                    Instrument instrument = new Instrument();
                    instrument.setId(UUID.randomUUID());
                    instrument.setName(name);
                    instrument.setSerialNumber(serial);
                    instrument.setCategory(category);
                    instrument.setLocation(location);
                    instrument.setPatrimony(patrimony);
                    instrument.setActive(true);
                    instrument.setDeleted(false);
                    instrument.setCreatedAt(LocalDateTime.now());

                    instrument = instrumentRepository.save(instrument);

                    String dateCalStr = getValidString(line[9]);
                    String dateNextStr = getValidString(line[10]);
                    String lab = getValidString(line[7]);
                    String cert = getValidString(line[8]);

                    if (dateCalStr != null) {
                        Calibration calibration = new Calibration();
                        calibration.setId(UUID.randomUUID());
                        calibration.setInstrument(instrument);
                        calibration.setCalibrationDate(LocalDate.parse(dateCalStr));
                        
                        if (dateNextStr != null) {
                            calibration.setNextCalibrationDate(LocalDate.parse(dateNextStr));
                        }
                        
                        calibration.setLaboratory(lab);
                        calibration.setCertificateNumber(cert);
                        
                        calibrationRepository.save(calibration);
                    }

                    count++;
                } catch (Exception e) {
                    log.warn("Falha ao processar linha do patrimônio {}: {}", patrimonyCode, e.getMessage());
                }
            }
            log.info("Carga de instrumentos finalizada. {} novos registros e calibrações inseridos.", count);
        }
    }

    private String getValidString(String value) {
        if (value == null || value.trim().isEmpty() || value.trim().equalsIgnoreCase("N/C")) {
            return null;
        }
        return value.trim();
    }
}
