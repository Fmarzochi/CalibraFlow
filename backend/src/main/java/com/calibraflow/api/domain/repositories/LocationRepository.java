package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {

    Optional<Location> findByName(String name);
}