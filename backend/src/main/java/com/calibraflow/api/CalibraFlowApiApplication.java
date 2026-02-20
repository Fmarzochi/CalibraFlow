package com.calibraflow.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.calibraflow")
public class CalibraFlowApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CalibraFlowApiApplication.class, args);
    }

}