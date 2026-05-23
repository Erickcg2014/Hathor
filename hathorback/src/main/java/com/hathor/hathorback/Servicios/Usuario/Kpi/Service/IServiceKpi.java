package com.hathor.hathorback.Servicios.Usuario.Kpi.Service;

import com.hathor.hathorback.Entities.Kpi.Kpi;
import com.hathor.hathorback.Servicios.Usuario.Kpi.DTO.KpiHistoricoDTO;
import com.hathor.hathorback.Servicios.Usuario.Kpi.DTO.KpiResultadoDTO;

import java.util.List;
import java.util.UUID;

public interface IServiceKpi {
    List<KpiResultadoDTO> calcularYGuardarKpis(UUID idHato, String email);
    List<KpiResultadoDTO> getKpisDelHato(UUID idHato);
    List<KpiHistoricoDTO> getHistoricoKpi(UUID idHato, String codigo, String email);
    public Kpi getKpiById (int id);
    List<Kpi> getCatalogoKpis();
}