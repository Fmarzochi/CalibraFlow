package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.dtos.LocationResponseDTO;
import com.calibraflow.api.domain.repositories.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    @Transactional(readOnly = true)
    public Page<LocationResponseDTO> findAll(Pageable pageable) {
        return locationRepository.findAll(pageable).map(loc -> LocationResponseDTO.builder()
                .id(loc.getId())
                .name(loc.getName())
                .description(loc.getDescription())
                .active(loc.isActive())
                .build());
    }
}