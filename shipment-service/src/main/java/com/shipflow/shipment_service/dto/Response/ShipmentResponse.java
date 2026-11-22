package com.shipflow.shipment_service.dto.Response;

import com.shipflow.shipment_service.entity.Shipment;
import com.shipflow.shipment_service.enums.ShipmentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ShipmentResponse(
        UUID id,
        String trackingNumber,
        UUID companyId,
        String companyName,
        ShipmentStatus status,
        String senderName,
        String senderPhone,
        String recipientName,
        String recipientPhone,
        String pickupAddress,
        String deliveryAddress,
        String city,
        String currentLocation,
        String packageDescription,
        Double packageWeight,
        BigDecimal shippingPrice,
        String deliveryNotes,
        UUID assignedAgentId,
        String assignedAgentName,
        LocalDate estimatedDeliveryDate,
        Instant createdAt,
        List<ShipmentHistoryItemResponse> history
) {
    public static ShipmentResponse fromEntity(Shipment s) {
        List<ShipmentHistoryItemResponse> historyList = s.getHistory() == null
                ? List.of()
                : s.getHistory().stream().map(ShipmentHistoryItemResponse::fromEntity).toList();

        return new ShipmentResponse(
                s.getId(),
                s.getTrackingNumber(),
                s.getCompanyId(),
                s.getCompanyName(),
                s.getStatus(),
                s.getSenderName(),
                s.getSenderPhone(),
                s.getRecipientName(),
                s.getRecipientPhone(),
                s.getPickupAddress(),
                s.getDeliveryAddress(),
                s.getCity(),
                s.getCurrentLocation(),
                s.getPackageDescription(),
                s.getPackageWeight(),
                s.getShippingPrice(),
                s.getDeliveryNotes(),
                s.getAssignedAgentId(),
                s.getAssignedAgentName(),
                s.getEstimatedDeliveryDate(),
                s.getCreatedAt(),
                historyList
        );
    }
}