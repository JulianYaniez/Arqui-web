package com.arquiweb.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        ServerHttpRequest request = exchange.getRequest();
        String method = request.getMethod().name();
        String path = request.getURI().getPath();

        String headerUserId = request.getHeaders().getFirst("X-User-Id");
        String userId = (headerUserId != null) ? headerUserId : "anonymous";

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long durationMs = System.currentTimeMillis() - startTime;
            HttpStatusCode status = exchange.getResponse().getStatusCode();
            int statusCode = (status != null) ? status.value() : 500;

            log.info("HTTP {} {} | user={} | status={} | duration={}ms",
                    method, path, userId, statusCode, durationMs);
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }
}
