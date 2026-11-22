package com.shipflow.shipment_service.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignAgentRequest(
        @NotNull UUID assignedAgentId,
        @NotBlank String assignedAgentName
) {}