package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.entities.Location;
import com.calibraflow.api.domain.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationRepository locationRepository;

    @GetMapping
    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    @PostMapping
    public Location create(@RequestBody Location location) {
        return locationRepository.save(location);
    }
}