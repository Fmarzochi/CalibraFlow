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
        for (String[] row : rows) {
            try {
                if (row.length < 10) continue;

                String instrumentName = row[0].trim();
                String patrimonyCode = row[3].trim();
                String serial = row[5].trim();
                String cert = row[8].trim();
                String dateStr = row[9].trim();
                String locName = row[11].trim();

                if (instrumentRepository.existsByPatrimonyCode(patrimonyCode)) continue;

                Integer days = periodicityRepository.findByInstrumentName(instrumentName)
                        .map(Periodicity::getDays)
                        .orElse(365);

                Category category = findOrCreateCategory(instrumentName, days);
                Location location = findOrCreateLocation(locName);

                Patrimony patrimony = new Patrimony();
                patrimony.setPatrimonyCode(patrimonyCode);
                patrimony.setTag(patrimonyCode);

                Instrument instrument = new Instrument();
                instrument.setName(instrumentName);
                instrument.setSerialNumber(serial);
                instrument.setCategory(category);
                instrument.setLocation(location);
                instrument.setPatrimony(patrimony);
                instrument.setActive(true);

                instrumentRepository.save(instrument);

                if (!cert.isEmpty() && !cert.equalsIgnoreCase("N/C") && !dateStr.isEmpty()) {
                    createCalibration(instrument, cert, dateStr, days);
                }

            } catch (Exception e) {
                log.error("Erro no registro: " + e.getMessage());
            }
        }
    }

    private Category findOrCreateCategory(String name, Integer days) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> {
                    Category c = new Category();
                    c.setName(name);
                    c.setCalibrationIntervalDays(days);
                    return categoryRepository.save(c);
                });
    }

    private Location findOrCreateLocation(String name) {
        return locationRepository.findByName(name)
                .orElseGet(() -> {
                    Location l = new Location();
                    l.setName(name);
                    l.setActive(true);
                    return locationRepository.save(l);
                });
    }

    private void createCalibration(Instrument instrument, String cert, String date, Integer days) {
        LocalDate calDate = LocalDate.parse(date);
        Calibration cal = new Calibration();
        cal.setInstrument(instrument);
        cal.setCertificateNumber(cert);
        cal.setLaboratory("Importacao");
        cal.setCalibrationDate(calDate);
        cal.setNextCalibrationDate(calDate.plusDays(days));
        calibrationRepository.save(cal);
    }
}