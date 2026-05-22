package com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Service;

import com.hathor.hathorback.Entities.Practicas.Practica;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.DTO.PracticaDetalleDTO;

import java.util.List;

public interface IServicePractica {

    // Devuelve el catálogo completo de prácticas activas filtrado
    // por la escala del hato — incluye las de esa escala específica
    // y las marcadas como TODAS.
    // Lo consume el controller para listar las prácticas disponibles.
    List<Practica> getCatalogoByEscala(String escala);

    // Devuelve el catálogo filtrado además por categoría
    // (PRODUCTIVIDAD, HATO, FINANCIERO, EFICIENCIA).
    // Útil para el frontend cuando quiere mostrar prácticas
    // de un tipo específico en el módulo de recomendaciones.
    List<Practica> getCatalogoByCategoriaYEscala(String categoria, String escala);

    // Devuelve el detalle completo de una práctica individual,
    // incluyendo los pasos parseados desde el JSON almacenado
    // en el campo pasos como lista de Strings lista para renderizar.
    PracticaDetalleDTO getDetalle(Integer idPractica);
}