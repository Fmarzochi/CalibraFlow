package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.InstrumentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstrumentStatusHistoryRepository extends JpaRepository<InstrumentStatusHistory, Long> {
}