package com.calibraflow.api.infrastructure.seeder;

import com.calibraflow.api.domain.entities.Calibration;
import com.calibraflow.api.domain.entities.Category;
import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.entities.Location;
import com.calibraflow.api.domain.entities.Periodicity;
import com.calibraflow.api.domain.repositories.CalibrationRepository;
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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Order(2)
@RequiredArgsConstructor
public class InstrumentDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InstrumentDataSeeder.class);
    private static final Path CLEAN_CSV_PATH = Paths.get("src/main/resources/instrumentos_limpos.csv");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final InstrumentRepository instrumentRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final PeriodicityRepository periodicityRepository;
    private final CalibrationRepository calibrationRepository;
    private final DataFixer dataFixer;
    private final Environment environment;

    @Override
    @Transactional
    public void run(String... args) {
        if (Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            log.info("Perfil 'test' detectado. Ignorando execução do InstrumentDataSeeder.");
            return;
        }

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
        List<Calibration> calibrationsToSave = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(CLEAN_CSV_PATH, StandardCharsets.UTF_8)) {
            String line;
            int count = 0;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length <= 4) continue; // precisa ter pelo menos 5 colunas

                String descricao = data[0].trim();       // Descrição
                String tag = data[4].trim();              // TAG (índice 4)
                String patrimonyCode = data[3].trim();    // Patrimônio (índice 3)
                String fabricante = data.length > 2 ? data[2].trim() : "";
                String modelo = data.length > 6 ? data[6].trim() : "";
                String serialNumber = data.length > 5 ? data[5].trim() : "";
                String range = data.length > 1 ? data[1].trim() : "";
                String local = data.length > 11 ? data[11].trim() : "";

                if (tag.isEmpty() || descricao.isEmpty() || patrimonyCode.isEmpty()) continue;

                Category category = findOrCreateCategory(descricao);
                Location location = findOrCreateLocation(local);
                Optional<Periodicity> periodicity = periodicityRepository.findByInstrumentName(descricao);

                Instrument instrument = Instrument.builder()
                        .tag(tag)
                        .name(descricao)
                        .category(category)
                        .location(location)
                        .patrimonyCode(patrimonyCode)
                        .periodicity(periodicity.orElse(null))
                        .manufacturer(fabricante)
                        .model(modelo)
                        .serialNumber(serialNumber)
                        .range(range)
                        .tolerance("")
                        .active(true)
                        .deleted(false)
                        .build();

                instrumentsToSave.add(instrument);
                count++;
            }

            instrumentRepository.saveAll(instrumentsToSave);
            log.info("Seeder finalizado. {} instrumentos inseridos em lote.", count);

            // Segunda leitura para criar calibrações
            try (BufferedReader reader2 = Files.newBufferedReader(CLEAN_CSV_PATH, StandardCharsets.UTF_8)) {
                String line2;
                int calCount = 0;

                while ((line2 = reader2.readLine()) != null) {
                    String[] data = line2.split(",", -1);
                    if (data.length <= 4) continue;

                    String patrimonyCode = data[3].trim();
                    String descricao = data[0].trim();
                    if (patrimonyCode.isEmpty() || descricao.isEmpty()) continue;

                    Optional<Instrument> instrumentOpt = instrumentRepository.findByPatrimonyCode(patrimonyCode);
                    if (instrumentOpt.isEmpty()) {
                        log.warn("Instrumento com patrimônio {} não encontrado para criar calibração.", patrimonyCode);
                        continue;
                    }

                    Instrument instrument = instrumentOpt.get();

                    String calDateStr = data.length > 9 ? data[9].trim() : "";
                    String nextCalDateStr = data.length > 10 ? data[10].trim() : "";
                    String laboratory = data.length > 7 ? data[7].trim() : "";
                    String certificate = data.length > 8 ? data[8].trim() : "";

                    LocalDate calibrationDate = parseDate(calDateStr);
                    LocalDate nextCalibrationDate = parseDate(nextCalDateStr);

                    if (calibrationDate != null) {
                        Calibration calibration = Calibration.builder()
                                .calibrationDate(calibrationDate)
                                .nextCalibrationDate(nextCalibrationDate)
                                .laboratory(laboratory)
                                .certificateNumber(certificate)
                                .instrument(instrument)
                                .build();
                        calibrationsToSave.add(calibration);
                        calCount++;
                    }
                }

                calibrationRepository.saveAll(calibrationsToSave);
                log.info("{} calibrações inseridas.", calCount);
            }

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

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
