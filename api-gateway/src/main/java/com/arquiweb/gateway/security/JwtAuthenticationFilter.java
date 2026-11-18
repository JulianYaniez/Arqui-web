package com.arquiweb.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String HEADER_USER_ID= "X-User-Id";
    private static final String HEADER_USER_ROLE = "X-User-Role";
    private static final String HEADER_USER_EMAIL = "X-User-Email";

    private final JwtValidator jwtValidator;
    public JwtAuthenticationFilter(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // Anti Spoofing
        ServerHttpRequest sanitizedRequest = request.mutate()
                .headers(headers -> {
                    headers.remove(HEADER_USER_ID);
                    headers.remove(HEADER_USER_ROLE);
                })
                .build();

        ServerWebExchange sanitizedExchange = exchange.mutate()
                .request(sanitizedRequest)
                .build();

        String authHeader = sanitizedRequest.getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        // Allow anonymous connections
        if (authHeader == null) {
            return chain.filter(exchange);
        }

        // Right authorization schema
        if (!authHeader.startsWith(BEARER_PREFIX)) {
            return rejectUnauthorized(exchange);
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();

        if (token.isEmpty()) {
            return rejectUnauthorized(exchange);
        }

        try {
            // Parse Claims
            Claims claims = jwtValidator.validateAndExtractClaims(token);

            String userId = claims.getSubject();
            String userRole = claims.get("role", String.class);
            String userEmail = claims.get("email", String.class);

            // Inject Headers
            ServerHttpRequest authenticatedRequest = sanitizedRequest.mutate()
                    .header(HEADER_USER_ID, userId)
                    .header(HEADER_USER_ROLE, userRole)
                    .header(HEADER_USER_EMAIL, userEmail)
                    .build();

            return  chain.filter(sanitizedExchange.mutate()
                    .request(authenticatedRequest)
                    .build());
        } catch (JwtException | IllegalArgumentException e) {
            return rejectUnauthorized(exchange);
        }

    }

    private Mono<Void> rejectUnauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
