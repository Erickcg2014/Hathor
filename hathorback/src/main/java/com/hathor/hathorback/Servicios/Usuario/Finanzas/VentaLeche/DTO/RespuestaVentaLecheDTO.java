package com.hathor.hathorback.Servicios.Usuario.Finanzas.VentaLeche.DTO;

import com.hathor.hathorback.Entities.Finanzas.VentaLeche;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaVentaLecheDTO {

    private VentaLeche ventaRegistrada;
    private boolean alerta;
    private float litrosFaltantes;
    private String mensaje;
}