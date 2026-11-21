package com.shipflow.gateway_service.controller;

import com.shipflow.gateway_service.entity.User;
import com.shipflow.gateway_service.service.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.io.IOException;
import java.net.URI;

@RestController
public class GatewayProxyController {

    private final RestClient restClient;
    private final String companyServiceUrl;
    private final String shipmentServiceUrl;
    private final ProxyService proxyService;

    public GatewayProxyController(
            RestClient.Builder restClientBuilder,
            @Value("${app.services.company-url:http://localhost:8081}") String companyServiceUrl,
            @Value("${app.services.shipment-url:http://localhost:8082}") String shipmentServiceUrl, ProxyService proxyService
    ) {
        this.restClient = restClientBuilder.build();
        this.companyServiceUrl = companyServiceUrl;
        this.shipmentServiceUrl = shipmentServiceUrl;
        this.proxyService = proxyService;
    }

    @RequestMapping({
            "/api/company/**",
            "/api/delivery-agents/**",
            "/api/shipments/**",
            "/api/dashboard/**",
            "/api/agent/**"
    })
    public ResponseEntity<byte[]> proxy(
            HttpServletRequest request,
            @AuthenticationPrincipal User user
    ) throws IOException {

        String path = request.getRequestURI();
        String query = request.getQueryString();

        boolean isCompanyRoute = path.startsWith("/api/company") || path.startsWith("/api/delivery-agents");

        // Protection rapide côté Gateway pour les routes Company & Delivery Agents
        if (isCompanyRoute) {
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            if (!"ADMIN".equals(user.getRole().name())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        }

        /**if (HttpMethod.POST.matches(request.getMethod())
                && "/api/delivery-agents".equals(path)) {

            proxyService.createDeliveryAgent(request, user);
        }**/

        String targetBase = isCompanyRoute ? companyServiceUrl : shipmentServiceUrl;
        URI targetUri = URI.create(targetBase + path + (query != null ? "?" + query : ""));

        byte[] body = StreamUtils.copyToByteArray(request.getInputStream());
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        try {
            var spec = restClient.method(method)
                    .uri(targetUri)
                    .headers(headers -> {
                        if (body.length > 0) {
                            headers.setContentType(MediaType.APPLICATION_JSON);
                        }
                        if (user != null) {
                            headers.set("X-User-Id", user.getId().toString());
                            headers.set("X-User-Email", user.getEmail());
                            headers.set("X-User-Role", user.getRole().name());
                            if (user.getCompanyId() != null) {
                                headers.set("X-Company-Id", user.getCompanyId().toString());
                            }
                        }
                    });

            ResponseEntity<byte[]> upstream = (body.length > 0)
                    ? spec.body(body).retrieve().toEntity(byte[].class)
                    : spec.retrieve().toEntity(byte[].class);

            ResponseEntity.BodyBuilder builder = ResponseEntity.status(upstream.getStatusCode());
            if (upstream.getHeaders().getContentType() != null) {
                builder.contentType(upstream.getHeaders().getContentType());
            }
            return builder.body(upstream.getBody());
        } catch (RestClientResponseException ex) {
            ResponseEntity.BodyBuilder builder = ResponseEntity.status(ex.getStatusCode());
            if (ex.getResponseHeaders() != null && ex.getResponseHeaders().getContentType() != null) {
                builder.contentType(ex.getResponseHeaders().getContentType());
            }
            return builder.body(ex.getResponseBodyAsByteArray());
        }
    }
}