package com.hathor.hathorback.Servicios.Usuario.Finanzas.VentaLeche.Service;

import com.hathor.hathorback.Entities.Finanzas.VentaLeche;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.VentaLeche.DTO.RegistroVentaLecheDTO;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.VentaLeche.DTO.RespuestaVentaLecheDTO;

import java.util.List;
import java.util.UUID;

public interface IServiceVentaLeche {
    VentaLeche createVentaLeche(VentaLeche ventaleche);
    List<VentaLeche> getByHato(UUID idHato);
    RespuestaVentaLecheDTO registrarVenta(RegistroVentaLecheDTO dto, String email);
}