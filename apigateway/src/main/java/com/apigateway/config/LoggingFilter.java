package com.apigateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Component
public class LoggingFilter
        implements GlobalFilter, Ordered {

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        long inicio = System.currentTimeMillis();

        // Extraer datos del request
        String metodo  = request.getMethod().name();
        String ruta    = request.getURI().getPath();
        String ip      = extraerIp(request);
        String userId  = extraerUserId(request);
        String momento = LocalDateTime.now().format(FMT);

        return chain.filter(exchange).then(
            Mono.fromRunnable(() -> {
                long tiempo = System.currentTimeMillis()
                    - inicio;
                int status  = exchange.getResponse()
                    .getStatusCode() != null
                    ? exchange.getResponse()
                        .getStatusCode().value()
                    : 0;

                // Formato del log
                String log = String.format(
                    "[%s] %s %s | IP: %s | User: %s " +
                    "| Status: %d | Time: %dms",
                    momento, metodo, ruta,
                    ip, userId, status, tiempo);

                // Color según status
                if (status >= 500) {
                    System.err.println("🔴 " + log);
                } else if (status >= 400) {
                    System.out.println("🟡 " + log);
                } else if (status >= 300) {
                    System.out.println("🔵 " + log);
                } else {
                    System.out.println("🟢 " + log);
                }

                // Log adicional para requests lentos
                if (tiempo > 3000) {
                    System.out.println(
                        "REQUEST LENTO: " + metodo +
                        " " + ruta + " tardó " +
                        tiempo + "ms");
                }
            })
        );
    }

    // ── Helpers ───────────────────────────────────────────

    private String extraerIp(ServerHttpRequest request) {
        // Verificar header X-Forwarded-For primero
        // (cuando hay proxy o load balancer)
        String forwarded = request.getHeaders()
            .getFirst("X-Forwarded-For");

        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }

        return request.getRemoteAddress() != null
            ? request.getRemoteAddress()
                .getAddress().getHostAddress()
            : "unknown";
    }

    private String extraerUserId(
            ServerHttpRequest request) {
        String authHeader = request.getHeaders()
            .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null ||
            !authHeader.startsWith("Bearer ")) {
            return "anonymous";
        }

        try {
            String token   = authHeader.substring(7);
            String[] parts = token.split("\\.");
            if (parts.length < 2) return "invalid-token";

            String payload = new String(
                Base64.getUrlDecoder().decode(parts[1]));

            // Extraer sub del JSON
            int subIdx = payload.indexOf("\"sub\":");
            if (subIdx < 0) return "no-sub";

            String sub    = payload.substring(subIdx + 7);
            int    endIdx = sub.indexOf("\"", 1);
            if (endIdx < 0) return "no-sub";

            String userId = sub.substring(1, endIdx);
            // Truncar UUID para el log
            return userId.length() > 8
                ? userId.substring(0, 8) + "..."
                : userId;

        } catch (Exception e) {
            return "parse-error";
        }
    }

    // Ejecutar primero — antes que cualquier filtro
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}