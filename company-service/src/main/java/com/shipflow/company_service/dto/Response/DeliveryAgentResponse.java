package com.shipflow.company_service.dto.Response;

import com.shipflow.company_service.entity.DeliveryAgent;

import java.time.Instant;
import java.util.UUID;

public record DeliveryAgentResponse(
        UUID id,
        UUID companyId,
        String fullName,
        String email,
        String phone,
        String vehicleType,
        boolean active,
        int assignedCount,
        int deliveredCount,
        double successRate,
        Instant createdAt
) {
    public static DeliveryAgentResponse fromEntity(DeliveryAgent agent) {
        return new DeliveryAgentResponse(
                agent.getId(),
                agent.getCompanyId(),
                agent.getFullName(),
                agent.getEmail(),
                agent.getPhone(),
                agent.getVehicleType(),
                agent.isActive(),
                agent.getAssignedCount(),
                agent.getDeliveredCount(),
                agent.getSuccessRate(),
                agent.getCreatedAt()
        );
    }
}