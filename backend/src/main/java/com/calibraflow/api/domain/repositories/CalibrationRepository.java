package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Calibration;
import com.calibraflow.api.domain.entities.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CalibrationRepository extends JpaRepository<Calibration, UUID> {

    List<Calibration> findByInstrumentId(UUID instrumentId);

    boolean existsByInstrumentAndCertificateNumber(Instrument instrument, String certificateNumber);
}