package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Location;
import com.calibraflow.api.domain.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByName(String name);
    Optional<Location> findByNameAndTenant(String name, Tenant tenant);
}