package com.calibraflow.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "datafixer")
@Data
public class DataFixerConfig {
    private Input input;
    private Output output;
    private Processing processing;

    @Data
    public static class Input {
        private String directory;
        private String filename;
    }

    @Data
    public static class Output {
        private String directory;
        private String filename;
    }

    @Data
    public static class Processing {
        private int skipLines;
    }
}