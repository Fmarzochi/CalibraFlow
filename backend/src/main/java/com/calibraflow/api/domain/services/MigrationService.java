package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.entities.*;
import com.calibraflow.api.domain.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MigrationService {

    private final InstrumentRepository instrumentRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final CalibrationRepository calibrationRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transactional
    public void importFromCsv(List<String[]> rows) {
        log.info(">>> MIGRATION: Iniciando processamento de {} linhas...", rows.size());

        if (rows.size() <= 1) {
            log.warn(">>> MIGRATION: O arquivo CSV parece estar vazio ou apenas com cabeçalho.");
            return;
        }

        int count = 0;
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            try {
                String patrimonyId = row[0].trim(); // TAG
                String name = row[1].trim();        // DESCRIÇÃO
                String serialNumber = row[2].trim(); // SÉRIE
                String locationName = row[3].trim(); // LOCALIZAÇÃO

                // Como não há coluna de categoria, usamos "Geral" ou tentamos inferir
                String categoryName = "Geral";
                if (name.toLowerCase().contains("manometro")) categoryName = "Pressão";
                if (name.toLowerCase().contains("termometro")) categoryName = "Temperatura";

                final String finalCategoryName = categoryName;
                Category category = categoryRepository.findByName(finalCategoryName)
                        .orElseGet(() -> categoryRepository.save(new Category(null, finalCategoryName, 365, "Auto-gerado")));

                Location location = locationRepository.findByName(locationName)
                        .orElseGet(() -> locationRepository.save(new Location(null, locationName, "Auto-gerado", true)));

                Instrument instrument = instrumentRepository.findByPatrimonyId(patrimonyId)
                        .orElseGet(() -> {
                            Instrument newIns = new Instrument();
                            newIns.setPatrimonyId(patrimonyId);
                            newIns.setName(name);
                            newIns.setSerialNumber(serialNumber);
                            newIns.setCategory(category);
                            newIns.setLocation(location);
                            newIns.setActive(true);
                            return instrumentRepository.save(newIns);
                        });

                if (row.length >= 7 && !row[4].isEmpty() && !row[6].isEmpty()) {
                    processCalibration(instrument, row[4], row[5], row[6]);
                }

                count++;
            } catch (Exception e) {
                log.error(">>> MIGRATION: Erro na linha {}: {}", i, e.getMessage());
            }
        }
        log.info(">>> MIGRATION: Sucesso! {} instrumentos processados.", count);
    }

    private void processCalibration(Instrument instrument, String dateStr, String expiryStr, String certNumber) {
        try {
            LocalDate date = LocalDate.parse(dateStr, formatter);
            LocalDate expiry = LocalDate.parse(expiryStr, formatter);

            if (!calibrationRepository.existsByInstrumentAndCertificateNumber(instrument, certNumber)) {
                Calibration cal = new Calibration();
                cal.setInstrument(instrument);
                cal.setCalibrationDate(date);
                cal.setNextCalibrationDate(expiry);
                cal.setCertificateNumber(certNumber);
                cal.setLaboratory("Importado");
                calibrationRepository.save(cal);
            }
        } catch (Exception e) {
            log.warn(">>> MIGRATION: Falha na calibração do instrumento {}: {}", instrument.getPatrimonyId(), e.getMessage());
        }
    }
}