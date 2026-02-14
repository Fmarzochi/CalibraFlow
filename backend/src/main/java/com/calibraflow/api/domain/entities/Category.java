package com.calibraflow.api.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tb_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name; // Ex: "Manômetro", "Termômetro"

    @Column(nullable = false)
    private Integer calibrationIntervalDays; // Periodicidade em dias (Regra de Negócio Crítica)

    private String description;
}