package com.calibraflow.api.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime movementDate; // Data e Hora exata da movimentação

    @Column(columnDefinition = "TEXT")
    private String reason; // Motivo (Ex: "Envio para calibração externa")

    @ManyToOne
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    @ManyToOne
    @JoinColumn(name = "origin_id")
    private Location origin; // De onde saiu (pode ser null se for novo)

    @ManyToOne
    @JoinColumn(name = "destination_id", nullable = false)
    private Location destination; // Para onde foi

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User movedBy; // Quem realizou a movimentação (Auditoria)

    @PrePersist
    public void prePersist() {
        if (this.movementDate == null) {
            this.movementDate = LocalDateTime.now();
        }
    }
}