package com.apigateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
public class JwtValidationFilter
        implements GlobalFilter, Ordered {

    // Rutas que NO requieren JWT
    private static final List<String> RUTAS_PUBLICAS =
        List.of(
            "/api/Usuario",
            "/api/Usuario/",
            "/api/seed"
        );

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        // Permitir OPTIONS — preflight CORS
        if (HttpMethod.OPTIONS.equals(
                request.getMethod())) {
            return chain.filter(exchange);
        }

        // Permitir rutas públicas
        String ruta = request.getURI().getPath();
        if (esRutaPublica(ruta)) {
            return chain.filter(exchange);
        }

        // Verificar header Authorization
        String authHeader = request.getHeaders()
            .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null ||
            !authHeader.startsWith("Bearer ")) {
            return rechazar(exchange,
                HttpStatus.UNAUTHORIZED,
                "TOKEN_REQUERIDO",
                "Se requiere autenticación para " +
                "acceder a este recurso.");
        }

        String token = authHeader.substring(7);

        // Validar estructura del JWT
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return rechazar(exchange,
                HttpStatus.UNAUTHORIZED,
                "TOKEN_INVALIDO",
                "El token de autenticación " +
                "no tiene un formato válido.");
        }

        // Validar expiración
        try {
            String payload = new String(
                Base64.getUrlDecoder()
                    .decode(parts[1]));

            // Extraer claim exp
            int expIdx = payload.indexOf("\"exp\":");
            if (expIdx >= 0) {
                String expStr = payload
                    .substring(expIdx + 6)
                    .replaceAll("[^0-9].*", "")
                    .trim();

                long exp = Long.parseLong(expStr);
                long ahora = System.currentTimeMillis()
                    / 1000;

                if (ahora > exp) {
                    return rechazar(exchange,
                        HttpStatus.UNAUTHORIZED,
                        "TOKEN_EXPIRADO",
                        "Tu sesión ha expirado. " +
                        "Por favor inicia sesión " +
                        "nuevamente.");
                }
            }

        } catch (Exception e) {
            return rechazar(exchange,
                HttpStatus.UNAUTHORIZED,
                "TOKEN_INVALIDO",
                "No se pudo validar el token " +
                "de autenticación.");
        }

        // Token válido — continuar
        return chain.filter(exchange);
    }

    // ── Helpers ───────────────────────────────────────────

    private boolean esRutaPublica(String ruta) {
        return RUTAS_PUBLICAS.stream()
            .anyMatch(publica ->
                ruta.equals(publica) ||
                ruta.startsWith(publica + "/"));
    }

    private Mono<Void> rechazar(
            ServerWebExchange exchange,
            HttpStatus status,
            String codigo,
            String mensaje) {

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders()
            .setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = mapper.writeValueAsBytes(
                Map.of(
                    "error",   codigo,
                    "mensaje", mensaje,
                    "status",  status.value()
                ));
            DataBuffer buffer = exchange.getResponse()
                .bufferFactory().wrap(bytes);
            return exchange.getResponse()
                .writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return exchange.getResponse().setComplete();
        }
    }

    // Ejecutar después del logging (orden 2)
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}