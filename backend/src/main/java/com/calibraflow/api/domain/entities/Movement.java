package com.calibraflow.api.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime movementDate;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @ManyToOne
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    @ManyToOne
    @JoinColumn(name = "origin_id")
    private Location origin;

    @ManyToOne
    @JoinColumn(name = "destination_id", nullable = false)
    private Location destination;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User movedBy;

    @PrePersist
    public void prePersist() {
        if (this.movementDate == null) {
            this.movementDate = LocalDateTime.now();
        }
    }
}