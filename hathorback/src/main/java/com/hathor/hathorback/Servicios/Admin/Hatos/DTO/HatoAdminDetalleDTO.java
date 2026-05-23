package com.hathor.hathorback.Servicios.Admin.Hatos.DTO;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HatoAdminDetalleDTO {
    private UUID    idHato;
    private String  nombreHato;
    private String  departamento;
    private String  ciudad;
    private String  direccion;
    private String  tropico;
    private String  escala;
    private String  tipoHato;
    private Float  areaHato;
    private Float  areaPastoreo;
    private Float   altitud;
    private Integer cantCorrales;
    private Integer cantSalasOrdenio;
    private Float  capacidadAlmacenarLeche;
    private Integer cantEmpleadosPermanentes;
    private Integer cantEmpleadosTemporales;
    private Double  gastoMensualNomina;
    private Double  gastoMensualAlimentacion;
    private Integer porcentajeCompletitud;
    private String  fechaRegistro;
    private Double  latitud;
    private Double  longitud;
    // Info usuario
    private UUID   idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;
    private String correoUsuario;
    private String celularUsuario;
}