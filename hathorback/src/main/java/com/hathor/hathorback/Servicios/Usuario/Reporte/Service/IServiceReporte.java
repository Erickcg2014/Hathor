package com.hathor.hathorback.Servicios.Usuario.Reporte.Service;

import java.util.UUID;

import com.hathor.hathorback.Servicios.Usuario.Reporte.DTO.ReporteConfigDTO;
import com.hathor.hathorback.Servicios.Usuario.Reporte.DTO.ReporteHistorialDTO;
import java.util.List;

public interface IServiceReporte {
    byte[] generarReporte(UUID idHato, ReporteConfigDTO config);
    List<ReporteHistorialDTO> getHistorial(UUID idHato);
    ReporteHistorialDTO       getReporteById(Integer idReporte);
    byte[]                    regenerarReporte(Integer idReporte);
    void                      generarReporteMensual(UUID idHato);
}