package com.calibraflow.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "datafixer")
public class DataFixerConfig {

    private Input input = new Input();
    private Output output = new Output();
    private Processing processing = new Processing();

    public static class Input {
        private String filename;
        private String directory;

        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }
        public String getDirectory() { return directory; }
        public void setDirectory(String directory) { this.directory = directory; }
    }

    public static class Output {
        private String filename;
        private String directory;

        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }
        public String getDirectory() { return directory; }
        public void setDirectory(String directory) { this.directory = directory; }
    }

    public static class Processing {
        private int skipLines;

        public int getSkipLines() { return skipLines; }
        public void setSkipLines(int skipLines) { this.skipLines = skipLines; }
    }

    public Input getInput() { return input; }
    public void setInput(Input input) { this.input = input; }
    public Output getOutput() { return output; }
    public void setOutput(Output output) { this.output = output; }
    public Processing getProcessing() { return processing; }
    public void setProcessing(Processing processing) { this.processing = processing; }
}