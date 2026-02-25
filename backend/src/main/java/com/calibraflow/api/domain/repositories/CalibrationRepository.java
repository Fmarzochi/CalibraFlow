package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Calibration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CalibrationRepository extends JpaRepository<Calibration, Long> {
    Page<Calibration> findByTenantId(Long tenantId, Pageable pageable);
    Page<Calibration> findByInstrumentId(Long instrumentId, Pageable pageable);
    List<Calibration> findByInstrumentId(Long instrumentId);

    @Query("SELECT c FROM Calibration c WHERE c.nextCalibrationDate BETWEEN :startDate AND :endDate")
    List<Calibration> findUpcomingCalibrations(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}