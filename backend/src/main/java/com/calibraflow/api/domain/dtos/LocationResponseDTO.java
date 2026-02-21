package com.calibraflow.api.domain.dtos;

import com.calibraflow.api.domain.entities.Location;

public record LocationResponseDTO(Long id, String name, String description, Boolean active) {
    public LocationResponseDTO(Location location) {
        this(location.getId(), location.getName(), location.getDescription(), location.getActive());
    }
}