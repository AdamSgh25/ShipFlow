package com.shipflow.shipment_service.controller;

import com.shipflow.shipment_service.dto.Response.*;
import com.shipflow.shipment_service.dto.Request.*;
import com.shipflow.shipment_service.enums.ShipmentStatus;
import com.shipflow.shipment_service.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService service;

    public ShipmentController(ShipmentService service) {
        this.service = service;
    }

    private UUID requireAdminCompanyId(String companyIdHeader, String roleHeader) {

        if (companyIdHeader == null || companyIdHeader.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing X-Company-Id header");
        }
        return UUID.fromString(companyIdHeader);
    }

    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<PublicTrackingResponse> track(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(service.trackPublic(trackingNumber));
    }

    @PostMapping
    public ResponseEntity<ShipmentResponse> create(
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestHeader(value = "X-Company-Name", required = false) String companyName,
            @Valid @RequestBody CreateShipmentRequest request
    ) {
        ShipmentResponse created = service.createShipment(
                requireAdminCompanyId(companyId, role),
                companyName,
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<ShipmentResponse>> list(
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ShipmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(100, Math.max(1, size)));
        return ResponseEntity.ok(
                service.getCompanyShipments(requireAdminCompanyId(companyId, role), search, status, date, pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponse> getById(
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(service.getShipmentById(requireAdminCompanyId(companyId, role), id));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<ShipmentResponse> assignAgent(
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable UUID id,
            @Valid @RequestBody AssignAgentRequest request
    ) {
        return ResponseEntity.ok(service.assignAgent(requireAdminCompanyId(companyId, role), id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ShipmentResponse> updateStatus(
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateShipmentStatusRequest request
    ) {
        return ResponseEntity.ok(service.updateStatusAsAdmin(requireAdminCompanyId(companyId, role), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable UUID id
    ) {
        service.deleteShipment(requireAdminCompanyId(companyId, role), id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShipmentResponse> updateShipment(
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateShipmentRequest request
    ) {
        return ResponseEntity.ok(
                service.updateShipment(
                        requireAdminCompanyId(companyId, role),
                        id,
                        request
                )
        );
    }
}