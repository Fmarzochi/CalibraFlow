package com.calibraflow.api.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_instruments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    private String specification;

    private String manufacturer;

    private String patrimony;

    private String tag;

    @Column(name = "serial_number")
    private String serialNumber;

    private String model;

    @Column(name = "calibration_laboratory")
    private String laboratory;

    @Column(name = "certificate_number")
    private String certificateNumber;

    @Column(name = "calibration_date")
    private LocalDate calibrationDate;

    @Column(name = "next_calibration_date")
    private LocalDate nextCalibrationDate;

    private String location;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private boolean deleted = false;

    private LocalDateTime deletedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}