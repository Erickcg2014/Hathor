package com.hathor.hathorback.Servicios.Usuario.Asistente.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hathor.hathorback.Servicios.Usuario.Asistente.DTO.ChatHistorialDTO;
import com.hathor.hathorback.Servicios.Usuario.Asistente.DTO.ClaudeMensajeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConversationHistoryService implements IConversationHistoryService {

    // Máximo de pares de mensajes a conservar (ventana deslizante)
    private static final int MAX_MENSAJES = 10;

    // TTL de la conversación en Redis: 2 horas
    private static final int TTL_SEGUNDOS = 7200;

    // Estimado de chars por token — para advertencia de límite
    private static final int CHARS_POR_TOKEN = 4;
    private static final int MAX_TOKENS_HISTORIAL = 6000;

    @Autowired
    private JedisPool jedisPool;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<ClaudeMensajeDTO> obtenerHistorial(String conversationId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(conversationId);
            if (json == null || json.isBlank()) return new ArrayList<>();

            return objectMapper.readValue(json,
                new TypeReference<List<ClaudeMensajeDTO>>() {});
        } catch (Exception e) {
            System.err.println("Error obteniendo historial Redis: "
                + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void agregarMensajes(String conversationId,String mensajeUsuario, String respuestaAsistente) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<ClaudeMensajeDTO> historial = obtenerHistorial(conversationId);

            // Agregar par usuario/asistente
            historial.add(ClaudeMensajeDTO.builder()
                .role("user")
                .content(mensajeUsuario)
                .build());
            historial.add(ClaudeMensajeDTO.builder()
                .role("assistant")
                .content(respuestaAsistente)
                .build());

            // Ventana deslizante: conservar solo los últimos MAX_MENSAJES pares
            // Cada par = 2 mensajes, entonces MAX_MENSAJES * 2
            int maxMensajesTotal = MAX_MENSAJES * 2;
            if (historial.size() > maxMensajesTotal) {
                historial = historial.subList(
                    historial.size() - maxMensajesTotal,
                    historial.size()
                );
            }

            String json = objectMapper.writeValueAsString(historial);
            jedis.setex(conversationId, TTL_SEGUNDOS, json);

        } catch (Exception e) {
            System.err.println("Error guardando historial Redis: "
                + e.getMessage());
        }
    }

    @Override
    public void limpiarHistorial(String conversationId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(conversationId);
        } catch (Exception e) {
            System.err.println("Error limpiando historial Redis: "
                + e.getMessage());
        }
    }

    @Override
    public boolean superaLimite(String conversationId) {
        List<ClaudeMensajeDTO> historial = obtenerHistorial(conversationId);

        int totalChars = historial.stream()
            .mapToInt(m -> m.getContent() != null
                ? m.getContent().length() : 0)
            .sum();

        int tokensEstimados = totalChars / CHARS_POR_TOKEN;
        return tokensEstimados > MAX_TOKENS_HISTORIAL;
    }

    @Override
    public String generarConversationId(String idHato, String email) {
        // Formato: hathor:conv:{idHato}:{email}
        // Así cada hato + usuario tiene su propia conversación
        return "hathor:conv:" + idHato + ":" + email;
    }

    @Override
    public ChatHistorialDTO obtenerHistorialParaFrontend(String conversationId) {
        List<ClaudeMensajeDTO> historial = obtenerHistorial(conversationId);

        if (historial.isEmpty()) {
            return ChatHistorialDTO.builder()
                .mensajes(List.of())
                .tieneHistorial(false)
                .conversationId(conversationId)
                .build();
        }

        List<ChatHistorialDTO.MensajeHistorialDTO> mensajes = historial.stream()
            .map(m -> ChatHistorialDTO.MensajeHistorialDTO.builder()
                .role(m.getRole())
                .content(m.getContent())
                .build())
            .collect(java.util.stream.Collectors.toList());

        return ChatHistorialDTO.builder()
            .mensajes(mensajes)
            .tieneHistorial(true)
            .conversationId(conversationId)
            .build();
    }
}