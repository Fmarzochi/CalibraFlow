package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.dtos.AuthenticationDTO;
import com.calibraflow.api.domain.dtos.RefreshTokenRequestDTO;
import com.calibraflow.api.domain.dtos.RegisterDTO;
import com.calibraflow.api.domain.entities.Role;
import com.calibraflow.api.domain.entities.User;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User existingUser;
    private String plainPassword = "password123";
    private String accessToken;
    private String refreshToken;

    @BeforeEach
    void setUp() throws Exception {
        Role roleUser = roleRepository.save(new Role("USUARIO"));
        Role roleAdmin = roleRepository.save(new Role("ADMIN"));

        existingUser = new User(
                "existinguser",
                passwordEncoder.encode(plainPassword),
                Set.of(roleUser, roleAdmin)
        );
        userRepository.save(existingUser);

        // Fazer login para obter tokens
        AuthenticationDTO loginDto = new AuthenticationDTO("existinguser", plainPassword);
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = loginResult.getResponse().getContentAsString();
        accessToken = objectMapper.readTree(jsonResponse).get("accessToken").asText();
        refreshToken = objectMapper.readTree(jsonResponse).get("refreshToken").asText();
    }

    @Test
    void register_WithNewUsername_ShouldReturnOk() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO("newuser", plainPassword, Set.of(new Role("USUARIO")));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void register_WithExistingUsername_ShouldReturnBadRequest() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO("existinguser", plainPassword, Set.of(new Role("USUARIO")));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WithValidCredentials_ShouldReturnTokens() throws Exception {
        AuthenticationDTO loginDTO = new AuthenticationDTO("existinguser", plainPassword);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void login_WithInvalidPassword_ShouldReturnUnauthorized() throws Exception {
        AuthenticationDTO loginDTO = new AuthenticationDTO("existinguser", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_WithValidRefreshToken_ShouldReturnNewTokens() throws Exception {
        RefreshTokenRequestDTO refreshRequest = new RefreshTokenRequestDTO(refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void refresh_WithInvalidRefreshToken_ShouldReturnBadRequest() throws Exception {
        RefreshTokenRequestDTO refreshRequest = new RefreshTokenRequestDTO("invalid-token");

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logout_WithValidRefreshToken_ShouldReturnOk() throws Exception {
        RefreshTokenRequestDTO logoutRequest = new RefreshTokenRequestDTO(refreshToken);

        mockMvc.perform(post("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isOk());
    }
}
