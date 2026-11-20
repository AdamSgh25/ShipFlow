package com.shipflow.gateway_service.controller;

import com.shipflow.gateway_service.dto.Response.AuthResponse;
import com.shipflow.gateway_service.dto.Request.LoginRequest;
import com.shipflow.gateway_service.dto.Request.RegisterCompanyAdminRequest;
import com.shipflow.gateway_service.dto.Response.UserResponse;
import com.shipflow.gateway_service.entity.User;
import com.shipflow.gateway_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterCompanyAdminRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerCompanyAdmin(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal User user) {
        authService.logout(user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(authService.getCurrentUser(user));
    }

    @GetMapping("/test")
    public String moo() {
        return "its working";
    }
}