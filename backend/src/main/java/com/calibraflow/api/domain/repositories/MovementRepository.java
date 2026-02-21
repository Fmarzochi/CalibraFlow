package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Movement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovementRepository extends JpaRepository<Movement, Long> {

      Page<Movement> findByInstrumentIdOrderByMovementDateDesc(Long instrumentId, Pageable pageable);
}