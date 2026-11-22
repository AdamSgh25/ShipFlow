package com.shipflow.shipment_service.service;

import com.shipflow.shipment_service.dto.Response.*;
import com.shipflow.shipment_service.dto.Request.*;
import com.shipflow.shipment_service.entity.Shipment;
import com.shipflow.shipment_service.enums.ShipmentStatus;
import com.shipflow.shipment_service.repository.ShipmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    public ShipmentService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @Transactional
    public ShipmentResponse createShipment(UUID companyId, String companyName, CreateShipmentRequest req) {
        String trackingNumber;
        do {
            trackingNumber = "SHP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (shipmentRepository.existsByTrackingNumber(trackingNumber));

        Shipment shipment = new Shipment();
        shipment.setTrackingNumber(trackingNumber);
        shipment.setCompanyId(companyId);
        shipment.setCompanyName(companyName != null && !companyName.isBlank() ? companyName : "ShipFlow");
        shipment.setSenderName(req.senderName().trim());
        shipment.setSenderPhone(req.senderPhone().trim());
        shipment.setRecipientName(req.recipientName().trim());
        shipment.setRecipientPhone(req.recipientPhone().trim());
        shipment.setPickupAddress(req.pickupAddress().trim());
        shipment.setDeliveryAddress(req.deliveryAddress().trim());
        shipment.setCity(req.city().trim());
        shipment.setCurrentLocation(req.pickupAddress().trim());
        shipment.setPackageDescription(req.packageDescription().trim());
        shipment.setPackageWeight(req.packageWeight());
        shipment.setShippingPrice(req.shippingPrice());
        shipment.setDeliveryNotes(req.deliveryNotes());
        shipment.setAssignedAgentId(req.assignedAgentId());
        shipment.setAssignedAgentName(req.assignedAgentName());
        shipment.setEstimatedDeliveryDate(req.estimatedDeliveryDate());

        ShipmentStatus initialStatus = (req.assignedAgentId() != null)
                ? ShipmentStatus.PICKED_UP
                : ShipmentStatus.PENDING;
        shipment.setStatus(initialStatus);
        shipment.addHistoryEvent(
                initialStatus,
                req.pickupAddress().trim(),
                initialStatus == ShipmentStatus.PICKED_UP
                        ? "Shipment created and assigned to agent " + req.assignedAgentName()
                        : "Shipment created and pending pickup"
        );

        return ShipmentResponse.fromEntity(shipmentRepository.save(shipment));
    }

    public Page<ShipmentResponse> getCompanyShipments(
            UUID companyId,
            String search,
            ShipmentStatus status,
            LocalDate date,
            Pageable pageable
    ) {
        Instant startOfDay = null;
        Instant endOfDay = null;
        if (date != null) {
            startOfDay = date.atStartOfDay().toInstant(ZoneOffset.UTC);
            endOfDay = date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        }

        return shipmentRepository.findCompanyShipments(companyId, search, status, startOfDay, endOfDay, pageable)
                .map(ShipmentResponse::fromEntity);
    }

    public ShipmentResponse getShipmentById(UUID companyId, UUID shipmentId) {
        Shipment shipment = shipmentRepository.findByIdAndCompanyId(shipmentId, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipment not found"));
        return ShipmentResponse.fromEntity(shipment);
    }

    @Transactional
    public ShipmentResponse assignAgent(UUID companyId, UUID shipmentId, AssignAgentRequest req) {
        Shipment shipment = shipmentRepository.findByIdAndCompanyId(shipmentId, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipment not found"));

        shipment.setAssignedAgentId(req.assignedAgentId());
        shipment.setAssignedAgentName(req.assignedAgentName().trim());
        if (shipment.getStatus() == ShipmentStatus.PENDING) {
            shipment.setStatus(ShipmentStatus.PICKED_UP);
        }
        shipment.addHistoryEvent(
                shipment.getStatus(),
                shipment.getCurrentLocation(),
                "Assigned to delivery agent: " + req.assignedAgentName().trim()
        );

        return ShipmentResponse.fromEntity(shipmentRepository.save(shipment));
    }

    @Transactional
    public ShipmentResponse updateStatusAsAdmin(UUID companyId, UUID shipmentId, UpdateShipmentStatusRequest req) {
        Shipment shipment = shipmentRepository.findByIdAndCompanyId(shipmentId, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipment not found"));
        return applyStatusUpdate(shipment, req);
    }

    public Page<ShipmentResponse> getAgentShipments(
            UUID companyId,
            UUID agentId,
            String search,
            ShipmentStatus status,
            Pageable pageable
    ) {
        String searchPattern = (search == null || search.isBlank())
                ? null
                : "%" + search.trim().toLowerCase() + "%";

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return shipmentRepository.findAgentShipments(companyId, agentId, searchPattern, status, sortedPageable)
                .map(ShipmentResponse::fromEntity);
    }

    @Transactional
    public ShipmentResponse updateStatusAsAgent(
            UUID companyId,
            UUID agentId,
            UUID shipmentId,
            UpdateShipmentStatusRequest req
    ) {
        Shipment shipment = shipmentRepository.findByIdAndCompanyId(shipmentId, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipment not found"));

        if (!agentId.equals(shipment.getAssignedAgentId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Shipment is not assigned to this agent");
        }

        return applyStatusUpdate(shipment, req);
    }

    private ShipmentResponse applyStatusUpdate(Shipment shipment, UpdateShipmentStatusRequest req) {
        shipment.setStatus(req.status());
        if (req.location() != null && !req.location().isBlank()) {
            shipment.setCurrentLocation(req.location().trim());
        }
        String loc = shipment.getCurrentLocation() != null ? shipment.getCurrentLocation() : shipment.getCity();
        String desc = (req.description() != null && !req.description().isBlank())
                ? req.description().trim()
                : "Status updated to " + req.status().name();

        shipment.addHistoryEvent(req.status(), loc, desc);
        return ShipmentResponse.fromEntity(shipmentRepository.save(shipment));
    }

    @Transactional
    public void deleteShipment(UUID companyId, UUID shipmentId) {
        Shipment shipment = shipmentRepository.findByIdAndCompanyId(shipmentId, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipment not found"));
        shipmentRepository.delete(shipment);
    }

    public PublicTrackingResponse trackPublic(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tracking number not found"));
        return PublicTrackingResponse.fromEntity(shipment);
    }

    public DashboardStatsResponse getDashboardStats(UUID companyId) {
        return new DashboardStatsResponse(
                shipmentRepository.countByCompanyId(companyId),
                shipmentRepository.countByCompanyIdAndStatus(companyId, ShipmentStatus.PENDING),
                shipmentRepository.countByCompanyIdAndStatus(companyId, ShipmentStatus.PICKED_UP),
                shipmentRepository.countByCompanyIdAndStatus(companyId, ShipmentStatus.IN_TRANSIT),
                shipmentRepository.countByCompanyIdAndStatus(companyId, ShipmentStatus.ARRIVED_AT_HUB),
                shipmentRepository.countByCompanyIdAndStatus(companyId, ShipmentStatus.OUT_FOR_DELIVERY),
                shipmentRepository.countByCompanyIdAndStatus(companyId, ShipmentStatus.DELIVERED),
                shipmentRepository.countByCompanyIdAndStatus(companyId, ShipmentStatus.FAILED_DELIVERY),
                shipmentRepository.countByCompanyIdAndStatus(companyId, ShipmentStatus.CANCELLED)
        );
    }


    @Transactional
    public ShipmentResponse updateShipment(
            UUID companyId,
            UUID shipmentId,
            UpdateShipmentRequest request
    ) {

        Shipment shipment = shipmentRepository
                .findByIdAndCompanyId(shipmentId, companyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Shipment not found"
                ));

        shipment.setSenderName(request.senderName().trim());
        shipment.setSenderPhone(request.senderPhone().trim());

        shipment.setRecipientName(request.recipientName().trim());
        shipment.setRecipientPhone(request.recipientPhone().trim());

        shipment.setPickupAddress(request.pickupAddress().trim());
        shipment.setDeliveryAddress(request.deliveryAddress().trim());

        shipment.setCity(request.city());
        shipment.setCurrentLocation(request.currentLocation());
        shipment.setDeliveryNotes(request.deliveryNotes());

        shipment.setPackageDescription(
                request.packageDescription().trim()
        );

        shipment.setPackageWeight(request.packageWeight());
        shipment.setShippingPrice(request.shippingPrice());

        shipment.setEstimatedDeliveryDate(
                request.estimatedDeliveryDate()
        );

        Shipment updatedShipment = shipmentRepository.save(shipment);

        return ShipmentResponse.fromEntity(updatedShipment);
    }

}