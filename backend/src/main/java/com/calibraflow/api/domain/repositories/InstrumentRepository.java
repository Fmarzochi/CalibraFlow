package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, UUID> {

    boolean existsByPatrimonyId(String patrimonyId);

    Optional<Instrument> findByPatrimonyId(String patrimonyId);
}