package com.calibraflow.api.infrastructure.seeder;

import com.calibraflow.api.domain.entities.*;
import com.calibraflow.api.domain.repositories.*;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
@Order(2)
public class DatabaseSeeder implements CommandLineRunner {

    private final InstrumentRepository instrumentRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final PatrimonyRepository patrimonyRepository;
    private final CalibrationRepository calibrationRepository;

    public DatabaseSeeder(InstrumentRepository instrumentRepository,
                          CategoryRepository categoryRepository,
                          LocationRepository locationRepository,
                          PatrimonyRepository patrimonyRepository,
                          CalibrationRepository calibrationRepository) {
        this.instrumentRepository = instrumentRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.patrimonyRepository = patrimonyRepository;
        this.calibrationRepository = calibrationRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (instrumentRepository.count() > 0) {
            return;
        }

        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();

        try (CSVReader reader = new CSVReaderBuilder(
                new InputStreamReader(new ClassPathResource("instruments.csv").getInputStream(), StandardCharsets.UTF_8))
                .withSkipLines(5)
                .withCSVParser(parser)
                .build()) {

            String[] data;
            int importedCount = 0;

            while ((data = reader.readNext()) != null) {
                if (data.length >= 6 && !data[0].trim().isEmpty()) {
                    String descricao = data[0].trim();
                    String patrimonyCode = data.length > 3 ? data[3].trim() : "N/C";
                    String tag = data.length > 4 ? data[4].trim() : "N/C";
                    String serialNumber = data.length > 5 ? data[5].trim() : "N/C";
                    String laboratory = data.length > 7 ? data[7].trim() : "Externo";
                    String certificate = data.length > 8 ? data[8].trim() : "N/C";
                    String calibDateStr = data.length > 9 ? data[9].trim() : "";
                    String nextCalibDateStr = data.length > 10 ? data[10].trim() : "";
                    String locationName = data.length > 11 && !data[11].trim().isEmpty() ? data[11].trim() : "Almoxarifado";

                    Category category = categoryRepository.findByName(descricao)
                            .orElseGet(() -> {
                                Category newCat = new Category();
                                newCat.setName(descricao);
                                newCat.setCalibrationIntervalDays(365);
                                newCat.setDescription("Importado automaticamente");
                                return categoryRepository.save(newCat);
                            });

                    Location location = locationRepository.findByName(locationName)
                            .orElseGet(() -> {
                                Location newLoc = new Location();
                                newLoc.setName(locationName);
                                newLoc.setDescription("Importado automaticamente");
                                newLoc.setActive(true);
                                return locationRepository.save(newLoc);
                            });

                    Patrimony patrimony = patrimonyRepository.findByPatrimonyCode(patrimonyCode)
                            .orElseGet(() -> {
                                Patrimony newPat = new Patrimony();
                                newPat.setPatrimonyCode(patrimonyCode);
                                newPat.setTag(tag);
                                return patrimonyRepository.save(newPat);
                            });

                    Instrument instrument = instrumentRepository.findBySerialNumber(serialNumber)
                            .orElseGet(() -> {
                                Instrument newInst = new Instrument();
                                newInst.setName(descricao);
                                newInst.setSerialNumber(serialNumber);
                                newInst.setCategory(category);
                                newInst.setLocation(location);
                                newInst.setPatrimony(patrimony);
                                newInst.setActive(true);
                                return instrumentRepository.save(newInst);
                            });

                    LocalDate calDate = parseDateRobust(calibDateStr);
                    if (calDate != null) {
                        LocalDate nextCalDate = calDate.plusDays(category.getCalibrationIntervalDays());
                        LocalDate parsedNextDate = parseDateRobust(nextCalibDateStr);

                        if (parsedNextDate != null) {
                            nextCalDate = parsedNextDate;
                        }

                        if (!calibrationRepository.existsByInstrumentAndCertificateNumber(instrument, certificate)) {
                            Calibration calibration = new Calibration();
                            calibration.setInstrument(instrument);
                            calibration.setCalibrationDate(calDate);
                            calibration.setNextCalibrationDate(nextCalDate);
                            calibration.setCertificateNumber(certificate);
                            calibration.setLaboratory(laboratory.isEmpty() ? "Externo" : laboratory);
                            calibrationRepository.save(calibration);
                        }
                    }
                    importedCount++;
                }
            }
            System.out.println(">>> CalibraFlow: Instrumentos e calibrações importados com sucesso. Total: " + importedCount);
        } catch (Exception e) {
            System.out.println("Erro na migração de instrumentos: " + e.getMessage());
        }
    }

    private LocalDate parseDateRobust(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty() || dateStr.equalsIgnoreCase("N/C")) return null;
        String cleanDate = dateStr.trim();
        try {
            if (cleanDate.contains("/")) {
                return LocalDate.parse(cleanDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } else {
                return LocalDate.parse(cleanDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }
}