package com.calibraflow.api.domain.repositories;

import com.calibraflow.api.domain.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    // Eu adicionei este método para que o Spring Data JPA gere automaticamente a consulta por nome necessária na migração
    Optional<Category> findByName(String name);
}