package com.calibraflow.api.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tb_calibrations")
@Data
@NoArgsConstructor
public class Calibration {
    @Id
    private UUID id;
    private LocalDate calibrationDate;
    private LocalDate nextCalibrationDate;
    private String certificateNumber;
    private String laboratory;

    @ManyToOne
    @JoinColumn(name = "instrument_id")
    private Instrument instrument;
}