// Admin/Hatos/Service/ServiceAdminHatos.java
package com.hathor.hathorback.Servicios.Admin.Hatos.Service;

import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Servicios.Admin.Hatos.DTO.*;
import com.hathor.hathorback.Servicios.Usuario.Hato.Repository.IRepositoryHato;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServiceAdminHatos implements IServiceAdminHatos {

    @Autowired
    private IRepositoryHato repositoryHato;

    @Override
    public List<HatoAdminDTO> getHatosFiltrados(FiltroHatoDTO filtro) {
        List<Hato> hatos = repositoryHato.findAllFiltrado(
            filtro.getDepartamento(),
            filtro.getRegion(),
            filtro.getTropico(),
            filtro.getEscala(),
            filtro.getTipoHato()
        );
        return hatos.stream()
            .map(this::toHatoAdminDTO)
            .collect(Collectors.toList());
    }

    @Override
    public HatoAdminDetalleDTO getDetalleHato(UUID idHato) {
        Hato hato = repositoryHato.findById(idHato)
            .orElseThrow(() -> new RuntimeException("HATO_NO_ENCONTRADO"));
        return toHatoAdminDetalleDTO(hato);
    }

    @Override
    public List<String> getDepartamentos() {
        return repositoryHato.findDepartamentosUnicos();
    }

    @Override
    public List<String> getRegionesPorDepartamento(String departamento) {
        return repositoryHato.findCiudadesPorDepartamento(departamento);
    }

    @Override
    public List<String> getTropicos() {
        return repositoryHato.findTropicosUnicos();
    }

    @Override
    public List<String> getEscalas() {
        return repositoryHato.findEscalasUnicas();
    }

    // ── Mappers ───────────────────────────────────────────────────────────

    private HatoAdminDTO toHatoAdminDTO(Hato h) {
        return HatoAdminDTO.builder()
            .idHato(h.getIdHato())
            .nombreHato(h.getNombreHato())
            .departamento(h.getDepartamento())
            .ciudad(h.getCiudad())
            .tropico(h.getTropico())
            .escala(h.getEscala())
            .tipoHato(h.getTipoHato())
            .areaHato(h.getAreaHato())
            .altitud(h.getAltitud())
            .porcentajeCompletitud(h.getPorcentajeCompletitud())
            .nombreUsuario(h.getUsuario() != null
                ? h.getUsuario().getNombre() : null)
            .apellidoUsuario(h.getUsuario() != null
                ? h.getUsuario().getApellido() : null)
            .correoUsuario(h.getUsuario() != null
                ? h.getUsuario().getCorreo() : null)
            .latitud(h.getLatitud())
            .longitud(h.getLongitud())
            .build();
    }

    private HatoAdminDetalleDTO toHatoAdminDetalleDTO(Hato h) {
        return HatoAdminDetalleDTO.builder()
            .idHato(h.getIdHato())
            .nombreHato(h.getNombreHato())
            .departamento(h.getDepartamento())
            .ciudad(h.getCiudad())
            .direccion(h.getDireccion())
            .tropico(h.getTropico())
            .escala(h.getEscala())
            .tipoHato(h.getTipoHato())
            .areaHato(h.getAreaHato())
            .areaPastoreo(h.getAreaPastoreo())
            .altitud(h.getAltitud())
            .cantCorrales(h.getCantCorrales())
            .cantSalasOrdenio(h.getCantSalasOrdenio())
            .capacidadAlmacenarLeche(h.getCapacidadAlmacenarLeche())
            .cantEmpleadosPermanentes(h.getCantEmpleadosPermanentes())
            .cantEmpleadosTemporales(h.getCantEmpleadosTemporales())
            .gastoMensualNomina(h.getGastoMensualNomina())
            .gastoMensualAlimentacion(h.getGastoMensualAlimentacion())
            .porcentajeCompletitud(h.getPorcentajeCompletitud())
            .latitud(h.getLatitud())
            .longitud(h.getLongitud())
            .idUsuario(h.getUsuario() != null
                ? h.getUsuario().getIdUsuario() : null)
            .nombreUsuario(h.getUsuario() != null
                ? h.getUsuario().getNombre() : null)
            .apellidoUsuario(h.getUsuario() != null
                ? h.getUsuario().getApellido() : null)
            .correoUsuario(h.getUsuario() != null
                ? h.getUsuario().getCorreo() : null)
            .celularUsuario(h.getUsuario() != null
                ? h.getUsuario().getCelular() : null)
            .build();
    }
}