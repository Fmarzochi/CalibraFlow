package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Calibration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalibrationRepository extends JpaRepository<Calibration, Long> {

    Page<Calibration> findByInstrumentId(Long instrumentId, Pageable pageable);
}