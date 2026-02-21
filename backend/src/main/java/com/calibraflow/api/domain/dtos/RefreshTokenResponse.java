package com.calibraflow.api.domain.dtos;

public record RefreshTokenResponse(
    String accessToken,
    String refreshToken
) {}
