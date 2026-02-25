package com.calibraflow.api.infrastructure.jobs;

import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.entities.InstrumentStatusHistory;
import com.calibraflow.api.domain.entities.Tenant;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.entities.enums.InstrumentStatus;
import com.calibraflow.api.domain.entities.enums.UserRole;
import com.calibraflow.api.domain.services.EmailService;
import com.calibraflow.api.infrastructure.security.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

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

        List<Tenant> tenants = entityManager.createQuery("SELECT t FROM Tenant t WHERE t.active = true", Tenant.class).getResultList();

        for (Tenant tenant : tenants) {
            try {
                TenantContext.setCurrentTenant(tenant.getId());

                String queryStr = "SELECT c.instrument FROM Calibration c " +
                        "JOIN c.instrument i " +
                        "WHERE i.status = 'ATIVO' AND c.nextCalibrationDate < :hoje";

                List<Instrument> expirados = entityManager.createQuery(queryStr, Instrument.class)
                        .setParameter("hoje", hoje)
                        .getResultList();

                if (expirados.isEmpty()) {
                    continue;
                }

                StringBuilder mensagem = new StringBuilder();
                mensagem.append("ALERTA CRITICO: Os instrumentos abaixo VENCERAM e foram bloqueados pelo sistema.\n");
                mensagem.append("O uso na fabrica esta EXPRESSAMENTE PROIBIDO a partir de hoje.\n\n");

                for (Instrument inst : expirados) {
                    inst.setStatus(InstrumentStatus.VENCIDO);
                    entityManager.merge(inst);

                    InstrumentStatusHistory history = InstrumentStatusHistory.builder()
                            .instrument(inst)
                            .tenant(tenant)
                            .previousStatus(InstrumentStatus.ATIVO)
                            .status(InstrumentStatus.VENCIDO)
                            .responsibleId(0L)
                            .responsibleFullName("SISTEMA AUTOMATICO")
                            .responsibleCpf("00000000000")
                            .sourceIp("127.0.0.1")
                            .justification("Bloqueio automatico por vencimento de prazo de calibracao.")
                            .changedAt(OffsetDateTime.now())
                            .build();

                    entityManager.persist(history);

                    mensagem.append("TAG: ").append(inst.getTag())
                            .append(" | Nome: ").append(inst.getName())
                            .append("\n");
                }

                mensagem.append("\nAcesse o painel do CalibraFlow imediatamente para providenciar a calibracao e o desbloqueio.");

                List<User> usuarios = entityManager.createQuery(
                                "SELECT u FROM User u WHERE u.role IN (:roles) AND u.enabled = true", User.class)
                        .setParameter("roles", List.of(UserRole.ADMINISTRADOR, UserRole.USUARIO))
                        .getResultList();

                for (User usuario : usuarios) {
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

            } finally {
                TenantContext.clear();
            }
        }
    }
}