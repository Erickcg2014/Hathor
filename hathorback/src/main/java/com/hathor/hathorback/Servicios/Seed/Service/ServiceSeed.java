package com.hathor.hathorback.Servicios.Seed.Service;

import com.hathor.hathorback.Entities.Finanzas.CategoriaFinanciera;
import com.hathor.hathorback.Entities.Finanzas.RegistroFinanciero;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Inventarios.CategoriaGanado;
import com.hathor.hathorback.Entities.Inventarios.CategoriaInventario;
import com.hathor.hathorback.Entities.Inventarios.InventarioGanado;
import com.hathor.hathorback.Entities.Inventarios.InventarioGeneral;
import com.hathor.hathorback.Entities.Inventarios.Raza;
import com.hathor.hathorback.Entities.Practicas.HatoPractica;
import com.hathor.hathorback.Entities.Practicas.Practica;
import com.hathor.hathorback.Entities.Produccion.PerfilProductivo;
import com.hathor.hathorback.Entities.Produccion.ProduccionLeche;
import com.hathor.hathorback.Entities.Usuario.Usuario;
import com.hathor.hathorback.Servicios.Seed.DTO.*;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.Service.IServiceBenchmarking;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking.Service.ServiceRanking;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.Repository.IRepositoryCategoriaFinanciera;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero.Repository.IRepositoryRegistroFinanciero;
import com.hathor.hathorback.Servicios.Usuario.Hato.Repository.IRepositoryHato;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaGanado.Repository.IRepositoryCategoriaGanado;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.CategoriaInventario.Repository.IRepositoryCategoriaInventario;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGanado.Repository.IRepositoryInventarioGanado;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGeneral.Repository.IRepositoryInventarioGeneral;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.Raza.Repository.IRepositoryRaza;
import com.hathor.hathorback.Servicios.Usuario.Kpi.Service.IServiceKpi;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Repository.IRepositoryHatoPractica;
import com.hathor.hathorback.Servicios.Usuario.Practicas.ServicePracticas.Repository.IRepositoryPractica;
import com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo.Repository.IRepositoryPerfilProductivo;
import com.hathor.hathorback.Servicios.Usuario.Produccion.ProduccionLeche.Repository.IRepositoryProduccionLeche;
import com.hathor.hathorback.Servicios.Usuario.Alertas.Service.IServiceAlertas;
import com.hathor.hathorback.Servicios.Usuario.InfoUsuario.Repository.IRepositoryUsuario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class ServiceSeed implements IServiceSeed {

    @Autowired private IRepositoryUsuario            repoUsuario;
    @Autowired private IRepositoryHato               repoHato;
    @Autowired private IRepositoryPerfilProductivo   repoPerfil;
    @Autowired private IRepositoryInventarioGanado   repoInvGanado;
    @Autowired private IRepositoryInventarioGeneral  repoInvGeneral;
    @Autowired private IRepositoryCategoriaGanado    repoCatGanado;
    @Autowired private IRepositoryCategoriaInventario repoCatInventario;
    @Autowired private IRepositoryRaza               repoRaza;
    @Autowired private IRepositoryRegistroFinanciero repoFinanciero;
    @Autowired private IRepositoryCategoriaFinanciera repoCatFinanciera;
    @Autowired private IRepositoryProduccionLeche    repoProduccion;
    @Autowired private IRepositoryHatoPractica       repoHatoPractica;
    @Autowired private IRepositoryPractica repoPractica;

    @Autowired private IServiceKpi                   serviceKpi;
    @Autowired private IServiceBenchmarking          serviceBenchmarking;
    @Autowired private IServiceAlertas               serviceAlertas;

    private static final Logger log = 
        LoggerFactory.getLogger(ServiceRanking.class);

    // ── Orquestador completo ──────────────────────────────────────────────

    @Override
    @Transactional
    public SeedResultadoDTO seedHatoCompleto(
            SeedHatoCompletoDTO dto) {

        List<String> advertencias = new ArrayList<>();

        // 1. Verificar usuario
        Usuario usuario = repoUsuario.findByIdAuth(
            UUID.fromString(dto.getIdUsuarioAuth()));
        if (usuario == null) {
            return SeedResultadoDTO.builder()
                .exitoso(false)
                .mensaje("Usuario no encontrado con idAuth: "
                    + dto.getIdUsuarioAuth())
                .advertencias(advertencias)
                .build();
        }

        // 2. Crear hato
        Hato hato = crearHato(dto.getHato(), usuario);

        // 3. Crear perfil productivo
        crearPerfilProductivo(dto.getPerfilProductivo(), hato);


        // 4. Crear inventarios
        int itemsGanado = 0;
        int itemsGeneral = 0;

        if (dto.getInventarioGanado() != null) {
            itemsGanado = crearInventarioGanado(
                dto.getInventarioGanado(), hato,
                advertencias);
        }

        if (dto.getInventarioGeneral() != null) {
            itemsGeneral = crearInventarioGeneral(
                dto.getInventarioGeneral(), hato,
                advertencias);
        }

        // 5. Crear finanzas
        int registrosFinancieros = 0;
        if (dto.getFinanzas() != null) {
            registrosFinancieros = crearFinanzas(
                dto.getFinanzas(), hato, advertencias);
        }

        // 6. Crear producción
        int diasProduccion = 0;
        if (dto.getProduccion() != null) {
            diasProduccion = crearProduccion(
                dto.getProduccion(), hato, advertencias);
        }

        // 7. Calcular KPIs
        try {
            serviceKpi.calcularYGuardarKpis(hato.getIdHato(), hato.getUsuario().getCorreo());
        } catch (Exception e) {
            advertencias.add("⚠️ Error calculando KPIs: "
                + e.getMessage());
        }

        // 8. Calcular benchmarking
        try {
            serviceBenchmarking.calcularTodo(
                hato.getIdHato());
        } catch (Exception e) {
            advertencias.add(
                "⚠️ Error calculando benchmarking: "
                + e.getMessage());
        }

        // 9. Evaluar alertas
        try {
            serviceAlertas.evaluarAlertas(
                hato.getIdHato());
        } catch (Exception e) {
            advertencias.add("⚠️ Error evaluando alertas: "
                + e.getMessage());
        }

        // 10. Asignar prácticas
        int practicasAsignadas = 0;
        if (dto.getPracticas() != null) {
            practicasAsignadas = asignarPracticas(
                dto.getPracticas(), hato, advertencias);
        }

        return SeedResultadoDTO.builder()
            .exitoso(true)
            .idHato(hato.getIdHato().toString())
            .nombreHato(hato.getNombreHato())
            .perfil(dto.getPerfil())
            .mensaje("Hato creado exitosamente")
            .registrosFinancieros(registrosFinancieros)
            .diasProduccion(diasProduccion)
            .itemsInventarioGanado(itemsGanado)
            .itemsInventarioGeneral(itemsGeneral)
            .practicasAsignadas(practicasAsignadas)
            .advertencias(advertencias)
            .build();
    }

    // ── Modulares ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public SeedResultadoDTO seedInventario(
            UUID idHato, SeedInventarioDTO dto) {

        Hato hato = repoHato.findById(idHato)
            .orElseThrow(() ->
                new RuntimeException("HATO_NO_ENCONTRADO"));

        List<String> advertencias = new ArrayList<>();
        int itemsGanado  = 0;
        int itemsGeneral = 0;

        if (dto.getInventarioGanado() != null) {
            itemsGanado = crearInventarioGanado(
                dto.getInventarioGanado(), hato,
                advertencias);
        }
        if (dto.getInventarioGeneral() != null) {
            itemsGeneral = crearInventarioGeneral(
                dto.getInventarioGeneral(), hato,
                advertencias);
        }

        return SeedResultadoDTO.builder()
            .exitoso(true)
            .idHato(idHato.toString())
            .nombreHato(hato.getNombreHato())
            .mensaje("Inventario creado correctamente")
            .itemsInventarioGanado(itemsGanado)
            .itemsInventarioGeneral(itemsGeneral)
            .advertencias(advertencias)
            .build();
    }

    @Override
    @Transactional
    public SeedResultadoDTO seedFinanzas(
            UUID idHato, SeedFinanzasDTO dto) {

        Hato hato = repoHato.findById(idHato)
            .orElseThrow(() ->
                new RuntimeException("HATO_NO_ENCONTRADO"));

        List<String> advertencias = new ArrayList<>();
        int registros = crearFinanzas(
            dto.getFinanzas(), hato, advertencias);

        return SeedResultadoDTO.builder()
            .exitoso(true)
            .idHato(idHato.toString())
            .nombreHato(hato.getNombreHato())
            .mensaje("Finanzas creadas correctamente")
            .registrosFinancieros(registros)
            .advertencias(advertencias)
            .build();
    }

    @Override
    @Transactional
    public SeedResultadoDTO seedProduccion(
            UUID idHato, SeedProduccionDTO dto) {

        Hato hato = repoHato.findById(idHato)
            .orElseThrow(() ->
                new RuntimeException("HATO_NO_ENCONTRADO"));

        List<String> advertencias = new ArrayList<>();
        int dias = crearProduccion(
            dto.getProduccion(), hato, advertencias);

        return SeedResultadoDTO.builder()
            .exitoso(true)
            .idHato(idHato.toString())
            .nombreHato(hato.getNombreHato())
            .mensaje("Producción creada correctamente")
            .diasProduccion(dias)
            .advertencias(advertencias)
            .build();
    }

    @Override
    @Transactional
    public SeedResultadoDTO seedKpis(UUID idHato) {

        Hato hato = repoHato.findById(idHato)
            .orElseThrow(() ->
                new RuntimeException("HATO_NO_ENCONTRADO"));

        List<String> advertencias = new ArrayList<>();

        try {
            serviceKpi.calcularYGuardarKpis(hato.getIdHato(),hato.getUsuario().getCorreo());
        } catch (Exception e) {
            advertencias.add("⚠️ Error KPIs: "
                + e.getMessage());
        }

        try {
            serviceBenchmarking.calcularTodo(idHato);
        } catch (Exception e) {
            advertencias.add("⚠️ Error benchmarking: "
                + e.getMessage());
        }

        try {
            serviceAlertas.evaluarAlertas(idHato);
        } catch (Exception e) {
            advertencias.add("⚠️ Error alertas: "
                + e.getMessage());
        }

        return SeedResultadoDTO.builder()
            .exitoso(true)
            .idHato(idHato.toString())
            .nombreHato(hato.getNombreHato())
            .mensaje("KPIs, benchmarking y alertas calculados")
            .advertencias(advertencias)
            .build();
    }

    @Override
    @Transactional
    public SeedResultadoDTO seedPracticas(
            UUID idHato, SeedPracticaDTO dto) {

        Hato hato = repoHato.findById(idHato)
            .orElseThrow(() ->
                new RuntimeException("HATO_NO_ENCONTRADO"));

        List<String> advertencias = new ArrayList<>();
        int asignadas = asignarPracticas(
            List.of(dto), hato, advertencias);

        return SeedResultadoDTO.builder()
            .exitoso(true)
            .idHato(idHato.toString())
            .nombreHato(hato.getNombreHato())
            .mensaje("Prácticas asignadas")
            .practicasAsignadas(asignadas)
            .advertencias(advertencias)
            .build();
    }

    // ── Métodos internos ──────────────────────────────────────────────────

    private Hato crearHato(
        SeedHatoInfoDTO dto, Usuario usuario) {

        Hato hato = Hato.builder()
            .nombreHato(dto.getNombreHato())
            .tipoHato(dto.getTipoHato())
            .departamento(dto.getDepartamento())
            .ciudad(dto.getCiudad())
            .direccion(dto.getDireccion())
            .tropico(dto.getTropico())
            .escala(dto.getEscala())
            .areaHato(dto.getAreaHato().floatValue())
            .areaPastoreo(dto.getAreaPastoreo() != null
                ? dto.getAreaPastoreo().floatValue() : 0f)
            .altitud(dto.getAltitud().floatValue())
            .latitud(dto.getLatitud())
            .longitud(dto.getLongitud())
            .cantCorrales(dto.getCantCorrales() != null
                ? dto.getCantCorrales() : 0)
            .cantSalasOrdenio(dto.getCantSalasOrdenio() != null
                ? dto.getCantSalasOrdenio() : 0)
            .capacidadAlmacenarLeche(
                dto.getCapacidadAlmacenarLeche() != null
                ? dto.getCapacidadAlmacenarLeche().floatValue()
                : 0f)
            .cantEmpleadosPermanentes(
                dto.getCantEmpleadosPermanentes() != null
                ? dto.getCantEmpleadosPermanentes() : 0)
            .cantEmpleadosTemporales(
                dto.getCantEmpleadosTemporales() != null
                ? dto.getCantEmpleadosTemporales() : 0)
            .gastoMensualNomina(dto.getGastoMensualNomina())
            .gastoMensualAlimentacion(
                dto.getGastoMensualAlimentacion())
            .porcentajeCompletitud(100)
            .usuario(usuario)
            .build();

        return repoHato.save(hato);
    }

    private void crearPerfilProductivo(SeedPerfilProductivoDTO dto, Hato hato) {

        PerfilProductivo perfil = PerfilProductivo.builder()
            .hato(hato)
            .razaPredominante(dto.getRazaPredominante())
            .produccionDiariaLitros(
                dto.getProduccionDiariaLitros())
            .precioLitroPromedio(dto.getPrecioLitroPromedio())
            .vacasEnOrdenio(dto.getVacasEnOrdenio())
            .frecuenciaOrdenio(dto.getFrecuenciaOrdenio() != null
                ? dto.getFrecuenciaOrdenio() : 2)
            .sistemaOrdenio(dto.getSistemaOrdenio())
            .destinoLeche(dto.getDestinoLeche() != null
                ? dto.getDestinoLeche() : "INDUSTRIA")
            .periodoLactanciaPromedio(dto.getDiasLactancia())
            .fechaActualizacion(LocalDate.now())
            .build();

        repoPerfil.save(perfil);
    }

    private int crearInventarioGanado(
            List<SeedInventarioGanadoItemDTO> items,
            Hato hato, List<String> advertencias) {

        int count = 0;
        for (SeedInventarioGanadoItemDTO item : items) {

            // Buscar categoría por nombre
            CategoriaGanado categoria = repoCatGanado
                .findAll().stream()
                .filter(c -> c.getNombreCategoria()
                    .equalsIgnoreCase(
                        item.getNombreCategoria()))
                .findFirst().orElse(null);

            if (categoria == null) {
                advertencias.add(
                    "⚠️ Categoría ganado no encontrada: "
                    + item.getNombreCategoria());
                continue;
            }

            // Buscar raza por nombre
            Raza raza = repoRaza.findAll().stream()
                .filter(r -> r.getNombre()
                    .equalsIgnoreCase(item.getNombreRaza()))
                .findFirst().orElse(null);

            if (raza == null) {
                advertencias.add(
                    "⚠️ Raza no encontrada: "
                    + item.getNombreRaza());
                continue;
            }

            float valorUnit = item.getValorUnitario() != null
                ? item.getValorUnitario() : 0f;

            InventarioGanado inv = InventarioGanado.builder()
                .hato(hato)
                .categoriaGanado(categoria)
                .raza(raza)
                .cantidad(item.getCantidad())
                .edadPromedioMeses(
                    item.getEdadPromedioMeses())
                .valorUnitario(valorUnit)
                .fechaRegistro(LocalDate.now())
                .build();

            repoInvGanado.save(inv);
            count++;
        }
        return count;
    }

    private int crearInventarioGeneral(
            List<SeedInventarioGeneralItemDTO> items,
            Hato hato, List<String> advertencias) {

        int count = 0;
        for (SeedInventarioGeneralItemDTO item : items) {

            // Buscar categoría por nombre
            CategoriaInventario categoria =
                repoCatInventario.findAll().stream()
                    .filter(c -> c.getNombre()
                        .equalsIgnoreCase(
                            item.getNombreCategoria()))
                    .findFirst().orElse(null);

            if (categoria == null) {
                advertencias.add(
                    "⚠️ Categoría inventario no encontrada: "
                    + item.getNombreCategoria());
                continue;
            }

            float valorUnit = item.getValorUnitario() != null
                ? item.getValorUnitario() : 0f;

            InventarioGeneral inv = InventarioGeneral.builder()
                .hato(hato)
                .categoriaInventario(categoria)
                .nombreItem(item.getNombre())
                .cantidad(item.getCantidad())
                .valorUnitario(valorUnit)
                .descripcion(item.getDescripcion())
                .build();

            repoInvGeneral.save(inv);
            count++;
        }
        return count;
    }

    private int crearFinanzas(
            List<SeedFinanzasMesDTO> meses,
            Hato hato, List<String> advertencias) {

        int count = 0;
        for (SeedFinanzasMesDTO mes : meses) {

            YearMonth ym = YearMonth.parse(mes.getMes());
            LocalDate fecha = ym.atDay(1);

            // Ingresos
            if (mes.getIngresos() != null) {
                for (SeedFinanzasItemDTO item
                        : mes.getIngresos()) {
                    CategoriaFinanciera cat =
                        buscarCategoriaFinanciera(
                            item.getNombreCategoria());
                    if (cat == null) {
                        advertencias.add(
                            "⚠️ Categoría financiera no "
                            + "encontrada: "
                            + item.getNombreCategoria());
                        continue;
                    }
                    repoFinanciero.save(
                        RegistroFinanciero.builder()
                            .hato(hato)
                            .titulo(item.getNombreCategoria())
                            .tipoMovimiento("INGRESO")
                            .fecha(fecha)
                            .monto(item.getMonto())
                            .precisionFecha("MENSUAL")
                            .esHistorico(true)
                            .categoriaFinanciera(cat)
                            .build());
                    count++;
                }
            }

            // Egresos
            if (mes.getEgresos() != null) {
                for (SeedFinanzasItemDTO item
                        : mes.getEgresos()) {
                    CategoriaFinanciera cat =
                        buscarCategoriaFinanciera(
                            item.getNombreCategoria());
                    if (cat == null) {
                        advertencias.add(
                            "⚠️ Categoría financiera no "
                            + "encontrada: "
                            + item.getNombreCategoria());
                        continue;
                    }
                    repoFinanciero.save(
                        RegistroFinanciero.builder()
                            .hato(hato)
                            .titulo(item.getNombreCategoria())
                            .tipoMovimiento("GASTO")
                            .fecha(fecha)
                            .monto(item.getMonto())
                            .precisionFecha("MENSUAL")
                            .esHistorico(true)
                            .categoriaFinanciera(cat)
                            .build());
                    count++;
                }
            }
        }
        return count;
    }

    private int crearProduccion(
            List<SeedProduccionDiaDTO> dias,
            Hato hato, List<String> advertencias) {

        int count = 0;
        for (SeedProduccionDiaDTO dia : dias) {
            try {
                ProduccionLeche prod =
                    ProduccionLeche.builder()
                        .hato(hato)
                        .fecha(LocalDate.parse(dia.getFecha()))
                        .litrosProducidos(
                            dia.getLitrosProducidos())
                        .vacasOrdenadas(
                            dia.getVacasOrdenadas())
                        .build();
                repoProduccion.save(prod);
                count++;
            } catch (Exception e) {
                advertencias.add(
                    "⚠️ Error producción fecha "
                    + dia.getFecha() + ": "
                    + e.getMessage());
            }
        }
        return count;
    }

    private int asignarPracticas(
        List<SeedPracticaDTO> practicas,
        Hato hato, List<String> advertencias) {

    int count = 0;
    for (SeedPracticaDTO dto : practicas) {
        try {
            // Buscar práctica por nombre
            Optional<Practica> practicaOpt =
                repoPractica.findAll().stream()
                    .filter(p -> p.getNombre()
                        .equalsIgnoreCase(
                            dto.getNombrePractica()))
                    .findFirst();

            if (practicaOpt.isEmpty()) {
                advertencias.add(
                    "⚠️ Práctica no encontrada: "
                    + dto.getNombrePractica());
                continue;
            }

            Practica practica = practicaOpt.get();

            // Verificar si ya existe asignada
            boolean yaExiste =
                repoHatoPractica.existsPendienteOEnCurso(
                    hato.getIdHato(),
                    practica.getIdPractica());

            if (yaExiste) {
                advertencias.add(
                    "⚠️ Práctica ya asignada: "
                    + dto.getNombrePractica());
                continue;
            }

            // Porcentaje según estado
            Float pct = switch (dto.getEstado()) {
                case "COMPLETADA" -> 100f;
                case "EN_CURSO"   -> 50f;
                default           -> 0f;
            };

            // Fechas según estado
            LocalDate fechaInicio = LocalDate.now()
                .minusMonths(2);
            LocalDate fechaFin = "COMPLETADA"
                .equals(dto.getEstado())
                ? LocalDate.now().minusMonths(1)
                : null;

            HatoPractica hatoPractica =
                HatoPractica.builder()
                    .hato(hato)
                    .practica(practica)
                    .estado(dto.getEstado())
                    .porcentajeAvance(pct)
                    .fechaInicio(fechaInicio)
                    .fechaFin(fechaFin)
                    .recomendacion(null)
                    .build();

            repoHatoPractica.save(hatoPractica);
            count++;

        } catch (Exception e) {
            advertencias.add(
                "⚠️ Error práctica '"
                + dto.getNombrePractica()
                + "': " + e.getMessage());
        }
    }
    return count;
}

    @Override
    public SeedResultadoDTO recalcularKpisTodos() {
        List<Hato> todos = repoHato.findAll();
        int total = todos.size();
        
        log.info("══════════════════════════════════════");
        log.info("Iniciando recálculo de KPIs");
        log.info("Total de hatos encontrados: {}", total);
        log.info("══════════════════════════════════════");

        List<String> advertencias = new ArrayList<>();
        int exitosos = 0;
        int fallidos = 0;

        for (int i = 0; i < todos.size(); i++) {
            Hato hato = todos.get(i);
            int numero = i + 1;

            log.info("[{}/{}] Calculando KPIs → '{}'",
                    numero, total, hato.getNombreHato());
            try {
                serviceKpi.calcularYGuardarKpis(
                    hato.getIdHato(),
                    hato.getUsuario().getCorreo()
                );
                log.info("[{}/{}] ✓ KPIs calculados correctamente", numero, total);
                exitosos++;

            } catch (Exception e) {
                log.error("[{}/{}] ✗ Error en '{}': {}",
                        numero, total, hato.getNombreHato(), e.getMessage());
                advertencias.add("✗ " + hato.getNombreHato()
                    + ": " + e.getMessage());
                fallidos++;
            }
        }

        log.info("══════════════════════════════════════");
        log.info("Recálculo completado");
        log.info("Exitosos: {} / {}", exitosos, total);
        log.info("Fallidos: {}", fallidos);
        log.info("══════════════════════════════════════");

        return SeedResultadoDTO.builder()
            .exitoso(fallidos == 0)
            .mensaje("Recálculo completado — Exitosos: "
                + exitosos + " | Fallidos: " + fallidos)
            .advertencias(advertencias)
            .build();
    }
    

    public SeedResultadoDTO recalcularBenchmarkingTodos() {
        List<Hato> todos = repoHato.findAll();
        int total    = todos.size();
        int exitosos = 0;
        int fallidos = 0;
        List<String> advertencias = new ArrayList<>();

        log.info("══════════════════════════════════════");
        log.info("Iniciando recálculo de benchmarking");
        log.info("Total de hatos: {}", total);
        log.info("══════════════════════════════════════");

        for (int i = 0; i < todos.size(); i++) {
            Hato hato   = todos.get(i);
            int  numero = i + 1;

            log.info("[{}/{}] Calculando benchmarking → '{}'",
                    numero, total, hato.getNombreHato());
            try {
                serviceBenchmarking.calcularTodo(hato.getIdHato());
                log.info("[{}/{}] ✓ Benchmarking calculado correctamente",
                        numero, total);
                exitosos++;
            } catch (Exception e) {
                log.error("[{}/{}] ✗ Error en '{}': {}",
                        numero, total, hato.getNombreHato(), e.getMessage());
                advertencias.add("✗ " + hato.getNombreHato()
                    + ": " + e.getMessage());
                fallidos++;
            }
        }

        log.info("══════════════════════════════════════");
        log.info("Benchmarking completado");
        log.info("Exitosos: {} / {}", exitosos, total);
        log.info("Fallidos: {}", fallidos);
        log.info("══════════════════════════════════════");

        return SeedResultadoDTO.builder()
            .exitoso(fallidos == 0)
            .mensaje("Benchmarking completado — Exitosos: "
                + exitosos + " | Fallidos: " + fallidos)
            .advertencias(advertencias)
            .build();
    }
    // ── Helper búsqueda categoría financiera ─────────────────────────────

    private CategoriaFinanciera buscarCategoriaFinanciera(
            String nombre) {
        return repoCatFinanciera.findAll().stream()
            .filter(c -> c.getNombre()
                .equalsIgnoreCase(nombre))
            .findFirst().orElse(null);
    }

    @Override
    @Transactional
    public SeedResultadoDTO eliminarHatoPorUsuario(String idUsuarioAuth) {

        Usuario usuario = repoUsuario.findByIdAuth(
            UUID.fromString(idUsuarioAuth));

        if (usuario == null) {
            return SeedResultadoDTO.builder()
                .exitoso(false)
                .mensaje("Usuario no encontrado: " + idUsuarioAuth)
                .build();
        }

        List<Hato> hatos = repoHato.findByUsuario_IdUsuario(usuario.getIdUsuario());

        if (hatos == null || hatos.isEmpty()) {
            return SeedResultadoDTO.builder()
                .exitoso(false)
                .mensaje("Hato no encontrado para usuario: " + idUsuarioAuth)
                .build();
        }

        Hato hato = hatos.get(0);

        UUID idHato = hato.getIdHato();

        repoHatoPractica.deleteByHato_IdHato(idHato);
        repoProduccion.deleteByHato_IdHato(idHato);
        repoFinanciero.deleteByHato_IdHato(idHato);
        repoInvGanado.deleteByHato_IdHato(idHato);
        repoInvGeneral.deleteByHato_IdHato(idHato);
        repoPerfil.deleteByHato_IdHato(idHato);
        repoHato.delete(hato);
        repoUsuario.delete(usuario);

        return SeedResultadoDTO.builder()
            .exitoso(true)
            .idHato(idHato.toString())
            .mensaje("Hato y usuario eliminados correctamente")
            .build();
    }


}