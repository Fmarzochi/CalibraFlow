package com.calibraflow.api.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_periodicities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Periodicity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String instrumentName;

    @Column(nullable = false)
    private Integer days;

    public Periodicity(String instrumentName, Integer days) {
        this.instrumentName = instrumentName;
        this.days = days;
    }
}