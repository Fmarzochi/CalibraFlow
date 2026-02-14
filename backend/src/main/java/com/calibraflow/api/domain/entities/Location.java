package com.calibraflow.api.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tb_locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name; // Ex: "Obra Alpha", "Laboratório Central", "Almoxarifado"

    private String description; // Ex: "Galpão 3, Prateleira B"

    @Builder.Default
    private Boolean active = true; // Para desativar locais que não existem mais (sem apagar histórico)
}