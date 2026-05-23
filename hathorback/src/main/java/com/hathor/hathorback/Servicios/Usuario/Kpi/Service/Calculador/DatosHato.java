package com.hathor.hathorback.Servicios.Usuario.Kpi.Service.Calculador;

import com.hathor.hathorback.Entities.Finanzas.RegistroFinanciero;
import com.hathor.hathorback.Entities.Finanzas.VentaLeche;
import com.hathor.hathorback.Entities.Inventarios.InventarioGanado;
import com.hathor.hathorback.Entities.Inventarios.InventarioGeneral;
import com.hathor.hathorback.Entities.Produccion.PerfilProductivo;
import com.hathor.hathorback.Entities.Produccion.ProduccionLeche;

import java.util.List;

public class DatosHato {
    public List<InventarioGanado>    inventarioGanado;
    public List<InventarioGeneral>   inventarioGeneral;
    public PerfilProductivo          perfilProductivo;
    public List<RegistroFinanciero>  registros;
    public List<ProduccionLeche>     produccionLeche;
    public List<VentaLeche>          ventasLeche;
}