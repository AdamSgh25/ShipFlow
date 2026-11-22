package com.shipflow.shipment_service.dto.Request;


import com.shipflow.shipment_service.enums.ShipmentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateShipmentStatusRequest(
        @NotNull ShipmentStatus status,
        String location,
        String description
) {}