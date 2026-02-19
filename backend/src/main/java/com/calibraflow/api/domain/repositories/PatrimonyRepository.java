package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Patrimony;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PatrimonyRepository extends JpaRepository<Patrimony, UUID> {
    Optional<Patrimony> findByPatrimonyCode(String code);
}