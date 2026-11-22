package com.shipflow.shipment_service.dto.Response;


import com.shipflow.shipment_service.entity.ShipmentHistory;
import com.shipflow.shipment_service.enums.ShipmentStatus;

import java.time.Instant;
import java.util.UUID;

public record ShipmentHistoryItemResponse(
        UUID id,
        ShipmentStatus status,
        String location,
        String description,
        Instant timestamp
) {
    public static ShipmentHistoryItemResponse fromEntity(ShipmentHistory h) {
        return new ShipmentHistoryItemResponse(
                h.getId(),
                h.getStatus(),
                h.getLocation(),
                h.getDescription(),
                h.getTimestamp()
        );
    }
}