package com.shipflow.company_service.repository;


import com.shipflow.company_service.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Optional<Company> findByEmail(String email);
    boolean existsByEmail(String email);
}