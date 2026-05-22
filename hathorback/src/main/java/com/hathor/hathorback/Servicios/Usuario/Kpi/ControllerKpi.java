package com.hathor.hathorback.Servicios.Usuario.Kpi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Kpi.Kpi;
import com.hathor.hathorback.Servicios.Usuario.Kpi.DTO.KpiHistoricoDTO;
import com.hathor.hathorback.Servicios.Usuario.Kpi.DTO.KpiResultadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Kpi.Service.IServiceKpi;
import com.hathor.hathorback.Servicios.Usuario.Practicas.Reglas.Service.IServiceMotorReglas;
import com.hathor.hathorback.Servicios.Usuario.Hato.Service.IServiceHato;


import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/Kpi")
public class ControllerKpi {

    @Autowired
    IServiceKpi serviceKpi;
    @Autowired private IServiceHato hatoService;
    @Autowired private IServiceMotorReglas motorReglas;



    // Calcular (o recalcular) todos los KPIs del hato
    @PostMapping("/calcular/{idHato}")
    public ResponseEntity<List<KpiResultadoDTO>> calcular(
        @PathVariable UUID idHato,
        @AuthenticationPrincipal Jwt jwt
    ) {
        String email = jwt.getClaim("email");
        return ResponseEntity.ok(serviceKpi.calcularYGuardarKpis(idHato, email));
    }

    // Obtener KPIs actuales del hato (último cálculo)
    @GetMapping("/hato/{idHato}")
    public ResponseEntity<List<KpiResultadoDTO>> getKpis(
        @PathVariable UUID idHato,
        @AuthenticationPrincipal Jwt jwt
    ) {
        String email = jwt.getClaim("email");
        return ResponseEntity.ok(serviceKpi.getKpisDelHato(idHato));
    }

    // Histórico de un KPI específico para gráficas
    @GetMapping("/hato/{idHato}/historico/{codigo}")
    public ResponseEntity<List<KpiHistoricoDTO>> getHistorico(
        @PathVariable UUID idHato,
        @PathVariable String codigo,
        @AuthenticationPrincipal Jwt jwt
    ) {
        String email = jwt.getClaim("email");
        return ResponseEntity.ok(serviceKpi.getHistoricoKpi(idHato, codigo, email));
    }
    
    // Catálogo completo de KPIs — usado por el admin para crear reglas
    @GetMapping("/catalogo")
    public ResponseEntity<List<Kpi>> getCatalogo() {
        return ResponseEntity.ok()
            .cacheControl(org.springframework.http.CacheControl.noStore())
            .body(serviceKpi.getCatalogoKpis());
    }

    @PostMapping("/evaluar/{idHato}")
    public ResponseEntity<?> evaluarReglas(
        @PathVariable UUID idHato,
        @AuthenticationPrincipal Jwt jwt
    ) {
        try {
            String email = jwt.getClaim("email");
            Hato hato = hatoService.findHatoById(idHato, email);

            List<KpiResultadoDTO> kpisDTO = serviceKpi.getKpisDelHato(idHato);

            if (kpisDTO == null || kpisDTO.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "No hay KPIs calculados. Ejecuta /calcular primero."));
            }

            Map<String, Float> valores = kpisDTO.stream()
                .filter(k -> k.getValor() != null)
                .collect(Collectors.toMap(
                    KpiResultadoDTO::getCodigo,
                    k -> k.getValor().floatValue()
                ));

            motorReglas.evaluar(hato, valores);

            return ResponseEntity.ok(Map.of(
                "mensaje",   "Evaluación de reglas completada",
                "idHato",    idHato.toString(),
                "kpisUsados", valores.size()
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }
} 