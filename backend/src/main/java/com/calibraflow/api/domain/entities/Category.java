package com.calibraflow.api.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "validity_days")
    private Integer validityDays; // renomeado de validity_months para validity_days, mas podemos manter os dois? Vamos padronizar para dias.

    private String description;
}