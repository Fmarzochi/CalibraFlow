package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
}