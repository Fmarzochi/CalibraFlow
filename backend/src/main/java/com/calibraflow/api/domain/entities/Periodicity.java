package com.calibraflow.api.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_periodicities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Periodicity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instrument_name", nullable = false, unique = true)
    private String instrumentName;

    @Column(nullable = false)
    private Integer days;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}