package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.dtos.InstrumentRequestDTO;
import com.calibraflow.api.domain.entities.Category;
import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.entities.Location;
import com.calibraflow.api.domain.entities.Periodicity;
import com.calibraflow.api.domain.repositories.CategoryRepository;
import com.calibraflow.api.domain.repositories.InstrumentRepository;
import com.calibraflow.api.domain.repositories.LocationRepository;
import com.calibraflow.api.domain.repositories.PeriodicityRepository;
import com.calibraflow.api.domain.repositories.MovementRepository;
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
    private CategoryRepository categoryRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private PeriodicityRepository periodicityRepository;

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InstrumentService instrumentService;

    private Category category;
    private Location location;
    private Periodicity periodicity;
    private InstrumentRequestDTO dto;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Test Category").build();
        location = Location.builder().id(1L).name("Test Location").active(true).build();
        periodicity = Periodicity.builder().id(1L).instrumentName("Test").days(365).build();

        dto = new InstrumentRequestDTO();
        dto.setTag("TAG001");
        dto.setName("Test Instrument");
        dto.setManufacturer("Test Manufacturer");
        dto.setModel("Test Model");
        dto.setSerialNumber("SN123");
        dto.setRange("0-100");
        dto.setTolerance("±1");
        dto.setResolution("0.1");
        dto.setPatrimonyCode("PAT001");
        dto.setCategoryId(1L);
        dto.setLocationId(1L);
        dto.setPeriodicityId(1L);
    }

    @Test
    void createFromDTO_WithValidData_ShouldCreateInstrument() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(periodicityRepository.findById(1L)).thenReturn(Optional.of(periodicity));

        Instrument savedInstrument = Instrument.builder()
                .id(1L)
                .tag(dto.getTag())
                .name(dto.getName())
                .category(category)
                .location(location)
                .periodicity(periodicity)
                .build();

        when(instrumentRepository.save(any(Instrument.class))).thenReturn(savedInstrument);

        Instrument result = instrumentService.createFromDTO(dto);

        assertNotNull(result);
        assertEquals("TAG001", result.getTag());
        assertEquals(category, result.getCategory());
        assertEquals(location, result.getLocation());
        assertEquals(periodicity, result.getPeriodicity());

        verify(categoryRepository, times(1)).findById(1L);
        verify(locationRepository, times(1)).findById(1L);
        verify(periodicityRepository, times(1)).findById(1L);
        verify(instrumentRepository, times(1)).save(any(Instrument.class));
    }

    @Test
    void createFromDTO_WithInvalidCategory_ShouldThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> instrumentService.createFromDTO(dto));

        verify(categoryRepository, times(1)).findById(1L);
        verify(locationRepository, never()).findById(anyLong());
        verify(periodicityRepository, never()).findById(anyLong());
        verify(instrumentRepository, never()).save(any());
    }

    @Test
    void createFromDTO_WithInvalidLocation_ShouldThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> instrumentService.createFromDTO(dto));

        verify(categoryRepository, times(1)).findById(1L);
        verify(locationRepository, times(1)).findById(1L);
        verify(periodicityRepository, never()).findById(anyLong());
        verify(instrumentRepository, never()).save(any());
    }
}
