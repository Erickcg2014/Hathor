package com.hathor.hathorback.Servicios.Usuario.Asistente.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hathor.hathorback.Servicios.Usuario.Asistente.DTO.*;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClaudeApiService implements IClaudeApiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${anthropic.api.key}")
    private String apiKey;

    @Value("${anthropic.api.model}")
    private String model;

    @Value("${anthropic.api.max-tokens}")
    private int maxTokens;

    public ClaudeApiService(WebClient.Builder webClientBuilder,
                            @Value("${anthropic.api.url}") String apiUrl) {

        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
            .responseTimeout(Duration.ofSeconds(40))
            .doOnConnected(conn -> conn
                .addHandlerLast(new ReadTimeoutHandler(40))
                .addHandlerLast(new WriteTimeoutHandler(10)));

        this.webClient = webClientBuilder
            .baseUrl(apiUrl)
            .defaultHeader("anthropic-version", "2023-06-01")
            .defaultHeader("content-type", "application/json")
            // ← header requerido para prompt caching
            .defaultHeader("anthropic-beta", "prompt-caching-2024-07-31")
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();

        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String enviarMensaje(String systemPrompt,
                                List<ClaudeMensajeDTO> historial,
                                String mensajeUsuario) {
        return enviarMensajeConTemperatura(
            systemPrompt, historial, mensajeUsuario, 0.7, maxTokens);
    }

    // Sobrecarga sin corpus — para generación de prácticas
    @Override
    public String enviarMensajeConTemperatura(String systemPrompt,
                                              List<ClaudeMensajeDTO> historial,
                                              String mensajeUsuario,
                                              double temperatura,
                                              int maxTokens) {
        return enviarMensajeInterno(
            systemPrompt, null, historial,
            mensajeUsuario, temperatura, maxTokens);
    }

    // Sobrecarga CON corpus separado — para chat con caching
    @Override
    public String enviarMensajeConCorpus(String systemPrompt,
                                         String corpus,
                                         List<ClaudeMensajeDTO> historial,
                                         String mensajeUsuario,
                                         double temperatura,
                                         int maxTokens) {
        return enviarMensajeInterno(
            systemPrompt, corpus, historial,
            mensajeUsuario, temperatura, maxTokens);
    }

    // ── Método interno unificado ───────────────────────────────────────

    private String enviarMensajeInterno(String systemPrompt,
                                         String corpus,
                                         List<ClaudeMensajeDTO> historial,
                                         String mensajeUsuario,
                                         double temperatura,
                                         int maxTokens) {
        List<ClaudeMensajeDTO> mensajes = new ArrayList<>(historial);
        mensajes.add(ClaudeMensajeDTO.builder()
            .role("user")
            .content(mensajeUsuario)
            .build());

        try {
            Map<String, Object> body = buildRequestMap(
                systemPrompt, corpus, mensajes, temperatura, maxTokens);

            String bodyJson = objectMapper.writeValueAsString(body);

            System.out.println("📤 Claude API request — model: " + model
                + " | mensajes: " + mensajes.size()
                + " | maxTokens: " + maxTokens
                + " | corpus caching: " + (corpus != null ? "SÍ" : "NO"));

            String respuesta = webClient.post()
                .header("x-api-key", apiKey)
                .header("content-type", "application/json")
                .bodyValue(bodyJson)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response ->
                    response.bodyToMono(String.class).map(errorBody -> {
                        System.err.println("❌ Claude API 4xx: "
                            + response.statusCode() + " — " + errorBody);
                        return new RuntimeException(
                            "CLAUDE_4XX: " + response.statusCode()
                            + " — " + errorBody);
                    }))
                .onStatus(status -> status.is5xxServerError(), response ->
                    response.bodyToMono(String.class).map(errorBody -> {
                        System.err.println("❌ Claude API 5xx: "
                            + response.statusCode() + " — " + errorBody);
                        return new RuntimeException(
                            "CLAUDE_5XX: " + response.statusCode()
                            + " — " + errorBody);
                    }))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(40))
                .block();

            System.out.println("📥 Claude API respondió — chars: "
                + (respuesta != null ? respuesta.length() : 0));

            if (respuesta == null || respuesta.isBlank())
                throw new RuntimeException("CLAUDE_RESPUESTA_VACIA");

            ClaudeResponseDTO dto = objectMapper.readValue(
                respuesta, ClaudeResponseDTO.class);

            return dto.getTexto();

        } catch (Exception e) {
            System.err.println("❌ Error en Claude API: "
                + e.getClass().getSimpleName() + " — " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("ERROR_CLAUDE_API: " + e.getMessage());
        }
    }

    // ── Builder del request ────────────────────────────────────────────

    private Map<String, Object> buildRequestMap(String systemPrompt,
                                                  String corpus,
                                                  List<ClaudeMensajeDTO> mensajes,
                                                  double temperatura,
                                                  int maxTokens) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("model", model);
        map.put("max_tokens", maxTokens);
        map.put("temperature", temperatura);

        // System como array de bloques para soportar cache_control
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            List<Map<String, Object>> systemBlocks = new ArrayList<>();

            // Bloque 1 — contexto dinámico del hato (NO se cachea, cambia por usuario)
            Map<String, Object> bloqueContexto = new LinkedHashMap<>();
            bloqueContexto.put("type", "text");
            bloqueContexto.put("text", systemPrompt);
            systemBlocks.add(bloqueContexto);

            // Bloque 2 — corpus documental (SE CACHEA, es estático)
            if (corpus != null && !corpus.isBlank()) {
                Map<String, Object> bloqueCorpus = new LinkedHashMap<>();
                bloqueCorpus.put("type", "text");
                bloqueCorpus.put("text", corpus);
                // cache_control marca este bloque para caching
                bloqueCorpus.put("cache_control",
                    Map.of("type", "ephemeral"));
                systemBlocks.add(bloqueCorpus);
            }

            map.put("system", systemBlocks);
        }

        // Mensajes
        List<Map<String, String>> msgs = new ArrayList<>();
        for (ClaudeMensajeDTO m : mensajes) {
            Map<String, String> msg = new LinkedHashMap<>();
            msg.put("role", m.getRole());
            msg.put("content", m.getContent());
            msgs.add(msg);
        }
        map.put("messages", msgs);

        return map;
    }
}