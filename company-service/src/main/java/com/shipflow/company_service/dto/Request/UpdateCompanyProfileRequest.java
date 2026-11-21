package com.shipflow.company_service.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateCompanyProfileRequest(
        @NotBlank String name,
        String logoUrl,
        @NotBlank @Email String email,
        @NotBlank String phone,
        @NotBlank String address,
        String plan
) {}