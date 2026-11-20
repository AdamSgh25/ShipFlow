package com.shipflow.gateway_service.dto.Response;

import com.shipflow.gateway_service.entity.User;
import com.shipflow.gateway_service.enums.Role;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        Role role,
        UUID companyId,
        String companyName
) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getCompanyId(),
                user.getCompanyName()
        );
    }
}