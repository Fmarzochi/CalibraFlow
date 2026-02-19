package com.calibraflow.api.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "tb_patrimonies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patrimony implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "patrimony_code", nullable = false, unique = true)
    private String patrimonyCode;

    @Column(nullable = false)
    private String tag;
}