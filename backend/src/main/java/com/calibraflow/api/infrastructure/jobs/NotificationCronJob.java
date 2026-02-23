package com.calibraflow.api.infrastructure.jobs;

import com.calibraflow.api.domain.dtos.UpcomingCalibrationDTO;
import com.calibraflow.api.domain.services.CalibrationService;
import com.calibraflow.api.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationCronJob {

    private final CalibrationService calibrationService;
    private final UserService userService;

    @Scheduled(cron = "0 0 8 * * *")
    public void checkUpcomingCalibrations() {
        log.info("Iniciando rotina de verificacao de calibracoes a vencer...");
        LocalDate today = LocalDate.now();
        LocalDate nextMonth = today.plusDays(30);

        List<UpcomingCalibrationDTO> upcoming = calibrationService.findUpcomingCalibrations(today, nextMonth);
        var users = userService.findAll(Pageable.unpaged());

        for (UpcomingCalibrationDTO cal : upcoming) {
            log.info("Alerta: Instrumento {} ({}) vence em {}", cal.instrumentTag(), cal.instrumentName(), cal.nextCalibrationDate());
        }
    }
}