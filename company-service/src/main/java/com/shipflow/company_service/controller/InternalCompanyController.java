package com.shipflow.company_service.controller;

import com.shipflow.company_service.dto.Response.CompanyProfileResponse;
import com.shipflow.company_service.dto.Request.InternalCompanyRequest;
import com.shipflow.company_service.service.CompanyManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/companies")
public class InternalCompanyController {

    private final CompanyManagementService service;

    public InternalCompanyController(CompanyManagementService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CompanyProfileResponse> createInternalCompany(
            @Valid @RequestBody InternalCompanyRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createCompanyFromRegister(request));
    }
}