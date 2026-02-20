package com.calibraflow.service;

import com.calibraflow.tools.DataFixer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class DataFixerService {
    
    @Autowired
    private DataFixer dataFixer;
    
    public Path processData() {
        return dataFixer.process();
    }
    
    public String getStatus() {
        return "DataFixer está pronto para processar arquivos";
    }
}
