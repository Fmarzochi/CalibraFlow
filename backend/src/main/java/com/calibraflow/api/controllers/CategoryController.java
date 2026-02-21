package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.dtos.CategoryRequestDTO;
import com.calibraflow.api.domain.dtos.CategoryResponseDTO;
import com.calibraflow.api.domain.entities.Category;
import com.calibraflow.api.domain.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<CategoryResponseDTO>> findAll(@PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<CategoryResponseDTO> categories = categoryService.findAll(pageable).map(CategoryResponseDTO::new);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> findById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(category -> ResponseEntity.ok(new CategoryResponseDTO(category)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@Valid @RequestBody CategoryRequestDTO dto) {
        Category category = Category.builder()
                .name(dto.name())
                .validityDays(dto.validityDays())
                .description(dto.description())
                .build();
        Category newCategory = categoryService.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CategoryResponseDTO(newCategory));
    }
}