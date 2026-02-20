package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Patrimony;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PatrimonyRepository extends JpaRepository<Patrimony, Long> {
    Optional<Patrimony> findByPatrimonyCode(String code);
}