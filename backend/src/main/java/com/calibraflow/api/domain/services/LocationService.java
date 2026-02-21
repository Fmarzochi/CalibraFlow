package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.entities.Location;
import com.calibraflow.api.domain.repositories.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    @Transactional(readOnly = true)
    public Page<Location> findAll(Pageable pageable) {
        return locationRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Location> findById(Long id) {
        return locationRepository.findById(id);
    }

    @Transactional
    public Location save(Location location) {
        return locationRepository.save(location);
    }

    @Transactional
    public Optional<Location> toggleActive(Long id) {
        return locationRepository.findById(id)
                .map(location -> {
                    location.setActive(!location.getActive());
                    return locationRepository.save(location);
                });
    }
}