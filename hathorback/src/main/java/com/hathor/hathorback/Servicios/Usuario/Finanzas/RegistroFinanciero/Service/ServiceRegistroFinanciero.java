package com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hathor.hathorback.Entities.Finanzas.CategoriaFinanciera;
import com.hathor.hathorback.Entities.Finanzas.RegistroFinanciero;
import com.hathor.hathorback.Entities.Finanzas.VentaLeche;
import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Usuario.Usuario;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.Service.IServiceCategoriaFinanciera;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero.DTO.RegistroFinancieroDTO;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero.Repository.IRepositoryRegistroFinanciero;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.VentaLeche.Service.IServiceVentaLeche;
import com.hathor.hathorback.Servicios.Usuario.Hato.Service.IServiceHato;
import com.hathor.hathorback.Servicios.Usuario.InfoUsuario.Service.IServiceUsuario;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

@Service
public class ServiceRegistroFinanciero implements IServiceRegistroFinanciero {

    @Autowired
    IServiceUsuario usuarioService;

    @Autowired
    IServiceVentaLeche ventaLecheService;

    @Autowired
    IServiceCategoriaFinanciera categoriaFinancieraService;

    @Autowired
    IRepositoryRegistroFinanciero registroFinancieroRepository;

    @Autowired
    IServiceHato hatoService;

    private static final Map<String, Integer> MESES_ES = new LinkedHashMap<>() {{
        put("ENERO", 1); put("FEBRERO", 2); put("MARZO", 3);
        put("ABRIL", 4); put("MAYO", 5); put("JUNIO", 6);
        put("JULIO", 7); put("AGOSTO", 8); put("SEPTIEMBRE", 9);
        put("OCTUBRE", 10); put("NOVIEMBRE", 11); put("DICIEMBRE", 12);
    }};

    // ===== USUARIO =====

    @Override
    public Usuario findUsuarioByTokenEmail(String email) {
        return usuarioService.findUsuarioByCorreo(email);
    }

    // ===== REGISTRO MANUAL =====

    @Override
    @Transactional
    public List<RegistroFinanciero> createRegistroFinanciero(List<RegistroFinancieroDTO> registrosDTO, String email) {

        List<RegistroFinanciero> registrosReturn = new ArrayList<>();

        Usuario usuario = findUsuarioByTokenEmail(email);

            for (RegistroFinancieroDTO registroDTO: registrosDTO){
            CategoriaFinanciera categoria = categoriaFinancieraService
                .getCategoriaFinancieraById(registroDTO.getId_categoria());

            if (categoria == null) throw new RuntimeException("Categoría no encontrada");

            Hato hato = hatoService.findHatoById(registroDTO.getId_hato());

            RegistroFinanciero registro = RegistroFinanciero.builder()
                .hato(hato)
                .titulo(registroDTO.getTitulo())
                .tipoMovimiento(registroDTO.getTipo_movimiento())
                .fecha(registroDTO.getFecha())
                .descripcion(registroDTO.getDescripcion())
                .monto(registroDTO.getMonto())
                .categoriaFinanciera(categoria)
                .esHistorico(false)
                .precisionFecha("EXACTA")
                .build();

            registro = registroFinancieroRepository.save(registro);

            if (registroDTO.getLitros_vendidos_leche() != null
                && registroDTO.getPrecio_litro_leche() != null
                && registroDTO.getLitros_vendidos_leche() != 0
                && registroDTO.getPrecio_litro_leche() != 0) {

                VentaLeche ventaLeche = VentaLeche.builder()
                    .registroFinanciero(registro)
                    .fecha(registroDTO.getFecha())
                    .litrosVendidos(registroDTO.getLitros_vendidos_leche())
                    .precioLitro(registroDTO.getPrecio_litro_leche())
                    .build();

                ventaLecheService.createVentaLeche(ventaLeche);
            }

            registrosReturn.add(registro);
        }

        return registrosReturn;
    }

    // ===== CARGA MASIVA — ENTRADA PRINCIPAL =====

    @Override
    @Transactional
    public List<Map<String, Object>> procesarCargaMasiva(
            MultipartFile archivo, UUID idHato, String email) {

        List<Map<String, Object>> resultados = new ArrayList<>();

        try (InputStream is = archivo.getInputStream();
            Workbook workbook = new XSSFWorkbook(is)) {

            Hato hato = hatoService.findHatoById(idHato, email);

            Sheet hojaIngresos = workbook.getSheet("Ingresos");
            Sheet hojaEgresos  = workbook.getSheet("Egresos");

            if (hojaIngresos != null || hojaEgresos != null) {
                if (hojaIngresos != null) {
                    String tipo = detectarTipoPlantilla(hojaIngresos);
                    List<Map<String, Object>> resIngresos = procesarHojaDinamica(hojaIngresos, hato, tipo, "INGRESO");
                    resultados.addAll(resIngresos);
                }
                if (hojaEgresos != null) {
                    String tipo = detectarTipoPlantilla(hojaEgresos);
                    List<Map<String, Object>> resEgresos = procesarHojaDinamica(hojaEgresos, hato, tipo, "GASTO");
                    resultados.addAll(resEgresos);
                }
            } else {
                Sheet sheet    = workbook.getSheetAt(0);
                String tipo    = detectarTipoPlantilla(sheet);
                int filaInicio = getFilaInicio(tipo);

                if (tipo.equals("DETALLADO")) {
                    resultados = procesarDetallado(sheet, hato, filaInicio);
                } else {
                    resultados = procesarPorPeriodoLegacy(sheet, hato, filaInicio, tipo);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error procesando el archivo Excel: " + e.getMessage());
        }

        return resultados;
    }

    // ===== PROCESAR HOJA DINÁMICA (INGRESOS O EGRESOS) =====

    private List<Map<String, Object>> procesarHojaDinamica(
            Sheet sheet, Hato hato, String tipo, String tipoMovimiento) {

        List<Map<String, Object>> resultados = new ArrayList<>();

        Row headerRow = sheet.getRow(2);
        if (headerRow == null) {
            resultados.add(Map.of(
                "hoja", tipoMovimiento, "fila", 1, "estado", "ERROR",
                "mensaje", "No se encontró la fila de encabezados en hoja " + tipoMovimiento
            ));
            return resultados;
        }

        Map<Integer, String> indiceCategoria = new LinkedHashMap<>();
        for (int c = 1; c < headerRow.getLastCellNum(); c++) {
            String nombreCol = getCellValue(headerRow.getCell(c)).trim();
            if (!nombreCol.isEmpty()) {
                indiceCategoria.put(c, nombreCol);
            }
        }

        int filaInicio = getFilaInicio(tipo);

        for (int i = filaInicio; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String fechaStr = getCellValue(row.getCell(0)).trim();
            if (fechaStr.isEmpty()) continue;

            try {
                LocalDate fecha = parsearFechaLegible(fechaStr, tipo);
                int registrosCreados = 0;

                for (Map.Entry<Integer, String> entry : indiceCategoria.entrySet()) {
                    int colIndex       = entry.getKey();
                    String nombreCat   = entry.getValue();
                    String montoStr    = getCellValue(row.getCell(colIndex)).trim();

                    if (montoStr.isEmpty() || montoStr.equals("0")) continue;

                    double monto;
                    try {
                        monto = Double.parseDouble(montoStr);
                    } catch (NumberFormatException e) {
                        resultados.add(Map.of(
                            "hoja", tipoMovimiento, "fila", i + 1, "estado", "ERROR",
                            "mensaje", "Valor no numérico en columna '" + nombreCat + "': " + montoStr
                        ));
                        continue;
                    }

                    if (monto <= 0) continue;

                    CategoriaFinanciera categoriaObj =
                        categoriaFinancieraService.getCategoriaByNombre(nombreCat);
                    if (categoriaObj == null)
                        categoriaObj = categoriaFinancieraService.getCategoriaByNombre("GENERAL");
                    if (categoriaObj == null) {
                        resultados.add(Map.of(
                            "hoja", tipoMovimiento, "fila", i + 1, "estado", "ERROR",
                            "mensaje", "Categoría no encontrada: " + nombreCat
                        ));
                        continue;
                    }

                    RegistroFinanciero registro = RegistroFinanciero.builder()
                        .hato(hato)
                        .categoriaFinanciera(categoriaObj)
                        .titulo(nombreCat)
                        .tipoMovimiento(tipoMovimiento)
                        .fecha(fecha)
                        .monto((float) monto)
                        .esHistorico(true)
                        .precisionFecha("MENSUAL")
                        .build();

                    registroFinancieroRepository.save(registro);
                    registrosCreados++;
                }

                if (registrosCreados > 0) {
                    resultados.add(Map.of(
                        "hoja", tipoMovimiento,
                        "fila", i + 1, "estado", "OK",
                        "titulo", "Período " + fechaStr + " — " + registrosCreados + " registros"
                    ));
                }

            } catch (Exception e) {
                resultados.add(Map.of(
                    "hoja", tipoMovimiento, "fila", i + 1, "estado", "ERROR",
                    "mensaje", "Error procesando fila " + fechaStr + ": " + e.getMessage()
                ));
            }
        }

        return resultados;
    }

    // =====Formato de hoja: DETALLADO =====

    private List<Map<String, Object>> procesarDetallado(
            Sheet sheet, Hato hato, int filaInicio) {

        List<Map<String, Object>> resultados = new ArrayList<>();

        for (int i = filaInicio; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                String fechaStr    = getCellValue(row.getCell(0));
                String tipo        = getCellValue(row.getCell(1)).toUpperCase().trim();
                String categoria   = getCellValue(row.getCell(2)).trim();
                String titulo      = getCellValue(row.getCell(3));
                String descripcion = getCellValue(row.getCell(4));
                String montoStr    = getCellValue(row.getCell(5));

                if (fechaStr.isEmpty() || tipo.isEmpty()
                        || titulo.isEmpty() || montoStr.isEmpty()) {
                    resultados.add(Map.of(
                        "fila", i + 1, "estado", "ERROR",
                        "mensaje", "Faltan campos obligatorios: fecha, tipo, titulo o monto_asociado"
                    ));
                    continue;
                }

                if (!tipo.matches("INGRESO|GASTO|COSTO|INVERSION")) {
                    resultados.add(Map.of(
                        "fila", i + 1, "estado", "ERROR",
                        "mensaje", "Tipo inválido: " + tipo
                    ));
                    continue;
                }

                if (categoria.isEmpty()) categoria = "GENERAL";
                CategoriaFinanciera categoriaObj =
                    categoriaFinancieraService.getCategoriaByNombre(categoria);
                if (categoriaObj == null)
                    categoriaObj = categoriaFinancieraService.getCategoriaByNombre("GENERAL");
                if (categoriaObj == null) {
                    resultados.add(Map.of(
                        "fila", i + 1, "estado", "ERROR",
                        "mensaje", "Categoría no encontrada: " + categoria
                    ));
                    continue;
                }

                double monto;
                try {
                    monto = Double.parseDouble(montoStr);
                } catch (NumberFormatException e) {
                    resultados.add(Map.of(
                        "fila", i + 1, "estado", "ERROR",
                        "mensaje", "Monto no numérico: " + montoStr
                    ));
                    continue;
                }

                String tituloFinal = titulo.isEmpty() ? categoria : titulo;

                RegistroFinanciero registro = RegistroFinanciero.builder()
                    .hato(hato)
                    .categoriaFinanciera(categoriaObj)
                    .titulo(tituloFinal)
                    .tipoMovimiento(tipo)
                    .fecha(parsearFecha(fechaStr))
                    .descripcion(descripcion.isEmpty() ? null : descripcion)
                    .monto((float) monto)
                    .esHistorico(false)
                    .precisionFecha("EXACTA")
                    .build();

                registroFinancieroRepository.save(registro);
                resultados.add(Map.of("fila", i + 1, "estado", "OK", "titulo", titulo));

            } catch (Exception e) {
                resultados.add(Map.of(
                    "fila", i + 1, "estado", "ERROR",
                    "mensaje", "Error procesando fila: " + e.getMessage()
                ));
            }
        }

        return resultados;
    }

    // ===== Formato de hoja -> MENSUAL / ANUAL CON COLUMNAS FIJAS =====

    private List<Map<String, Object>> procesarPorPeriodoLegacy(
            Sheet sheet, Hato hato, int filaInicio, String tipo) {

        List<Map<String, Object>> resultados = new ArrayList<>();

        Row headerRow = sheet.getRow(2);
        if (headerRow == null) {
            resultados.add(Map.of(
                "fila", 1, "estado", "ERROR",
                "mensaje", "No se encontró la fila de encabezados"
            ));
            return resultados;
        }

        Map<Integer, String> indiceColumna = new LinkedHashMap<>();
        for (int c = 1; c < headerRow.getLastCellNum(); c++) {
            String nombreCol = getCellValue(headerRow.getCell(c)).toLowerCase().trim();
            if (!nombreCol.isEmpty()) indiceColumna.put(c, nombreCol);
        }

        for (int i = filaInicio; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String fechaStr = getCellValue(row.getCell(0)).trim();
            if (fechaStr.isEmpty()) continue;

            try {
                LocalDate fecha = parsearFechaPorTipo(fechaStr, tipo);
                int registrosCreados = 0;

                for (Map.Entry<Integer, String> entry : indiceColumna.entrySet()) {
                    int colIndex         = entry.getKey();
                    String nombreColumna = entry.getValue();
                    String montoStr      = getCellValue(row.getCell(colIndex)).trim();

                    if (montoStr.isEmpty() || montoStr.equals("0")) continue;

                    double monto;
                    try {
                        monto = Double.parseDouble(montoStr);
                    } catch (NumberFormatException e) {
                        resultados.add(Map.of(
                            "fila", i + 1, "estado", "ERROR",
                            "mensaje", "Valor inválido en columna '" + nombreColumna + "': " + montoStr
                        ));
                        continue;
                    }

                    if (monto <= 0) continue;

                    CategoriaFinanciera categoriaObj =
                        categoriaFinancieraService.getCategoriaByNombre(nombreColumna);
                    if (categoriaObj == null)
                        categoriaObj = categoriaFinancieraService.getCategoriaByNombre("GENERAL");
                    if (categoriaObj == null) continue;

                    RegistroFinanciero registro = RegistroFinanciero.builder()
                        .hato(hato)
                        .categoriaFinanciera(categoriaObj)
                        .titulo(nombreColumna.replace("_", " "))
                        .tipoMovimiento("GASTO")
                        .fecha(fecha)
                        .monto((float) monto)
                        .esHistorico(true)
                        .precisionFecha(tipo.equals("ANUAL") ? "ANUAL" : "MENSUAL")
                        .build();

                    registroFinancieroRepository.save(registro);
                    registrosCreados++;
                }

                if (registrosCreados > 0) {
                    resultados.add(Map.of(
                        "fila", i + 1, "estado", "OK",
                        "titulo", "Período " + fechaStr + " — " + registrosCreados + " registros"
                    ));
                }

            } catch (Exception e) {
                resultados.add(Map.of(
                    "fila", i + 1, "estado", "ERROR",
                    "mensaje", "Error procesando fila " + fechaStr + ": " + e.getMessage()
                ));
            }
        }

        return resultados;
    }

    // ===== UTILIDADES =====

    private String detectarTipoPlantilla(Sheet sheet) {
        Row primeraFila = sheet.getRow(0);
        if (primeraFila == null) return "MENSUAL";
        String celda0 = getCellValue(primeraFila.getCell(0)).toUpperCase();
        String celda1 = getCellValue(primeraFila.getCell(1)).toUpperCase();
        if ("TIPO_PLANTILLA".equals(celda0)) return celda1;
        return "MENSUAL";
    }

    private int getFilaInicio(String tipo) {
        return tipo.equals("DETALLADO") ? 1 : 3;
    }

    // Parseaer 3 fechas legibles que son generadas por el frontend:

    private LocalDate parsearFechaLegible(String fechaStr, String tipo) {
        if (fechaStr == null || fechaStr.isEmpty())
            throw new RuntimeException("Fecha vacía");

        String normalizado = fechaStr.trim().toUpperCase();

        // Formato "ENERO 2024" 
        String[] partes = normalizado.split("\\s+");
        if (partes.length == 2) {
            Integer mes = MESES_ES.get(partes[0]);
            if (mes != null) {
                int anio = Integer.parseInt(partes[1]);
                return LocalDate.of(anio, mes, 1);
            }
        }

        // Formato "2024" (que sería anual)
        if (normalizado.matches("\\d{4}")) {
            return LocalDate.of(Integer.parseInt(normalizado), 1, 1);
        }

        // Fallback a parseo estándar
        return parsearFecha(fechaStr);
    }

    private LocalDate parsearFechaPorTipo(String fechaStr, String tipo) {
        if (tipo.equals("ANUAL") && fechaStr.matches("\\d{4}")) {
            return LocalDate.parse(fechaStr + "-01-01");
        }
        return parsearFecha(fechaStr);
    }

    private LocalDate parsearFecha(String fechaStr) {
        if (fechaStr == null || fechaStr.isEmpty())
            throw new RuntimeException("Fecha vacía");
        if (fechaStr.matches("\\d{4}-\\d{2}"))
            return LocalDate.parse(fechaStr + "-01");
        if (fechaStr.matches("\\d{4}-\\d{2}-\\d{2}"))
            return LocalDate.parse(fechaStr);
        throw new RuntimeException("Formato de fecha inválido: " + fechaStr);
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                ? cell.getLocalDateTimeCellValue().toLocalDate().toString()
                : String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default      -> "";
        };
    }

    @Override
    public List<RegistroFinanciero> getRegistrosRealesByHato(UUID idHato, String email) {
        hatoService.findHatoById(idHato, email);
        return registroFinancieroRepository.findByHato_IdHatoAndEsHistoricoFalse(idHato);
    }

    @Override
    public List<RegistroFinanciero> getRegistrosHistoricosByHato(UUID idHato, String email) {
        hatoService.findHatoById(idHato, email);
        return registroFinancieroRepository.findByHato_IdHatoAndEsHistoricoTrue(idHato);
    }

    // TODO: PRUEBA — eliminar antes de producción
    @Override
    @Transactional
    public void limpiarRegistrosPorHato(UUID idHato, String email) {
        Hato hato = hatoService.findHatoById(idHato, email);
        if (hato == null) throw new RuntimeException("Hato no encontrado");
        registroFinancieroRepository.deleteVentaLecheByHatoId(hato.getIdHato());
        registroFinancieroRepository.deleteByHatoId(hato.getIdHato());
    }

    @Override
    public List<RegistroFinanciero> getRegistrosFinancierosByIdHato(UUID id_hato) {
        return registroFinancieroRepository.findByHato_IdHato(id_hato);
    }

    @Override
    public List<RegistroFinanciero> getRegistrosPorPeriodo(
            UUID idHato, String email,
            String mesDesde, String mesHasta) {
        hatoService.findHatoById(idHato, email);
        return registroFinancieroRepository
            .findByHatoAndRangoMeses(
                idHato, mesDesde, mesHasta);
    }
 
    @Override
    public void eliminarRegistroFinanciero(UUID id_registro) {
        registroFinancieroRepository.deleteById(id_registro);
    }
}