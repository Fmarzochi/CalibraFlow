package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.dtos.AuthenticationDTO;
import com.calibraflow.api.domain.dtos.InstrumentRequestDTO;
import com.calibraflow.api.domain.dtos.LoginResponseDTO;
import com.calibraflow.api.domain.entities.Category;
import com.calibraflow.api.domain.entities.Instrument;
import com.calibraflow.api.domain.entities.Location;
import com.calibraflow.api.domain.entities.Role;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.repositories.CategoryRepository;
import com.calibraflow.api.domain.repositories.InstrumentRepository;
import com.calibraflow.api.domain.repositories.LocationRepository;
import com.calibraflow.api.domain.repositories.RoleRepository;
import com.calibraflow.api.domain.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class InstrumentControllerIntegrationTest {

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
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Category category;
    private Location location;
    private User user;
    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        category = categoryRepository.save(Category.builder().name("Test Category").build());
        location = locationRepository.save(Location.builder().name("Test Location").active(true).build());

        Role roleUser = roleRepository.save(new Role("USUARIO"));
        Role roleAdmin = roleRepository.save(new Role("ADMIN"));

        user = new User("testuser", passwordEncoder.encode("password"), Set.of(roleUser, roleAdmin));
        user = userRepository.save(user);

        AuthenticationDTO loginDto = new AuthenticationDTO("testuser", "password");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = loginResult.getResponse().getContentAsString();
        LoginResponseDTO loginResponse = objectMapper.readValue(jsonResponse, LoginResponseDTO.class);
        accessToken = loginResponse.accessToken();
    }

    @Test
    void createInstrument_WithValidData_ShouldReturnCreated() throws Exception {
        InstrumentRequestDTO dto = new InstrumentRequestDTO();
        dto.setTag("TAG-123");
        dto.setName("Test Instrument");
        dto.setManufacturer("Test Manufacturer");
        dto.setModel("Model X");
        dto.setSerialNumber("SN123");
        dto.setRange("0-100");
        dto.setTolerance("±1");
        dto.setResolution("0.1");
        dto.setPatrimonyCode("PAT-123");
        dto.setCategoryId(category.getId());
        dto.setLocationId(location.getId());

        mockMvc.perform(post("/api/instruments")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tag").value("TAG-123"))
                .andExpect(jsonPath("$.name").value("Test Instrument"))
                .andExpect(jsonPath("$.categoryName").value("Test Category"))
                .andExpect(jsonPath("$.locationName").value("Test Location"));
    }

    @Test
    void createInstrument_WithInvalidCategory_ShouldReturnBadRequest() throws Exception {
        InstrumentRequestDTO dto = new InstrumentRequestDTO();
        dto.setTag("TAG-456");
        dto.setName("Test Instrument");
        dto.setCategoryId(999L);
        dto.setLocationId(location.getId());

        mockMvc.perform(post("/api/instruments")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateLocation_WithValidData_ShouldReturnUpdated() throws Exception {
        Instrument instrument = Instrument.builder()
                .tag("TAG-789")
                .name("Test")
                .category(category)
                .location(location)
                .active(true)
                .deleted(false)
                .build();
        instrument = instrumentRepository.save(instrument);

        Location newLocation = locationRepository.save(Location.builder().name("New Location").active(true).build());

        mockMvc.perform(put("/api/instruments/{id}/location", instrument.getId())
                .header("Authorization", "Bearer " + accessToken)
                .param("reason", "Test movement")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newLocation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locationName").value("New Location"));
    }

    @Test
    void softDelete_WithAdminRole_ShouldReturnNoContent() throws Exception {
        Instrument instrument = Instrument.builder()
                .tag("TAG-999")
                .name("Test")
                .category(category)
                .location(location)
                .active(true)
                .deleted(false)
                .build();
        instrument = instrumentRepository.save(instrument);

        mockMvc.perform(delete("/api/instruments/{id}", instrument.getId())
                .header("Authorization", "Bearer " + accessToken)
                .param("reason", "Test delete"))
                .andExpect(status().isNoContent());

        Instrument deleted = instrumentRepository.findById(instrument.getId()).orElseThrow();
        assert deleted.isDeleted() && !deleted.isActive();
    }
}
