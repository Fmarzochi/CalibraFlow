package com.calibraflow.api.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.calibraflow.api.domain.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret:calibraflow-secret-default}")
    private String secret;

    @Value("${api.security.token.expiration:7200000}") // 2 horas em ms
    private Long accessTokenDurationMs;

    public String generateToken(User user) {
        return generateToken(user, accessTokenDurationMs);
    }

    public String generateToken(User user, Long durationMs) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("calibraflow-api")
                    .withSubject(user.getUsername())
                    .withExpiresAt(genExpirationDate(durationMs))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("calibraflow-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    private Instant genExpirationDate(Long durationMs) {
        return Instant.now().plusMillis(durationMs);
    }
}
