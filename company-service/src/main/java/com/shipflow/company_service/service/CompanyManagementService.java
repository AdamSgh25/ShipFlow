package com.shipflow.company_service.service;



import com.shipflow.company_service.dto.Response.*;
import com.shipflow.company_service.dto.Request.*;
import com.shipflow.company_service.entity.Company;
import com.shipflow.company_service.entity.DeliveryAgent;
import com.shipflow.company_service.repository.CompanyRepository;
import com.shipflow.company_service.repository.DeliveryAgentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
public class CompanyManagementService {

    private final CompanyRepository companyRepository;
    private final DeliveryAgentRepository deliveryAgentRepository;

    public CompanyManagementService(
            CompanyRepository companyRepository,
            DeliveryAgentRepository deliveryAgentRepository
    ) {
        this.companyRepository = companyRepository;
        this.deliveryAgentRepository = deliveryAgentRepository;
    }


    @Transactional
    public CompanyProfileResponse getProfile(UUID companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        return CompanyProfileResponse.fromEntity(company);
    }

    @Transactional
    public CompanyProfileResponse updateProfile(UUID companyId, UpdateCompanyProfileRequest request) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        company.setName(request.name().trim());
        company.setEmail(request.email().trim().toLowerCase());
        company.setPhone(request.phone().trim());
        company.setAddress(request.address().trim());
        if (request.logoUrl() != null) {
            company.setLogoUrl(request.logoUrl().trim());
        }
        if (request.plan() != null && !request.plan().isBlank()) {
            company.setPlan(request.plan().trim());
        }

        return CompanyProfileResponse.fromEntity(companyRepository.save(company));
    }

    public List<DeliveryAgentResponse> getAgents(UUID companyId, String search, Boolean active) {
        return deliveryAgentRepository.searchAgents(companyId, search, active)
                .stream()
                .map(DeliveryAgentResponse::fromEntity)
                .toList();
    }

    @Transactional
    public DeliveryAgentResponse createAgent(UUID companyId, CreateDeliveryAgentRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        if (deliveryAgentRepository.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Agent email already exists");
        }

        DeliveryAgent agent = new DeliveryAgent();
        agent.setCompanyId(companyId);
        agent.setFullName(request.fullName().trim());
        agent.setEmail(normalizedEmail);
        agent.setPhone(request.phone().trim());
        agent.setVehicleType(request.vehicleType().trim());
        agent.setActive(true);
        agent.setAssignedCount(0);
        agent.setDeliveredCount(0);
        agent.setSuccessRate(100.0);

        return DeliveryAgentResponse.fromEntity(deliveryAgentRepository.save(agent));
    }

    @Transactional
    public DeliveryAgentResponse updateAgent(UUID companyId, UUID agentId, UpdateDeliveryAgentRequest request) {
        DeliveryAgent agent = deliveryAgentRepository.findByIdAndCompanyId(agentId, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery agent not found"));

        String normalizedEmail = request.email().trim().toLowerCase();
        deliveryAgentRepository.findByEmail(normalizedEmail)
                .filter(existing -> !existing.getId().equals(agentId))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already used by another agent");
                });

        agent.setFullName(request.fullName().trim());
        agent.setEmail(normalizedEmail);
        agent.setPhone(request.phone().trim());
        agent.setVehicleType(request.vehicleType().trim());
        if (request.active() != null) {
            agent.setActive(request.active());
        }

        return DeliveryAgentResponse.fromEntity(deliveryAgentRepository.save(agent));
    }

    @Transactional
    public DeliveryAgentResponse toggleAgentStatus(UUID companyId, UUID agentId) {
        DeliveryAgent agent = deliveryAgentRepository.findByIdAndCompanyId(agentId, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery agent not found"));
        agent.setActive(!agent.isActive());
        return DeliveryAgentResponse.fromEntity(deliveryAgentRepository.save(agent));
    }

    @Transactional
    public void deleteAgent(UUID companyId, UUID agentId) {
        DeliveryAgent agent = deliveryAgentRepository.findByIdAndCompanyId(agentId, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery agent not found"));
        deliveryAgentRepository.delete(agent);
    }

    @Transactional
    public CompanyProfileResponse createCompanyFromRegister(InternalCompanyRequest request) {
        if (companyRepository.existsById(request.companyId())) {
            return CompanyProfileResponse.fromEntity(companyRepository.findById(request.companyId()).orElseThrow());
        }

        Company company = new Company();
        company.setId(request.companyId());
        company.setName(request.name().trim());
        company.setEmail(request.email().trim().toLowerCase());
        company.setPhone(request.phone().trim());
        company.setAddress(request.address().trim());
        company.setPlan("Enterprise");

        return CompanyProfileResponse.fromEntity(companyRepository.save(company));
    }

    @Transactional
    public DeliveryAgentResponse setAgentStatus(UUID companyId, UUID agentId, boolean active) {
        DeliveryAgent agent = deliveryAgentRepository.findByIdAndCompanyId(agentId, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery agent not found"));

        agent.setActive(active);
        DeliveryAgent saved = deliveryAgentRepository.save(agent);


        return DeliveryAgentResponse.fromEntity(saved);
    }


}