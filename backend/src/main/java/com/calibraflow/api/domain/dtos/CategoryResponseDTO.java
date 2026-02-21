package com.calibraflow.api.domain.dtos;

import com.calibraflow.api.domain.entities.Category;

public record CategoryResponseDTO(Long id, String name, Integer validityDays, String description) {
    public CategoryResponseDTO(Category category) {
        this(category.getId(), category.getName(), category.getValidityDays(), category.getDescription());
    }
}