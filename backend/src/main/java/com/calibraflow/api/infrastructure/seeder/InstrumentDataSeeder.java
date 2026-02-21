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
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

                String tag = data[1]; // TAG
                String name = data[0]; // Descrição

                if (tag.isEmpty() || name.isEmpty()) continue;

                Category category = findOrCreateCategory(name);
                Location location = findOrCreateLocation(data[11]); // Obra/Local
                Optional<Periodicity> periodicity = periodicityRepository.findByInstrumentName(name);

                String patrimonyCode = data[3]; // Patrimônio

                Instrument instrument = Instrument.builder()
                        .tag(tag)
                        .name(name)
                        .category(category)
                        .location(location)
                        .patrimonyCode(patrimonyCode)
                        .periodicity(periodicity.orElse(null))
                        .manufacturer(data.length > 2 ? data[2] : "") // Fabricante
                        .model(data.length > 6 ? data[6] : "") // Modelo
                        .serialNumber(data.length > 5 ? data[5] : "") // Número de Série
                        .range(data.length > 1 ? data[1] : "") // especificação (range)
                        .tolerance("") // não temos na planilha
                        .active(true)
                        .deleted(false)
                        .build();

                instrumentsToSave.add(instrument);

                LocalDate calibrationDate = parseDate(data.length > 9 ? data[9] : "");
                LocalDate nextCalibrationDate = parseDate(data.length > 10 ? data[10] : "");
                String certificateNumber = data.length > 8 ? data[8] : ""; // Certificado
                String laboratory = data.length > 7 ? data[7] : ""; // Laboratório

                if (calibrationDate != null || nextCalibrationDate != null || !certificateNumber.isEmpty()) {
                    Calibration calibration = Calibration.builder()
                            .calibrationDate(calibrationDate)
                            .nextCalibrationDate(nextCalibrationDate)
                            .certificateNumber(certificateNumber)
                            .laboratory(laboratory)
                            .instrument(instrument) // Associação temporária, será salva após persistência do instrumento
                            .build();
                    calibrationsToSave.add(calibration);
                }

                count++;
            }

            List<Instrument> savedInstruments = instrumentRepository.saveAll(instrumentsToSave);
            log.info("Seeder finalizado. {} instrumentos inseridos.", savedInstruments.size());

            int calibrationCount = 0;
            for (int i = 0; i < savedInstruments.size(); i++) {
                Instrument savedInstrument = savedInstruments.get(i);
                // Filtra as calibrações que foram criadas para este instrumento (pela posição)
                if (i < calibrationsToSave.size()) {
                    Calibration cal = calibrationsToSave.get(i);
                    cal.setInstrument(savedInstrument);
                    calibrationsToSave.set(i, cal);
                    calibrationCount++;
                }
            }

            calibrationRepository.saveAll(calibrationsToSave);
            log.info("{} calibrações inseridas.", calibrationCount);

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
        if (dateStr == null || dateStr.isEmpty() || dateStr.equalsIgnoreCase("INDETERMINADO") || dateStr.equalsIgnoreCase("N/C")) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
