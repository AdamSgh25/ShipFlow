package com.shipflow.shipment_service.controller;

import com.shipflow.shipment_service.dto.Response.DashboardStatsResponse;
import com.shipflow.shipment_service.service.ShipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardStatsController {

    private final ShipmentService service;

    public DashboardStatsController(ShipmentService service) {
        this.service = service;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> stats(
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN can view dashboard stats");
        }
        if (companyId == null || companyId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing X-Company-Id header");
        }
        return ResponseEntity.ok(service.getDashboardStats(UUID.fromString(companyId)));
    }
}