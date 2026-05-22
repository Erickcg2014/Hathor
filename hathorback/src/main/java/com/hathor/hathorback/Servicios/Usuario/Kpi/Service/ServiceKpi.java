package com.hathor.hathorback.Servicios.Usuario.Kpi.Service;

import com.hathor.hathorback.Entities.Benchmark.BenchmarkReferencia;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Kpi.Kpi;
import com.hathor.hathorback.Entities.Kpi.KpiHato;
import com.hathor.hathorback.Servicios.Usuario.Alertas.Service.IServiceAlertas;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.BenchmarkReferencia.Service.IServiceBenchmarkReferencia;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.Service.IServiceBenchmarking;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.Service.ServiceBenchmarkingAsync;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking.Service.ServiceRanking;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero.Repository.IRepositoryRegistroFinanciero;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.VentaLeche.Repository.IRepositoryVentaLeche;
import com.hathor.hathorback.Servicios.Usuario.Hato.Repository.IRepositoryHato;
import com.hathor.hathorback.Servicios.Usuario.Hato.Service.IServiceHato;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGanado.Repository.IRepositoryInventarioGanado;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGeneral.Repository.IRepositoryInventarioGeneral;
import com.hathor.hathorback.Servicios.Usuario.Kpi.DTO.DetalleCalculoItem;
import com.hathor.hathorback.Servicios.Usuario.Kpi.DTO.KpiHistoricoDTO;
import com.hathor.hathorback.Servicios.Usuario.Kpi.DTO.KpiResultadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Kpi.Repository.IRepositoryKpi;
import com.hathor.hathorback.Servicios.Usuario.Kpi.Repository.IRepositoryKpiHato;
import com.hathor.hathorback.Servicios.Usuario.Kpi.Service.Calculador.DatosHato;
import com.hathor.hathorback.Servicios.Usuario.Kpi.Service.Calculador.KpiCalculadorService;
import com.hathor.hathorback.Servicios.Usuario.Kpi.Service.Calculador.KpiDetalleService;
import com.hathor.hathorback.Servicios.Usuario.Kpi.Service.Calculador.KpiMapperService;
import com.hathor.hathorback.Servicios.Usuario.Kpi.Service.Calculador.KpiRazonesService;
import com.hathor.hathorback.Servicios.Usuario.Practicas.Reglas.Service.IServiceMotorReglas;
import com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo.Repository.IRepositoryPerfilProductivo;
import com.hathor.hathorback.Servicios.Usuario.Produccion.ProduccionLeche.Repository.IRepositoryProduccionLeche;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class ServiceKpi implements IServiceKpi {

    @Autowired private IServiceHato hatoService;
    @Autowired private IRepositoryKpi repositoryKpi;
    @Autowired private IRepositoryKpiHato repositoryKpiHato;
    @Autowired private IRepositoryInventarioGanado repositoryInventarioGanado;
    @Autowired private IRepositoryInventarioGeneral repositoryInventarioGeneral;
    @Autowired private IRepositoryPerfilProductivo repositoryPerfilProductivo;
    @Autowired private IRepositoryProduccionLeche repositoryProduccionLeche;
    @Autowired private IRepositoryRegistroFinanciero repositoryRegistroFinanciero;
    @Autowired private IRepositoryVentaLeche repositoryVentaLeche;
    @Autowired private KpiCalculadorService calculadorService;
    @Autowired private KpiMapperService mapperService;
    @Autowired private IRepositoryHato repositoryHato;
    @Autowired private IServiceBenchmarkReferencia benchmarkService;
    @Autowired private IServiceMotorReglas motorReglas;
    @Autowired private KpiDetalleService detalleService;
    @Autowired private KpiRazonesService razonesService;
    @Autowired private IServiceAlertas serviceAlertas;

    @Autowired
    private ServiceBenchmarkingAsync serviceBenchmarkingAsync;

     private static final Logger log = 
        LoggerFactory.getLogger(ServiceKpi.class);

    @Override
    public Kpi getKpiById (int id){
        return repositoryKpi.findByIdKpi(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = "kpisHato", key = "#idHato")
    public List<KpiResultadoDTO> calcularYGuardarKpis(UUID idHato, String email) {
        Hato hato = hatoService.findHatoById(idHato, email);
        DatosHato datos = cargarDatos(idHato);
        Map<String, Float> valores = calculadorService.calcularTodos(hato, datos);

        String periodo = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate hoy  = LocalDate.now();
        List<Kpi> todosKpis = repositoryKpi.findAll();
        Map<String, String> razones = razonesService.getRazonesSinDatos(hato, datos);

        Map<String, KpiHato> existentesHoy = repositoryKpiHato
            .findByHato_IdHatoAndFechaCalculo(idHato, hoy)
            .stream()
            .collect(Collectors.toMap(k -> k.getKpi().getCodigo(), k -> k));

        List<KpiHato> paraGuardar = new ArrayList<>();

        for (Kpi kpi : todosKpis) {
            Float valor = valores.get(kpi.getCodigo());
            Optional<BenchmarkReferencia> bench =
                benchmarkService.getBenchmark(kpi.getCodigo(), hato.getTropico());

            Float benchPromedio = bench.map(BenchmarkReferencia::getValorPromedio).orElse(null);
            Float benchTop      = bench.map(BenchmarkReferencia::getValorTop).orElse(null);
            String estado = mapperService.calcularEstado(
                kpi.getCodigo(), valor, benchPromedio, benchTop);

            KpiHato existente = existentesHoy.get(kpi.getCodigo());
            if (existente != null) {
                existente.setValor(valor);
                existente.setEstado(estado);
                paraGuardar.add(existente);
            } else {
                paraGuardar.add(KpiHato.builder()
                    .hato(hato).kpi(kpi).valor(valor)
                    .fechaCalculo(hoy).periodo(periodo).estado(estado)
                    .build());
            }
        }

        repositoryKpiHato.saveAll(paraGuardar);

        motorReglas.evaluar(hato, valores);

        try { serviceAlertas.evaluarAlertas(idHato); }
        catch (Exception e) { log.warn("⚠️ Error alertas: {}", e.getMessage()); }

        // ── Lanzar benchmarking en segundo plano ────────────────────
        serviceBenchmarkingAsync.calcularEnSegundoPlano(idHato);

        List<KpiHato> registros = repositoryKpiHato
            .findByHato_IdHatoOrderByFechaCalculoDesc(idHato);
        Map<String, KpiHato> porCodigo = new LinkedHashMap<>();
        for (KpiHato k : registros)
            porCodigo.putIfAbsent(k.getKpi().getCodigo(), k);

        Map<String, List<DetalleCalculoItem>> detalles =
            detalleService.calcularDetalles(hato, datos);

        return porCodigo.values().stream()
            .map(kh -> mapperService.toDTO(kh, razones, hato.getTropico(), detalles))
            .collect(Collectors.toList());
    }
    
    @Override
    @Cacheable(value = "kpisHato", key = "#idHato")
    public List<KpiResultadoDTO> getKpisDelHato(UUID idHato) {
        Hato hato = repositoryHato.findById(idHato)
            .orElseThrow(() -> new RuntimeException("Hato no encontrado"));

        List<KpiHato> registros = repositoryKpiHato
            .findByHato_IdHatoOrderByFechaCalculoDesc(idHato);

        if (registros.isEmpty()) return List.of();

        DatosHato datos = cargarDatos(idHato);
        Map<String, String> razones = razonesService.getRazonesSinDatos(hato, datos);
        Map<String, List<DetalleCalculoItem>> detalles =
            detalleService.calcularDetalles(hato, datos);

        Map<String, KpiHato> porCodigo = new LinkedHashMap<>();
        for (KpiHato k : registros)
            porCodigo.putIfAbsent(k.getKpi().getCodigo(), k);

        return porCodigo.values().stream()
            .map(kh -> mapperService.toDTO(kh, razones, hato.getTropico(), detalles))
            .collect(Collectors.toList());
    }

    @Override
    public List<KpiHistoricoDTO> getHistoricoKpi(UUID idHato, String codigo, String email) {
        hatoService.findHatoById(idHato, email);

        return repositoryKpiHato
            .findByHato_IdHatoAndKpi_CodigoOrderByFechaCalculoDesc(idHato, codigo)
            .stream()
            .map(k -> KpiHistoricoDTO.builder()
                .periodo(k.getPeriodo())
                .fechaCalculo(k.getFechaCalculo().toString())
                .valor(k.getValor())
                .estado(k.getEstado())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "catalogoKpis")
    public List<Kpi> getCatalogoKpis() {
        return repositoryKpi.findAllOrdenados();
    }

    private DatosHato cargarDatos(UUID idHato) {
        DatosHato d = new DatosHato();
        d.inventarioGanado  = repositoryInventarioGanado.findByHato_IdHato(idHato);
        d.inventarioGeneral = repositoryInventarioGeneral.findByHato_IdHato(idHato);
        d.perfilProductivo  = repositoryPerfilProductivo.findByHato_IdHato(idHato).orElse(null);
        d.registros         = repositoryRegistroFinanciero.findByHato_IdHato(idHato);
        d.produccionLeche   = repositoryProduccionLeche.findByHato_IdHatoOrderByFechaDesc(idHato);
        d.ventasLeche       = repositoryVentaLeche.findByHatoId(idHato);

        return d;
    }
}