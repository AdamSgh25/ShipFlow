package com.shipflow.company_service.controller;


import com.shipflow.company_service.dto.Response.*;
import com.shipflow.company_service.dto.Request.*;
import com.shipflow.company_service.service.CompanyManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/delivery-agents")
public class DeliveryAgentController {

    private final CompanyManagementService service;

    public DeliveryAgentController(CompanyManagementService service) {
        this.service = service;
    }

    private UUID extractAdminCompanyId(String companyIdHeader, String roleHeader) {
        if (!"ADMIN".equals(roleHeader)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN can manage delivery agents");
        }
        if (companyIdHeader == null || companyIdHeader.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing X-Company-Id header");
        }
        return UUID.fromString(companyIdHeader);
    }

    @GetMapping
    public ResponseEntity<List<DeliveryAgentResponse>> listAgents(
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active
    ) {
        return ResponseEntity.ok(service.getAgents(extractAdminCompanyId(companyId, role), search, active));
    }

    @PostMapping
    public ResponseEntity<DeliveryAgentResponse> createAgent(
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @Valid @RequestBody CreateDeliveryAgentRequest request
    ) {
        DeliveryAgentResponse created = service.createAgent(extractAdminCompanyId(companyId, role), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryAgentResponse> updateAgent(
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDeliveryAgentRequest request
    ) {
        return ResponseEntity.ok(service.updateAgent(extractAdminCompanyId(companyId, role), id, request));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<DeliveryAgentResponse> toggleStatus(
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(service.toggleAgentStatus(extractAdminCompanyId(companyId, role), id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgent(
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable UUID id
    ) {
        service.deleteAgent(extractAdminCompanyId(companyId, role), id);
        return ResponseEntity.noContent().build();
    }

    public record AgentStatusRequest(boolean active) {}

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<DeliveryAgentResponse> deactivateAgent(
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(service.setAgentStatus(extractAdminCompanyId(companyId, role), id, false));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DeliveryAgentResponse> updateStatus(
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable UUID id,
            @RequestBody AgentStatusRequest request
    ) {
        return ResponseEntity.ok(service.setAgentStatus(extractAdminCompanyId(companyId, role), id, request.active()));
    }
}