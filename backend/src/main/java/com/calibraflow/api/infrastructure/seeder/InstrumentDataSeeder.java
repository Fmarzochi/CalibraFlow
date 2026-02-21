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
import java.util.List;
import java.util.Optional;

@Component
@Order(2)
@RequiredArgsConstructor
public class InstrumentDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InstrumentDataSeeder.class);
    private static final Path CLEAN_CSV_PATH = Paths.get("src/main/resources/instrumentos_limpos.csv");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final InstrumentRepository instrumentRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final PeriodicityRepository periodicityRepository;
    private final CalibrationRepository calibrationRepository;
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
        List<Calibration> calibrationsToSave = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(CLEAN_CSV_PATH, StandardCharsets.UTF_8)) {
            String line;
            int count = 0;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length <= 3) continue;

                String name = data[0].trim();               // Descrição
                String tag = data.length > 1 ? data[1].trim() : "";
                String manufacturer = data.length > 2 ? data[2].trim() : "";
                String patrimonyCode = data.length > 3 ? data[3].trim() : "";
                String serialNumber = data.length > 5 ? data[5].trim() : "";
                String model = data.length > 6 ? data[6].trim() : "";
                String laboratory = data.length > 7 ? data[7].trim() : "";
                String certificate = data.length > 8 ? data[8].trim() : "";
                String calibrationDateStr = data.length > 9 ? data[9].trim() : "";
                String nextCalibrationDateStr = data.length > 10 ? data[10].trim() : "";
                String locationName = data.length > 11 ? data[11].trim() : "";

                if (tag.isEmpty() || name.isEmpty()) continue;

                Category category = findOrCreateCategory(name);
                Location location = findOrCreateLocation(locationName);
                Optional<Periodicity> periodicity = periodicityRepository.findByInstrumentName(name);

                Instrument instrument = Instrument.builder()
                        .tag(tag)
                        .name(name)
                        .category(category)
                        .location(location)
                        .patrimonyCode(patrimonyCode)
                        .periodicity(periodicity.orElse(null))
                        .manufacturer(manufacturer)
                        .model(model)
                        .serialNumber(serialNumber)
                        .range("")
                        .tolerance("")
                        .active(true)
                        .deleted(false)
                        .build();

                instrumentsToSave.add(instrument);
                count++;

                if (!calibrationDateStr.isEmpty() && !calibrationDateStr.equalsIgnoreCase("INDETERMINADO")) {
                    try {
                        LocalDate calibrationDate = LocalDate.parse(calibrationDateStr, DATE_FORMAT);
                        LocalDate nextCalibrationDate = null;
                        if (!nextCalibrationDateStr.isEmpty() && !nextCalibrationDateStr.equalsIgnoreCase("INDETERMINADO")) {
                            nextCalibrationDate = LocalDate.parse(nextCalibrationDateStr, DATE_FORMAT);
                        }

                        Calibration calibration = Calibration.builder()
                                .calibrationDate(calibrationDate)
                                .nextCalibrationDate(nextCalibrationDate)
                                .certificateNumber(certificate)
                                .laboratory(laboratory)
                                .instrument(instrument)  // associação temporária, será persistida após salvar instrumento
                                .build();

                        calibrationsToSave.add(calibration);
                    } catch (DateTimeParseException e) {
                        log.warn("Data de calibração inválida para o instrumento {}: {}", tag, calibrationDateStr);
                    }
                }
            }

            instrumentRepository.saveAll(instrumentsToSave);
            log.info("{} instrumentos salvos.", instrumentsToSave.size());

            calibrationRepository.saveAll(calibrationsToSave);
            log.info("{} calibrações salvas.", calibrationsToSave.size());

            log.info("Seeder finalizado. Total de instrumentos processados: {}", count);

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
