package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstrumentRepository extends JpaRepository<Instrument, Long> {

    boolean existsByTagAndTenant(String tag, Tenant tenant);
}