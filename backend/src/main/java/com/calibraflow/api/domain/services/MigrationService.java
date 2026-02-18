package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.entities.*;
import com.calibraflow.api.domain.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MigrationService {

    private final InstrumentRepository instrumentRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

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
                // Mapeamento baseado no seu CSV (Patrimônio, Nome, Categoria, Local, Serial)
                String patrimonyId = row[0].trim();
                String name = row[1].trim();
                String categoryName = row[2].trim();
                String locationName = row[3].trim();

                Category category = categoryRepository.findByName(categoryName)
                        .orElseGet(() -> categoryRepository.save(new Category(null, categoryName, 365, "Auto-gerado")));

                Location location = locationRepository.findByName(locationName)
                        .orElseGet(() -> locationRepository.save(new Location(null, locationName, "Auto-gerado", true)));

                if (!instrumentRepository.findByPatrimonyId(patrimonyId).isPresent()) {
                    Instrument ins = new Instrument();
                    ins.setPatrimonyId(patrimonyId);
                    ins.setName(name);
                    ins.setCategory(category);
                    ins.setLocation(location);
                    ins.setActive(true);
                    instrumentRepository.save(ins);
                    count++;
                }
            } catch (Exception e) {
                log.error(">>> MIGRATION: Erro na linha {}: {}", i, e.getMessage());
            }
        }
        log.info(">>> MIGRATION: Sucesso! {} novos instrumentos cadastrados.", count);
    }
}