package com.shipflow.gateway_service.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateAgentUserRequest(
        @NotNull UUID companyId,
        @NotBlank String companyName,
        @NotBlank String fullName,
        @NotBlank @Email String email,
        String password
) {}