package com.hathor.hathorback.Servicios.Usuario.Hato.DTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OpcionesHatoDTO {
    private List<String> tiposHato;
    private List<String> tropicos;
}
