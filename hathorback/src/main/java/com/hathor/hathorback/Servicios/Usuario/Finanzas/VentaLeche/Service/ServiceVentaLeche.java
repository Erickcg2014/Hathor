package com.hathor.hathorback.Servicios.Usuario.Finanzas.VentaLeche.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hathor.hathorback.Entities.Finanzas.CategoriaFinanciera;
import com.hathor.hathorback.Entities.Finanzas.RegistroFinanciero;
import com.hathor.hathorback.Entities.Finanzas.VentaLeche;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.Repository.IRepositoryCategoriaFinanciera;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero.Repository.IRepositoryRegistroFinanciero;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.VentaLeche.DTO.RegistroVentaLecheDTO;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.VentaLeche.Repository.IRepositoryVentaLeche;
import com.hathor.hathorback.Servicios.Usuario.Hato.Repository.IRepositoryHato;
import com.hathor.hathorback.Servicios.Usuario.InfoUsuario.Service.IServiceUsuario;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.VentaLeche.DTO.RespuestaVentaLecheDTO;
import com.hathor.hathorback.Servicios.Usuario.Produccion.ProduccionLeche.Repository.IRepositoryProduccionLeche;
import java.time.format.DateTimeFormatter;

import jakarta.transaction.Transactional;

@Service
public class ServiceVentaLeche implements IServiceVentaLeche{

    @Autowired
    IServiceUsuario usuarioService;
    
    @Autowired
    IRepositoryVentaLeche repositoryVentaLeche;

    @Autowired
    private IRepositoryRegistroFinanciero repoRegistroFinanciero;

    @Autowired
    private IRepositoryHato repoHato;

    @Autowired
    private IRepositoryProduccionLeche repoProduccionLeche;

    @Autowired
    private IRepositoryCategoriaFinanciera repoCategoria;

    @Override
    public VentaLeche createVentaLeche(VentaLeche ventaleche) {
        return repositoryVentaLeche.save(ventaleche);
    }
    @Override
    public List<VentaLeche> getByHato(UUID idHato) {
        return repositoryVentaLeche.findByHatoId(idHato);
    }
    
    @Override
    @Transactional
    public RespuestaVentaLecheDTO registrarVenta(RegistroVentaLecheDTO dto, String email) {
        Hato hato = repoHato.findById(dto.getIdHato())
            .orElseThrow(() -> new RuntimeException("HATO_NO_ENCONTRADO"));

        float totalVenta = dto.getPrecioLitro() * dto.getLitrosVendidos();

        CategoriaFinanciera categoria = repoCategoria
            .findAll()
            .stream()
            .filter(c -> c.getNombre().toUpperCase()
                .contains("LECHE") || c.getNombre().toUpperCase()
                .contains("VENTA"))
            .findFirst()
            .orElse(null);

        RegistroFinanciero registro = RegistroFinanciero.builder()
            .hato(hato)
            .titulo("Venta de leche — " + dto.getFecha())
            .monto(totalVenta)
            .fecha(dto.getFecha())
            .tipoMovimiento("INGRESO")
            .categoriaFinanciera(categoria)
            .esHistorico(false)
            .build();

        registro = repoRegistroFinanciero.save(registro);

        VentaLeche venta = VentaLeche.builder()
            .fecha(dto.getFecha())
            .precioLitro(dto.getPrecioLitro())
            .litrosVendidos(dto.getLitrosVendidos())
            .registroFinanciero(registro)
            .build();

        venta = repositoryVentaLeche.save(venta);

        // ── Comparación litros vendidos vs producidos en el mes ──
        String mes = dto.getFecha()
            .format(DateTimeFormatter.ofPattern("yyyy-MM"));

        Float litrosVendidosMes = repositoryVentaLeche
            .sumLitrosVendidosByHatoAndMes(dto.getIdHato(), mes);
        Float litrosProducidosMes = repoProduccionLeche
            .sumLitrosProducidosByHatoAndMes(dto.getIdHato(), mes);

        litrosVendidosMes  = litrosVendidosMes  != null ? litrosVendidosMes  : 0f;
        litrosProducidosMes = litrosProducidosMes != null ? litrosProducidosMes : 0f;

        boolean alerta = litrosVendidosMes > litrosProducidosMes;
        float litrosFaltantes = alerta
            ? litrosVendidosMes - litrosProducidosMes
            : 0f;

        String mensaje = alerta
            ? "Estás vendiendo " + String.format("%.1f", litrosFaltantes)
            + " litros más de los que tienes registrados como producción en "
            + mes + ". ¿Deseas agregar esos litros a tu registro de producción?"
            : null;

        return RespuestaVentaLecheDTO.builder()
            .ventaRegistrada(venta)
            .alerta(alerta)
            .litrosFaltantes(litrosFaltantes)
            .mensaje(mensaje)
            .build();
    }
}
