package com.hathor.hathorback.Servicios.Usuario.Finanzas.VentaLeche;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.function.EntityResponse;

import com.hathor.hathorback.Entities.Finanzas.VentaLeche;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.VentaLeche.DTO.RegistroVentaLecheDTO;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.VentaLeche.DTO.RespuestaVentaLecheDTO;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.VentaLeche.Service.IServiceVentaLeche;


@RestController
@RequestMapping("/VentaLeche")
public class ControllerVentaLeche {

    @Autowired
    IServiceVentaLeche serviceVentaLeche;

    @PostMapping("/")
    public ResponseEntity<VentaLeche> createVentaLeche(@RequestBody VentaLeche ventaleche) {        
        return new ResponseEntity<VentaLeche>(serviceVentaLeche.createVentaLeche(ventaleche), HttpStatus.CREATED);
    }

    @GetMapping("/hato/{idHato}")
    public ResponseEntity<List<VentaLeche>> getByHato(@PathVariable UUID idHato) {
        return ResponseEntity.ok(serviceVentaLeche.getByHato(idHato));
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarVenta(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody RegistroVentaLecheDTO dto) {
        try {
            String email = jwt.getClaim("email");
            RespuestaVentaLecheDTO respuesta =
                serviceVentaLeche.registrarVenta(dto, email);
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}
