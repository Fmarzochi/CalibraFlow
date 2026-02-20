package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovementRepository extends JpaRepository<Movement, Long> {

      List<Movement> findByInstrumentIdOrderByMovementDateDesc(Long instrumentId);
}