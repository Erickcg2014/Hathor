package com.apigateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class CacheHeadersFilter
        implements GlobalFilter, Ordered {

    // ── Endpoints que NUNCA deben cachearse ───────────────
    private static final List<String> NO_CACHE =
        List.of(
            "/api/KPIs",
            "/api/Finanzas",
            "/api/alertas",
            "/api/proyecciones",
            "/api/inversiones",
            "/api/produccion",
            "/api/Benchmarking/calcularBenchmarking",
            "/api/recomendaciones-generales",
            "/api/seed"
        );

    // ── Endpoints semi-estáticos — 5 minutos ─────────────
    private static final List<String> CACHE_CORTO =
        List.of(
            "/api/Benchmarking",
            "/api/CategoriaFinanciera",
            "/api/hato",
            "/api/perfil-productivo"
        );
    
    private static final List<String> CACHE_RANKING =
    List.of(
        "/api/Benchmarking/ranking"  
    );

    // ── Endpoints estáticos — 1 hora ─────────────────────
    private static final List<String> CACHE_LARGO =
        List.of(
            "/api/practica",
            "/api/raza",
            "/api/categoria-ganado",
            "/api/categoria-inventario",
            "/api/reglas"
        );

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String ruta = request.getURI().getPath();

        return chain.filter(exchange).then(
            Mono.fromRunnable(() -> {
                HttpHeaders headers = exchange
                    .getResponse().getHeaders();

                if (esRuta(ruta, NO_CACHE)) {
                    // Nunca cachear — datos en tiempo real
                    headers.add("Cache-Control",
                        "no-store, no-cache, " +
                        "must-revalidate, max-age=0");
                    headers.add("Pragma", "no-cache");
                    headers.add("Expires", "0");

                } else if (esRuta(ruta, CACHE_RANKING)) {   
                    headers.add("Cache-Control",
                        "private, max-age=600, must-revalidate"); 

                } else if (esRuta(ruta, CACHE_CORTO)) {
                    // Cachear 5 minutos
                    headers.add("Cache-Control",
                        "private, max-age=300, " +
                        "must-revalidate");

                } else if (esRuta(ruta, CACHE_LARGO)) {
                    // Cachear 1 hora
                    headers.add("Cache-Control",
                        "private, max-age=3600, " +
                        "must-revalidate");

                } else {
                    // Por defecto — no cachear
                    headers.add("Cache-Control",
                        "no-store");
                }
            })
        );
    }

    // ── Helper ────────────────────────────────────────────

    private boolean esRuta(
            String ruta, List<String> lista) {
        return lista.stream()
            .anyMatch(r ->
                ruta.equals(r) ||
                ruta.startsWith(r + "/"));
    }

    // Ejecutar justo antes de enviar la respuesta
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 2;
    }
}