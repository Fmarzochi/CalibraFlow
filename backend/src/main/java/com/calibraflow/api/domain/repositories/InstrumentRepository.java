package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstrumentRepository extends JpaRepository<Instrument, Long> {
    boolean existsByTagAndTenant(String tag, Tenant tenant);
    Optional<Instrument> findByTagAndTenant(String tag, Tenant tenant);
}