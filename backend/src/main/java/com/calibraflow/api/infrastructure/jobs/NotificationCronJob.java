package com.calibraflow.api.infrastructure.jobs;

import com.calibraflow.api.domain.entities.Calibration;
import com.calibraflow.api.domain.services.CalibrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationCronJob {

    private final CalibrationService calibrationService;

    @Scheduled(cron = "0 0 8 * * ?")
    public void notifyUpcomingCalibrations() {
        log.info("Iniciando rotina de verificação de calibrações próximas ao vencimento...");
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(30);

        List<Calibration> upcoming = calibrationService.findUpcomingCalibrations(start, end);

        for (Calibration calibration : upcoming) {
            log.info("Instrumento [{}] vence em: {}", calibration.getInstrument().getTag(), calibration.getNextCalibrationDate());
        }

        log.info("Rotina de calibrações finalizada. Total de notificações preparadas: {}", upcoming.size());
    }
}