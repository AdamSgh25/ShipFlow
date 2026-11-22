
package com.shipflow.gateway_service.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.UUID;

@RestController
public class GatewayProxyController {

    private final RestClient restClient;
    private final String companyServiceUrl;
    private final String shipmentServiceUrl;
    private final ProxyService proxyService;
    private final ObjectMapper objectMapper;

    public GatewayProxyController(
            RestClient.Builder restClientBuilder,
            @Value("${app.services.company-url:http://localhost:8081}")
            String companyServiceUrl,
            @Value("${app.services.shipment-url:http://localhost:8082}")
            String shipmentServiceUrl,
            ProxyService proxyService
    ) {
        this.restClient = restClientBuilder.build();
        this.companyServiceUrl = companyServiceUrl;
        this.shipmentServiceUrl = shipmentServiceUrl;
        this.proxyService = proxyService;
        this.objectMapper = new ObjectMapper();
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

        boolean isCompanyRoute =
                path.startsWith("/api/company")
                        || path.startsWith("/api/delivery-agents");

        // Security: Company and Delivery Agents
        if (isCompanyRoute) {

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            if (!"ADMIN".equals(user.getRole().name())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        // Read request body
        byte[] body = StreamUtils.copyToByteArray(
                request.getInputStream()
        );



        // Create Delivery Agent
        if (HttpMethod.POST.matches(request.getMethod())
                && "/api/delivery-agents".equals(path)) {

            UUID id = proxyService.createDeliveryAgent(body, user);
            body = modifyShipmentBody(body,id);
        }

        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        String targetBase = isCompanyRoute
                ? companyServiceUrl
                : shipmentServiceUrl;

        URI targetUri = URI.create(
                targetBase + path
                        + (query != null ? "?" + query : "")
        );

        try {

            byte[] finalBody = body;
            var spec = restClient.method(method)
                    .uri(targetUri)
                    .headers(headers -> {

                        if (finalBody.length > 0) {
                            headers.setContentType(MediaType.APPLICATION_JSON);
                        }

                        if (user != null) {

                            headers.set("X-User-Id", user.getId().toString());
                            headers.set("X-User-Email", user.getEmail());
                            headers.set("X-User-Role", user.getRole().name());

                            if (user.getCompanyId() != null) {
                                headers.set(
                                        "X-Company-Id",
                                        user.getCompanyId().toString()
                                );
                            }

                            if (user.getCompanyName() != null) {
                                headers.set(
                                        "X-Company-Name",
                                        user.getCompanyName()
                                );
                            }
                        }
                    });

            ResponseEntity<byte[]> upstream = (body.length > 0)
                    ? spec.body(body).retrieve().toEntity(byte[].class)
                    : spec.retrieve().toEntity(byte[].class);

            ResponseEntity.BodyBuilder builder =
                    ResponseEntity.status(upstream.getStatusCode());

            if (upstream.getHeaders().getContentType() != null) {
                builder.contentType(
                        upstream.getHeaders().getContentType()
                );
            }

            return builder.body(upstream.getBody());

        } catch (RestClientResponseException ex) {

            ResponseEntity.BodyBuilder builder =
                    ResponseEntity.status(ex.getStatusCode());

            if (ex.getResponseHeaders() != null
                    && ex.getResponseHeaders().getContentType() != null) {

                builder.contentType(
                        ex.getResponseHeaders().getContentType()
                );
            }

            return builder.body(ex.getResponseBodyAsByteArray());
        }
    }

    // Modify shipment JSON body
    private byte[] modifyShipmentBody(byte[] body,UUID id) throws IOException {

        if (body == null || body.length == 0) {
            return body;
        }

        JsonNode jsonNode = objectMapper.readTree(body);

        if (jsonNode instanceof ObjectNode objectNode) {


            objectNode.put("agentId", String.valueOf(id));

        }
        System.out.println(jsonNode.path("agentId").asText());
        return objectMapper.writeValueAsBytes(jsonNode);
    }

    private byte[] getCompanyId(byte[] body,UUID id) throws IOException {

        if (body == null || body.length == 0) {
            return body;
        }

        JsonNode jsonNode = objectMapper.readTree(body);

        if (jsonNode instanceof ObjectNode objectNode) {


            objectNode.put("agentId", String.valueOf(id));

        }
        System.out.println(jsonNode.path("agentId").asText());
        return objectMapper.writeValueAsBytes(jsonNode);
    }
}
