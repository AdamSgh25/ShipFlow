package com.shipflow.company_service.controller;

import com.shipflow.company_service.dto.Response.CompanyProfileResponse;
import com.shipflow.company_service.dto.Request.UpdateCompanyProfileRequest;
import com.shipflow.company_service.service.CompanyManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private final CompanyManagementService service;

    public CompanyController(CompanyManagementService service) {
        this.service = service;
    }

    private UUID extractCompanyId(String companyIdHeader, String roleHeader) {
        if (!"ADMIN".equals(roleHeader)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN can access company profile");
        }
        if (companyIdHeader == null || companyIdHeader.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing X-Company-Id header");
        }
        return UUID.fromString(companyIdHeader);
    }

    @GetMapping("/profile")
    public ResponseEntity<CompanyProfileResponse> getProfile(
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        return ResponseEntity.ok(service.getProfile(extractCompanyId(companyId, role)));
    }

    @PutMapping("/profile")
    public ResponseEntity<CompanyProfileResponse> updateProfile(
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @Valid @RequestBody UpdateCompanyProfileRequest request
    ) {
        return ResponseEntity.ok(service.updateProfile(extractCompanyId(companyId, role), request));
    }
}