package com.shipflow.gateway_service.service;

import com.shipflow.gateway_service.dto.Response.AuthResponse;
import com.shipflow.gateway_service.dto.Request.LoginRequest;
import com.shipflow.gateway_service.dto.Request.RegisterCompanyAdminRequest;
import com.shipflow.gateway_service.dto.Response.UserResponse;
import com.shipflow.gateway_service.entity.User;
import com.shipflow.gateway_service.enums.Role;
import com.shipflow.gateway_service.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse registerCompanyAdmin(RegisterCompanyAdminRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        UUID companyId = UUID.randomUUID();

        User admin = new User();
        admin.setCompanyId(companyId);
        admin.setCompanyName(request.companyName().trim());
        admin.setFullName(request.fullName().trim());
        admin.setEmail(normalizedEmail);
        admin.setPassword(passwordEncoder.encode(request.password()));
        admin.setRole(Role.ADMIN);
        admin.setTokenId(UUID.randomUUID().toString());
        admin.setActive(true);

        User savedUser = userRepository.save(admin);
        String accessToken = jwtService.generateToken(savedUser);

        return new AuthResponse(accessToken, UserResponse.fromEntity(savedUser));
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .filter(User::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        user.setTokenId(UUID.randomUUID().toString());
        userRepository.save(user);

        String accessToken = jwtService.generateToken(user);
        return new AuthResponse(accessToken, UserResponse.fromEntity(user));
    }

    @Transactional
    public void logout(User principal) {
        if (principal != null) {
            userRepository.findById(principal.getId()).ifPresent(user -> {
                user.setTokenId(UUID.randomUUID().toString());
                userRepository.save(user);
            });
        }
    }

    public UserResponse getCurrentUser(User principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return UserResponse.fromEntity(principal);
    }
}