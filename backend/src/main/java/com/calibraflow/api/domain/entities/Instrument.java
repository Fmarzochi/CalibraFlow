package com.calibraflow.api.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "tb_instruments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String patrimonyId;

    @Column(unique = true, nullable = false)
    private String serialNumber;

    private LocalDate acquisitionDate;

    @Builder.Default
    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "instrument", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Movement> movements = new ArrayList<>();

    @OneToMany(mappedBy = "instrument", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Calibration> calibrations = new ArrayList<>();
}
