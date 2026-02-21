package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.entities.Calibration;
import com.calibraflow.api.domain.repositories.CalibrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalibrationServiceTest {

    @Mock
    private CalibrationRepository calibrationRepository;

    @InjectMocks
    private CalibrationService calibrationService;

    private Calibration calibration1;
    private Calibration calibration2;
    private LocalDate start;
    private LocalDate end;

    @BeforeEach
    void setUp() {
        calibration1 = Calibration.builder()
                .id(1L)
                .calibrationDate(LocalDate.of(2025, 1, 10))
                .nextCalibrationDate(LocalDate.of(2026, 1, 10))
                .laboratory("Lab A")
                .certificateNumber("CERT-001")
                .build();

        calibration2 = Calibration.builder()
                .id(2L)
                .calibrationDate(LocalDate.of(2025, 2, 15))
                .nextCalibrationDate(LocalDate.of(2026, 2, 15))
                .laboratory("Lab B")
                .certificateNumber("CERT-002")
                .build();

        start = LocalDate.of(2026, 1, 1);
        end = LocalDate.of(2026, 2, 28);
    }

    @Test
    void findUpcomingCalibrations_ShouldReturnList() {
        when(calibrationRepository.findByNextCalibrationDateBetween(start, end))
                .thenReturn(Arrays.asList(calibration1, calibration2));

        List<Calibration> result = calibrationService.findUpcomingCalibrations(start, end);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(calibrationRepository).findByNextCalibrationDateBetween(start, end);
    }

    @Test
    void findUpcomingCalibrations_WithNoResults_ShouldReturnEmptyList() {
        when(calibrationRepository.findByNextCalibrationDateBetween(start, end))
                .thenReturn(List.of());

        List<Calibration> result = calibrationService.findUpcomingCalibrations(start, end);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(calibrationRepository).findByNextCalibrationDateBetween(start, end);
    }

    @Test
    void findById_ShouldReturnCalibration_WhenExists() {
        when(calibrationRepository.findById(1L)).thenReturn(Optional.of(calibration1));

        Optional<Calibration> result = calibrationService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(calibrationRepository).findById(1L);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        when(calibrationRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Calibration> result = calibrationService.findById(99L);

        assertFalse(result.isPresent());
        verify(calibrationRepository).findById(99L);
    }
}
