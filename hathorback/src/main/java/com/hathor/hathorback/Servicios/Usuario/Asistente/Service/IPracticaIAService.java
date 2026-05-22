package com.hathor.hathorback.Servicios.Usuario.Asistente.Service;

import com.hathor.hathorback.Servicios.Usuario.Asistente.DTO.PracticaIARequestDTO;
import com.hathor.hathorback.Servicios.Usuario.Asistente.DTO.PracticaIAResponseDTO;

public interface IPracticaIAService {

    PracticaIAResponseDTO generarPractica(PracticaIARequestDTO request, String email);
}