package com.calibraflow.api.infrastructure.seeder;

import com.calibraflow.api.domain.entities.*;
import com.calibraflow.api.domain.repositories.*;
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

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

        try (CSVReader reader = new CSVReaderBuilder(
                new InputStreamReader(new ClassPathResource("instruments.csv").getInputStream(), StandardCharsets.UTF_8))
                .withSkipLines(5)
                .build()) {

            String[] data;
            int importedCount = 0;

            while ((data = reader.readNext()) != null) {
                if (data.length >= 12 && !data[0].trim().isEmpty()) {
                    String descricao = data[0].trim();
                    String patrimonyCode = data[3].trim();
                    String tag = data[4].trim();
                    String serialNumber = data[5].trim();
                    String laboratory = data[7].trim();
                    String certificate = data[8].trim();
                    String calibDateStr = data[9].trim();
                    String nextCalibDateStr = data[10].trim();
                    String locationName = data[11].trim();

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

                    if (!calibDateStr.isEmpty() && !calibDateStr.equalsIgnoreCase("N/C")) {
                        try {
                            LocalDate calDate = LocalDate.parse(calibDateStr, FORMATTER);
                            LocalDate nextCalDate = calDate.plusDays(category.getCalibrationIntervalDays());

                            if (!nextCalibDateStr.isEmpty() && !nextCalibDateStr.equalsIgnoreCase("N/C")) {
                                nextCalDate = LocalDate.parse(nextCalibDateStr, FORMATTER);
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
                        } catch (DateTimeParseException ignored) {
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
}