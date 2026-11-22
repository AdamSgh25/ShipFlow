package com.shipflow.shipment_service.dto.Request;


import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateShipmentRequest(
        @NotBlank String senderName,
        @NotBlank String senderPhone,
        @NotBlank String recipientName,
        @NotBlank String recipientPhone,
        @NotBlank String pickupAddress,
        @NotBlank String deliveryAddress,
        @NotBlank String city,
        @NotBlank String packageDescription,
        @NotNull @Positive Double packageWeight,
        @NotNull @DecimalMin("0.0") BigDecimal shippingPrice,
        String deliveryNotes,
        UUID assignedAgentId,
        String assignedAgentName,
        LocalDate estimatedDeliveryDate
) {}