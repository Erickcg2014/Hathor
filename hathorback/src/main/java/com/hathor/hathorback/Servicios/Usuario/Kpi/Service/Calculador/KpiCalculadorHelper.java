package com.hathor.hathorback.Servicios.Usuario.Kpi.Service.Calculador;

import com.hathor.hathorback.Entities.Finanzas.CategoriaFinanciera;
import com.hathor.hathorback.Servicios.Usuario.Kpi.DTO.DetalleCalculoItem;

import org.springframework.stereotype.Component;

@Component
public class KpiCalculadorHelper {

    public boolean perteneceAGrupo(CategoriaFinanciera cat, String... nombresGrupo) {
        if (cat == null) return false;
        String nombreCat = cat.getNombre().toUpperCase();
        for (String grupo : nombresGrupo) {
            if (nombreCat.equals(grupo.toUpperCase())) return true;
        }
        CategoriaFinanciera padre = cat.getCategoriaPadre();
        if (padre != null) {
            String nombrePadre = padre.getNombre().toUpperCase();
            for (String grupo : nombresGrupo) {
                if (nombrePadre.equals(grupo.toUpperCase())) return true;
            }
            CategoriaFinanciera abuelo = padre.getCategoriaPadre();
            if (abuelo != null) {
                String nombreAbuelo = abuelo.getNombre().toUpperCase();
                for (String grupo : nombresGrupo) {
                    if (nombreAbuelo.equals(grupo.toUpperCase())) return true;
                }
            }
        }
        return false;
    }

    public DetalleCalculoItem item(String variable, double valor, String unidad, String tipo) {
        return DetalleCalculoItem.builder()
            .variable(variable)
            .valor(valor)
            .unidad(unidad)
            .tipo(tipo)
            .build();
    }

    public DetalleCalculoItem item(String variable, String tipo) {
        return DetalleCalculoItem.builder()
            .variable(variable)
            .valor(null)
            .unidad(null)
            .tipo(tipo)
            .build();
    }
}