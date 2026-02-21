package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.dtos.LocationRequestDTO;
import com.calibraflow.api.domain.dtos.LocationResponseDTO;
import com.calibraflow.api.domain.entities.Location;
import com.calibraflow.api.domain.services.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<Page<LocationResponseDTO>> findAll(@PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<LocationResponseDTO> locations = locationService.findAll(pageable).map(LocationResponseDTO::new);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationResponseDTO> findById(@PathVariable Long id) {
        return locationService.findById(id)
                .map(location -> ResponseEntity.ok(new LocationResponseDTO(location)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LocationResponseDTO> create(@Valid @RequestBody LocationRequestDTO dto) {
        Location location = Location.builder()
                .name(dto.name())
                .description(dto.description())
                .active(dto.active())
                .build();
        Location savedLocation = locationService.save(location);
        return ResponseEntity.status(HttpStatus.CREATED).body(new LocationResponseDTO(savedLocation));
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<LocationResponseDTO> toggleActive(@PathVariable Long id) {
        return locationService.toggleActive(id)
                .map(location -> ResponseEntity.ok(new LocationResponseDTO(location)))
                .orElse(ResponseEntity.notFound().build());
    }
}