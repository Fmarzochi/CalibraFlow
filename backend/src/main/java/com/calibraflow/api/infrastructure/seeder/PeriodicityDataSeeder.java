package com.calibraflow.api.infrastructure.seeder;

import com.calibraflow.api.domain.entities.Category;
import com.calibraflow.api.domain.entities.Periodicity;
import com.calibraflow.api.domain.repositories.CategoryRepository;
import com.calibraflow.api.domain.repositories.PeriodicityRepository;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class PeriodicityDataSeeder implements CommandLineRunner {

    private final PeriodicityRepository periodicityRepository;
    private final CategoryRepository categoryRepository;

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
                if (line.length < 3 || line[0].trim().isEmpty()) {
                    continue;
                }

                try {
                    String instrumentName = line[0].trim();
                    String daysStr = line[2].trim();

                    if (daysStr.equalsIgnoreCase("Indeterminado")) {
                        continue;
                    }

                    Integer days = Integer.parseInt(daysStr);

                    Optional<Category> categoryOpt = categoryRepository.findByName(instrumentName);
                    Category category = categoryOpt.orElse(null);

                    Periodicity periodicity = Periodicity.builder()
                            .instrumentName(instrumentName)
                            .days(days)
                            .category(category)
                            .build();

                    periodicityRepository.save(periodicity);
                    count++;
                } catch (Exception e) {
                    log.warn("Falha ao processar linha de periodicidade: {}", e.getMessage());
                }
            }
            log.info("Carga de periodicidades finalizada. {} novos registros inseridos.", count);
        }
    }
}