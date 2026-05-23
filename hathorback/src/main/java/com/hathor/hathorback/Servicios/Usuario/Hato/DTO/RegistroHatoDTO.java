package com.hathor.hathorback.Servicios.Usuario.Hato.DTO;
import lombok.Data;

@Data
public class RegistroHatoDTO {
    private String nombreHato;
    private String departamento;
    private String ciudad;
    private float altitud;   
    private String tropico;
    private float areaHato;   
    private float areaPastoreo;
    private int cantCorrales;
    private int cantSalasOrdenio;
    private float capacidadAlmacenarLeche;
    private int cantEmpleadosPermanentes;
    private int cantEmpleadosTemporales;
    private String tipoHato;
    private Double latitud;  
    private Double longitud;
}
