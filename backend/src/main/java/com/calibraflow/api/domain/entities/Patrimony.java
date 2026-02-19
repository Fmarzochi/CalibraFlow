package com.calibraflow.api.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "tb_patrimonies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patrimony {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String patrimonyCode;

    @Column(nullable = false)
    private String tag;
}