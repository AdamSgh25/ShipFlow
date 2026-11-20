package com.shipflow.company_service.repository;


import com.shipflow.company_service.entity.DeliveryAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, UUID> {
    List<DeliveryAgent> findByCompanyId(UUID companyId);
    List<DeliveryAgent> findByCompanyIdAndActive(UUID companyId, boolean active);
    Optional<DeliveryAgent> findByIdAndCompanyId(UUID id, UUID companyId);
    Optional<DeliveryAgent> findByEmail(String email);
    boolean existsByEmail(String email);
    long countByCompanyId(UUID companyId);
    long countByCompanyIdAndActive(UUID companyId, boolean active);

    @Query("""
        SELECT a FROM DeliveryAgent a
        WHERE a.companyId = :companyId
          AND (:active IS NULL OR a.active = :active)
          AND (
               :search IS NULL OR :search = ''
               OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(a.email) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(a.phone) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(a.vehicleType) LIKE LOWER(CONCAT('%', :search, '%'))
          )
        ORDER BY a.createdAt DESC
        """)
    List<DeliveryAgent> searchAgents(
            @Param("companyId") UUID companyId,
            @Param("search") String search,
            @Param("active") Boolean active
    );
}