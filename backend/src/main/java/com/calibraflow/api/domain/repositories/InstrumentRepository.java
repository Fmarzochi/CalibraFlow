package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, UUID> {

    @Query("SELECT COUNT(i) > 0 FROM Instrument i WHERE i.patrimony.patrimonyCode = :code")
    boolean existsByPatrimonyCode(@Param("code") String code);

    Optional<Instrument> findBySerialNumber(String serialNumber);
}