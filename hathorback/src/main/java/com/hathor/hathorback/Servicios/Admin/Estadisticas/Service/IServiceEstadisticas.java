package com.hathor.hathorback.Servicios.Admin.Estadisticas.Service;

import com.hathor.hathorback.Servicios.Admin.Estadisticas.DTO.*;
import java.util.List;

public interface IServiceEstadisticas {
    EstadisticasGlobalesDTO      getEstadisticasGlobales();
    List<EstadisticasDepartamentoDTO> getEstadisticasPorDepartamento();
    List<EstadisticasEscalaDTO>       getEstadisticasPorEscala();
    List<EstadisticasTropicoDTO>      getEstadisticasPorTropico();
}