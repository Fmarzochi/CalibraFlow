package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.dtos.CategoryResponseDTO;
import com.calibraflow.api.domain.entities.Category;
import com.calibraflow.api.domain.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<Page<CategoryResponseDTO>> findAll(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<CategoryResponseDTO> categories = categoryRepository.findAll(pageable)
                .map(this::mapToResponseDTO);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> findById(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(this::mapToResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private CategoryResponseDTO mapToResponseDTO(Category category) {
        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}