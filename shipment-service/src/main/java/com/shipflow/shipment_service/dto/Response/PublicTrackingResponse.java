package com.shipflow.shipment_service.dto.Response;

import com.shipflow.shipment_service.entity.Shipment;
import com.shipflow.shipment_service.enums.ShipmentStatus;

import java.time.LocalDate;
import java.util.List;

public record PublicTrackingResponse(
        String trackingNumber,
        String companyName,
        ShipmentStatus status,
        String city,
        String currentLocation,
        LocalDate estimatedDeliveryDate,
        List<ShipmentHistoryItemResponse> history
) {
    public static PublicTrackingResponse fromEntity(Shipment s) {
        List<ShipmentHistoryItemResponse> historyList = s.getHistory() == null
                ? List.of()
                : s.getHistory().stream().map(ShipmentHistoryItemResponse::fromEntity).toList();

        return new PublicTrackingResponse(
                s.getTrackingNumber(),
                s.getCompanyName(),
                s.getStatus(),
                s.getCity(),
                s.getCurrentLocation(),
                s.getEstimatedDeliveryDate(),
                historyList
        );
    }
}