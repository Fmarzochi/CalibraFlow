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
import java.time.format.DateTimeFormatter;
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
            log.info("Banco já possui dados. Ignorando carga de instrumentos.");
            return;
        }

        InputStream is = getClass().getResourceAsStream("/instrumentos.csv");
        if (is == null) {
            log.error("Arquivo instrumentos.csv não encontrado.");
            return;
        }

        CSVParser parser = new CSVParserBuilder().withSeparator(',').build();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d/M/yyyy");

        try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReaderBuilder(reader)
                     .withSkipLines(5) // Pula as 5 linhas de cabeçalho (CCI-INS, DATA, REVISÃO, etc)
                     .withCSVParser(parser)
                     .build()) {

            String[] line;
            int count = 0;

            while ((line = csvReader.readNext()) != null) {
                try {
                    if (line.length < 11 || line[0].trim().isEmpty()) continue;

                    String name = line[0].trim();
                    String patrimonyCode = line[3].trim();
                    if (patrimonyCode.isEmpty() || patrimonyCode.equalsIgnoreCase("N/C")) continue;
                    
                    if (patrimonyRepository.findByPatrimonyCode(patrimonyCode).isPresent()) continue;

                    Category cat = categoryRepository.findByName(name)
                            .orElseGet(() -> categoryRepository.save(new Category(UUID.randomUUID(), name, 365, "Importação")));

                    String locName = (line.length > 11 && !line[11].trim().isEmpty()) ? line[11].trim() : "ALMOXARIFADO";
                    Location loc = locationRepository.findByName(locName)
                            .orElseGet(() -> locationRepository.save(new Location(UUID.randomUUID(), locName, "Local", true)));

                    Patrimony pat = patrimonyRepository.save(new Patrimony(UUID.randomUUID(), patrimonyCode, line[4].trim()));

                    Instrument ins = new Instrument();
                    ins.setId(UUID.randomUUID());
                    ins.setName(name);
                    ins.setSerialNumber(line[5].trim());
                    ins.setCategory(cat);
                    ins.setLocation(loc);
                    ins.setPatrimony(pat);
                    ins.setActive(true);
                    ins.setDeleted(false);
                    ins.setCreatedAt(LocalDateTime.now());
                    instrumentRepository.save(ins);

                    if (!line[9].trim().isEmpty() && !line[9].equalsIgnoreCase("N/C")) {
                        Calibration cal = new Calibration();
                        cal.setId(UUID.randomUUID());
                        cal.setInstrument(ins);
                        cal.setCalibrationDate(LocalDate.parse(line[9].trim(), fmt));
                        if (!line[10].trim().isEmpty() && !line[10].equalsIgnoreCase("N/C")) {
                            cal.setNextCalibrationDate(LocalDate.parse(line[10].trim(), fmt));
                        }
                        cal.setLaboratory(line[7].trim());
                        cal.setCertificateNumber(line[8].trim());
                        calibrationRepository.save(cal);
                    }
                    count++;
                } catch (Exception e) {
                    log.warn("Erro na linha {}: {}", count + 6, e.getMessage());
                }
            }
            log.info("Carga finalizada: {} instrumentos inseridos.", count);
        }
    }
}
