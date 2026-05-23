package com.apigateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityHeadersFilter
        implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        return chain.filter(exchange).then(
            Mono.fromRunnable(() -> {
                HttpHeaders headers = exchange
                    .getResponse().getHeaders();

                // Evita que el browser interprete el tipo
                // MIME de forma diferente al declarado
                headers.add(
                    "X-Content-Type-Options", "nosniff");

                // Evita que la app se cargue en un iframe
                headers.add(
                    "X-Frame-Options", "DENY");

                // Fuerza HTTPS por 1 año
                headers.add(
                    "Strict-Transport-Security",
                    "max-age=31536000; includeSubDomains");

                // Solo permite recursos del mismo origen
                headers.add(
                    "Content-Security-Policy",
                    "default-src 'self'");

                // Evita filtrar información del referrer
                headers.add(
                    "Referrer-Policy",
                    "strict-origin-when-cross-origin");

                // Desactiva funciones del browser
                // que no necesita la app
                headers.add(
                    "Permissions-Policy",
                    "camera=(), microphone=(), " +
                    "geolocation=(), payment=()");

                // Evita que el browser cachée
                // respuestas con datos sensibles
                headers.add(
                    "X-XSS-Protection", "1; mode=block");
            })
        );
    }

    // Ejecutar después del rate limiter (orden 1)
    // pero antes de que la respuesta salga
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }
}