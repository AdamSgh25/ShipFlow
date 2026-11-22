package com.shipflow.shipment_service.dto.Request;


import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateShipmentRequest(

        @NotBlank
        String senderName,

        @NotBlank
        String senderPhone,

        @NotBlank
        String recipientName,

        @NotBlank
        String recipientPhone,

        @NotBlank
        String pickupAddress,

        @NotBlank
        String deliveryAddress,

        String city,

        String currentLocation,

        String deliveryNotes,

        @NotBlank
        String packageDescription,

        @NotNull
        @Positive
        double packageWeight,

        @NotNull
        @PositiveOrZero
        BigDecimal shippingPrice,

        LocalDate estimatedDeliveryDate

) {}
