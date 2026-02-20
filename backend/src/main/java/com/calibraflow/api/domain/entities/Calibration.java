package com.calibraflow.api.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "tb_calibrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calibration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate calibrationDate;

    private LocalDate nextCalibrationDate;

    private String certificateNumber;

    private String laboratory;

    @ManyToOne
    @JoinColumn(name = "instrument_id")
    private Instrument instrument;
}