import { Component, OnInit, Input, Output, EventEmitter, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import {
  CategoriaFinancieraService,
  CategoriaAgrupada,
  CategoriasAgrupadas,
  CrearCategoriaPersonalizadaDTO,
} from '../../../../../../../core/services/finanzas/categoria-financiera.service';
import { DatosManual, ModoManual, RegistroManualItem } from '../seccion-financiero.component';
import { SpinnerComponent } from '../../../../../../../shared/spinner/spinner.component';
import { Hato } from '../../../../../../../core/services/hato/hato.service';

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

export interface CategoriaPersonalizada {
  tempId: string;
  nombre: string;
  tipo: 'INGRESO' | 'GASTO';
  confirmada: boolean;
  idReal?: string;
  creando: boolean;
  error: string;
}

@Component({
  selector: 'app-finanzas-manual',
  standalone: true,
  imports: [CommonModule, FormsModule, SpinnerComponent],
  templateUrl: './finanzas-manual.component.html',
  styleUrl: './finanzas-manual.component.css',
})
export class FinanzasManualComponent implements OnInit {
  @Input() hatoActual: Hato | null = null;
  @Output() datosListos = new EventEmitter<DatosManual>();

  loadingCategorias = false;
  categoriasIngresos: CategoriaAgrupada[] = [];
  categoriasGastos: CategoriaAgrupada[] = [];
  seccionIngresosAbierta = true;
  seccionGastosAbierta = true;

  private readonly NOMBRE_OTROS_INGRESOS = 'OTROS INGRESOS';
  private readonly NOMBRE_OTROS_GASTOS = 'OTROS GASTOS';

  nombresPersonalizados: Map<string, string> = new Map();
  creandoCategoria: Map<string, boolean> = new Map();
  errorCategoria: Map<string, string> = new Map();

  pasoActual: 1 | 2 = 1;
  modoSeleccionado: ModoManual | null = null;

  mesInicio = '';
  anioInicio: number | null = null;
  anioActual = new Date().getFullYear();

  mostrarOverlayLimite = false;

  datosIngreso: Map<string, Map<string, string>> = new Map();
  datosGasto: Map<string, Map<string, string>> = new Map();
  periodosGenerados: { clave: string; etiqueta: string }[] = [];

  otrosIngresos: CategoriaPersonalizada[] = [];
  otrosGastos: CategoriaPersonalizada[] = [];
  error = '';

  constructor(
    private categoriaService: CategoriaFinancieraService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.cargarCategorias();
  }

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

  agregarOtroIngreso(): void {
    this.otrosIngresos.push({
      tempId: crypto.randomUUID(),
      nombre: '',
      tipo: 'INGRESO',
      confirmada: false,
      creando: false,
      error: '',
    });
    this.cdr.markForCheck();
  }

  agregarOtroGasto(): void {
    this.otrosGastos.push({
      tempId: crypto.randomUUID(),
      nombre: '',
      tipo: 'GASTO',
      confirmada: false,
      creando: false,
      error: '',
    });
    this.cdr.markForCheck();
  }
  eliminarOtro(lista: CategoriaPersonalizada[], tempId: string): void {
    const idx = lista.findIndex((o) => o.tempId === tempId);
    if (idx !== -1) lista.splice(idx, 1);
    this.cdr.markForCheck();
  }

  setNombreOtro(item: CategoriaPersonalizada, valor: string): void {
    item.nombre = valor;
    item.error = '';
    this.cdr.markForCheck();
  }

  async confirmarOtro(item: CategoriaPersonalizada): Promise<void> {
    const nombre = item.nombre.trim();
    if (!nombre) {
      item.error = 'Escribe un nombre para la categoría.';
      this.cdr.markForCheck();
      return;
    }

    item.creando = true;
    item.error = '';
    this.cdr.markForCheck();

    try {
      const dto: CrearCategoriaPersonalizadaDTO = { nombre, tipo: item.tipo };
      const nueva = await firstValueFrom(this.categoriaService.crearCategoriaPersonalizada(dto));
      item.idReal = nueva.idCategoria;
      item.nombre = nueva.nombre;
      item.confirmada = true;
    } catch {
      item.error = 'Error al crear la categoría. Intenta de nuevo.';
    } finally {
      item.creando = false;
      this.cdr.markForCheck();
    }
  }

  editarOtro(item: CategoriaPersonalizada): void {
    item.confirmada = false;
    item.idReal = undefined;
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

  todosIngresosSeleccionados(): boolean {
    return this.categoriasIngresos?.every((c) => c.seleccionada);
  }

  todosGastosSeleccionados(): boolean {
    return this.categoriasGastos?.every((c) => c.seleccionada);
  }

  seleccionarTodasIngresos(): void {
    const todas = this.categoriasIngresos.every((c) => c.seleccionada);
    this.categoriasIngresos.forEach((c) => (c.seleccionada = !todas));
    this.cdr.markForCheck();
  }

  seleccionarTodasGastos(): void {
    const todas = this.categoriasGastos.every((c) => c.seleccionada);
    this.categoriasGastos.forEach((c) => (c.seleccionada = !todas));
    this.cdr.markForCheck();
  }

  esOtros(cat: CategoriaAgrupada): boolean {
    return cat.nombre === this.NOMBRE_OTROS_INGRESOS || cat.nombre === this.NOMBRE_OTROS_GASTOS;
  }

  getNombrePersonalizado(idCategoria: string): string {
    return this.nombresPersonalizados.get(idCategoria) ?? '';
  }

  setNombrePersonalizado(idCategoria: string, valor: string): void {
    this.nombresPersonalizados.set(idCategoria, valor);
    this.errorCategoria.delete(idCategoria);
    this.cdr.markForCheck();
  }

  async confirmarCategoriaPersonalizada(cat: CategoriaAgrupada): Promise<void> {
    const nombre = this.nombresPersonalizados.get(cat.idCategoria)?.trim();
    if (!nombre) {
      this.errorCategoria.set(cat.idCategoria, 'Escribe un nombre para la categoría.');
      this.cdr.markForCheck();
      return;
    }

    this.creandoCategoria.set(cat.idCategoria, true);
    this.cdr.markForCheck();

    try {
      const dto: CrearCategoriaPersonalizadaDTO = { nombre, tipo: cat.tipo };
      const nueva = await firstValueFrom(this.categoriaService.crearCategoriaPersonalizada(dto));

      if (cat.tipo === 'INGRESO') {
        const idx = this.categoriasIngresos.findIndex((c) => c.idCategoria === cat.idCategoria);
        if (idx !== -1) {
          this.categoriasIngresos.splice(idx, 0, { ...nueva, seleccionada: true });
          this.categoriasIngresos.find((c) => c.idCategoria === cat.idCategoria)!.seleccionada =
            false;
        }
      } else {
        const idx = this.categoriasGastos.findIndex((c) => c.idCategoria === cat.idCategoria);
        if (idx !== -1) {
          this.categoriasGastos.splice(idx, 0, { ...nueva, seleccionada: true });
          this.categoriasGastos.find((c) => c.idCategoria === cat.idCategoria)!.seleccionada =
            false;
        }
      }

      this.nombresPersonalizados.delete(cat.idCategoria);
      this.errorCategoria.delete(cat.idCategoria);
    } catch {
      this.errorCategoria.set(cat.idCategoria, 'Error al crear la categoría. Intenta de nuevo.');
    } finally {
      this.creandoCategoria.set(cat.idCategoria, false);
      this.cdr.markForCheck();
    }
  }

  get ingresosSeleccionados(): CategoriaAgrupada[] {
    return this.categoriasIngresos.filter((c) => c.seleccionada);
  }
  get otrosIngresosConfirmados(): CategoriaPersonalizada[] {
    return this.otrosIngresos.filter((o) => o.confirmada && o.idReal);
  }

  get otrosGastosConfirmados(): CategoriaPersonalizada[] {
    return this.otrosGastos.filter((o) => o.confirmada && o.idReal);
  }

  get gastosSeleccionados(): CategoriaAgrupada[] {
    return this.categoriasGastos.filter((c) => c.seleccionada);
  }

  get haySeleccion(): boolean {
    const totalIngresos = this.ingresosSeleccionados.length + this.otrosIngresosConfirmados.length;
    const totalGastos = this.gastosSeleccionados.length + this.otrosGastosConfirmados.length;
    return totalIngresos > 0 && totalGastos > 0;
  }

  get totalIngresosSeleccionados(): number {
    return this.ingresosSeleccionados.length + this.otrosIngresosConfirmados.length;
  }

  get totalGastosSeleccionados(): number {
    return this.gastosSeleccionados.length + this.otrosGastosConfirmados.length;
  }

  get mensajeSeleccion(): string {
    const i = this.totalIngresosSeleccionados;
    const g = this.totalGastosSeleccionados;
    if (i === 0 && g === 0) return 'Selecciona al menos un ingreso y un gasto.';
    if (i === 0) return 'Debes seleccionar al menos un ingreso.';
    if (g === 0) return 'Debes seleccionar al menos un gasto.';
    return '';
  }

  get periodosListos(): boolean {
    return this.periodosGenerados.length > 0;
  }

  get hayDatos(): boolean {
    return this.totalIngresos > 0 || this.totalGastos > 0;
  }

  get totalIngresos(): number {
    const normales = this.ingresosSeleccionados.reduce(
      (acc, cat) => acc + this.getTotalCategoria(this.datosIngreso, cat.idCategoria),
      0,
    );
    const otros = this.otrosIngresosConfirmados.reduce(
      (acc, o) => acc + this.getTotalCategoria(this.datosIngreso, o.idReal!),
      0,
    );
    return normales + otros;
  }

  get totalGastos(): number {
    const normales = this.gastosSeleccionados.reduce(
      (acc, cat) => acc + this.getTotalCategoria(this.datosGasto, cat.idCategoria),
      0,
    );
    const otros = this.otrosGastosConfirmados.reduce(
      (acc, o) => acc + this.getTotalCategoria(this.datosGasto, o.idReal!),
      0,
    );
    return normales + otros;
  }

  get balance(): number {
    return this.totalIngresos - this.totalGastos;
  }

  avanzarAPaso2(): void {
    if (!this.haySeleccion) return;
    this.pasoActual = 2;
    this.cdr.markForCheck();
  }

  volverAPaso1(): void {
    this.pasoActual = 1;
    this.modoSeleccionado = null;
    this.periodosGenerados = [];
    this.datosIngreso = new Map();
    this.datosGasto = new Map();
    this.cdr.markForCheck();
  }

  seleccionarModo(modo: ModoManual): void {
    this.modoSeleccionado = modo;
    this.mesInicio = '';
    this.anioInicio = null;
    this.mostrarOverlayLimite = false;
    this.periodosGenerados = [];
    this.datosIngreso = new Map();
    this.datosGasto = new Map();
    this.error = '';

    if (modo === 'PROMEDIO') {
      this.generarPeriodosPromedio();
    }

    this.cdr.markForCheck();
  }

  private generarPeriodosPromedio(): void {
    const ahora = new Date();
    const clave = `${ahora.getFullYear()}-${(ahora.getMonth() + 1).toString().padStart(2, '0')}`;
    const etiqueta = `${MESES_ES[ahora.getMonth()]} ${ahora.getFullYear()}`;
    this.periodosGenerados = [{ clave, etiqueta }];
    this.inicializarMapasDatos();
  }

  generarPeriodosMensual(): void {
    if (!this.mesInicio) return;

    const [anioIni, mesIni] = this.mesInicio.split('-').map(Number);
    const ahora = new Date();
    const fechaLimite = new Date(ahora.getFullYear(), ahora.getMonth() - 12, 1);
    const fechaInicio = new Date(anioIni, mesIni - 1, 1);

    if (fechaInicio < fechaLimite) {
      this.mostrarOverlayLimite = true;
      this.cdr.markForCheck();
      return;
    }

    this.periodosGenerados = [];
    let anio = anioIni;
    let mes = mesIni;

    while (
      anio < ahora.getFullYear() ||
      (anio === ahora.getFullYear() && mes <= ahora.getMonth() + 1)
    ) {
      const clave = `${anio}-${mes.toString().padStart(2, '0')}`;
      this.periodosGenerados.push({ clave, etiqueta: `${MESES_ES[mes - 1]} ${anio}` });
      mes++;
      if (mes > 12) {
        mes = 1;
        anio++;
      }
    }

    this.inicializarMapasDatos();
    this.cdr.markForCheck();
  }

  generarPeriodosAnual(): void {
    if (!this.anioInicio) return;

    if (this.anioInicio < this.anioActual - 3) {
      this.mostrarOverlayLimite = true;
      this.cdr.markForCheck();
      return;
    }

    this.periodosGenerados = [];
    for (let a = this.anioInicio; a <= this.anioActual; a++) {
      this.periodosGenerados.push({ clave: `${a}-01-01`, etiqueta: String(a) });
    }

    this.inicializarMapasDatos();
    this.cdr.markForCheck();
  }

  private inicializarMapasDatos(): void {
    this.datosIngreso = new Map();
    this.datosGasto = new Map();

    // Categorías predefinidas seleccionadas
    for (const cat of this.ingresosSeleccionados) {
      const mapa = new Map<string, string>();
      for (const p of this.periodosGenerados) mapa.set(p.clave, '');
      this.datosIngreso.set(cat.idCategoria, mapa);
    }

    for (const cat of this.gastosSeleccionados) {
      const mapa = new Map<string, string>();
      for (const p of this.periodosGenerados) mapa.set(p.clave, '');
      this.datosGasto.set(cat.idCategoria, mapa);
    }

    // Categorías personalizadas confirmadas
    for (const otro of this.otrosIngresosConfirmados) {
      const mapa = new Map<string, string>();
      for (const p of this.periodosGenerados) mapa.set(p.clave, '');
      this.datosIngreso.set(otro.idReal!, mapa);
    }

    for (const otro of this.otrosGastosConfirmados) {
      const mapa = new Map<string, string>();
      for (const p of this.periodosGenerados) mapa.set(p.clave, '');
      this.datosGasto.set(otro.idReal!, mapa);
    }
  }

  cerrarOverlay(): void {
    this.mostrarOverlayLimite = false;
    this.mesInicio = '';
    this.anioInicio = null;
    this.cdr.markForCheck();
  }

  getValor(mapa: Map<string, Map<string, string>>, idCategoria: string, clave: string): string {
    return mapa.get(idCategoria)?.get(clave) ?? '';
  }

  setValor(
    mapa: Map<string, Map<string, string>>,
    idCategoria: string,
    clave: string,
    valor: string,
  ): void {
    if (valor !== '' && (isNaN(Number(valor)) || Number(valor) < 0)) return;
    mapa.get(idCategoria)?.set(clave, valor);
    this.emitirDatos();
    this.cdr.markForCheck();
  }

  soloNumeros(event: KeyboardEvent): boolean {
    const char = event.key;
    if (['Backspace', 'Tab', 'Delete', 'ArrowLeft', 'ArrowRight'].includes(char)) return true;
    if (char === '.' && !(event.target as HTMLInputElement).value.includes('.')) return true;
    return /^\d$/.test(char);
  }

  getTotalCategoria(mapa: Map<string, Map<string, string>>, idCategoria: string): number {
    return Array.from(mapa.get(idCategoria)?.values() ?? [])
      .filter((v) => v !== '' && !isNaN(Number(v)))
      .reduce((acc, v) => acc + Number(v), 0);
  }

  get todasCategoriasConDatos(): boolean {
    // Verificar que todas las categorías seleccionadas tengan al menos un valor
    for (const cat of this.ingresosSeleccionados) {
      if (this.getTotalCategoria(this.datosIngreso, cat.idCategoria) === 0) return false;
    }
    for (const otro of this.otrosIngresosConfirmados) {
      if (this.getTotalCategoria(this.datosIngreso, otro.idReal!) === 0) return false;
    }
    for (const cat of this.gastosSeleccionados) {
      if (this.getTotalCategoria(this.datosGasto, cat.idCategoria) === 0) return false;
    }
    for (const otro of this.otrosGastosConfirmados) {
      if (this.getTotalCategoria(this.datosGasto, otro.idReal!) === 0) return false;
    }
    return this.haySeleccion && this.periodosListos;
  }

  get categoriaSinDatos(): string[] {
    const faltantes: string[] = [];
    for (const cat of this.ingresosSeleccionados) {
      if (this.getTotalCategoria(this.datosIngreso, cat.idCategoria) === 0)
        faltantes.push(cat.nombre);
    }
    for (const otro of this.otrosIngresosConfirmados) {
      if (this.getTotalCategoria(this.datosIngreso, otro.idReal!) === 0)
        faltantes.push(otro.nombre);
    }
    for (const cat of this.gastosSeleccionados) {
      if (this.getTotalCategoria(this.datosGasto, cat.idCategoria) === 0)
        faltantes.push(cat.nombre);
    }
    for (const otro of this.otrosGastosConfirmados) {
      if (this.getTotalCategoria(this.datosGasto, otro.idReal!) === 0) faltantes.push(otro.nombre);
    }
    return faltantes;
  }

  private emitirDatos(): void {
    if (!this.modoSeleccionado || !this.todasCategoriasConDatos) {
      this.datosListos.emit({ modo: this.modoSeleccionado ?? 'PROMEDIO', registros: [] });
      return;
    }

    const registros: RegistroManualItem[] = [];

    // Predefinidas ingresos
    for (const cat of this.ingresosSeleccionados) {
      const periodos = Array.from(this.datosIngreso.get(cat.idCategoria)?.entries() ?? [])
        .filter(([, v]) => v !== '' && Number(v) > 0)
        .map(([fecha, v]) => ({ fecha, monto: Number(v) }));
      if (periodos.length > 0) {
        registros.push({
          idCategoria: cat.idCategoria,
          nombre: cat.nombre,
          tipo: 'INGRESO',
          periodos,
        });
      }
    }

    // Personalizadas ingresos
    for (const otro of this.otrosIngresosConfirmados) {
      const periodos = Array.from(this.datosIngreso.get(otro.idReal!)?.entries() ?? [])
        .filter(([, v]) => v !== '' && Number(v) > 0)
        .map(([fecha, v]) => ({ fecha, monto: Number(v) }));
      if (periodos.length > 0) {
        registros.push({
          idCategoria: otro.idReal!,
          nombre: otro.nombre,
          tipo: 'INGRESO',
          periodos,
        });
      }
    }

    // Predefinidas gastos
    for (const cat of this.gastosSeleccionados) {
      const periodos = Array.from(this.datosGasto.get(cat.idCategoria)?.entries() ?? [])
        .filter(([, v]) => v !== '' && Number(v) > 0)
        .map(([fecha, v]) => ({ fecha, monto: Number(v) }));
      if (periodos.length > 0) {
        registros.push({
          idCategoria: cat.idCategoria,
          nombre: cat.nombre,
          tipo: 'GASTO',
          periodos,
        });
      }
    }

    // Personalizadas gastos
    for (const otro of this.otrosGastosConfirmados) {
      const periodos = Array.from(this.datosGasto.get(otro.idReal!)?.entries() ?? [])
        .filter(([, v]) => v !== '' && Number(v) > 0)
        .map(([fecha, v]) => ({ fecha, monto: Number(v) }));
      if (periodos.length > 0) {
        registros.push({ idCategoria: otro.idReal!, nombre: otro.nombre, tipo: 'GASTO', periodos });
      }
    }

    this.datosListos.emit({ modo: this.modoSeleccionado, registros });
  }

  formatearPesos(valor: number): string {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(valor);
  }

  trackById(_index: number, item: CategoriaAgrupada): string {
    return item.idCategoria;
  }
  trackByPeriodo(_index: number, periodo: { clave: string }): string {
    return periodo.clave;
  }
  trackByTempId(_index: number, item: CategoriaPersonalizada): string {
    return item.tempId;
  }
}
