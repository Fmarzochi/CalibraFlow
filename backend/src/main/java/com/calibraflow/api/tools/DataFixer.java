package com.calibraflow.api.tools;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class DataFixer {

    private static final Logger log = LoggerFactory.getLogger(DataFixer.class);
    private static final Path INPUT_PATH = Paths.get("src/main/resources/instrumentos_completo.csv");
    private static final Path OUTPUT_PATH = Paths.get("src/main/resources/instrumentos_limpos.csv");
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final DateTimeFormatter OUTPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Path process() {
        log.info("Iniciando processo de ETL no arquivo: {}", INPUT_PATH);

        if (!Files.exists(INPUT_PATH)) {
            log.error("Arquivo de entrada não encontrado: {}", INPUT_PATH);
            return null;
        }

        Map<String, String[]> deduplicatedRecords = new LinkedHashMap<>();
        int linesRead = 0;
        int duplicatesRemoved = 0;
        int discardedRecords = 0;

        try (BufferedReader reader = Files.newBufferedReader(INPUT_PATH, StandardCharsets.UTF_8)) {
            String line;
            int skipCount = 5;

            while (skipCount > 0 && reader.readLine() != null) {
                skipCount--;
            }

            while ((line = reader.readLine()) != null) {
                linesRead++;
                String[] columns = line.split(",", -1);

                for (int i = 0; i < columns.length; i++) {
                    columns[i] = columns[i].trim();
                }

                if (columns.length <= 3) {
                    discardedRecords++;
                    continue;
                }

                String patrimony = columns[3];

                if (patrimony.isEmpty() || patrimony.equalsIgnoreCase("N/C")) {
                    log.warn("Registro descartado: Patrimônio inválido ou vazio. Linha relativa: {}", linesRead);
                    discardedRecords++;
                    continue;
                }

                if (columns.length > 9) {
                    columns[9] = normalizeDate(columns[9]);
                }
                if (columns.length > 10) {
                    columns[10] = normalizeDate(columns[10]);
                }

                if (deduplicatedRecords.containsKey(patrimony)) {
                    String[] existing = deduplicatedRecords.get(patrimony);
                    if (countFilledColumns(columns) >= countFilledColumns(existing)) {
                        deduplicatedRecords.put(patrimony, columns);
                    }
                    duplicatesRemoved++;
                } else {
                    deduplicatedRecords.put(patrimony, columns);
                }
            }

        } catch (Exception e) {
            log.error("Erro durante a extração e transformação: {}", e.getMessage());
            return null;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(OUTPUT_PATH, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

            for (String[] record : deduplicatedRecords.values()) {
                csvPrinter.printRecord((Object[]) record);
            }

            log.info("ETL concluído com sucesso.");
            log.info("Linhas lidas: {}", linesRead);
            log.info("Registros descartados: {}", discardedRecords);
            log.info("Duplicatas removidas: {}", duplicatesRemoved);
            log.info("Linhas salvas: {}", deduplicatedRecords.size());

            return OUTPUT_PATH;

        } catch (Exception e) {
            log.error("Erro durante o carregamento: {}", e.getMessage());
            return null;
        }
    }

    private String normalizeDate(String dateStr) {
        if (dateStr.isEmpty() || dateStr.equalsIgnoreCase("INDETERMINADO") || dateStr.equalsIgnoreCase("N/C")) {
            return "";
        }
        try {
            LocalDate date = LocalDate.parse(dateStr, INPUT_DATE_FORMAT);
            return date.format(OUTPUT_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            return "";
        }
    }

    private int countFilledColumns(String[] columns) {
        int count = 0;
        for (String col : columns) {
            if (!col.isEmpty()) {
                count++;
            }
        }
        return count;
    }
}