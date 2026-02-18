package com.calibraflow.api.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "tb_patrimonies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Patrimony implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code; // Representa a coluna "Patrimônio" (ex: 1251, BAL-001)

    private String tag;  // Representa a coluna "TAG" (ex: 1251, BAL-001)
}
