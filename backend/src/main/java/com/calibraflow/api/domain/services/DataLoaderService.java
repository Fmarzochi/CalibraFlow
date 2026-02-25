package com.calibraflow.api.domain.services;

import com.calibraflow.api.config.DataFixerConfig;
import com.calibraflow.api.domain.entities.*;
import com.calibraflow.api.domain.entities.enums.InstrumentStatus;
import com.calibraflow.api.domain.repositories.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataLoaderService {

    private final DataFixerConfig config;
    private final TenantRepository tenantRepository;
    private final CategoryRepository categoryRepository;
    private final PeriodicityRepository periodicityRepository;
    private final LocationRepository locationRepository;
    private final InstrumentRepository instrumentRepository;
    private final CalibrationRepository calibrationRepository;

    @Transactional
    public void loadData() {
        Path inputPath = Paths.get(config.getOutput().getDirectory(), config.getOutput().getFilename());

        if (!Files.exists(inputPath)) {
            throw new RuntimeException("Arquivo não encontrado.");
        }

        Tenant tenant1 = tenantRepository.findById(1L).orElseThrow();
        Tenant tenant2 = tenantRepository.findById(2L).orElseThrow();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Location> lCache = new HashMap<>();
        Map<String, Category> cCache = new HashMap<>();
        Map<String, Periodicity> pCache = new HashMap<>();
        Map<String, Instrument> iCache = new HashMap<>();

        try (CSVReader reader = new CSVReaderBuilder(Files.newBufferedReader(inputPath)).build()) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length < 11) continue;

                String catName = line[0].trim();
                String spec = line[1].trim();
                String man = line[2].trim();
                String tag = line[4].trim();
                String serial = line[5].trim();
                String model = line[6].trim();
                String cert = line[8].trim();
                String d1S = line[9].trim();
                String d2S = line[10].trim();
                String locName = line[11].trim();

                // Processa Empresa 1 (Completo)
                saveData(tenant1, tag, catName, locName, spec, man, serial, model, cert, d1S, d2S, lCache, cCache, pCache, iCache, true, dateFormatter);
                // Processa Empresa 2 (Engenharia)
                saveData(tenant2, tag, catName, locName, spec, man, serial, model, cert, d1S, d2S, lCache, cCache, pCache, iCache, false, dateFormatter);
            }
        } catch (IOException | CsvValidationException e) {
            log.error("Erro no ETL: {}", e.getMessage());
        }
    }

    private void saveData(Tenant t, String tag, String cn, String ln, String sp, String mn, String sn, String mo, String ce, String d1s, String d2s, Map<String, Location> lc, Map<String, Category> cc, Map<String, Periodicity> pc, Map<String, Instrument> ic, boolean full, DateTimeFormatter df) {

        Location loc = lc.computeIfAbsent(t.getId() + "|" + ln, k -> locationRepository.findByNameAndTenant(ln, t).orElseGet(() -> {
            Location l = new Location(); l.setTenant(t); l.setName(ln); l.setActive(true);
            return locationRepository.save(l);
        }));

        Category cat = cc.computeIfAbsent(t.getId() + "|" + cn, k -> categoryRepository.findByNameAndTenantId(cn, t.getId()).orElseGet(() -> {
            Category c = new Category(); c.setTenant(t); c.setName(cn);
            return categoryRepository.save(c);
        }));

        Periodicity per = null;
        LocalDate d1 = null;
        LocalDate d2 = null;
        if (full && !d1s.isEmpty() && !d2s.isEmpty()) {
            try {
                d1 = LocalDate.parse(d1s, df);
                d2 = LocalDate.parse(d2s, df);
                int days = (int) ChronoUnit.DAYS.between(d1, d2);
                if (days > 0) {
                    per = pc.computeIfAbsent(t.getId() + "|" + cat.getId() + "|" + days, k -> periodicityRepository.findByTenantIdAndCategoryIdAndDays(t.getId(), cat.getId(), days).orElseGet(() -> {
                        Periodicity p = new Periodicity(); p.setTenant(t); p.setCategory(cat); p.setDays(days);
                        return periodicityRepository.save(p);
                    }));
                }
            } catch (DateTimeParseException ignored) {}
        }

        Instrument inst = ic.computeIfAbsent(t.getId() + "|" + tag, k -> instrumentRepository.findByTagAndTenant(tag, t).orElseGet(() -> {
            Instrument i = new Instrument(); i.setTenant(t); i.setTag(tag); i.setCreatedAt(OffsetDateTime.now());
            return i;
        }));

        inst.setName(cn); inst.setTolerance(sp); inst.setManufacturer(mn); inst.setSerialNumber(sn); inst.setModel(mo);
        inst.setLocation(loc); inst.setCategory(cat); inst.setPeriodicity(per); inst.setStatus(InstrumentStatus.ATIVO); inst.setActive(true);
        instrumentRepository.save(inst);

        if (full && d1 != null) {
            Calibration c = new Calibration();
            c.setTenant(t); c.setInstrument(inst); c.setCalibrationDate(d1);
            c.setNextCalibrationDate(d2); c.setCertificateNumber(ce); c.setApproved(true);
            calibrationRepository.save(c);
        }
    }
}