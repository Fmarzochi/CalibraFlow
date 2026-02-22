package com.calibraflow.api.application.controllers;

import com.calibraflow.api.domain.dtos.InstrumentRequestDTO;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.services.InstrumentStatusService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/instruments")
@RequiredArgsConstructor
public class InstrumentStatusController {

    private final InstrumentStatusService statusService;

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable Long id,
            @RequestBody @Valid InstrumentRequestDTO.InstrumentStatusChangeDTO dto,
            @AuthenticationPrincipal User loggedUser,
            HttpServletRequest request) {

        String sourceIp = request.getHeader("X-Forwarded-For");
        if (sourceIp == null || sourceIp.isEmpty()) {
            sourceIp = request.getRemoteAddr();
        }

        statusService.changeStatus(id, dto, loggedUser, sourceIp);

        return ResponseEntity.noContent().build();
    }
}