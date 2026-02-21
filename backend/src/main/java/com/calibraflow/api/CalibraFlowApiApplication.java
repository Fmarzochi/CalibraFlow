package com.calibraflow.api;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.File;

@SpringBootApplication
public class CalibraFlowApiApplication {
    public static void main(String[] args) {
        String path = new File("./.env").exists() ? "./" : "./backend/";
        Dotenv dotenv = Dotenv.configure()
                .directory(path)
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );

        SpringApplication.run(CalibraFlowApiApplication.class, args);
    }
}