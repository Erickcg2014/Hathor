package com.apigateway.config;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import java.util.List;

@Configuration
public class BackendInstancesConfig {

    @Bean
    public ServiceInstanceListSupplier serviceInstanceListSupplier() {
        return new ServiceInstanceListSupplier() {

            @Override
            public String getServiceId() {
                return "hathor-backend";
            }

           @Override
            public Flux<List<ServiceInstance>> get() {
                return Flux.just(List.of(

                    // ── LOCAL 
                    new DefaultServiceInstance(
                        "backend-1", "hathor-backend",
                        "localhost", 8080, false)
                    // new DefaultServiceInstance(
                    //     "backend-1", "hathor-backend",
                    //     "playset-polyester-sermon.ngrok-free.dev", 443, true), // DANIEL
                    // new DefaultServiceInstance(
                    //     "backend-2", "hathor-backend",
                    //     "neriah-burriest-sentiently.ngrok-free.dev", 443, true)  // FABIO
                ));
            }
        };
    }
}