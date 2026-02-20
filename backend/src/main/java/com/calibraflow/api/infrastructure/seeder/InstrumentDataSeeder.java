package com.calibraflow.api.infrastructure.seeder;

import com.calibraflow.api.domain.entities.*;
import com.calibraflow.api.domain.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Optional;

@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class InstrumentDataSeeder implements CommandLineRunner {

    private final InstrumentRepository instrumentRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final PeriodicityRepository periodicityRepository;

    @Override
    public void run(String... args) {
        if (instrumentRepository.count() > 0) {
            log.info("Instrumentos já existem no banco. Seeder ignorado.");
            return;
        }

        log.info("Iniciando seeder de Instrumentos...");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream("/instrumentos.csv")), StandardCharsets.UTF_8))) {

            String line;
            int skipLines = 5;
            while (skipLines > 0 && reader.readLine() != null) {
                skipLines--;
            }

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length < 8) continue; // Pula linhas malformadas

                String tag = data[0].trim();
                if (tag.isEmpty()) continue;

                String name = data[1].trim();
                String locationName = data[2].trim();
                String manufacturer = data[3].trim();
                String model = data[4].trim();
                String serialNumber = data[5].trim();
                String range = data[6].trim();
                String tolerance = data[7].trim();

                Category category = findOrCreateCategory(name);
                Location location = findOrCreateLocation(locationName);

                Optional<Periodicity> periodicityOpt = periodicityRepository.findByInstrumentName(name);

                Instrument instrument = Instrument.builder()
                        .tag(tag)
                        .name(name)
                        .manufacturer(manufacturer)
                        .model(model)
                        .serialNumber(serialNumber)
                        .range(range)
                        .tolerance(tolerance)
                        .category(category)
                        .location(location)
                        .periodicity(periodicityOpt.orElse(null))
                        .build();

                instrumentRepository.save(instrument);
                log.info("Instrumento salvo: {}", tag);
            }
            log.info("Seeder de Instrumentos finalizado com sucesso.");

        } catch (Exception e) {
            log.error("Erro ao executar seeder de instrumentos", e);
        }
    }

    private Category findOrCreateCategory(String name) {
        if (name == null || name.isEmpty()) return null;
        return categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.save(Category.builder().name(name).build()));
    }

    private Location findOrCreateLocation(String name) {
        if (name == null || name.isEmpty()) return null;
        return locationRepository.findByName(name)
                .orElseGet(() -> locationRepository.save(Location.builder().name(name).build()));
    }

    private LocalDate parseDateSafe(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty() ||
                dateStr.trim().equalsIgnoreCase("N/C") ||
                dateStr.trim().equalsIgnoreCase("INDETERMINADO")) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr.trim(), DateTimeFormatter.ofPattern("d/M/yyyy"));
        } catch (DateTimeParseException e) {
            log.warn("Data inválida ignorada no seeder: {}", dateStr);
            return null;
        }
    }
}