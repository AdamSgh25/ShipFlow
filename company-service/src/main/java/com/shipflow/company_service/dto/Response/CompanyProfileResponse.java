package com.shipflow.company_service.dto.Response;


import com.shipflow.company_service.entity.Company;

import java.time.Instant;
import java.util.UUID;

public record CompanyProfileResponse(
        UUID id,
        String name,
        String logoUrl,
        String email,
        String phone,
        String address,
        String plan,
        Instant createdAt
) {
    public static CompanyProfileResponse fromEntity(Company company) {
        return new CompanyProfileResponse(
                company.getId(),
                company.getName(),
                company.getLogoUrl(),
                company.getEmail(),
                company.getPhone(),
                company.getAddress(),
                company.getPlan(),
                company.getCreatedAt()
        );
    }
}