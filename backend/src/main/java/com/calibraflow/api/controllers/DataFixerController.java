package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.services.DataFixerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/datafixer")
public class DataFixerController {

    @Autowired
    private DataFixerService dataFixerService;

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processData() {
        Path result = dataFixerService.processData();

        Map<String, Object> response = new HashMap<>();
        if (result != null) {
            response.put("success", true);
            response.put("message", "Arquivo processado com sucesso");
            response.put("outputPath", result.toString());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Falha no processamento do arquivo");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok(dataFixerService.getStatus());
    }
}