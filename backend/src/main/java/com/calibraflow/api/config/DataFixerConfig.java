package com.calibraflow.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "datafixer")
public class DataFixerConfig {
    private Input input;
    private Output output;
    private Processing processing;

    @Data
    public static class Input {
        private String filename;
        private String directory;
    }

    @Data
    public static class Output {
        private String filename;
        private String directory;
    }

    @Data
    public static class Processing {
        private int skipLines;
    }
}
