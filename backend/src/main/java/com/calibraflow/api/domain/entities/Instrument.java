package com.calibraflow.api.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tb_instruments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String patrimonyId; // Código do Patrimônio (Regra: Deve ser único)

    @Column(unique = true, nullable = false)
    private String serialNumber; // Número de Série (Regra: Deve ser único)

    private LocalDate acquisitionDate;

    @Builder.Default
    private Boolean active = true; // Para Soft Delete (Inativar em vez de apagar)

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // A categoria define o intervalo de vencimento
}