package com.hathor.hathorback.Servicios.Usuario.Asistente.Service;

import com.hathor.hathorback.Servicios.Usuario.Asistente.DTO.ClaudeMensajeDTO;
import java.util.List;

public interface IClaudeApiService {

    String enviarMensaje(String systemPrompt, List<ClaudeMensajeDTO> historial, String mensajeUsuario);

    String enviarMensajeConTemperatura(String systemPrompt, List<ClaudeMensajeDTO> historial, String mensajeUsuario, double temperatura, int maxTokens);

    String enviarMensajeConCorpus(String systemPrompt, String corpus, List<ClaudeMensajeDTO> historial, String mensajeUsuario, double temperatura, int maxTokens);
}