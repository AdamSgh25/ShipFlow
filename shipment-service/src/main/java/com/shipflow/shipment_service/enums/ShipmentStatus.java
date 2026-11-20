package com.shipflow.shipment_service.enums;

public enum ShipmentStatus {
    PENDING,
    PICKED_UP,
    IN_TRANSIT,
    ARRIVED_AT_HUB,
    OUT_FOR_DELIVERY,
    DELIVERED,
    FAILED_DELIVERY,
    CANCELLED
}