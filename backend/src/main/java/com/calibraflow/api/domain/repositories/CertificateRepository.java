package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
}