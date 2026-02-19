package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Patrimony;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface PatrimonyRepository extends JpaRepository<Patrimony, UUID> {
}