package com.hathor.hathorback.Servicios.Usuario.Finanzas.Inversiones.Service;

import com.hathor.hathorback.Entities.Finanzas.InversionPlaneada;
import com.hathor.hathorback.Entities.Finanzas.RegistroFinanciero;
import com.hathor.hathorback.Entities.Finanzas.CategoriaFinanciera;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.Repository.IRepositoryCategoriaFinanciera;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.Inversiones.DTO.*;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.Inversiones.Repository.IRepositoryInversionPlaneada;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero.Repository.IRepositoryRegistroFinanciero;
import com.hathor.hathorback.Servicios.Usuario.Hato.Repository.IRepositoryHato;
import com.hathor.hathorback.Servicios.Usuario.InfoUsuario.Repository.IRepositoryUsuario;
import com.hathor.hathorback.Servicios.Usuario.Kpi.Service.IServiceKpi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServiceInversion implements IServiceInversion {

    @Autowired
    private IRepositoryInversionPlaneada repoInversion;

    @Autowired
    private IRepositoryHato              repoHato;

    @Autowired
    private IRepositoryCategoriaFinanciera repoCategoria;

    @Autowired private IRepositoryRegistroFinanciero repoFinanciero;
    @Autowired private IServiceKpi                   serviceKpi;
    @Autowired private IRepositoryUsuario            repoUsuario;

    // ── Consultas ─────────────────────────────────────────────────────────

    @Override
    public List<InversionPlaneadaDTO> getByHato(UUID idHato) {
        return repoInversion.findActivasByHato(idHato)
            .stream().map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<InversionPlaneadaDTO> getByHatoAndRango(
            UUID idHato, String mesDesde, String mesHasta) {
        return repoInversion
            .findPlaneadasByHatoAndRango(idHato, mesDesde, mesHasta)
            .stream().map(this::toDTO)
            .collect(Collectors.toList());
    }

    // ── Crear ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public InversionPlaneadaDTO crear(
            UUID idHato, CrearInversionDTO dto) {

        Hato hato = repoHato.findById(idHato)
            .orElseThrow(() ->
                new RuntimeException("HATO_NO_ENCONTRADO"));

        CategoriaFinanciera categoria = null;
        if (dto.getIdCategoria() != null) {
            categoria = repoCategoria
                .findById(UUID.fromString(dto.getIdCategoria()))
                .orElse(null);
        }

        InversionPlaneada inversion = InversionPlaneada.builder()
            .hato(hato)
            .descripcion(dto.getDescripcion())
            .monto(dto.getMonto())
            .mesEjecucion(dto.getMesEjecucion())
            .retornoEsperadoPct(dto.getRetornoEsperadoPct())
            .mesesRetorno(dto.getMesesRetorno())
            .categoriaFinanciera(categoria)
            .estado("PLANEADA")
            .fechaCreacion(LocalDateTime.now())
            .build();

        return toDTO(repoInversion.save(inversion));
    }

    // ── Actualizar ────────────────────────────────────────────────────────

    @Override
    @Transactional
    public InversionPlaneadaDTO actualizar(
            Long idInversion, ActualizarInversionDTO dto) {

        InversionPlaneada inv = repoInversion
            .findById(idInversion)
            .orElseThrow(() ->
                new RuntimeException("INVERSION_NO_ENCONTRADA"));

        String estadoAnterior = inv.getEstado();

        if (dto.getDescripcion() != null)
            inv.setDescripcion(dto.getDescripcion());
        if (dto.getMonto() != null)
            inv.setMonto(dto.getMonto());
        if (dto.getMesEjecucion() != null)
            inv.setMesEjecucion(dto.getMesEjecucion());
        if (dto.getRetornoEsperadoPct() != null)
            inv.setRetornoEsperadoPct(dto.getRetornoEsperadoPct());
        if (dto.getMesesRetorno() != null)
            inv.setMesesRetorno(dto.getMesesRetorno());
        if (dto.getEstado() != null)
            inv.setEstado(dto.getEstado());
        if (dto.getIdCategoria() != null) {
            repoCategoria.findById(
                UUID.fromString(dto.getIdCategoria()))
                .ifPresent(inv::setCategoriaFinanciera);
        }

        InversionPlaneada guardada = repoInversion.save(inv);

        boolean cambioAEjecutada =
            "PLANEADA".equals(estadoAnterior) &&
            "EJECUTADA".equals(dto.getEstado());

        if (cambioAEjecutada) {
            ejecutarInversion(guardada);
        }

        return toDTO(guardada);
    }

    // ── Método privado ────────────────────────────────────────

    private void ejecutarInversion(InversionPlaneada inv) {
        try {
            RegistroFinanciero registro =
                RegistroFinanciero.builder()
                    .hato(inv.getHato())
                    .titulo(inv.getDescripcion())
                    .tipoMovimiento("INVERSION")
                    .fecha(LocalDate.now())
                    .monto(inv.getMonto().floatValue())
                    .precisionFecha("EXACTA")
                    .esHistorico(false)
                    .categoriaFinanciera(
                        inv.getCategoriaFinanciera())
                    .descripcion("Inversión ejecutada: "
                        + inv.getDescripcion())
                    .build();

            repoFinanciero.save(registro);

            String email = inv.getHato()
                .getUsuario().getCorreo();

            serviceKpi.calcularYGuardarKpis(
                inv.getHato().getIdHato(), email);

        } catch (Exception e) {
            System.err.println(
                "⚠️ Error ejecutando inversión: "
                + e.getMessage());
        }
    }

    // ── Cancelar ──────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void cancelar(Long idInversion) {
        repoInversion.findById(idInversion).ifPresent(i -> {
            i.setEstado("CANCELADA");
            repoInversion.save(i);
        });
    }

    // ── Mapper ────────────────────────────────────────────────────────────

    private InversionPlaneadaDTO toDTO(InversionPlaneada i) {
        Double retornoMensual = null;
        if (i.getMonto() != null &&
            i.getRetornoEsperadoPct() != null &&
            i.getMesesRetorno() != null &&
            i.getMesesRetorno() > 0) {
            retornoMensual = (i.getMonto() *
                i.getRetornoEsperadoPct() / 100)
                / i.getMesesRetorno();
        }

        return InversionPlaneadaDTO.builder()
            .idInversion(i.getIdInversion())
            .descripcion(i.getDescripcion())
            .monto(i.getMonto())
            .mesEjecucion(i.getMesEjecucion())
            .mesEjecucionLabel(
                formatearMes(i.getMesEjecucion()))
            .retornoEsperadoPct(i.getRetornoEsperadoPct())
            .mesesRetorno(i.getMesesRetorno())
            .estado(i.getEstado())
            .fechaCreacion(i.getFechaCreacion() != null
                ? i.getFechaCreacion().format(
                    DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm"))
                : null)
            .idCategoria(i.getCategoriaFinanciera() != null
                ? i.getCategoriaFinanciera()
                    .getIdCategoriaFinanciera().toString()
                : null)
            .nombreCategoria(i.getCategoriaFinanciera() != null
                ? i.getCategoriaFinanciera().getNombre()
                : null)
            .tipoCategoria(i.getCategoriaFinanciera() != null
                ? i.getCategoriaFinanciera().getTipo()
                : null)
            .retornoMensualEstimado(retornoMensual)
            .build();
    }

    private String formatearMes(String mes) {
        if (mes == null) return "—";
        try {
            YearMonth ym = YearMonth.parse(mes);
            return ym.getMonth().getDisplayName(
                TextStyle.FULL,
                new Locale("es", "CO")) +
                " " + ym.getYear();
        } catch (Exception e) {
            return mes;
        }
    }
}