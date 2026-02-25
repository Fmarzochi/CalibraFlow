package com.calibraflow.api.domain.services;

import com.calibraflow.api.tools.DataFixer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class DataFixerService {

    private final DataFixer dataFixer;

    public Path processData() {
        return dataFixer.process();
    }
}