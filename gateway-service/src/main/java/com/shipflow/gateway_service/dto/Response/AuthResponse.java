package com.shipflow.gateway_service.dto.Response;

public record AuthResponse(
        String accessToken,
        UserResponse user
) {}