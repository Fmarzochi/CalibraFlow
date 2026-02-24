package com.calibraflow.api.infrastructure.seeder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@Slf4j
@Profile("!test")
public class PeriodicityDataSeeder implements CommandLineRunner {

    @Override
    public void run(String... args) {
        log.info("Seeder estatico de periodicidade desativado. Os dados serao injetados via rotina de ETL.");
    }
}