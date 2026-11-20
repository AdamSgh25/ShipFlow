package com.shipflow.shipment_service.repository;


import com.shipflow.shipment_service.entity.ShipmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShipmentHistoryRepository extends JpaRepository<ShipmentHistory, UUID> {
    List<ShipmentHistory> findByShipmentIdOrderByTimestampDesc(UUID shipmentId);
    List<ShipmentHistory> findTop10ByShipmentCompanyIdOrderByTimestampDesc(UUID companyId);
}