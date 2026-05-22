package com.hathor.hathorback.Servicios.Usuario.Finanzas.PerfilFinanciero.Service;

import com.hathor.hathorback.Entities.Finanzas.CategoriaFinanciera;
import com.hathor.hathorback.Entities.Finanzas.PerfilFinanciero;
import com.hathor.hathorback.Entities.Finanzas.RegistroFinanciero;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.Service.IServiceCategoriaFinanciera;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.PerfilFinanciero.DTO.RegistroPerfilFinancieroDTO;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.PerfilFinanciero.Repository.IRepositoryPerfilFinanciero;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.PerfilFinanciero.Repository.IRepositoryPerfilFinancieroDetalle;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero.Repository.IRepositoryRegistroFinanciero;
import com.hathor.hathorback.Servicios.Usuario.Hato.Service.IServiceHato;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ServicePerfilFinanciero implements IServicePerfilFinanciero {

    @Autowired
    IRepositoryPerfilFinanciero repositoryPerfilFinanciero;

    @Autowired
    IRepositoryPerfilFinancieroDetalle repositoryDetalle;

    @Autowired
    IServiceCategoriaFinanciera categoriaFinancieraService;

    @Autowired
    IRepositoryRegistroFinanciero registroFinancieroRepository;

    @Autowired
    IServiceHato hatoService;

    @Override
    @Transactional
    public PerfilFinanciero crearPerfil(RegistroPerfilFinancieroDTO dto, String email) {
        Hato hato = hatoService.findHatoById(dto.getIdHato(), email);

        if (!dto.getMetodoRegistro().matches("EXCEL|MANUAL|OMITIDO")) {
            throw new RuntimeException("METODO_INVALIDO");
        }

        PerfilFinanciero perfil = PerfilFinanciero.builder()
            .hato(hato)
            .metodoRegistro(dto.getMetodoRegistro())
            .periodo(dto.getPeriodo())
            .descripcion(dto.getDescripcion())
            .build();

        perfil = repositoryPerfilFinanciero.save(perfil);

        // Si es MANUAL — guardar detalles en registrofinanciero
        if ("MANUAL".equals(dto.getMetodoRegistro())
                && dto.getDetalles() != null
                && !dto.getDetalles().isEmpty()) {

            int anio = Integer.parseInt(dto.getPeriodo().substring(0, 4));
            int mes  = Integer.parseInt(dto.getPeriodo().substring(5, 7));
            LocalDate fechaBase = LocalDate.of(anio, mes, 1);

            for (RegistroPerfilFinancieroDTO.DetallePerfilDTO d : dto.getDetalles()) {
                CategoriaFinanciera categoria = categoriaFinancieraService
                    .getCategoriaFinancieraById(d.getIdCategoria());
                if (categoria == null) continue;

                RegistroFinanciero registro = RegistroFinanciero.builder()
                    .hato(hato)
                    .categoriaFinanciera(categoria)
                    .titulo(d.getTitulo())
                    .tipoMovimiento(d.getTipo())
                    .fecha(fechaBase)
                    .monto(d.getMontoMensual().floatValue())
                    .esHistorico(true)
                    .precisionFecha("MENSUAL")
                    .build();

                registroFinancieroRepository.save(registro);
            }
        }

    return perfil;
}

    @Override
    public List<PerfilFinanciero> getPerfilesByHato(UUID idHato) {
        return repositoryPerfilFinanciero.findByHato_IdHato(idHato);
    }

    @Override
    public boolean existePerfilParaHato(UUID idHato) {
        return repositoryPerfilFinanciero.existsByHato_IdHato(idHato);
    }
}