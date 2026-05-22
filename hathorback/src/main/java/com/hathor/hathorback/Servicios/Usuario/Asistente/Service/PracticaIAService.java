package com.hathor.hathorback.Servicios.Usuario.Asistente.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Practicas.HatoPractica;
import com.hathor.hathorback.Entities.Practicas.Practica;
import com.hathor.hathorback.Servicios.Usuario.Asistente.DTO.*;
import com.hathor.hathorback.Servicios.Usuario.Hato.Repository.IRepositoryHato;
import com.hathor.hathorback.Servicios.Usuario.Kpi.DTO.KpiResultadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Kpi.Service.IServiceKpi;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Repository.IRepositoryHatoPractica;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Repository.IRepositoryPractica;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PracticaIAService implements IPracticaIAService {

    @Autowired private IClaudeApiService claudeApiService;
    @Autowired private AsistenteContextService contextService;
    @Autowired private IRepositoryHato repositoryHato;
    @Autowired private IServiceKpi serviceKpi;
    @Autowired private IRepositoryPractica repositoryPractica;
    @Autowired private IRepositoryHatoPractica repositoryHatoPractica;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Codigos KPI válidos — para validación
    private static final List<String> CODIGOS_KPI_VALIDOS = List.of(
        "KPI_LITROS_VACA_DIA", "KPI_LITROS_HA_ANIO",
        "KPI_PRODUCCION_HA_DIA", "KPI_CAP_ALMAC_UTILIZADA",
        "KPI_LACTANCIA_VS_ESTANDAR", "KPI_FRECUENCIA_ORDENIO",
        "KPI_LITROS_EMPLEADO", "KPI_CARGA_ANIMAL",
        "KPI_PCT_VACAS_ORDENIO", "KPI_HEMBRAS_RECRIA_VACA",
        "KPI_MARGEN_NETO", "KPI_MARGEN_BRUTO_PCT",
        "KPI_RATIO_INGRESO_EGRESO", "KPI_BALANCE_NETO",
        "KPI_INGRESO_VACA", "KPI_ROA", "KPI_ROTACION_ACTIVOS",
        "KPI_INGRESO_HA_ANIO", "KPI_IOFC", "KPI_INGRESO_LITRO",
        "KPI_COSTO_LITRO", "KPI_BREAKEVEN_LITRO",
        "KPI_EMPLEADOS_HA", "KPI_COSTO_LABORAL_PCT"
    );

    @Override
    @Transactional
    public PracticaIAResponseDTO generarPractica(PracticaIARequestDTO request,
                                                  String email) {
        UUID idHato = request.getIdHato();
        String codigoKpi = request.getCodigoKpi();

        // 1. Cargar datos del hato y contexto
        Hato hato = repositoryHato.findById(idHato)
            .orElseThrow(() -> new RuntimeException("HATO_NO_ENCONTRADO"));

        ContextoHatoDTO contexto = contextService.construirContexto(idHato);

        // 2. Obtener el KPI específico solicitado
        List<KpiResultadoDTO> kpis = serviceKpi.getKpisDelHato(idHato);
        KpiResultadoDTO kpiObjetivo = kpis.stream()
            .filter(k -> codigoKpi.equals(k.getCodigo()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("KPI_NO_ENCONTRADO"));

        String prompt = construirPromptGeneracion(hato, contexto, kpiObjetivo);

        // 4. Llamar a Claude con temperatura baja para JSON consistente
        String respuestaJson = claudeApiService.enviarMensajeConTemperatura(
            construirSystemPromptGeneracion(),
            List.of(),
            prompt,
            0.3,
            2000
        );

        // 5. Validar y parsear el JSON
        PracticaGeneradaDTO practicaGenerada = validarYParsear(respuestaJson,
            hato, codigoKpi);

        if (!request.isConfirmar()) {
            return PracticaIAResponseDTO.builder()
                .practicaGenerada(practicaGenerada)
                .pendienteConfirmacion(true)
                .mensaje("Revisa la práctica generada y confírmala " +
                    "para agregarla a tu plan de mejora.")
                .build();
        }

        UUID idHatoPractica = persistirPractica(practicaGenerada, hato);

        return PracticaIAResponseDTO.builder()
            .practicaGenerada(practicaGenerada)
            .idHatoPracticaCreada(idHatoPractica)
            .pendienteConfirmacion(false)
            .mensaje("Práctica agregada exitosamente a tu plan de mejora.")
            .build();
    }

    // System prompt para generar prácticas
    private String construirSystemPromptGeneracion() {
        return """
            Eres un experto en ganadería lechera colombiana. Tu tarea es \
            generar una práctica de mejora personalizada para un hato lechero.
            
            DEBES responder ÚNICAMENTE con un objeto JSON válido, \
            sin texto adicional, sin markdown, sin explicaciones. \
            Solo el JSON puro.
            
            El JSON debe tener exactamente esta estructura:
            {
              "nombre": "string — máximo 80 caracteres",
              "descripcion": "string — 2 a 3 oraciones",
              "objetivo": "string — resultado medible esperado",
              "categoria": "PRODUCTIVIDAD | HATO | FINANCIERO | EFICIENCIA",
              "impactoEsperado": "string — qué mejora y cuánto",
              "pasos": ["paso 1", "paso 2", "paso 3"],
              "kpiImpactado": "código exacto del KPI",
              "dificultad": "BAJA | MEDIA | ALTA",
              "duracionDias": número entre 7 y 180,
              "escala": "PEQUEÑA | MEDIANA | GRANDE | EMPRESARIAL | TODAS",
              "tropicaAplicable": "FRIO | TEMPLADO | CALIDO | TODOS"
            }
            
            Reglas:
            - pasos: mínimo 3, máximo 8, cada uno comienza con verbo infinitivo
            - duracionDias: número entero, sin comillas
            - Adapta la práctica al trópico y escala del hato
            - Lenguaje claro para ganaderos colombianos
            - No uses tecnicismos innecesarios
            """;
    }

    // Prompt con contexto específico del hato y KPI
    private String construirPromptGeneracion(Hato hato, ContextoHatoDTO ctx, KpiResultadoDTO kpi) {
        return String.format("""
            Genera una práctica de mejora para el siguiente hato lechero:
            
            DATOS DEL HATO:
            - Nombre: %s
            - Trópico: %s
            - Escala: %s
            - Departamento: %s
            - Altitud: %.0f msnm
            - Área pastoreo: %.1f ha
            - Empleados totales: %d
            - Producción diaria: %s litros
            - Vacas en ordeño: %s
            - Raza predominante: %s
            
            KPI A MEJORAR:
            - Nombre: %s
            - Código: %s
            - Valor actual: %s %s
            - Estado: %s
            - Promedio del sector: %s
            - Diferencia vs sector: %s%%
            - Categoría: %s
            
            Genera una práctica específica, accionable y adaptada \
            a las condiciones de este hato para mejorar este KPI.
            """,
            hato.getNombreHato(),
            hato.getTropico(),
            hato.getEscala(),
            hato.getDepartamento(),
            hato.getAltitud(),
            hato.getAreaPastoreo(),
            hato.getCantEmpleadosPermanentes() + hato.getCantEmpleadosTemporales(),
            ctx.getProduccionDiariaLitros() != null
                ? ctx.getProduccionDiariaLitros().toString() : "no registrada",
            ctx.getVacasEnOrdenio() != null
                ? ctx.getVacasEnOrdenio().toString() : "no registrado",
            ctx.getRazaPredominante() != null
                ? ctx.getRazaPredominante() : "no registrada",
            kpi.getNombre(),
            kpi.getCodigo(),
            kpi.getValor() != null ? kpi.getValor().toString() : "sin dato",
            kpi.getUnidad() != null ? kpi.getUnidad() : "",
            kpi.getEstado(),
            kpi.getBenchmarkPromedio() != null
                ? kpi.getBenchmarkPromedio().toString() : "no disponible",
            kpi.getDiferenciaPct() != null
                ? String.format("%.1f", kpi.getDiferenciaPct()) : "N/A",
            kpi.getCategoria()
        );
    }

    // Validador del JSON devuelto por Claude
    private PracticaGeneradaDTO validarYParsear(String json,
                                                 Hato hato,
                                                 String codigoKpi) {
        try {
            String jsonLimpio = json.trim();
            if (jsonLimpio.startsWith("```")) {
                jsonLimpio = jsonLimpio
                    .replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();
            }

            PracticaGeneradaDTO dto = objectMapper.readValue(
                jsonLimpio, PracticaGeneradaDTO.class);

            if (dto.getNombre() == null || dto.getNombre().isBlank())
                throw new RuntimeException("PRACTICA_NOMBRE_INVALIDO");

            if (dto.getPasos() == null || dto.getPasos().size() < 3)
                throw new RuntimeException("PRACTICA_PASOS_INSUFICIENTES");

            if (dto.getDuracionDias() == null
                    || dto.getDuracionDias() < 7
                    || dto.getDuracionDias() > 180)
                dto.setDuracionDias(30); 

            dto.setCategoria(validarEnum(dto.getCategoria(),
                List.of("PRODUCTIVIDAD", "HATO", "FINANCIERO", "EFICIENCIA"),
                "PRODUCTIVIDAD"));

            dto.setDificultad(validarEnum(dto.getDificultad(),
                List.of("BAJA", "MEDIA", "ALTA"), "MEDIA"));

            dto.setEscala(validarEnum(dto.getEscala(),
                List.of("PEQUEÑA", "MEDIANA", "GRANDE",
                    "EMPRESARIAL", "TODAS"),
                hato.getEscala() != null ? hato.getEscala() : "TODAS"));

            dto.setTropicaAplicable(validarEnum(dto.getTropicaAplicable(),
                List.of("FRIO", "TEMPLADO", "CALIDO", "TODOS"),
                hato.getTropico() != null ? hato.getTropico() : "TODOS"));

            if (!CODIGOS_KPI_VALIDOS.contains(dto.getKpiImpactado()))
                dto.setKpiImpactado(codigoKpi);

            if (dto.getNombre().length() > 80)
                dto.setNombre(dto.getNombre().substring(0, 80));

            return dto;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                "PRACTICA_JSON_INVALIDO: " + e.getMessage());
        }
    }

    private String validarEnum(String valor, List<String> validos,
                                String fallback) {
        if (valor == null) return fallback;
        String upper = valor.toUpperCase().trim();
        return validos.contains(upper) ? upper : fallback;
    }

    // Persiste la práctica generada en BD y la asigna al hato
    private UUID persistirPractica(PracticaGeneradaDTO dto, Hato hato) {
        try {
            // Convertir pasos a JSON string para la entidad
            String pasosJson = objectMapper.writeValueAsString(dto.getPasos());

            // Verificar si ya existe una práctica con el mismo nombre y escala
            boolean yaExiste = repositoryPractica
                .findByNombreAndEscala(dto.getNombre(), dto.getEscala())
                .isPresent();

            Practica practica;
            if (yaExiste) {
                practica = repositoryPractica
                    .findByNombreAndEscala(dto.getNombre(), dto.getEscala())
                    .get();
            } else {
                // Crear nueva práctica en el catálogo marcada como IA
                practica = repositoryPractica.save(Practica.builder()
                    .nombre(dto.getNombre())
                    .descripcion(dto.getDescripcion())
                    .objetivo(dto.getObjetivo())
                    .categoria(dto.getCategoria())
                    .impactoEsperado(dto.getImpactoEsperado())
                    .pasos(pasosJson)
                    .kpiImpactado(dto.getKpiImpactado())
                    .dificultad(dto.getDificultad())
                    .duracionDias(dto.getDuracionDias())
                    .escala(dto.getEscala())
                    .tropicaAplicable(dto.getTropicaAplicable())
                    .estado("ACTIVA")
                    .build());
            }

            // Verificar que no esté ya asignada al hato
            boolean yaAsignada = repositoryHatoPractica
                .existsPendienteOEnCurso(hato.getIdHato(),
                    practica.getIdPractica());

            if (yaAsignada)
                throw new RuntimeException("PRACTICA_YA_ASIGNADA");

            // Crear HatoPractica — sin recomendación (origen IA)
            HatoPractica hatoPractica = repositoryHatoPractica.save(
                HatoPractica.builder()
                    .hato(hato)
                    .practica(practica)
                    .estado("PENDIENTE")
                    .porcentajeAvance(0f)
                    .build());

            return hatoPractica.getIdHatoPractica();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                "ERROR_PERSISTIENDO_PRACTICA: " + e.getMessage());
        }
    }
}