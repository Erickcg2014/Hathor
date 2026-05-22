import { Component, OnInit, Input, Output, EventEmitter, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import * as XLSX from 'xlsx';
import { firstValueFrom } from 'rxjs';
import { Hato } from '../../../../../../../core/services/hato/hato.service';
import {
  CategoriaFinancieraService,
  CategoriaAgrupada,
  CategoriasAgrupadas,
} from '../../../../../../../core/services/finanzas/categoria-financiera.service';
import { RegistroFinancieroService } from '../../../../../../../core/services/finanzas/registro-financiero.service';
import { SpinnerComponent } from '../../../../../../../shared/spinner/spinner.component';

interface FilaResultado {
  fila: number;
  estado: 'OK' | 'ERROR';
  titulo?: string;
  mensaje?: string;
}
interface CategoriaPersonalizadaExcel {
  tempId: string;
  nombre: string;
  tipo: 'INGRESO' | 'GASTO';
}

const MESES_ES = [
  'ENERO',
  'FEBRERO',
  'MARZO',
  'ABRIL',
  'MAYO',
  'JUNIO',
  'JULIO',
  'AGOSTO',
  'SEPTIEMBRE',
  'OCTUBRE',
  'NOVIEMBRE',
  'DICIEMBRE',
];

@Component({
  selector: 'app-finanzas-excel',
  standalone: true,
  imports: [CommonModule, FormsModule, SpinnerComponent],
  templateUrl: './finanzas-excel.component.html',
  styleUrl: './finanzas-excel.component.css',
})
export class FinanzasExcelComponent implements OnInit {
  @Input() hatoActual: Hato | null = null;
  @Input() mostrarAnual = true;
  @Output() cargaExitosa = new EventEmitter<void>();

  loadingCategorias = false;
  categoriasIngresos: CategoriaAgrupada[] = [];
  categoriasGastos: CategoriaAgrupada[] = [];

  seccionIngresosAbierta = true;
  seccionGastosAbierta = true;

  tipoPlantilla: 'DETALLADO' | 'MENSUAL' | 'ANUAL' | null = null;
  fechaInicioDetallado = '';
  mesInicio = '';
  anioInicio: number | null = null;
  anioActual = new Date().getFullYear();

  archivoExcel: File | null = null;
  nombreArchivo = '';
  isDragOver = false;
  loadingCarga = false;
  resultadosCarga: FilaResultado[] = [];
  cargaCompletada = false;
  error = '';
  errorIngreso = '';
  errorGasto = '';

  pasoActual: 1 | 2 = 1;
  plantillaDescargada = false;

  otrosIngresos: CategoriaPersonalizadaExcel[] = [];
  otrosGastos: CategoriaPersonalizadaExcel[] = [];
  nuevaCatIngreso = '';
  nuevaCatGasto = '';

  constructor(
    private categoriaService: CategoriaFinancieraService,
    private registroFinancieroService: RegistroFinancieroService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.cargarCategorias();
  }

  // ===== CATEGORÍAS =====

  private async cargarCategorias(): Promise<void> {
    this.loadingCategorias = true;
    try {
      const agrupadas: CategoriasAgrupadas = await firstValueFrom(
        this.categoriaService.getCategoriasAgrupadas(),
      );
      this.categoriasIngresos = agrupadas.ingresos
        .filter((c) => c.nombre !== 'OTROS INGRESOS')
        .map((c) => ({ ...c, seleccionada: false }));
      this.categoriasGastos = agrupadas.gastos
        .filter((c) => c.nombre !== 'OTROS GASTOS')
        .map((c) => ({ ...c, seleccionada: false }));
    } catch {
      this.error = 'Error al cargar las categorías. Intenta de nuevo.';
    } finally {
      this.loadingCategorias = false;
      this.cdr.markForCheck();
    }
  }

  // ==== INCLUIR UN GASTO O UNA CATEGORÍA ====
  private existeCategoriaSimilar(nombre: string, tipo: 'INGRESO' | 'GASTO'): string | null {
    const IGNORAR = ['de', 'del', 'la', 'el', 'los', 'las', 'y', 'o', 'en', 'a', 'por'];

    const palabrasNueva = nombre
      .toUpperCase()
      .split(/\s+/)
      .filter((p) => p.length > 2 && !IGNORAR.includes(p.toLowerCase()));

    const categoriasPredefinidas =
      tipo === 'INGRESO' ? this.categoriasIngresos : this.categoriasGastos;

    for (const cat of categoriasPredefinidas) {
      const palabrasCat = cat.nombre
        .toUpperCase()
        .split(/\s+/)
        .filter((p) => p.length > 2 && !IGNORAR.includes(p.toLowerCase()));

      const coincide = palabrasNueva.some((p) => palabrasCat.includes(p));
      if (coincide) return cat.nombre;
    }

    const yaCreadas = tipo === 'INGRESO' ? this.otrosIngresos : this.otrosGastos;
    for (const otro of yaCreadas) {
      const palabrasOtro = otro.nombre
        .toUpperCase()
        .split(/\s+/)
        .filter((p) => p.length > 2 && !IGNORAR.includes(p.toLowerCase()));

      const coincide = palabrasNueva.some((p) => palabrasOtro.includes(p));
      if (coincide) return otro.nombre;
    }

    return null;
  }

  agregarOtroIngreso(): void {
    const nombre = this.nuevaCatIngreso.trim().toUpperCase();
    if (!nombre) return;
    const existe = this.otrosIngresos.some((o) => o.nombre === nombre);
    if (existe) return;
    this.otrosIngresos.push({ tempId: crypto.randomUUID(), nombre, tipo: 'INGRESO' });
    this.nuevaCatIngreso = '';
    this.cdr.markForCheck();
  }

  agregarOtroGasto(): void {
    const nombre = this.nuevaCatGasto.trim().toUpperCase();
    if (!nombre) return;
    const existe = this.otrosGastos.some((o) => o.nombre === nombre);
    if (existe) return;
    this.otrosGastos.push({ tempId: crypto.randomUUID(), nombre, tipo: 'GASTO' });
    this.nuevaCatGasto = '';
    this.cdr.markForCheck();
  }

  eliminarOtroIngreso(tempId: string): void {
    this.otrosIngresos = this.otrosIngresos.filter((o) => o.tempId !== tempId);
    this.cdr.markForCheck();
  }

  eliminarOtroGasto(tempId: string): void {
    this.otrosGastos = this.otrosGastos.filter((o) => o.tempId !== tempId);
    this.cdr.markForCheck();
  }

  toggleCategoria(categoria: CategoriaAgrupada): void {
    categoria.seleccionada = !categoria.seleccionada;
    this.cdr.markForCheck();
  }

  toggleSeccionIngresos(): void {
    this.seccionIngresosAbierta = !this.seccionIngresosAbierta;
    this.cdr.markForCheck();
  }

  toggleSeccionGastos(): void {
    this.seccionGastosAbierta = !this.seccionGastosAbierta;
    this.cdr.markForCheck();
  }

  seleccionarTodasIngresos(): void {
    const todasSeleccionadas = this.categoriasIngresos.every((c) => c.seleccionada);
    this.categoriasIngresos.forEach((c) => (c.seleccionada = !todasSeleccionadas));
    this.cdr.markForCheck();
  }

  seleccionarTodasGastos(): void {
    const todasSeleccionadas = this.categoriasGastos.every((c) => c.seleccionada);
    this.categoriasGastos.forEach((c) => (c.seleccionada = !todasSeleccionadas));
    this.cdr.markForCheck();
  }

  // ===== GETTERS =====

  get ingresosSeleccionados(): CategoriaAgrupada[] {
    return this.categoriasIngresos.filter((c) => c.seleccionada);
  }

  get gastosSeleccionados(): CategoriaAgrupada[] {
    return this.categoriasGastos.filter((c) => c.seleccionada);
  }

  get haySeleccion(): boolean {
    const totalIngresos = this.ingresosSeleccionados.length + this.otrosIngresos.length;
    const totalGastos = this.gastosSeleccionados.length + this.otrosGastos.length;
    return totalIngresos > 0 && totalGastos > 0;
  }
  get totalIngresosSeleccionados(): number {
    return this.ingresosSeleccionados.length + this.otrosIngresos.length;
  }

  get totalGastosSeleccionados(): number {
    return this.gastosSeleccionados.length + this.otrosGastos.length;
  }
  get mensajeSeleccion(): string {
    const i = this.totalIngresosSeleccionados;
    const g = this.totalGastosSeleccionados;
    if (i === 0 && g === 0) return 'Selecciona al menos un ingreso y un gasto.';
    if (i === 0) return 'Debes seleccionar al menos un ingreso.';
    if (g === 0) return 'Debes seleccionar al menos un gasto.';
    return '';
  }
  get puedeDescargarPlantilla(): boolean {
    if (!this.tipoPlantilla) return false;
    if (this.tipoPlantilla === 'DETALLADO') return !!this.fechaInicioDetallado;
    if (this.tipoPlantilla === 'MENSUAL') return !!this.mesInicio;
    if (this.tipoPlantilla === 'ANUAL') return !!this.anioInicio;
    return false;
  }

  get registrosExitosos(): number {
    return this.resultadosCarga.filter((r) => r.estado === 'OK').length;
  }

  get registrosConError(): number {
    return this.resultadosCarga.filter((r) => r.estado === 'ERROR').length;
  }

  get excelValido(): boolean {
    return this.cargaCompletada && this.registrosExitosos > 0;
  }

  //* ======== PASO 2 :============
  avanzarAPaso2(): void {
    if (!this.haySeleccion) return;
    this.pasoActual = 2;
    this.tipoPlantilla = null;
    this.fechaInicioDetallado = '';
    this.mesInicio = '';
    this.anioInicio = null;
    this.archivoExcel = null;
    this.nombreArchivo = '';
    this.resultadosCarga = [];
    this.cargaCompletada = false;
    this.cdr.markForCheck();
  }

  volverAPaso1(): void {
    this.pasoActual = 1;
    this.cdr.markForCheck();
  }

  marcarPlantillaDescargada(): void {
    this.plantillaDescargada = true;
    this.generarYDescargarPlantilla();
    this.cdr.markForCheck();
  }

  // ===== SELECTOR DE TIPO DE PLANTILLA =====

  seleccionarTipoPlantilla(tipo: 'DETALLADO' | 'MENSUAL' | 'ANUAL'): void {
    this.tipoPlantilla = tipo;
    this.fechaInicioDetallado = '';
    this.mesInicio = '';
    this.anioInicio = null;
    this.cdr.markForCheck();
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDragOver = true;
    this.cdr.markForCheck();
  }

  onDragLeave(): void {
    this.isDragOver = false;
    this.cdr.markForCheck();
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragOver = false;
    const file = event.dataTransfer?.files[0];
    if (file) this.procesarArchivoSeleccionado(file);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (file) this.procesarArchivoSeleccionado(file);
  }

  private procesarArchivoSeleccionado(file: File): void {
    const extensionValida = file.name.endsWith('.xlsx') || file.name.endsWith('.xls');
    if (!extensionValida) {
      this.error = 'Solo se aceptan archivos Excel (.xlsx o .xls)';
      this.cdr.markForCheck();
      return;
    }
    this.archivoExcel = file;
    this.nombreArchivo = file.name;
    this.resultadosCarga = [];
    this.cargaCompletada = false;
    this.error = '';
    this.cdr.markForCheck();
  }

  // ===== PROCESAR EXCEL =====

  async procesarExcel(): Promise<void> {
    if (!this.archivoExcel || !this.hatoActual?.idHato) return;

    this.loadingCarga = true;
    this.error = '';

    try {
      const resultado = await firstValueFrom(
        this.registroFinancieroService.cargaMasiva(this.hatoActual.idHato, this.archivoExcel),
      );
      this.resultadosCarga = resultado.detalle;
      this.cargaCompletada = true;

      if (this.excelValido) {
        this.cargaExitosa.emit();
      }

      this.cdr.markForCheck();
    } catch {
      this.error = 'Error al procesar el archivo. Verifica el formato e intenta de nuevo.';
      this.cdr.markForCheck();
    } finally {
      this.loadingCarga = false;
      this.cdr.markForCheck();
    }
  }

  private generarPlantillaDetallada(): void {
    if (!this.fechaInicioDetallado) return;

    const wb = XLSX.utils.book_new();

    const [anioIni, mesIni, diaIni] = this.fechaInicioDetallado.split('-').map(Number);
    const fechaInicio = new Date(anioIni, mesIni - 1, diaIni);
    const excelEpoch = new Date(1899, 11, 30);
    const fechaSerialInicio = Math.floor(
      (fechaInicio.getTime() - excelEpoch.getTime()) / (1000 * 60 * 60 * 24),
    );

    const headers = ['FECHA', 'TIPO', 'CATEGORIA', 'TITULO', 'DESCRIPCION', 'MONTO'];
    const primeraFila = [fechaSerialInicio, '', '', '', '', ''];
    const filasVacias = Array.from({ length: 20 }, () => ['', '', '', '', '', '']);

    const wsData = XLSX.utils.aoa_to_sheet([headers, primeraFila, ...filasVacias]);

    wsData['!cols'] = [
      { wch: 14 },
      { wch: 12 },
      { wch: 28 },
      { wch: 24 },
      { wch: 30 },
      { wch: 16 },
    ];

    const cellRef = XLSX.utils.encode_cell({ r: 1, c: 0 });
    if (wsData[cellRef]) {
      wsData[cellRef].t = 'n';
      wsData[cellRef].z = 'yyyy-mm-dd';
    }

    // Instrucciones
    const instrucciones: any[][] = [
      ['PLANTILLA HATHOR — REGISTRO DETALLADO'],
      [''],
      ['INSTRUCCIONES:'],
      ['— Columna FECHA: ingresa en formato YYYY-MM-DD (ej: 2024-01-15)'],
      ['— TIPO debe ser: INGRESO, GASTO, COSTO o INVERSION (en mayúsculas)'],
      ['— CATEGORIA debe coincidir con las categorías de Hathor'],
      ['— MONTO debe ser un valor numérico sin puntos ni comas'],
      ['— Puedes agregar más filas siguiendo el mismo formato'],
      [''],
      ['CATEGORÍAS VÁLIDAS DE INGRESO:'],
      [
        'VENTA DE LECHE',
        'VENTA DE ANIMALES',
        'VENTA DE DERIVADOS LÁCTEOS',
        'VENTA DE GENÉTICA',
        'BONIFICACIONES Y PREMIOS',
        'INCENTIVOS GUBERNAMENTALES',
        'ALQUILER DE MAQUINARIA Y EQUIPOS',
        'VENTA DE SUBPRODUCTOS',
        'OTROS INGRESOS',
      ],
      [''],
      ['CATEGORÍAS VÁLIDAS DE GASTO:'],
      [
        'ALIMENTACIÓN',
        'SANIDAD ANIMAL',
        'MEDICAMENTOS',
        'VACUNAS',
        'SERVICIOS VETERINARIOS',
        'REPRODUCCIÓN',
        'MANO DE OBRA',
        'SALARIOS',
        'SERVICIOS PÚBLICOS',
        'TRANSPORTE',
        'ARRIENDOS',
        'MANTENIMIENTO',
      ],
    ];

    const wsInstrucciones = XLSX.utils.aoa_to_sheet(instrucciones);
    wsInstrucciones['!cols'] = [{ wch: 35 }, { wch: 35 }, { wch: 35 }];

    XLSX.utils.book_append_sheet(wb, wsData, 'Registros');
    XLSX.utils.book_append_sheet(wb, wsInstrucciones, 'Instrucciones');
    XLSX.writeFile(wb, 'hathor_registro_detallado.xlsx');
  }

  // ===== GENERACIÓN DE PLANTILLAS =====

  generarYDescargarPlantilla(): void {
    switch (this.tipoPlantilla) {
      case 'DETALLADO':
        this.generarPlantillaDetallada();
        break;
      case 'MENSUAL':
        this.generarPlantillaMensual();
        break;
      case 'ANUAL':
        this.generarPlantillaAnual();
        break;
    }
  }

  // ===== UTILIDADES DE FECHA =====

  private generarFilasFecha(tipo: 'MENSUAL' | 'ANUAL'): string[] {
    const filas: string[] = [];

    if (tipo === 'MENSUAL') {
      const [anioIni, mesIni] = this.mesInicio.split('-').map(Number);
      const ahora = new Date();
      let anio = anioIni;
      let mes = mesIni;

      while (
        anio < ahora.getFullYear() ||
        (anio === ahora.getFullYear() && mes <= ahora.getMonth() + 1)
      ) {
        filas.push(`${MESES_ES[mes - 1]} ${anio}`);
        mes++;
        if (mes > 12) {
          mes = 1;
          anio++;
        }
      }
    }

    if (tipo === 'ANUAL') {
      for (let a = this.anioInicio!; a <= this.anioActual; a++) {
        filas.push(String(a));
      }
    }

    return filas;
  }

  // Convierte "ENERO 2024" → "2024-01", "2024" → "2024-01-01"
  static parsearFechaLegible(texto: string): string {
    const partesMes = texto.trim().split(' ');
    if (partesMes.length === 2) {
      const mesIdx = MESES_ES.indexOf(partesMes[0].toUpperCase());
      if (mesIdx !== -1) {
        const mesStr = (mesIdx + 1).toString().padStart(2, '0');
        return `${partesMes[1]}-${mesStr}`;
      }
    }
    if (/^\d{4}$/.test(texto.trim())) {
      return `${texto.trim()}-01-01`;
    }
    return texto;
  }

  private generarHojaInstrucciones(): any[][] {
    const instrucciones: any[][] = [
      [`PLANTILLA HATHOR — REGISTRO ${this.tipoPlantilla}`],
      [''],
      ['HOJA "Ingresos": registra los montos de cada categoría de ingreso por período.'],
      ['HOJA "Egresos": registra los montos de cada categoría de gasto por período.'],
      [''],
      ['REGLAS IMPORTANTES:'],
      ['— Solo ingresa valores numéricos. No uses texto, puntos ni comas en los montos.'],
      ['— Deja la celda vacía si no hubo movimiento en ese período.'],
      ['— No modifiques los encabezados ni el orden de las columnas.'],
      ['— La columna de fecha ya está generada, no la modifiques.'],
      [''],
      ['CATEGORÍAS DE INGRESOS INCLUIDAS:'],
      ...this.ingresosSeleccionados.map((c) => [c.nombre, c.descripcion ?? '']),
      [''],
      ['CATEGORÍAS DE EGRESOS INCLUIDAS:'],
      ...this.gastosSeleccionados.map((c) => [c.nombre, c.descripcion ?? '']),
    ];
    return instrucciones;
  }

  private generarPlantillaMensual(): void {
    if (!this.mesInicio || !this.haySeleccion) return;

    const wb = XLSX.utils.book_new();
    const filasFecha = this.generarFilasFecha('MENSUAL');

    // Hoja Ingresos
    const colsIngresos = [
      ...this.ingresosSeleccionados.map((c) => c.nombre),
      ...this.otrosIngresos.map((o) => o.nombre),
    ];
    const headerIngresos = ['PERÍODO', ...colsIngresos];
    const filasIngresos = [
      ['TIPO_PLANTILLA', 'MENSUAL'],
      [],
      headerIngresos,
      ...filasFecha.map((f) => [f, ...Array(colsIngresos.length).fill(null)]),
    ];
    const wsIngresos = XLSX.utils.aoa_to_sheet(filasIngresos);
    wsIngresos['!cols'] = [{ wch: 18 }, ...colsIngresos.map(() => ({ wch: 20 }))];

    // Hoja Egresos
    const colsGastos = [
      ...this.gastosSeleccionados.map((c) => c.nombre),
      ...this.otrosGastos.map((o) => o.nombre),
    ];
    const headerGastos = ['PERÍODO', ...colsGastos];
    const filasGastos = [
      ['TIPO_PLANTILLA', 'MENSUAL'],
      [],
      headerGastos,
      ...filasFecha.map((f) => [f, ...Array(colsGastos.length).fill(null)]),
    ];
    const wsGastos = XLSX.utils.aoa_to_sheet(filasGastos);
    wsGastos['!cols'] = [{ wch: 18 }, ...colsGastos.map(() => ({ wch: 20 }))];

    // Hoja Instrucciones
    const wsInstrucciones = XLSX.utils.aoa_to_sheet(this.generarHojaInstrucciones());
    wsInstrucciones['!cols'] = [{ wch: 45 }, { wch: 50 }];

    XLSX.utils.book_append_sheet(wb, wsIngresos, 'Ingresos');
    XLSX.utils.book_append_sheet(wb, wsGastos, 'Egresos');
    XLSX.utils.book_append_sheet(wb, wsInstrucciones, 'Instrucciones');
    XLSX.writeFile(wb, 'hathor_registro_mensual.xlsx');
  }

  private generarPlantillaAnual(): void {
    if (!this.anioInicio || !this.haySeleccion) return;

    const wb = XLSX.utils.book_new();
    const filasFecha = this.generarFilasFecha('ANUAL');

    // Hoja Ingresos
    const colsIngresos = [
      ...this.ingresosSeleccionados.map((c) => c.nombre),
      ...this.otrosIngresos.map((o) => o.nombre),
    ];
    const filasIngresos = [
      ['TIPO_PLANTILLA', 'ANUAL'],
      [],
      ['AÑO', ...colsIngresos],
      ...filasFecha.map((f) => [f, ...Array(colsIngresos.length).fill(null)]),
    ];
    const wsIngresos = XLSX.utils.aoa_to_sheet(filasIngresos);
    wsIngresos['!cols'] = [{ wch: 10 }, ...colsIngresos.map(() => ({ wch: 20 }))];

    // Hoja Egresos
    const colsGastos = [
      ...this.gastosSeleccionados.map((c) => c.nombre),
      ...this.otrosGastos.map((o) => o.nombre),
    ];
    const filasGastos = [
      ['TIPO_PLANTILLA', 'ANUAL'],
      [],
      ['AÑO', ...colsGastos],
      ...filasFecha.map((f) => [f, ...Array(colsGastos.length).fill(null)]),
    ];
    const wsGastos = XLSX.utils.aoa_to_sheet(filasGastos);
    wsGastos['!cols'] = [{ wch: 10 }, ...colsGastos.map(() => ({ wch: 20 }))];

    // Hoja Instrucciones
    const wsInstrucciones = XLSX.utils.aoa_to_sheet(this.generarHojaInstrucciones());
    wsInstrucciones['!cols'] = [{ wch: 45 }, { wch: 50 }];

    XLSX.utils.book_append_sheet(wb, wsIngresos, 'Ingresos');
    XLSX.utils.book_append_sheet(wb, wsGastos, 'Egresos');
    XLSX.utils.book_append_sheet(wb, wsInstrucciones, 'Instrucciones');
    XLSX.writeFile(wb, 'hathor_registro_anual.xlsx');
  }

  todosGastosSeleccionados(): boolean {
    return this.categoriasGastos?.every((c) => c.seleccionada);
  }

  todosIngresosSeleccionados(): boolean {
    return this.categoriasIngresos?.every((c) => c.seleccionada);
  }
}
