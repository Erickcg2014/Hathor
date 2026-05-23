package com.hathor.hathorback.Servicios.Usuario.Asistente.Service;

import com.hathor.hathorback.Servicios.Usuario.Asistente.DTO.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
public class AsistenteService implements IAsistenteService {

    @Autowired private IClaudeApiService claudeApiService;
    @Autowired private IConversationHistoryService historialService;
    @Autowired private AsistenteContextService contextService;
    @Autowired private ResourceLoader resourceLoader;
    @Value("${anthropic.api.max-tokens}")
    private int maxTokens;

    // Corpus documental 
    private String corpusCache = null;

    @Override
    public ChatResponseDTO chat(ChatRequestDTO request, String email) {

        UUID idHato = request.getIdHato();
        String mensaje = request.getMensaje();
        String contextoActivo = request.getContextoActivo();

        // 1. ID de conversación único por hato + usuario
        String convId = historialService.generarConversationId(
            idHato.toString(), email);

        // 2. Construir contexto del hato
        ContextoHatoDTO contexto = contextService.construirContexto(idHato);

        // 3. Cargar corpus
        String corpus = cargarCorpus();

        // 4. Enriquecer mensaje con contexto activo 
        String mensajeEnriquecido = enriquecerMensaje(
            mensaje, contextoActivo, contexto);

        // 5. Construir system prompt
        String systemPrompt = contextService.construirSystemPrompt(contexto, "");

        // 6. Obtener historial de Redis
        List<ClaudeMensajeDTO> historial =
            historialService.obtenerHistorial(convId);

        // 7. Llamar a Claude API
        String respuesta = claudeApiService.enviarMensajeConCorpus(
            systemPrompt,
            corpus,                  
            historial,
            mensajeEnriquecido,
            0.7,
            maxTokens);

        // 8. Guardar en historial
        historialService.agregarMensajes(convId, mensajeEnriquecido, respuesta);

        // 9. Verificar si el historial está creciendo mucho para no superar el limite
        boolean historialLargo = historialService.superaLimite(convId);

        // 10. Estimar tokens usados (aproximado)
        int tokensEstimados = (systemPrompt.length()
            + mensajeEnriquecido.length()
            + respuesta.length()) / 4;

        return ChatResponseDTO.builder()
            .respuesta(respuesta)
            .historialLargo(historialLargo)
            .tokensEstimados(tokensEstimados)
            .conversationId(convId)
            .build();
    }

    @Override
    public void limpiarConversacion(String idHato, String email) {
        String convId = historialService
            .generarConversationId(idHato, email);
        historialService.limpiarHistorial(convId);
    }

    private String enriquecerMensaje(String mensaje, String contextoActivo, ContextoHatoDTO ctx) {
        if (contextoActivo == null || contextoActivo.isBlank())
            return mensaje;

        return switch (contextoActivo) {
            case "kpis" -> mensaje +
                "\n[Contexto: El usuario está viendo la sección de KPIs. " +
                "Hay " + contarCriticos(ctx) + " KPIs en estado crítico.]";
            case "benchmarking" -> mensaje +
                "\n[Contexto: El usuario está viendo la sección de " +
                "benchmarking comparativo.]";
            case "practicas" -> mensaje +
                "\n[Contexto: El usuario está viendo sus prácticas " +
                "recomendadas de mejora.]";
            case "finanzas" -> mensaje +
                "\n[Contexto: El usuario está viendo la sección " +
                "financiera. Balance neto actual: $" +
                String.format("%,.0f", ctx.getBalanceNeto()) + " COP]";
            default -> mensaje;
        };
    }

    private long contarCriticos(ContextoHatoDTO ctx) {
        if (ctx.getKpis() == null) return 0;
        return ctx.getKpis().stream()
            .filter(k -> "CRITICO".equals(k.getEstado()))
            .count();
    }

    // Se cargan los documentos .md del corpus desde resources
    private String cargarCorpus() {
        if (corpusCache != null) return corpusCache;

        StringBuilder corpus = new StringBuilder();
        String[] archivos = {
            "classpath:corpus/parametros_referencia_colombia.md",
            "classpath:corpus/bpg_produccion_leche_colombia.md",
            "classpath:corpus/mejora_financiera_hatos.md",
            "classpath:corpus/practicas_ganaderos_expertos.md"
        };

        for (String archivo : archivos) {
            try {
                Resource resource = resourceLoader.getResource(archivo);
                if (resource.exists()) {
                    corpus.append(new String(
                        resource.getInputStream().readAllBytes(),
                        StandardCharsets.UTF_8));
                    corpus.append("\n\n");
                }
            } catch (Exception e) {
                System.err.println("No se pudo cargar corpus: "
                    + archivo + " — " + e.getMessage());
            }
        }

        corpusCache = corpus.toString();
        return corpusCache;
    }

    @Override
    public ChatHistorialDTO getHistorial(String idHato, String email) {
        String convId = historialService.generarConversationId(idHato, email);
        return historialService.obtenerHistorialParaFrontend(convId);
    }
}