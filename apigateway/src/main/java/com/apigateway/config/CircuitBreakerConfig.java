package com.apigateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfig {

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory>
            defaultCircuitBreaker() {

        return factory -> factory.configureDefault(
            id -> new Resilience4JConfigBuilder(id)

                .timeLimiterConfig(
                TimeLimiterConfig.custom()
                    .timeoutDuration(Duration.ofSeconds(180))
                    .build())

                .circuitBreakerConfig(
                    io.github.resilience4j
                        .circuitbreaker.CircuitBreakerConfig
                        .custom()
                        .slidingWindowSize(20)
                        .slidingWindowType(
                            SlidingWindowType.COUNT_BASED)
                        // Más tolerante — abre con 70% de fallos
                        .failureRateThreshold(70)
                        .slowCallRateThreshold(70)
                        .slowCallDurationThreshold(Duration.ofSeconds(15))
                        .minimumNumberOfCalls(10)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .permittedNumberOfCallsInHalfOpenState(5)
                        .build())

                .build());
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory>
            intensiveCircuitBreaker() {

        return factory -> factory.configure(
            builder -> builder

                .timeLimiterConfig(
                    TimeLimiterConfig.custom()

                        .timeoutDuration(
                            Duration.ofSeconds(30))
                        .build())

                .circuitBreakerConfig(
                    io.github.resilience4j
                        .circuitbreaker.CircuitBreakerConfig
                        .custom()
                        .slidingWindowSize(5)
                        .slidingWindowType(
                            SlidingWindowType.COUNT_BASED)
                        .failureRateThreshold(60)
                        .slowCallRateThreshold(60)
                        .slowCallDurationThreshold(
                            Duration.ofSeconds(20))
                        .waitDurationInOpenState(
                            Duration.ofSeconds(60))
                        .permittedNumberOfCallsInHalfOpenState(2)
                        .minimumNumberOfCalls(3)
                        .build()),

            "kpis-calcular",
            "benchmarking-calcular"
        );
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory>
            asistenteCircuitBreaker() {

        return factory -> factory.configure(
            builder -> builder

                .timeLimiterConfig(
                    TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(50))
                        .build())

                .circuitBreakerConfig(
                    io.github.resilience4j
                        .circuitbreaker.CircuitBreakerConfig
                        .custom()
                        .slidingWindowSize(5)
                        .slidingWindowType(SlidingWindowType.COUNT_BASED)
                        .failureRateThreshold(80)
                        .slowCallRateThreshold(80)
                        .slowCallDurationThreshold(Duration.ofSeconds(40))
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        .permittedNumberOfCallsInHalfOpenState(2)
                        .minimumNumberOfCalls(3)
                        .build()),

            "asistente"
        );
    }
}