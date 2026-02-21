package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Calibration;
import com.calibraflow.api.domain.entities.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CalibrationRepository extends JpaRepository<Calibration, Long> {

    List<Calibration> findByInstrumentIdOrderByCalibrationDateDesc(Long instrumentId);

    boolean existsByInstrumentAndCertificateNumber(Instrument instrument, String certificateNumber);

    List<Calibration> findByNextCalibrationDateBetween(LocalDate start, LocalDate end);
}
