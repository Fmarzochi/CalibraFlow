package com.calibraflow.api.controllers;

import com.calibraflow.api.domain.dtos.AuthenticationDTO;
import com.calibraflow.api.domain.dtos.LoginResponseDTO;
import com.calibraflow.api.domain.dtos.RefreshTokenRequestDTO;
import com.calibraflow.api.domain.dtos.RefreshTokenResponseDTO;
import com.calibraflow.api.domain.dtos.RegisterDTO;
import com.calibraflow.api.domain.entities.RefreshToken;
import com.calibraflow.api.domain.entities.User;
import com.calibraflow.api.domain.repositories.UserRepository;
import com.calibraflow.api.domain.services.RefreshTokenService;
import com.calibraflow.api.infrastructure.security.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        Authentication auth = this.authenticationManager.authenticate(usernamePassword);
        User user = (User) auth.getPrincipal();

        String accessToken = tokenService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(new LoginResponseDTO(accessToken, refreshToken.getToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data) {
        if (this.userRepository.findByUsername(data.username()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.username(), encryptedPassword, data.roles());
        this.userRepository.save(newUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDTO> refresh(@RequestBody RefreshTokenRequestDTO request) {
        String requestRefreshToken = request.refreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = tokenService.generateToken(user);
                    // Opcional: gerar novo refresh token (rotação)
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
                    return ResponseEntity.ok(new RefreshTokenResponseDTO(newAccessToken, newRefreshToken.getToken()));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token não encontrado"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequestDTO request) {
        refreshTokenService.findByToken(request.refreshToken())
                .ifPresent(refreshToken -> refreshTokenService.deleteByUser(refreshToken.getUser()));
        return ResponseEntity.ok().build();
    }
}
