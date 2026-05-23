package com.hathor.hathorback.Servicios.Usuario.Asistente.Service;

import com.hathor.hathorback.Servicios.Usuario.Asistente.DTO.ChatHistorialDTO;
import com.hathor.hathorback.Servicios.Usuario.Asistente.DTO.ChatRequestDTO;
import com.hathor.hathorback.Servicios.Usuario.Asistente.DTO.ChatResponseDTO;

public interface IAsistenteService {

    ChatResponseDTO chat(ChatRequestDTO request, String email);

    void limpiarConversacion(String idHato, String email);

    ChatHistorialDTO getHistorial(String idHato, String email);

}