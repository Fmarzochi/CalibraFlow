package com.calibraflow.api.infrastructure.jobs;

import com.calibraflow.api.domain.entities.Calibration;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.services.CalibrationService;
import com.calibraflow.api.domain.services.EmailService;
import com.calibraflow.api.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationCronJob {

    private final CalibrationService calibrationService;
    private final EmailService emailService;
    private final UserService userService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Scheduled(cron = "0 0 8 * * MON")
    public void alertarVencimentosSemanais() {
        log.info("Iniciando varredura semanal de vencimentos (Padrao BR)...");

        LocalDate hoje = LocalDate.now();
        LocalDate daquiA30Dias = hoje.plusDays(30);

        List<Calibration> vencimentos = calibrationService.findUpcomingCalibrations(hoje, daquiA30Dias);

        if (vencimentos.isEmpty()) {
            log.info("Nenhum instrumento a vencer nos proximos 30 dias.");
            return;
        }

        StringBuilder mensagem = new StringBuilder();
        mensagem.append("Ola! O CalibraFlow identificou instrumentos que exigem sua atencao.\n");
        mensagem.append("Segue o resumo de instrumentos vencendo nos proximos 30 dias:\n\n");

        for (Calibration cal : vencimentos) {
            String dataFormatada = cal.getNextCalibrationDate().format(formatter);
            mensagem.append("TAG: ").append(cal.getInstrument().getTag())
                    .append(" | Nome: ").append(cal.getInstrument().getName())
                    .append(" | Vence em: ").append(dataFormatada)
                    .append("\n");
        }

        mensagem.append("\nAcesse o painel do CalibraFlow para agendar as Calibracões.");

        List<User> usuarios = userService.findAll();
        for (User usuario : usuarios) {
            if (usuario.isEnabled()) {
                try {
                    emailService.enviarEmail(
                            usuario.getUsername(),
                            "CalibraFlow - Alerta de Vencimento de Instrumentos",
                            mensagem.toString()
                    );
                    log.info("Alerta enviado para: {}", usuario.getUsername());
                } catch (Exception e) {
                    log.error("Falha ao enviar e-mail para {}: {}", usuario.getUsername(), e.getMessage());
                }
            }
        }
    }
}