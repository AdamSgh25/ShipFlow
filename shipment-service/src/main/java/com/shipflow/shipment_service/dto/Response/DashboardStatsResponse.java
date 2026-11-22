package com.shipflow.shipment_service.dto.Response;

public record DashboardStatsResponse(
        long totalShipments,
        long pending,
        long pickedUp,
        long inTransit,
        long arrivedAtHub,
        long outForDelivery,
        long delivered,
        long failedDelivery,
        long cancelled
) {}