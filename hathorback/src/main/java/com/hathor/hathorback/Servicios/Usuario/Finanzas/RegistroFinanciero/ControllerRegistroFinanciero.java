package com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.hathor.hathorback.Entities.Finanzas.RegistroFinanciero;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.PerfilFinanciero.Repository.IRepositoryPerfilFinanciero;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.PerfilFinanciero.Repository.IRepositoryPerfilFinancieroDetalle;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero.DTO.RegistroFinancieroDTO;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero.Service.IServiceRegistroFinanciero;
import com.hathor.hathorback.Servicios.Usuario.Hato.Service.IServiceHato;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/Finanzas")
public class ControllerRegistroFinanciero {

    @Autowired
    IServiceRegistroFinanciero registroService;

     // TODO: PRUEBA — eliminar antes de producción
    @Autowired
    IRepositoryPerfilFinanciero perfilFinancieroRepository;
    // TODO: PRUEBA — eliminar antes de producción
    @Autowired
    IServiceHato hatoService;

    @PostMapping("/")
    public ResponseEntity<List<RegistroFinanciero>> createRegistroFinanciero(
        @AuthenticationPrincipal Jwt jwt,
        HttpServletRequest request,
        @RequestBody List<RegistroFinancieroDTO> registro
    ) {
        String email = jwt.getClaim("email");
        return new ResponseEntity<>(
            registroService.createRegistroFinanciero(registro, email),
            HttpStatus.CREATED
        );
    }

    @GetMapping("/{id_hato}")
    public ResponseEntity<List<RegistroFinanciero>> getRegistrosFinancierosByIdHato(@PathVariable UUID id_hato) {
       return new ResponseEntity<List<RegistroFinanciero>>(
            registroService.getRegistrosFinancierosByIdHato(id_hato),
            HttpStatus.OK
        ); 
    }

    @DeleteMapping("/{id_registro}")
    public ResponseEntity<Void> eliminarRegistroFinanciero(@PathVariable UUID id_registro) {
        registroService.eliminarRegistroFinanciero(id_registro);
        return ResponseEntity.noContent().build(); // 204
    }
    

    @PostMapping("/carga-masiva")
    public ResponseEntity<?> cargaMasiva(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam("archivo") MultipartFile archivo,
        @RequestParam("idHato") UUID idHato
    ) {
        try {
            String email = jwt.getClaim("email");
            List<Map<String, Object>> resultados = registroService
                .procesarCargaMasiva(archivo, idHato, email);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Carga completada",
                "registros", resultados.size(),
                "detalle", resultados
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/hato/{idHato}/reales")
    public ResponseEntity<List<RegistroFinanciero>> getRegistrosReales(
        @PathVariable UUID idHato,
        @AuthenticationPrincipal Jwt jwt
    ) {
        String email = jwt.getClaim("email");
        return ResponseEntity.ok(
            registroService.getRegistrosRealesByHato(idHato, email)
        );
    }

    @GetMapping("/hato/{idHato}/historicos")
    public ResponseEntity<List<RegistroFinanciero>> getRegistrosHistoricos(
        @PathVariable UUID idHato,
        @AuthenticationPrincipal Jwt jwt
    ) {
        String email = jwt.getClaim("email");
        return ResponseEntity.ok(
            registroService.getRegistrosHistoricosByHato(idHato, email)
        );
    }

    @GetMapping("/hato/{idHato}/periodo")
    public ResponseEntity<List<RegistroFinanciero>> getRegistrosPorPeriodo(
            @PathVariable UUID idHato,
            @RequestParam String mesDesde,
            @RequestParam String mesHasta,
            @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaim("email");
        return ResponseEntity.ok(
            registroService.getRegistrosPorPeriodo(
                idHato, email, mesDesde, mesHasta));
    }
    // TODO: PRUEBA — eliminar antes de producción
    @Autowired
    IRepositoryPerfilFinancieroDetalle perfilFinancieroDetalleRepository;

    // TODO: PRUEBA — eliminar antes de producción
    @Transactional
    @DeleteMapping("/prueba/limpiar/{idHato}")
    public ResponseEntity<?> limpiarParaPruebas(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID idHato
    ) {
        try {
            String email = jwt.getClaim("email");

            // 1. Borrar detalles del perfil financiero
            perfilFinancieroDetalleRepository.deleteByHatoIdHato(idHato);

            // 2. Borrar perfil financiero
            perfilFinancieroRepository.deleteByHatoIdHato(idHato);

            // 3. Borrar registros financieros
            registroService.limpiarRegistrosPorHato(idHato, email);

            // 4. Resetear completitud del hato a 75%
            // (vuelve al estado previo al paso de finanzas)
            hatoService.actualizarCompletitud(idHato, 75, email);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Datos financieros eliminados y completitud reseteada",
                "idHato", idHato
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
}