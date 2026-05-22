package com.apigateway.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/general")
    @PostMapping("/general")
    public ResponseEntity<Map<String, Object>>
            fallbackGeneral() {
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Map.of(
                "error",   "SERVICIO_NO_DISPONIBLE",
                "mensaje", "El servicio no está disponible " +
                           "en este momento. Por favor " +
                           "intenta en unos minutos.",
                "status",  503
            ));
    }

    @GetMapping("/intensivo")
    @PostMapping("/intensivo")
    public ResponseEntity<Map<String, Object>>
            fallbackIntensivo() {
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Map.of(
                "error",   "CALCULO_NO_DISPONIBLE",
                "mensaje", "El cálculo no pudo completarse " +
                           "en este momento. El servicio " +
                           "se está recuperando. Intenta " +
                           "de nuevo en 1 minuto.",
                "status",  503
            ));
    }
}