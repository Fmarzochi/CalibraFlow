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
    private final PeriodicityRepository periodicityRepository;

    @Transactional
    public void importFromCsv(List<String[]> rows) {
        log.info(">>> MIGRATION: Iniciando importação de {} instrumentos...", rows.size());

        for (String[] row : rows) {
            try {
                if (row.length < 12) continue;

                String description = row[0].trim();
                String patrimonyCode = row[3].trim();
                String tag = row[4].trim();
                String serialNumber = row[5].trim();
                String lab = row[7].trim();
                String certNumber = row[8].trim();
                String calDateStr = row[9].trim();
                String locationName = row[11].trim();

                if (instrumentRepository.existsByPatrimonyCode(patrimonyCode)) {
                    continue;
                }

                Integer intervalDays = periodicityRepository.findByInstrumentName(description)
                        .map(Periodicity::getDays)
                        .orElse(365);

                Category category = findOrCreateCategory(description, intervalDays);
                Location location = findOrCreateLocation(locationName);

                Instrument instrument = new Instrument();
                instrument.setName(description);
                instrument.setSerialNumber(serialNumber);
                instrument.setCategory(category);
                instrument.setLocation(location);
                instrument.setActive(true);

                Patrimony patrimony = new Patrimony();
                patrimony.setPatrimonyCode(patrimonyCode);
                patrimony.setTag(tag);
                instrument.setPatrimony(patrimony);

                instrumentRepository.save(instrument);

                if (!calDateStr.isEmpty() && !certNumber.equalsIgnoreCase("N/C") && !certNumber.isEmpty()) {
                    createCalibration(instrument, calDateStr, certNumber, lab, intervalDays);
                }

            } catch (Exception e) {
                log.error(">>> MIGRATION: Erro ao processar instrumento {}: {}", row[3], e.getMessage());
            }
        }
        log.info(">>> MIGRATION: Importação concluída com sucesso!");
    }

    private Category findOrCreateCategory(String name, Integer days) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> {
                    Category c = new Category();
                    c.setName(name);
                    c.setDescription("Categoria importada automaticamente");
                    c.setCalibrationIntervalDays(days);
                    return categoryRepository.save(c);
                });
    }

    private Location findOrCreateLocation(String name) {
        return locationRepository.findByName(name)
                .orElseGet(() -> {
                    Location l = new Location();
                    l.setName(name);
                    l.setDescription("Local importado automaticamente");
                    l.setActive(true);
                    return locationRepository.save(l);
                });
    }

    private void createCalibration(Instrument instrument, String dateStr, String cert, String lab, Integer days) {
        LocalDate calDate = LocalDate.parse(dateStr);
        Calibration cal = new Calibration();
        cal.setInstrument(instrument);
        cal.setLaboratory(lab);
        cal.setCertificateNumber(cert);
        cal.setCalibrationDate(calDate);
        cal.setNextCalibrationDate(calDate.plusDays(days));
        calibrationRepository.save(cal);
    }
}