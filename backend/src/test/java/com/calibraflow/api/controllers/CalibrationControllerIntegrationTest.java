package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.entities.Calibration;
import com.calibraflow.api.domain.entities.Category;
import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.entities.Location;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.repositories.CalibrationRepository;
import com.calibraflow.api.domain.repositories.CategoryRepository;
import com.calibraflow.api.domain.repositories.InstrumentRepository;
import com.calibraflow.api.domain.repositories.LocationRepository;
import com.calibraflow.api.domain.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CalibrationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Autowired
    private CalibrationRepository calibrationRepository;

    @Autowired
    private UserRepository userRepository;

    private Instrument instrument;
    private Calibration calibration1;
    private Calibration calibration2;

    @BeforeEach
    void setUp() {
        Category category = categoryRepository.save(Category.builder().name("Test Category").build());
        Location location = locationRepository.save(Location.builder().name("Test Location").active(true).build());
        User user = userRepository.save(new User("testuser", "password", null));

        instrument = instrumentRepository.save(Instrument.builder()
                .tag("TAG-001")
                .name("Test Instrument")
                .category(category)
                .location(location)
                .active(true)
                .deleted(false)
                .build());

        calibration1 = calibrationRepository.save(Calibration.builder()
                .calibrationDate(LocalDate.now().minusMonths(6))
                .nextCalibrationDate(LocalDate.now().plusMonths(1))
                .laboratory("Lab A")
                .certificateNumber("CERT-001")
                .instrument(instrument)
                .build());

        calibration2 = calibrationRepository.save(Calibration.builder()
                .calibrationDate(LocalDate.now().minusMonths(3))
                .nextCalibrationDate(LocalDate.now().plusMonths(2))
                .laboratory("Lab B")
                .certificateNumber("CERT-002")
                .instrument(instrument)
                .build());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USUARIO"})
    void findAll_ShouldReturnAllCalibrations() throws Exception {
        mockMvc.perform(get("/api/calibrations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].certificateNumber").value("CERT-001"))
                .andExpect(jsonPath("$[1].certificateNumber").value("CERT-002"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USUARIO"})
    void findById_WithValidId_ShouldReturnCalibration() throws Exception {
        mockMvc.perform(get("/api/calibrations/{id}", calibration1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.certificateNumber").value("CERT-001"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USUARIO"})
    void findById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/calibrations/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USUARIO"})
    void findByInstrument_ShouldReturnCalibrationsForInstrument() throws Exception {
        mockMvc.perform(get("/api/calibrations/instrument/{instrumentId}", instrument.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USUARIO"})
    void createCalibration_ShouldReturnCreated() throws Exception {
        Calibration newCalibration = Calibration.builder()
                .calibrationDate(LocalDate.now())
                .nextCalibrationDate(LocalDate.now().plusYears(1))
                .laboratory("Lab C")
                .certificateNumber("CERT-003")
                .instrument(instrument)
                .build();

        mockMvc.perform(post("/api/calibrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCalibration)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.certificateNumber").value("CERT-003"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USUARIO"})
    void getUpcomingCalibrations_ShouldReturnCalibrationsWithinRange() throws Exception {
        LocalDate start = LocalDate.now().plusDays(20);
        LocalDate end = LocalDate.now().plusMonths(2).plusDays(10);

        mockMvc.perform(get("/api/calibrations/upcoming")
                .param("start", start.toString())
                .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        LocalDate start2 = LocalDate.now().plusDays(25);
        LocalDate end2 = LocalDate.now().plusMonths(1).plusDays(5);
        mockMvc.perform(get("/api/calibrations/upcoming")
                .param("start", start2.toString())
                .param("end", end2.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].certificateNumber").value("CERT-001"));
    }
}
