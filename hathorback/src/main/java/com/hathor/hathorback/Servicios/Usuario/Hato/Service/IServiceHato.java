package com.hathor.hathorback.Servicios.Usuario.Hato.Service;

import java.util.List;
import java.util.UUID;

import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Usuario.Usuario;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO.HatoAnonimizadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Hato.DTO.CostosFijosDTO;
import com.hathor.hathorback.Servicios.Usuario.Hato.DTO.InfraestructuraBasicaDTO;
import com.hathor.hathorback.Servicios.Usuario.Hato.DTO.RegistroHatoDTO;

public interface IServiceHato {
    public Hato createHato(RegistroHatoDTO hato, String email);
    public List<Hato> findByUsuario_IdUsuario(UUID idUsuario);
    public Usuario findUsuarioByTokenEmail(String email);
    public Hato actualizarCompletitud(UUID idHato, int porcentaje, String email);
    public Hato findHatoById(UUID idHato, String email);
    public Hato findHatoById(UUID idHato);
    public Hato actualizarInfraestructuraBasica(UUID idHato, InfraestructuraBasicaDTO dto, String email);
    Hato actualizarCostosFijos(UUID idHato, CostosFijosDTO dto, String email);
    List<HatoAnonimizadoDTO> getMapaGeneral();
}