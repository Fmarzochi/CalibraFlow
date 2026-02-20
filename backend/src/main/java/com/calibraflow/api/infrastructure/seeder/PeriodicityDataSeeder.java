package com.calibraflow.api.infrastructure.seeder;

import com.calibraflow.api.domain.entities.Periodicity;
import com.calibraflow.api.domain.repositories.PeriodicityRepository;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class PeriodicityDataSeeder implements CommandLineRunner {

    private final PeriodicityRepository periodicityRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (periodicityRepository.count() > 0) {
            log.info("Tabela de periodicidades já populada. Ignorando seeder.");
            return;
        }

        InputStream is = getClass().getResourceAsStream("/periodicidades.csv");
        if (is == null) {
            log.error("Arquivo periodicidades.csv não encontrado no diretório resources.");
            return;
        }

        CSVParser parser = new CSVParserBuilder().withSeparator(',').build();

        try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReaderBuilder(reader)
                     .withSkipLines(3)
                     .withCSVParser(parser)
                     .build()) {

            String[] line;
            int count = 0;

            while ((line = csvReader.readNext()) != null) {
                if (line.length < 2 || line[0].trim().isEmpty()) {
                    continue;
                }

                String instrumentName = line[0].trim();
                String daysStr = line[1].trim();

                if (daysStr.equalsIgnoreCase("Indeterminado") || daysStr.isEmpty() || daysStr.equalsIgnoreCase("N/C")) {
                    continue;
                }

                if (periodicityRepository.findByInstrumentName(instrumentName).isPresent()) {
                    continue;
                }

                try {
                    int days = Integer.parseInt(daysStr);
                    Periodicity periodicity = new Periodicity();
                    periodicity.setId(UUID.randomUUID());
                    periodicity.setInstrumentName(instrumentName);
                    periodicity.setDays(days);

                    periodicityRepository.save(periodicity);
                    count++;
                } catch (NumberFormatException e) {
                    log.warn("Formato de dias inválido para o instrumento {}: {}", instrumentName, daysStr);
                }
            }
            log.info("Carga de periodicidades finalizada. {} novos registros inseridos.", count);
        }
    }
}