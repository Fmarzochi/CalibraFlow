package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Calibration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CalibrationRepository extends JpaRepository<Calibration, Long> {
    Page<Calibration> findByInstrumentId(Long instrumentId, Pageable pageable);

    @Query("SELECT c FROM Calibration c WHERE c.nextCalibrationDate BETWEEN :startDate AND :endDate")
    List<Calibration> findUpcomingCalibrations(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}