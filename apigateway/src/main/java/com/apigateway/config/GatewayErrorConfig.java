package com.apigateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Configuration
public class GatewayErrorConfig {

    @Bean
    @Order(-1)
    public ErrorWebExceptionHandler rateLimitErrorHandler() {
        return (exchange, ex) -> {

            if (ex instanceof ResponseStatusException rse &&
                rse.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {

                exchange.getResponse()
                    .setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                exchange.getResponse().getHeaders()
                    .setContentType(MediaType.APPLICATION_JSON);
                exchange.getResponse().getHeaders()
                    .add("Retry-After", "60");
                exchange.getResponse().getHeaders()
                    .add("X-RateLimit-Limit", "100");

                try {
                    byte[] bytes = new ObjectMapper()
                        .writeValueAsBytes(Map.of(
                            "error",   "TOO_MANY_REQUESTS",
                            "mensaje", "Has excedido el límite " +
                                "de peticiones. Espera 1 minuto " +
                                "antes de intentar de nuevo.",
                            "status",  429
                        ));
                    DataBuffer buffer = exchange.getResponse()
                        .bufferFactory().wrap(bytes);
                    return exchange.getResponse().writeWith(
                        Mono.just(buffer));
                } catch (Exception e) {
                    return Mono.error(e);
                }
            }
            return Mono.error(ex);
        };
    }
}