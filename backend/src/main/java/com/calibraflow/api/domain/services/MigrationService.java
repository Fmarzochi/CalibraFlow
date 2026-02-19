package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.entities.*;
import com.calibraflow.api.domain.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class MigrationService {

    private final InstrumentRepository instrumentRepository;
    private final PatrimonyRepository patrimonyRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    public MigrationService(InstrumentRepository instrumentRepository,
                            PatrimonyRepository patrimonyRepository,
                            CategoryRepository categoryRepository,
                            LocationRepository locationRepository) {
        this.instrumentRepository = instrumentRepository;
        this.patrimonyRepository = patrimonyRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
    }

    @Transactional
    public void importFromCsv(List<String[]> csvData) {
        // Pula o cabeçalho se necessário ou itera sobre os dados
        for (String[] row : csvData) {
            if (row.length < 4) continue;

            String patrimonyCode = row[0].trim();
            String instrumentName = row[1].trim();
            String serial = row[2].trim();
            String locationName = row[3].trim();

            Category category = categoryRepository.findAll().stream().findFirst().orElse(null);

            Location location = locationRepository.findByName(locationName)
                    .orElseGet(() -> locationRepository.save(new Location(null, locationName, "Importado", true)));

            saveInstrument(patrimonyCode, instrumentName, serial, category, location);
        }
    }

    private void saveInstrument(String patrimonyCode, String instrumentName, String serial,
                                Category category, Location location) {

        Patrimony patrimony = patrimonyRepository.findByPatrimonyCode(patrimonyCode)
                .orElseGet(() -> {
                    Patrimony newPatrimony = new Patrimony();
                    newPatrimony.setPatrimonyCode(patrimonyCode);
                    newPatrimony.setTag(patrimonyCode);
                    return patrimonyRepository.save(newPatrimony);
                });

        Instrument instrument = new Instrument();
        instrument.setName(instrumentName);
        instrument.setSerialNumber(serial);
        instrument.setCategory(category);
        instrument.setLocation(location);
        instrument.setPatrimony(patrimony);
        instrument.setActive(true);

        instrumentRepository.save(instrument);
    }
}