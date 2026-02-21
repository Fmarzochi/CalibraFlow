package com.calibraflow.api.tools;

import com.calibraflow.config.DataFixerConfig;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final DateTimeFormatter OUTPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final DataFixerConfig config;

    public DataFixer(DataFixerConfig config) {
        this.config = config;
    }

    public Path process() {
        Path inputPath = Paths.get(config.getInput().getDirectory(), config.getInput().getFilename());
        Path outputPath = Paths.get(config.getOutput().getDirectory(), config.getOutput().getFilename());

        log.info("Iniciando processo de ETL no arquivo: {}", inputPath);

        if (!Files.exists(inputPath)) {
            log.error("Arquivo de entrada não encontrado: {}", inputPath);
            return null;
        }

        Map<String, String[]> deduplicatedRecords = new LinkedHashMap<>();
        int linesRead = 0;
        int duplicatesRemoved = 0;
        int discardedRecords = 0;

        CSVParser parser = new CSVParserBuilder()
                .withSeparator(',')
                .withIgnoreQuotations(false)
                .build();

        try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReaderBuilder(reader)
                     .withSkipLines(config.getProcessing().getSkipLines())
                     .withCSVParser(parser)
                     .build()) {

            String[] columns;
            while ((columns = csvReader.readNext()) != null) {
                linesRead++;

                for (int i = 0; i < columns.length; i++) {
                    if (columns[i] != null) {
                        columns[i] = columns[i].trim();
                    }
                }

                if (columns.length <= 3) {
                    discardedRecords++;
                    continue;
                }

                String patrimony = columns[3];

                if (patrimony == null || patrimony.isEmpty() || patrimony.equalsIgnoreCase("N/C")) {
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

        } catch (IOException e) {
            log.error("Erro durante a leitura do arquivo CSV: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Erro inesperado durante o processamento: {}", e.getMessage());
            return null;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8);
             CSVWriter csvWriter = new CSVWriter(writer)) {

            for (String[] record : deduplicatedRecords.values()) {
                csvWriter.writeNext(record);
            }

            log.info("ETL concluído com sucesso.");
            log.info("Linhas lidas: {}", linesRead);
            log.info("Registros descartados: {}", discardedRecords);
            log.info("Duplicatas removidas: {}", duplicatesRemoved);
            log.info("Linhas salvas: {}", deduplicatedRecords.size());

            return outputPath;

        } catch (IOException e) {
            log.error("Erro durante a escrita do arquivo CSV: {}", e.getMessage());
            return null;
        }
    }

    private String normalizeDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty() || dateStr.equalsIgnoreCase("INDETERMINADO") || dateStr.equalsIgnoreCase("N/C")) {
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
            if (col != null && !col.isEmpty()) {
                count++;
            }
        }
        return count;
    }
}