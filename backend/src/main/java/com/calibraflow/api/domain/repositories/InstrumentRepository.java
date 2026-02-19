package com.calibraflow.api.repositories;

import com.calibraflow.api.entities.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, Long> {

    @Query("SELECT i FROM Instrument i WHERE i.deleted = false")
    List<Instrument> findAllActive();

    @Query("SELECT i FROM Instrument i WHERE i.id = :id AND i.deleted = false")
    Optional<Instrument> findActiveById(Long id);

    Optional<Instrument> findBySerialNumber(String serialNumber);
}