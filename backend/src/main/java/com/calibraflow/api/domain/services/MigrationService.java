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
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (String[] columns : rows) {
            if (columns.length < 12 ||
                    columns[4].equalsIgnoreCase("TAG") ||
                    columns[4].trim().isEmpty()) {
                continue;
            }

            String tag = columns[4].trim();

            try {
                String name = columns[0].trim();        // Coluna 0: Descrição
                String serial = columns[5].trim();      // Coluna 5: Número de Série
                String cert = columns[8].trim();        // Coluna 8: Certificado
                String dataCalibStr = columns[9].trim(); // Coluna 9: Data Calibração
                String dataVencStr = columns[10].trim(); // Coluna 10: Data Vencimento
                String localName = columns[11].trim();  // Coluna 11: Obra/Local

                Location location = locationRepository.findByName(localName)
                        .orElseGet(() -> locationRepository.save(Location.builder().name(localName).build()));

                Category category = findCategoryByDescription(name);

                Instrument instrument = instrumentRepository.findByPatrimonyId(tag)
                        .orElseGet(() -> instrumentRepository.save(Instrument.builder()
                                .patrimonyId(tag)
                                .name(name)
                                .serialNumber(serial)
                                .location(location)
                                .category(category)
                                .active(true)
                                .build()));

                if (dataCalibStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    if (!calibrationRepository.existsByInstrumentAndCertificateNumber(instrument, cert)) {
                        Calibration calib = new Calibration();
                        calib.setInstrument(instrument);
                        calib.setCalibrationDate(LocalDate.parse(dataCalibStr, dtf));
                        calib.setNextCalibrationDate(LocalDate.parse(dataVencStr, dtf));
                        calib.setCertificateNumber(cert);
                        calib.setLaboratory(columns[7].trim()); // Coluna 7: Laboratório
                        calibrationRepository.save(calib);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro no registro " + tag + ": " + e.getMessage());
            }
        }
    }

    private Category findCategoryByDescription(String name) {
        String upper = name.toUpperCase();
        if (upper.contains("AMPERIMETRO") || upper.contains("MULTIMETRO")) return categoryRepository.findByName("Elétrica").orElse(null);
        if (upper.contains("MANÔMETRO")) return categoryRepository.findByName("Pressão").orElse(null);
        if (upper.contains("TERMÔMETRO")) return categoryRepository.findByName("Temperatura").orElse(null);
        return categoryRepository.findByName("Dimensional").orElse(null);
    }
}