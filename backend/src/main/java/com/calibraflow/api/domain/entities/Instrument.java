package com.calibraflow.api.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

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

    @Column(nullable = false, unique = true)
    private String patrimonyId;

    @Column(nullable = false)
    private String name;

    private String serialNumber;

    private String manufacturer;

    private String model;

    private LocalDate acquisitionDate;

    @Builder.Default
    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(mappedBy = "instrument", cascade = CascadeType.ALL)
    private List<Calibration> calibrations;
}