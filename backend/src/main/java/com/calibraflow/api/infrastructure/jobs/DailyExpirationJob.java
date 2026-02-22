package com.calibraflow.api.infrastructure.jobs;

import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.entities.InstrumentStatusHistory;
import com.calibraflow.api.domain.entities.Tenant;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.entities.enums.InstrumentStatus;
import com.calibraflow.api.domain.entities.enums.UserRole;
import com.calibraflow.api.domain.services.EmailService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyExpirationJob {

    @PersistenceContext
    private EntityManager entityManager;

    private final EmailService emailService;

    @Scheduled(cron = "0 1 0 * * *", zone = "America/Sao_Paulo")
    @Transactional
    public void processarVencimentosDiarios() {
        log.info("Iniciando varredura automatica de vencimentos (00:01 BRT)...");

        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

        String queryStr = "SELECT c.instrument FROM Calibration c " +
                "JOIN c.instrument i " +
                "WHERE i.status = 'ATIVO' AND c.nextCalibrationDate < :hoje";

        List<Instrument> expirados = entityManager.createQuery(queryStr, Instrument.class)
                .setParameter("hoje", hoje)
                .getResultList();

        if (expirados.isEmpty()) {
            log.info("Nenhum instrumento venceu na data de hoje.");
            return;
        }

        Map<Tenant, List<Instrument>> porEmpresa = expirados.stream()
                .collect(Collectors.groupingBy(Instrument::getTenant));

        for (Map.Entry<Tenant, List<Instrument>> entry : porEmpresa.entrySet()) {
            Tenant tenant = entry.getKey();
            List<Instrument> instrumentos = entry.getValue();

            StringBuilder mensagem = new StringBuilder();
            mensagem.append("ALERTA CRITICO: Os instrumentos abaixo VENCERAM e foram bloqueados pelo sistema.\n");
            mensagem.append("O uso na fabrica esta EXPRESSAMENTE PROIBIDO a partir de hoje.\n\n");

            for (Instrument inst : instrumentos) {
                inst.setStatus(InstrumentStatus.VENCIDO);
                entityManager.merge(inst);

                InstrumentStatusHistory history = new InstrumentStatusHistory();
                history.setInstrument(inst);
                history.setTenant(tenant);
                history.setPreviousStatus(InstrumentStatus.ATIVO);
                history.setNewStatus(InstrumentStatus.VENCIDO);
                history.setResponsibleId(0L);
                history.setResponsibleFullName("SISTEMA AUTOMATICO");
                history.setResponsibleCpf("00000000000");
                history.setSourceIp("127.0.0.1");
                history.setJustification("Bloqueio automatico por vencimento de prazo de calibracao.");
                entityManager.persist(history);

                mensagem.append("TAG: ").append(inst.getTag())
                        .append(" | Nome: ").append(inst.getName())
                        .append("\n");
            }

            mensagem.append("\nAcesse o painel do CalibraFlow imediatamente para providenciar a calibracao e o desbloqueio.");

            List<User> usuarios = entityManager.createQuery(
                            "SELECT u FROM User u WHERE u.tenant.id = :tenantId AND u.enabled = true", User.class)
                    .setParameter("tenantId", tenant.getId())
                    .getResultList();

            for (User usuario : usuarios) {
                if (usuario.getRole() == UserRole.ADMINISTRADOR || usuario.getRole() == UserRole.USUARIO) {
                    try {
                        emailService.enviarEmail(
                                usuario.getEmail(),
                                "[URGENTE] CalibraFlow - Instrumentos Vencidos e Bloqueados",
                                mensagem.toString()
                        );
                        log.info("Alerta de bloqueio enviado para a empresa {} | Usuario: {}", tenant.getName(), usuario.getEmail());
                    } catch (Exception e) {
                        log.error("Falha ao enviar e-mail de bloqueio para {}: {}", usuario.getEmail(), e.getMessage());
                    }
                }
            }
        }
    }
}