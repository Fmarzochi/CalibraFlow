package com.calibraflow.api.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_calibrations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calibration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private LocalDate calibrationDate; // Data da realização física

    @Column(nullable = false)
    private LocalDate nextCalibrationDate; // Vencimento (Calculado: Data + Intervalo da Categoria)

    private String certificateUrl; // Caminho para o PDF do certificado

    @Column(columnDefinition = "TEXT")
    private String observation;

    @Builder.Default
    private Boolean active = true; // Se FALSE, foi invalidada pelo ADMIN (Regra de Auditoria)

    @Column(updatable = false)
    private LocalDateTime registeredAt; // Carimbo de tempo imutável (Quando foi digitado)

    @PrePersist
    public void prePersist() {
        this.registeredAt = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User performedBy; // Quem registrou no sistema (Rastreabilidade)
}