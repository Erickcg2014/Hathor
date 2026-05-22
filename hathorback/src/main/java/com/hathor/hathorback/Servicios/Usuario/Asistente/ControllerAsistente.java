package com.hathor.hathorback.Servicios.Usuario.Asistente;

import com.hathor.hathorback.Servicios.Usuario.Asistente.DTO.ChatRequestDTO;
import com.hathor.hathorback.Servicios.Usuario.Asistente.DTO.ChatResponseDTO;
import com.hathor.hathorback.Servicios.Usuario.Asistente.DTO.PracticaIARequestDTO;
import com.hathor.hathorback.Servicios.Usuario.Asistente.DTO.PracticaIAResponseDTO;
import com.hathor.hathorback.Servicios.Usuario.Asistente.DTO.ChatHistorialDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.hathor.hathorback.Servicios.Usuario.Asistente.Service.IAsistenteService;
import com.hathor.hathorback.Servicios.Usuario.Asistente.Service.IPracticaIAService;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/asistente")
public class ControllerAsistente {

    @Autowired
    private IAsistenteService asistenteService;

    @Autowired
    private IPracticaIAService practicaIAService;

    // POST /api/asistente/chat
    @PostMapping("/chat")
    public ResponseEntity<ChatResponseDTO> chat(
            @RequestBody ChatRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            String email = jwt.getClaim("email");
            ChatResponseDTO response = asistenteService.chat(request, email);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if ("HATO_NO_ENCONTRADO".equals(e.getMessage()))
                return ResponseEntity.notFound().build();
            return ResponseEntity.status(500).build();
        }
    }

    // DELETE /api/asistente/conversacion/{idHato}
    @DeleteMapping("/conversacion/{idHato}")
    public ResponseEntity<Void> limpiarConversacion(
            @PathVariable String idHato,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            String email = jwt.getClaim("email");
            asistenteService.limpiarConversacion(idHato, email);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // POST /api/asistente/generar-practica
    @PostMapping("/generar-practica")
    public ResponseEntity<?> generarPractica(
            @RequestBody PracticaIARequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            String email = jwt.getClaim("email");
            PracticaIAResponseDTO response =
                practicaIAService.generarPractica(request, email);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return switch (e.getMessage()) {
                case "HATO_NO_ENCONTRADO"        -> ResponseEntity.notFound().build();
                case "KPI_NO_ENCONTRADO"         -> ResponseEntity.badRequest()
                    .body("KPI no encontrado para este hato");
                case "PRACTICA_YA_ASIGNADA"      -> ResponseEntity.status(409)
                    .body("Esta práctica ya está asignada al hato");
                case "PRACTICA_JSON_INVALIDO"    -> ResponseEntity.status(422)
                    .body("Error generando la práctica, intenta de nuevo");
                default -> ResponseEntity.status(500).build();
            };
        }
    }

    // GET /api/asistente/historial/{idHato}
    @GetMapping("/historial/{idHato}")
    public ResponseEntity<ChatHistorialDTO> getHistorial(
            @PathVariable String idHato,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            String email = jwt.getClaim("email");
            ChatHistorialDTO historial =
                asistenteService.getHistorial(idHato, email);
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}