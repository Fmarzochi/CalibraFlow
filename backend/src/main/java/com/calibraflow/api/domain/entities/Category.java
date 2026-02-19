package com.calibraflow.api.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "tb_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer calibrationIntervalDays;

    private String description;
}