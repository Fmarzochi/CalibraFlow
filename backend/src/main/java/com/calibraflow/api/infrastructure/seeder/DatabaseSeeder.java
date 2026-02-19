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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
                    if (data.length >= 7) {
                        String tag = data[0].trim();
                        String description = data[1].trim();
                        String serial = data[2].trim();
                        String locationName = data[3].trim();
                        String calibDateStr = data[4].trim();
                        String nextCalibDateStr = data[5].trim();
                        String certificate = data[6].trim();

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

                        Instrument instrument = instrumentRepository.findBySerialNumber(serial)
                                .orElseGet(() -> {
                                    Instrument newI = new Instrument();
                                    newI.setName(description);
                                    newI.setSerialNumber(serial);
                                    newI.setPatrimony(patrimony);
                                    newI.setLocation(location);
                                    newI.setCategory(category);
                                    newI.setActive(true);
                                    return instrumentRepository.save(newI);
                                });

                        if (!calibrationRepository.existsByInstrumentAndCertificateNumber(instrument, certificate)) {
                            Calibration calibration = new Calibration();
                            calibration.setInstrument(instrument);
                            calibration.setCalibrationDate(LocalDate.parse(calibDateStr, formatter));
                            calibration.setNextCalibrationDate(LocalDate.parse(nextCalibDateStr, formatter));
                            calibration.setCertificateNumber(certificate);
                            calibration.setLaboratory("Laboratório Externo (Migração)");

                            calibrationRepository.save(calibration);
                        }
                    }
                }
                System.out.println(">>> CalibraFlow: Importação total concluída!");
            }
        } catch (Exception e) {
            System.out.println("Erro na carga completa: " + e.getMessage());
        }
    }
}