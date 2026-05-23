package com.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader != null &&
                authHeader.startsWith("Bearer ")) {
                try {
                    String token = authHeader.substring(7);
                    String[] parts = token.split("\\.");
                    if (parts.length >= 2) {
                        String payload = new String(
                            Base64.getUrlDecoder()
                                .decode(parts[1]));
                        int subIdx = payload.indexOf("\"sub\":");
                        if (subIdx >= 0) {
                            String sub = payload
                                .substring(subIdx + 7);
                            int endIdx = sub.indexOf("\"", 1);
                            if (endIdx > 0) {
                                return Mono.just(
                                    sub.substring(1, endIdx));
                            }
                        }
                    }
                } catch (Exception e) {
                    // Si falla → usar IP
                }
            }

            // Fallback — usar IP del cliente
            return Mono.just(
                exchange.getRequest()
                    .getRemoteAddress() != null
                ? exchange.getRequest()
                    .getRemoteAddress()
                    .getAddress()
                    .getHostAddress()
                : "unknown");
        };
    }
}