package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.dtos.InstrumentRequestDTO;
import com.calibraflow.api.domain.entities.Category;
import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.entities.Location;
import com.calibraflow.api.domain.repositories.CategoryRepository;
import com.calibraflow.api.domain.repositories.InstrumentRepository;
import com.calibraflow.api.domain.repositories.LocationRepository;
import com.calibraflow.api.domain.repositories.MovementRepository;
import com.calibraflow.api.domain.repositories.PeriodicityRepository;
import com.calibraflow.api.domain.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstrumentServiceTest {

    @Mock
    private InstrumentRepository instrumentRepository;

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private PeriodicityRepository periodicityRepository;

    @InjectMocks
    private InstrumentService instrumentService;

    private Category category;
    private Location location;
    private InstrumentRequestDTO dto;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Test Category").build();
        location = Location.builder().id(1L).name("Test Location").active(true).build();

        dto = new InstrumentRequestDTO();
        dto.setTag("TAG-001");
        dto.setName("Test Instrument");
        dto.setManufacturer("Test Manufacturer");
        dto.setModel("Model X");
        dto.setSerialNumber("SN123");
        dto.setRange("0-100");
        dto.setTolerance("±1");
        dto.setResolution("0.1");
        dto.setPatrimonyCode("PAT-001");
        dto.setCategoryId(1L);
        dto.setLocationId(1L);
        dto.setPeriodicityId(null);
    }

    @Test
    void createFromDTO_WithValidData_ShouldCreateInstrument() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(instrumentRepository.save(any(Instrument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Instrument created = instrumentService.createFromDTO(dto);

        assertNotNull(created);
        assertEquals("TAG-001", created.getTag());
        assertEquals("Test Instrument", created.getName());
        assertEquals(category, created.getCategory());
        assertEquals(location, created.getLocation());
        assertNull(created.getPeriodicity());
        assertTrue(created.isActive());
        assertFalse(created.isDeleted());

        verify(categoryRepository).findById(1L);
        verify(locationRepository).findById(1L);
        verify(instrumentRepository).save(any(Instrument.class));
        verify(periodicityRepository, never()).findById(any());
    }

    @Test
    void createFromDTO_WithNonExistentCategory_ShouldThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> instrumentService.createFromDTO(dto));

        verify(categoryRepository).findById(1L);
        verify(locationRepository, never()).findById(any());
        verify(instrumentRepository, never()).save(any());
    }

    @Test
    void createFromDTO_WithNonExistentLocation_ShouldThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> instrumentService.createFromDTO(dto));

        verify(categoryRepository).findById(1L);
        verify(locationRepository).findById(1L);
        verify(instrumentRepository, never()).save(any());
    }
}
