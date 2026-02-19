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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            ClassPathResource resource = new ClassPathResource("instruments.csv");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                int lineNumber = 0;

                while ((line = br.readLine()) != null) {
                    lineNumber++;
                    if (lineNumber <= 5) {
                        continue;
                    }

                    String[] data = line.split(",");
                    if (data.length >= 10) {
                        String description = data[0].trim();
                        String manufacturer = data[2].trim(); // Coluna Fabricante
                        String patrimonyCode = data[3].trim();
                        String tag = data[4].trim();
                        String serialOriginal = data[5].trim();
                        String model = data[6].trim(); // Coluna Modelo
                        String laboratory = data[7].trim(); // Coluna Laboratório
                        String certificate = data[8].trim();
                        String calibDateStr = data[9].trim();
                        String nextCalibDateStr = data.length > 10 ? data[10].trim() : "";
                        String locationName = data.length > 11 ? data[11].trim() : "Não Informado";
                        String tempSerial;
                        if (serialOriginal.equalsIgnoreCase("N/C") || serialOriginal.equalsIgnoreCase("S/N") || serialOriginal.isEmpty()) {
                            tempSerial = "GENERIC-" + patrimonyCode;
                        } else {
                            tempSerial = serialOriginal;
                        }

                        String serialForLambda = tempSerial;

                        Patrimony patrimony = patrimonyRepository.findByPatrimonyCode(patrimonyCode)
                                .orElseGet(() -> {
                                    Patrimony newP = new Patrimony();
                                    newP.setPatrimonyCode(patrimonyCode);
                                    newP.setTag(tag.isEmpty() ? patrimonyCode : tag);
                                    return patrimonyRepository.save(newP);
                                });

                        Location location = locationRepository.findByName(locationName)
                                .orElseGet(() -> locationRepository.save(new Location(null, locationName, "Importado", true)));

                        Category category = categoryRepository.findAll().stream().findFirst().orElse(null);

                        Instrument instrument = instrumentRepository.findBySerialNumber(serialForLambda)
                                .orElseGet(() -> {
                                    Instrument newI = new Instrument();
                                    newI.setName(description);
                                    newI.setManufacturer(manufacturer);
                                    newI.setModel(model);
                                    newI.setSerialNumber(serialForLambda); // Uso permitido aqui
                                    newI.setPatrimony(patrimony);
                                    newI.setLocation(location);
                                    newI.setCategory(category);
                                    newI.setActive(true);
                                    return instrumentRepository.save(newI);
                                });

                        if (!calibDateStr.isEmpty() && !calibDateStr.equals("N/C")) {
                            try {
                                LocalDate calDate = LocalDate.parse(calibDateStr, formatter);
                                LocalDate nextCalDate = null;
                                if (!nextCalibDateStr.isEmpty() && !nextCalibDateStr.equals("N/C")) {
                                    nextCalDate = LocalDate.parse(nextCalibDateStr, formatter);
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
                            } catch (DateTimeParseException ignored) { }
                        }
                    }
                }
                System.out.println(">>> CalibraFlow: Migração da Planilha Real finalizada com sucesso!");
            }
        } catch (Exception e) {
            System.out.println("Erro na migração: " + e.getMessage());
        }
    }
}