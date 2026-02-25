package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.dtos.MovementResponseDTO;
import com.calibraflow.api.domain.repositories.MovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
public class MovementController {

    private final MovementRepository movementRepository;

    @GetMapping
    public ResponseEntity<Page<MovementResponseDTO>> findAll(@PageableDefault(size = 20) Pageable pageable) {
        Page<MovementResponseDTO> response = movementRepository.findAll(pageable).map(m -> MovementResponseDTO.builder()
                .id(m.getId())
                .instrumentTag(m.getInstrument().getTag())
                .originName(m.getOrigin().getName())
                .destinationName(m.getDestination().getName())
                .reason(m.getReason())
                .movedByName(m.getUser().getName())
                .movementDate(m.getMovementDate())
                .build());
        return ResponseEntity.ok(response);
    }
}