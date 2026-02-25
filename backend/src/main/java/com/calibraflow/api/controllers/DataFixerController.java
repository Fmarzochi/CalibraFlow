package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.services.DataFixerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api/datafixer")
@RequiredArgsConstructor
public class DataFixerController {

    private final DataFixerService dataFixerService;

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> process() {
        Path outputPath = dataFixerService.processData();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Arquivo processado com sucesso",
                "outputPath", outputPath.toString()
        ));
    }
}