package com.shipflow.gateway_service.dto.Request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterCompanyAdminRequest(
        @NotBlank String companyName,
        @NotBlank @Email String companyEmail,
        @NotBlank String companyPhone,
        @NotBlank String companyAddress,
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, message = "Password must be at least 6 characters") String password
) {}
