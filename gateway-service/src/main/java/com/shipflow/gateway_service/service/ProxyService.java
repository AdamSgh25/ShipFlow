package com.shipflow.gateway_service.service;

import com.shipflow.gateway_service.entity.User;
import com.shipflow.gateway_service.enums.Role;
import com.shipflow.gateway_service.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

@Service
public class ProxyService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProxyService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }




    public void createDeliveryAgent(byte[] body, User user) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        JsonNode json = mapper.readTree(body);

        String email = json.path("email").asText();
        String fullName = json.path("fullName").asText();
        String password = json.path("password").asText();
        String companyName = json.path("companyName").asText();
        String companyId = json.path("companyId").asText();

        String normalizedEmail = email.trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User email already exists"
            );
        }

        String rawPassword = (password != null && !password.isBlank())
                ? password
                : "Agent123!";

        User agentUser = new User();

        agentUser.setCompanyId(UUID.fromString(UUID.randomUUID().toString()));
        agentUser.setCompanyName(companyName);
        agentUser.setFullName(fullName.trim());
        agentUser.setEmail(normalizedEmail);
        agentUser.setPassword(passwordEncoder.encode(rawPassword));
        agentUser.setRole(Role.DELIVERY_AGENT);
        agentUser.setTokenId(UUID.randomUUID().toString());
        agentUser.setActive(true);

        userRepository.save(agentUser);
    }

}
