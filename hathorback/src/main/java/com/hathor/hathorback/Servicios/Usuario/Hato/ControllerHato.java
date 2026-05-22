package com.hathor.hathorback.Servicios.Usuario.Hato;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Usuario.Usuario;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO.HatoAnonimizadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Hato.DTO.CostosFijosDTO;
import com.hathor.hathorback.Servicios.Usuario.Hato.DTO.InfraestructuraBasicaDTO;
import com.hathor.hathorback.Servicios.Usuario.Hato.DTO.RegistroHatoDTO;
import com.hathor.hathorback.Servicios.Usuario.Hato.Service.IServiceHato;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/Hato")
public class ControllerHato {

    @Autowired
    IServiceHato serviceHato;
    
    @PostMapping
    public ResponseEntity<Hato> createHato(@AuthenticationPrincipal Jwt jwt, HttpServletRequest request, @RequestBody RegistroHatoDTO hato) {
        String email = jwt.getClaim("email");
        return new ResponseEntity<Hato>(serviceHato.createHato(hato, email), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<List<Hato>> HatosByIdUsuario (@AuthenticationPrincipal Jwt jwt, HttpServletRequest request) {
        String email = jwt.getClaim("email");
        Usuario usuario = serviceHato.findUsuarioByTokenEmail(email);
        return new ResponseEntity<List<Hato>>(serviceHato.findByUsuario_IdUsuario(usuario.getIdUsuario()), HttpStatus.OK);
    }
    
    @GetMapping("/mapa-general")
    public ResponseEntity<List<HatoAnonimizadoDTO>> getMapaGeneral() {
        return ResponseEntity.ok()
            .cacheControl(org.springframework.http.CacheControl.noStore())
            .body(serviceHato.getMapaGeneral());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHatoById(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID id
    ) {
        try {
            String email = jwt.getClaim("email");
            return ResponseEntity.ok(serviceHato.findHatoById(id, email));
        } catch (RuntimeException e) {
            if ("HATO_NO_ENCONTRADO".equals(e.getMessage()))
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Hato no encontrado"));
            if ("HATO_NO_AUTORIZADO".equals(e.getMessage()))
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "No tienes acceso a este hato"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    @PutMapping("/{idHato}/completitud")
    public ResponseEntity<?> actualizarCompletitud(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID idHato,
        @RequestBody Map<String, Integer> body
    ) {
        try {
            String email = jwt.getClaim("email");
            Integer porcentaje = body.get("porcentaje");
            if (porcentaje == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "El campo porcentaje es requerido"));
            }
            return ResponseEntity.ok(serviceHato.actualizarCompletitud(idHato, porcentaje, email));
        } catch (RuntimeException e) {
            if ("HATO_NO_ENCONTRADO".equals(e.getMessage()))
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Hato no encontrado"));
            if ("HATO_NO_AUTORIZADO".equals(e.getMessage()))
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "No tienes acceso a este hato"));
            if ("PORCENTAJE_INVALIDO".equals(e.getMessage()))
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "El porcentaje debe estar entre 0 y 100"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    @PutMapping("/{idHato}/infraestructura-basica")
    public ResponseEntity<?> actualizarInfraestructuraBasica(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID idHato,
        @RequestBody InfraestructuraBasicaDTO dto
    ) {
        try {
            String email = jwt.getClaim("email");
            return ResponseEntity.ok(
                serviceHato.actualizarInfraestructuraBasica(idHato, dto, email)
            );
        } catch (RuntimeException e) {
            if ("HATO_NO_ENCONTRADO".equals(e.getMessage()))
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Hato no encontrado"));
            if ("HATO_NO_AUTORIZADO".equals(e.getMessage()))
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "No tienes acceso a este hato"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // =========== COSTOS FIJOS =========
    @PutMapping("/{idHato}/costos-fijos")
    public ResponseEntity<Hato> actualizarCostosFijos(
        @PathVariable UUID idHato,
        @RequestBody CostosFijosDTO dto,
        @AuthenticationPrincipal Jwt jwt
    ) {
        String email = jwt.getClaim("email");
        return ResponseEntity.ok(serviceHato.actualizarCostosFijos(idHato, dto, email));
    }

}
