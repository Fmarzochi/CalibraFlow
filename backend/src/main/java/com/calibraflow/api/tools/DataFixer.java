package com.calibraflow.api.tools;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DataFixer {

    @Value("${datafixer.input.filename:instrumentos_completo.csv}")
    private String inputFileName;

    @Value("${datafixer.output.filename:instrumentos_limpos.xlsx}")
    private String outputFileName;

    @Value("${datafixer.processing.skip-lines:5}")
    private int skipLines;

    public Path process() {
        log.info("=== CalibraFlow Data Engineering: Iniciando Tratamento com Apache POI ===");

        try {
            Path root = Paths.get("").toAbsolutePath();
            Path inputPath = findFilePath(root, inputFileName);

            if (inputPath == null) {
                log.error("ERRO CRÍTICO: Não foi possível localizar {}", inputFileName);
                return null;
            }

            Path outputPath = inputPath.getParent().resolve(outputFileName);
            log.info("Lendo arquivo de: {}", inputPath);

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Dados_Tratados");

                List<String[]> rows = readRawCsv(inputPath.toFile(), skipLines);

                int rowIdx = 0;
                for (String[] columns : rows) {
                    Row row = sheet.createRow(rowIdx++);
                    for (int colIdx = 0; colIdx < columns.length; colIdx++) {
                        String rawValue = columns[colIdx].trim();

                        if ((colIdx == 9 || colIdx == 10) && isGarbage(rawValue)) {
                            row.createCell(colIdx).setCellValue("");
                        } else {
                            row.createCell(colIdx).setCellValue(rawValue);
                        }
                    }
                }

                try (FileOutputStream fos = new FileOutputStream(outputPath.toFile())) {
                    workbook.write(fos);
                }

                log.info("=== SUCESSO ===");
                log.info("Arquivo gerado: {}", outputPath.toAbsolutePath());
                log.info("Total de registros limpos: {}", (rowIdx - 1));

                return outputPath;
            }

        } catch (Exception e) {
            log.error("FALHA NA OPERAÇÃO: {}", e.getMessage(), e);
            return null;
        }
    }

    private boolean isGarbage(String val) {
        if (val == null || val.isEmpty()) return true;
        String v = val.toUpperCase();
        return v.contains("INDETERMINADO") || v.contains("DESATIVADO") || v.equals("N/C");
    }

    private Path findFilePath(Path start, String name) throws IOException {
        return Files.walk(start)
                .filter(p -> p.getFileName().toString().equals(name))
                .findFirst()
                .orElse(null);
    }

    private List<String[]> readRawCsv(File file, int skip) throws IOException {
        List<String[]> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (count < skip) {
                    count++;
                    continue;
                }
                list.add(line.split(",", -1));
            }
        }
        return list;
    }
}