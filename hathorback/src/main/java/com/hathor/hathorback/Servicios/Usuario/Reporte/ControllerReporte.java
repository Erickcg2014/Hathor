package com.hathor.hathorback.Servicios.Usuario.Reporte;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.hathor.hathorback.Servicios.Usuario.Reporte.DTO.ReporteConfigDTO;
import com.hathor.hathorback.Servicios.Usuario.Reporte.Service.IServiceReporte;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/Reporte")
public class ControllerReporte {

    @Autowired
    private IServiceReporte serviceReporte;

    @PostMapping("/{idHato}/generar")
    public ResponseEntity<?> generarReporte(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID idHato,
            @RequestBody ReporteConfigDTO config
    ) {
        try {
            byte[] pdf = serviceReporte.generarReporte(idHato, config);

            // Nombre del archivo con fecha
            String fecha    = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String filename = "reporte-hathor-" + fecha + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdf.length);
            headers.setCacheControl("no-store");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

        } catch (RuntimeException e) {
            if (e.getMessage() != null
                    && e.getMessage().startsWith("ERROR_GENERANDO_REPORTE")) {
                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of(
                        "error", "No se pudo generar el reporte",
                        "detalle", e.getMessage()
                    ));
            }
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of("error", e.getMessage()));
        }
    }


    // ── Historial de reportes ─────────────────────────────────────────────

    @GetMapping("/{idHato}/historial")
    public ResponseEntity<?> getHistorial(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID idHato) {
        try {
            return ResponseEntity.ok()
                .cacheControl(org.springframework.http.CacheControl.noStore())
                .body(serviceReporte.getHistorial(idHato));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/detalle/{idReporte}")
    public ResponseEntity<?> getReporteById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer idReporte) {
        try {
            return ResponseEntity.ok(serviceReporte.getReporteById(idReporte));
        } catch (RuntimeException e) {
            if ("REPORTE_NO_ENCONTRADO".equals(e.getMessage())) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{idReporte}/regenerar")
    public ResponseEntity<?> regenerarReporte(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer idReporte) {
        try {
            byte[] pdf = serviceReporte.regenerarReporte(idReporte);

            String fecha    = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String filename = "reporte-regenerado-" + fecha + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdf.length);
            headers.setCacheControl("no-store");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

        } catch (RuntimeException e) {
            if ("REPORTE_NO_ENCONTRADO".equals(e.getMessage())) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

}