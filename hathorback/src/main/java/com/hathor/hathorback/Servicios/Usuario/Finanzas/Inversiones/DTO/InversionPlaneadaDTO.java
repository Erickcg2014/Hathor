package com.hathor.hathorback.Servicios.Usuario.Finanzas.Inversiones.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InversionPlaneadaDTO {
    private Long    idInversion;
    private String  descripcion;
    private Double  monto;
    private String  mesEjecucion;
    private String  mesEjecucionLabel;
    private Double  retornoEsperadoPct;
    private Integer mesesRetorno;
    private String  estado;
    private String  fechaCreacion;

    // Categoría financiera
    private String idCategoria;
    private String  nombreCategoria;
    private String  tipoCategoria;

    // Retorno proyectado mensual estimado
    private Double  retornoMensualEstimado;
}