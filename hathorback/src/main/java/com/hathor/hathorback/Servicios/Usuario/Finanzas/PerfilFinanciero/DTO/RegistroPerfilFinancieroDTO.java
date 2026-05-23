package com.hathor.hathorback.Servicios.Usuario.Finanzas.PerfilFinanciero.DTO;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class RegistroPerfilFinancieroDTO {
    private UUID idHato;
    private String metodoRegistro; 
    private String periodo;       
    private String descripcion;
    private List<DetallePerfilDTO> detalles;

    @Data
    public static class DetallePerfilDTO {
        private UUID idCategoria;
        private String tipo;        
        private String titulo;
        private Float montoMensual;
    }
}