package com.calibraflow.api.domain.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendTestEmail(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nao-responda@calibraflow.com");
        message.setTo(to);
        message.setSubject("Teste de Integração SMTP - CalibraFlow");
        message.setText("Olá!\n\nSe você está lendo esta mensagem, significa que o seu backend Spring Boot conectou com sucesso ao servidor SMTP do Gmail usando a senha de aplicativo.\n\nA infraestrutura está pronta para os alertas de calibração!\n\nSistema CalibraFlow");

        mailSender.send(message);
    }
}