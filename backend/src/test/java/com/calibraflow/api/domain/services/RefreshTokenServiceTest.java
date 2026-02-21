package com.calibraflow.api.domain.services;

import com.calibraflow.api.domain.entities.RefreshToken;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.repositories.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User user;
    private RefreshToken refreshToken;
    private final Long refreshTokenDurationMs = 86400000L; // 24 horas

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        refreshToken = RefreshToken.builder()
                .id(1L)
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", refreshTokenDurationMs);
    }

    @Test
    void createRefreshToken_ShouldSaveAndReturnToken() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        RefreshToken created = refreshTokenService.createRefreshToken(user);

        assertNotNull(created);
        assertEquals(user, created.getUser());
        assertNotNull(created.getToken());
        assertNotNull(created.getExpiryDate());

        verify(refreshTokenRepository).deleteByUser(user);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void findByToken_WhenTokenExists_ShouldReturnToken() {
        when(refreshTokenRepository.findByToken(refreshToken.getToken())).thenReturn(Optional.of(refreshToken));

        Optional<RefreshToken> found = refreshTokenService.findByToken(refreshToken.getToken());

        assertTrue(found.isPresent());
        assertEquals(refreshToken.getToken(), found.get().getToken());
        verify(refreshTokenRepository).findByToken(refreshToken.getToken());
    }

    @Test
    void findByToken_WhenTokenDoesNotExist_ShouldReturnEmpty() {
        when(refreshTokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

        Optional<RefreshToken> found = refreshTokenService.findByToken("invalid");

        assertFalse(found.isPresent());
        verify(refreshTokenRepository).findByToken("invalid");
    }

    @Test
    void verifyExpiration_WhenTokenIsValid_ShouldReturnToken() {
        RefreshToken verified = refreshTokenService.verifyExpiration(refreshToken);

        assertNotNull(verified);
        assertEquals(refreshToken.getToken(), verified.getToken());
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    void verifyExpiration_WhenTokenIsExpired_ShouldThrowException() {
        refreshToken.setExpiryDate(Instant.now().minusMillis(1000));

        assertThrows(RuntimeException.class, () -> refreshTokenService.verifyExpiration(refreshToken));

        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void deleteByUser_ShouldCallRepository() {
        doNothing().when(refreshTokenRepository).deleteByUser(user);

        refreshTokenService.deleteByUser(user);

        verify(refreshTokenRepository).deleteByUser(user);
    }
}
