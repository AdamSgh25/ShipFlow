package com.shipflow.gateway_service.service;

import com.shipflow.gateway_service.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret:SECRET_KEY_123_SECRET_KEY_123_SECRET_KEY_123_MIN_32_CHARS}") String secret,
            @Value("${app.jwt.access-expiration-ms:86400000}") long expirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public SecretKey getKey() {
        return key;
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .id(user.getTokenId())
                .claim("role", user.getRole().name())
                .claim("companyId", user.getCompanyId() != null ? user.getCompanyId().toString() : null)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Date getExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }
}