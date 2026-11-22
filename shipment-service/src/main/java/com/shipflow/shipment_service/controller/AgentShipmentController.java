package com.shipflow.shipment_service.controller;

import com.shipflow.shipment_service.dto.Response.ShipmentResponse;
import com.shipflow.shipment_service.dto.Request.UpdateShipmentStatusRequest;
import com.shipflow.shipment_service.enums.ShipmentStatus;
import com.shipflow.shipment_service.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/agent/shipments")
public class AgentShipmentController {

    private final ShipmentService service;

    public AgentShipmentController(ShipmentService service) {
        this.service = service;
    }

    private void requireAgentRole(String roleHeader, String companyIdHeader, String userIdHeader) {
        if (!"DELIVERY_AGENT".equals(roleHeader)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only DELIVERY_AGENT can access agent shipments");
        }
        if (companyIdHeader == null || companyIdHeader.isBlank() || userIdHeader == null || userIdHeader.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing agent context headers");
        }
    }

    @GetMapping
    public ResponseEntity<Page<ShipmentResponse>> myShipments(
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ShipmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        System.out.println("Received by Shipment Service:");
        System.out.println("User ID: " + userId);
        System.out.println("Role: " + role);
        System.out.println("Company ID: " + companyId);
        requireAgentRole(role, companyId, userId);
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(100, Math.max(1, size)));
        return ResponseEntity.ok(
                service.getAgentShipments(UUID.fromString(companyId), UUID.fromString(userId), search, status, pageable)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ShipmentResponse> updateAgentShipmentStatus(
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateShipmentStatusRequest request
    ) {
        requireAgentRole(role, companyId, userId);
        return ResponseEntity.ok(
                service.updateStatusAsAgent(UUID.fromString(companyId), UUID.fromString(userId), id, request)
        );
    }
}