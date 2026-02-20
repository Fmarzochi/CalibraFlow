package com.calibraflow.api.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_patrimonies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patrimony {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String patrimonyCode;

    @Column(nullable = false)
    private String tag;
}