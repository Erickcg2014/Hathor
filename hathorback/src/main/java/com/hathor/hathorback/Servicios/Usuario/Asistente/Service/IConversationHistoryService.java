package com.hathor.hathorback.Servicios.Usuario.Asistente.Service;

import com.hathor.hathorback.Servicios.Usuario.Asistente.DTO.ChatHistorialDTO;
import com.hathor.hathorback.Servicios.Usuario.Asistente.DTO.ClaudeMensajeDTO;
import java.util.List;

public interface IConversationHistoryService {

    List<ClaudeMensajeDTO> obtenerHistorial(String conversationId);

    ChatHistorialDTO obtenerHistorialParaFrontend(String conversationId);

    void agregarMensajes(String conversationId, String mensajeUsuario, String respuestaAsistente);

    // Limpiar conversación
    void limpiarHistorial(String conversationId);

    // Verificar si el historial supera el límite de tokens estimado
    boolean superaLimite(String conversationId);

    // Generar ID de conversación único por hato + usuario
    String generarConversationId(String idHato, String email);
}