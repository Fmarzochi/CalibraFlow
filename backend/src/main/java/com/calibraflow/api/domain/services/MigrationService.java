package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.entities.*;
import com.calibraflow.api.domain.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MigrationService {

    private final InstrumentRepository instrumentRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final CalibrationRepository calibrationRepository;

    @Transactional
    public void importFromCsv(List<String[]> rows) {
        log.info(">>> MIGRATION: Iniciando importação de {} instrumentos...", rows.size());

        Category catPadrao = createCategory("Geral", "Categoria Padrão", 365);

        for (String[] row : rows) {
            try {
                String patrimonyCode = row[0];
                String name = row[1];
                String serialNumber = row[2];
                String locationName = row[3];
                String certNumber = row.length > 6 ? row[6] : "N/A";

                if (instrumentRepository.existsByPatrimonyCode(patrimonyCode)) {
                    continue;
                }

                Location location = createLocation(locationName, "Local importado");

                Patrimony patrimony = new Patrimony();
                patrimony.setCode(patrimonyCode);
                patrimony.setTag(patrimonyCode);

                Instrument instrument = new Instrument();
                instrument.setName(name);
                instrument.setSerialNumber(serialNumber);
                instrument.setPatrimony(patrimony);
                instrument.setCategory(catPadrao);
                instrument.setLocation(location);
                instrument.setActive(true);

                instrumentRepository.save(instrument);

                createInitialCalibration(instrument, certNumber);

            } catch (Exception e) {
                log.error("Erro linha: {}", e.getMessage());
            }
        }
        log.info(">>> MIGRATION: Importação concluída!");
    }

    private Category createCategory(String name, String desc, Integer days) {
        return categoryRepository.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    Category c = new Category();
                    c.setName(name);
                    c.setDescription(desc);
                    c.setCalibrationIntervalDays(days);
                    return categoryRepository.save(c);
                });
    }

    private Location createLocation(String name, String desc) {
        return locationRepository.findAll().stream()
                .filter(l -> l.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    Location l = new Location();
                    l.setName(name);
                    l.setDescription(desc);
                    l.setActive(true);
                    return locationRepository.save(l);
                });
    }

    private void createInitialCalibration(Instrument instrument, String certNumber) {
        Calibration cal = new Calibration();
        cal.setInstrument(instrument);
        cal.setLaboratory("Lab Importação");
        cal.setCertificateNumber(certNumber);
        cal.setCalibrationDate(LocalDate.now());
        cal.setNextCalibrationDate(LocalDate.now().plusDays(365));
        calibrationRepository.save(cal);
    }
}