package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.dtos.CategoryResponseDTO;
import com.calibraflow.api.domain.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<CategoryResponseDTO> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(cat -> CategoryResponseDTO.builder()
                .id(cat.getId())
                .name(cat.getName())
                .description(cat.getDescription())
                .build());
    }
}