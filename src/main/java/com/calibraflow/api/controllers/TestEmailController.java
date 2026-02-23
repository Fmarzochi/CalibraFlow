package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test-email")
@RequiredArgsConstructor
public class TestEmailController {

    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<String> sendTest() {
        emailService.enviarEmail(
                "calibraflow@gmail.com",
                "Teste de Integracao CalibraFlow",
                "O backend do CalibraFlow enviou um e-mail com sucesso atraves do arquivo .env!"
        );
        return ResponseEntity.ok("Comando de e-mail disparado!");
    }
}
