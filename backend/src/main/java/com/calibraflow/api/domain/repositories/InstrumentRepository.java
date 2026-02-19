package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, UUID> {

    @Query("SELECT i FROM Instrument i WHERE i.deleted = false")
    List<Instrument> findAllActive();

    @Query("SELECT i FROM Instrument i WHERE i.id = :id AND i.deleted = false")
    Optional<Instrument> findActiveById(UUID id);

    Optional<Instrument> findBySerialNumber(String serialNumber);

    Optional<Instrument> findByPatrimony(String patrimony);
}