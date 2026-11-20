package com.shipflow.shipment_service.repository;



import com.shipflow.shipment_service.entity.Shipment;
import com.shipflow.shipment_service.enums.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    boolean existsByTrackingNumber(String trackingNumber);
    Optional<Shipment> findByIdAndCompanyId(UUID id, UUID companyId);
    List<Shipment> findByCompanyIdAndAssignedAgentId(UUID companyId, UUID assignedAgentId);

    long countByCompanyId(UUID companyId);
    long countByCompanyIdAndStatus(UUID companyId, ShipmentStatus status);
    long countByCompanyIdAndStatusIn(UUID companyId, List<ShipmentStatus> statuses);

    @Query("""
        SELECT s.status, COUNT(s)
        FROM Shipment s
        WHERE s.companyId = :companyId
        GROUP BY s.status
        """)
    List<Object[]> countShipmentsByStatusForCompany(@Param("companyId") UUID companyId);

    @Query("""
        SELECT s FROM Shipment s
        WHERE s.companyId = :companyId
          AND (:status IS NULL OR s.status = :status)
          AND (:search IS NULL OR :search = '' OR LOWER(s.trackingNumber) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:startOfDay IS NULL OR (s.createdAt >= :startOfDay AND s.createdAt < :endOfDay))
        ORDER BY s.createdAt DESC
        """)
    Page<Shipment> findCompanyShipments(
            @Param("companyId") UUID companyId,
            @Param("search") String search,
            @Param("status") ShipmentStatus status,
            @Param("startOfDay") Instant startOfDay,
            @Param("endOfDay") Instant endOfDay,
            Pageable pageable
    );

    @Query("""
        SELECT s FROM Shipment s
        WHERE s.companyId = :companyId
          AND s.assignedAgentId = :agentId
          AND (:status IS NULL OR s.status = :status)
          AND (:search IS NULL OR :search = '' OR LOWER(s.trackingNumber) LIKE LOWER(CONCAT('%', :search, '%')))
        ORDER BY s.createdAt DESC
        """)
    Page<Shipment> findAgentShipments(
            @Param("companyId") UUID companyId,
            @Param("agentId") UUID agentId,
            @Param("search") String search,
            @Param("status") ShipmentStatus status,
            Pageable pageable
    );
}