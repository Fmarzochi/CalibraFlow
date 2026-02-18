package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.entities.*;
import com.calibraflow.api.domain.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MigrationService {

    private final CategoryRepository categoryRepository;
    private final InstrumentRepository instrumentRepository;
    private final LocationRepository locationRepository;
    private final CalibrationRepository calibrationRepository;

    @Transactional
    public void importFromCsv(List<String[]> rows) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (String[] columns : rows) {
            if (columns.length < 7 || columns[0].equalsIgnoreCase("TAG")) continue;

            String tag = columns[0].trim();
            String desc = columns[1].trim();
            String serial = columns[2].trim();
            String localName = columns[3].trim();
            String dataCalibStr = columns[4].trim();
            String dataVencStr = columns[5].trim();
            String cert = columns[6].trim();

            Location location = locationRepository.findByName(localName)
                    .orElseGet(() -> locationRepository.save(Location.builder().name(localName).build()));

            Category category = findCategoryByDescription(desc);

            if (!instrumentRepository.existsByPatrimonyId(tag)) {
                Instrument instrument = Instrument.builder()
                        .patrimonyId(tag)
                        .name(desc)
                        .serialNumber(serial)
                        .category(category)
                        .active(true)
                        .build();

                Instrument savedInstrument = instrumentRepository.save(instrument);

                if (!dataCalibStr.isEmpty() && !dataVencStr.isEmpty()) {
                    Calibration calib = new Calibration();
                    calib.setInstrument(savedInstrument);
                    calib.setCalibrationDate(LocalDate.parse(dataCalibStr, dtf));
                    calib.setNextCalibrationDate(LocalDate.parse(dataVencStr, dtf));
                    calib.setCertificateNumber(cert);
                    calib.setLaboratory("MIGRAÇÃO PLANILHA");
                    calibrationRepository.save(calib);
                }
            }
        }
    }

    private Category findCategoryByDescription(String desc) {
        String upper = desc.toUpperCase();
        if (upper.contains("MANÔMETRO") || upper.contains("PRESSÃO")) return categoryRepository.findByName("Pressão").orElse(null);
        if (upper.contains("TERMÔMETRO") || upper.contains("TEMP")) return categoryRepository.findByName("Temperatura").orElse(null);
        return categoryRepository.findByName("Dimensional").orElse(null);
    }
}