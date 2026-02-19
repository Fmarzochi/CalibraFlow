package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Periodicity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface PeriodicityRepository extends JpaRepository<Periodicity, UUID> {
    Optional<Periodicity> findByInstrumentName(String instrumentName);
}