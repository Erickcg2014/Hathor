import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectorRef,
  ChangeDetectionStrategy,
} from '@angular/core';
import { CommonModule, KeyValuePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';

import { HatoStateService } from '../../../../core/services/ui-state/hato-state.service';
import { Hato } from '../../../../core/services/hato/hato.service';
import {
  CategoriaFinancieraService,
  CategoriaFinanciera,
} from '../../../../core/services/finanzas/categoria-financiera.service';
import {
  RegistroFinancieroService,
  RegistroFinancieroDTO,
} from '../../../../core/services/finanzas/registro-financiero.service';
import { SpinnerComponent } from '../../../../shared/spinner/spinner.component';
import {
  VentaLecheService,
  RespuestaVentaLeche,
} from '../../../../core/services/finanzas/venta-leche.service';
import { ProduccionLecheService } from '../../../../core/services/produccion/produccion-leche.service';
import { KpiService } from '../../../../core/services/kpi/kpi.service';
import { FinanzasExcelComponent } from './../../hato/nuevo-hato/secciones/seccion-financiero/finanzas-excel/finanzas-excel.component';

interface RegistroForm extends Partial<RegistroFinancieroDTO> {
  _esVentaLeche?: boolean;
  _categoriaPadreId?: string;
  _usandoPersonalizada?: boolean;
}

interface TipoMovimiento {
  value: 'INGRESO' | 'GASTO' | 'INVERSION';
  label: string;
  icono: string;
}

@Component({
  selector: 'app-registro-financiero',
  standalone: true,
  imports: [CommonModule, FormsModule, SpinnerComponent, KeyValuePipe, FinanzasExcelComponent],
  templateUrl: './registro-financiero.component.html',
  styleUrl: './registro-financiero.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegistroFinancieroComponent implements OnInit, OnDestroy {
  hatoActivo: Hato | null = null;
  categorias: CategoriaFinanciera[] = [];

  registros: RegistroForm[] = [];
  submitted = false;
  guardando = false;
  toastVisible = false;

  // ── Alerta producción ─────────────────────────────────────
  alertaProduccion = false;
  litrosFaltantes = 0;
  mensajeAlerta = '';
  agregandoProduccionSugerida = false;
  ventaLecheAlertaFecha = '';
  ventaLecheAlertaLitros = 0;

  // ── Gestión de categorías ─────────────────────────────────
  mostrarGestionCategorias = false;
  creandoCategoria = false;
  nuevaCategoriaNombre = '';
  nuevaCategoriaTipo: 'INGRESO' | 'GASTO' | 'INVERSION' = 'INGRESO';
  nuevaCategoriaPadreId = '';
  errorCategoria = '';
  exitoCategoria = '';

  readonly tiposMovimiento: TipoMovimiento[] = [
    { value: 'INGRESO', label: 'Ingreso', icono: '↑' },
    { value: 'GASTO', label: 'Gasto', icono: '↓' },
    { value: 'INVERSION', label: 'Inversión', icono: '◈' },
  ];

  modoRegistro: 'MANUAL' | 'EXCEL' = 'MANUAL';

  private destroy$ = new Subject<void>();

  constructor(
    private cdr: ChangeDetectorRef,
    private hatoState: HatoStateService,
    private categoriaService: CategoriaFinancieraService,
    private registroService: RegistroFinancieroService,
    private kpiService: KpiService,
    private ventaLecheService: VentaLecheService,
    private produccionService: ProduccionLecheService,
  ) {}

  ngOnInit(): void {
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.hatoActivo = hato;
      if (hato) {
        this.cargarCategorias();
        this.registros = [this.nuevoRegistroVacio()];
        this.submitted = false;
      }
    });
  }

  // ── Carga de categorías ───────────────────────────────────

  private cargarCategorias(): void {
    this.categoriaService
      .getCategoriasMe()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (cats) => {
          this.categorias = cats;
          this.cdr.markForCheck();
        },
      });
  }

  // ── Getters para selector en cascada ─────────────────────

  getCategoriasPadre(tipo: string): CategoriaFinanciera[] {
    if (!tipo) return [];
    const nivelesRaiz = [
      'GENERAL',
      'INGRESOS',
      'GASTOS DE PRODUCCIÓN',
      'GASTOS OPERATIVOS',
      'INVERSIONES',
    ];
    return this.categorias.filter((c) => {
      if (c.tipo !== tipo) return false;
      const nombrePadre = c.categoriaPadre?.nombre?.toUpperCase() ?? '';
      return nivelesRaiz.includes(nombrePadre);
    });
  }

  getCategoriasPredefinidas(tipo: string): CategoriaFinanciera[] {
    if (!tipo) return [];
    const nivelesRaiz = [
      'GENERAL',
      'INGRESOS',
      'GASTOS DE PRODUCCIÓN',
      'GASTOS OPERATIVOS',
      'INVERSIONES',
    ];
    return this.categorias.filter((c) => {
      if (c.tipo !== tipo || !c.esPredefinida) return false;
      const nombrePadre = c.categoriaPadre?.nombre?.toUpperCase() ?? '';
      return nivelesRaiz.includes(nombrePadre);
    });
  }

  getCategoriasPersonalizadasPorTipo(tipo: string): CategoriaFinanciera[] {
    if (!tipo) return [];
    return this.categorias.filter((c) => c.tipo === tipo && !c.esPredefinida);
  }

  getSubcategorias(tipo: string, idPadre: string): CategoriaFinanciera[] {
    if (!tipo || !idPadre) return [];
    return this.categorias.filter(
      (c) => c.tipo === tipo && c.categoriaPadre?.idCategoriaFinanciera === idPadre,
    );
  }

  tieneSubcategorias(tipo: string, idPadre: string): boolean {
    return this.getSubcategorias(tipo, idPadre).length > 0;
  }

  // ── Acciones del selector en cascada ─────────────────────

  seleccionarPadre(index: number, idPadre: string): void {
    this.registros[index]._categoriaPadreId = idPadre;
    this.registros[index].id_categoria = '';
    this.registros[index]._esVentaLeche = false;

    // Si no tiene subcategorías, seleccionar el padre directamente
    const tipo = this.registros[index].tipo_movimiento ?? '';
    if (!this.tieneSubcategorias(tipo, idPadre)) {
      this.registros[index].id_categoria = idPadre;
      this.onCategoriaChange(index);
    }
    this.cdr.markForCheck();
  }

  seleccionarSubcategoria(index: number, idCategoria: string): void {
    this.registros[index].id_categoria = idCategoria;
    this.onCategoriaChange(index);
    this.cdr.markForCheck();
  }

  // ── Gestión de categorías personalizadas ─────────────────

  toggleGestionCategorias(): void {
    this.mostrarGestionCategorias = !this.mostrarGestionCategorias;
    this.errorCategoria = '';
    this.exitoCategoria = '';
    this.nuevaCategoriaNombre = '';
    this.nuevaCategoriaPadreId = '';
    this.cdr.markForCheck();
  }

  getCategoriasPadreParaGestion(): CategoriaFinanciera[] {
    const nivelesRaiz = [
      'GENERAL',
      'INGRESOS',
      'GASTOS DE PRODUCCIÓN',
      'GASTOS OPERATIVOS',
      'INVERSIONES',
    ];
    return this.categorias.filter((c) => {
      if (c.tipo !== this.nuevaCategoriaTipo) return false;
      const nombrePadre = c.categoriaPadre?.nombre?.toUpperCase() ?? '';
      return nivelesRaiz.includes(nombrePadre);
    });
  }

  get categoriasPersonalizadas(): CategoriaFinanciera[] {
    return this.categorias.filter((c) => !c.esPredefinida);
  }

  crearCategoriaPersonalizada(): void {
    if (!this.nuevaCategoriaNombre.trim()) {
      this.errorCategoria = 'El nombre es requerido.';
      this.cdr.markForCheck();
      return;
    }

    this.creandoCategoria = true;
    this.errorCategoria = '';
    this.exitoCategoria = '';
    this.cdr.markForCheck();

    this.categoriaService
      .crearCategoriaPersonalizada({
        nombre: this.nuevaCategoriaNombre.trim(),
        tipo: this.nuevaCategoriaTipo,
        idCategoriaPadre: this.nuevaCategoriaPadreId || undefined,
      })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.exitoCategoria = 'Categoría creada correctamente.';
          this.nuevaCategoriaNombre = '';
          this.nuevaCategoriaPadreId = '';
          this.creandoCategoria = false;
          this.cargarCategorias();
          this.cdr.markForCheck();
        },
        error: () => {
          this.errorCategoria = 'Error al crear la categoría.';
          this.creandoCategoria = false;
          this.cdr.markForCheck();
        },
      });
  }

  // ── Resto de métodos existentes ───────────────────────────

  seleccionarModo(modo: 'MANUAL' | 'EXCEL'): void {
    this.modoRegistro = modo;
    this.cdr.markForCheck();
  }

  onCargaExcelExitosa(): void {
    if (this.hatoActivo?.idHato) {
      this.kpiService.calcularKpis(this.hatoActivo.idHato).subscribe();
    }
    this.cdr.markForCheck();
  }

  seleccionarTipo(index: number, tipo: string): void {
    this.registros[index].tipo_movimiento = tipo as RegistroFinancieroDTO['tipo_movimiento'];
    this.registros[index].id_categoria = '';
    this.registros[index]._categoriaPadreId = '';
    this.registros[index]._esVentaLeche = false;
    this.registros[index].litros_vendidos_leche = null;
    this.registros[index].precio_litro_leche = null;
    this.cdr.markForCheck();
  }

  onCategoriaChange(index: number): void {
    const registro = this.registros[index];
    const cat = this.categorias.find((c) => c.idCategoriaFinanciera === registro.id_categoria);
    const esVenta = cat?.nombre?.toLowerCase().includes('venta de leche') ?? false;
    registro._esVentaLeche = esVenta;
    if (!esVenta) {
      registro.litros_vendidos_leche = null;
      registro.precio_litro_leche = null;
    } else {
      registro.monto = 0;
    }
  }

  esVentaLeche(index: number): boolean {
    return !!this.registros[index]._esVentaLeche;
  }

  calcularMontoLeche(index: number): void {
    const r = this.registros[index];
    const litros = r.litros_vendidos_leche ?? 0;
    const precio = r.precio_litro_leche ?? 0;
    r.monto = litros * precio;
  }

  formatCOP(valor: number | undefined): string {
    if (!valor && valor !== 0) return '$ 0';
    return '$ ' + new Intl.NumberFormat('es-CO').format(valor);
  }

  tieneErrores(index: number): boolean {
    const r = this.registros[index];
    if (!r.titulo || !r.tipo_movimiento || !r.id_categoria || !r.fecha) return true;
    if (this.esVentaLeche(index)) {
      return !r.litros_vendidos_leche || !r.precio_litro_leche;
    }
    return !r.monto || r.monto <= 0;
  }

  private validarTodo(): boolean {
    return this.registros.every((_, i) => !this.tieneErrores(i));
  }

  private nuevoRegistroVacio(): RegistroForm {
    return {
      titulo: '',
      tipo_movimiento: undefined,
      id_categoria: '',
      _categoriaPadreId: '',
      fecha: new Date().toISOString().split('T')[0],
      descripcion: '',
      monto: 0,
      litros_vendidos_leche: null,
      precio_litro_leche: null,
      _esVentaLeche: false,
      _usandoPersonalizada: false,
    };
  }

  agregarRegistro(): void {
    this.registros.push(this.nuevoRegistroVacio());
  }

  eliminarRegistro(index: number): void {
    this.registros.splice(index, 1);
  }

  togglePersonalizada(index: number): void {
    this.registros[index]._usandoPersonalizada = !this.registros[index]._usandoPersonalizada;
    this.registros[index].id_categoria = '';
    this.cdr.markForCheck();
  }
  guardar(): void {
    this.submitted = true;
    if (!this.hatoActivo || !this.validarTodo()) return;

    this.guardando = true;
    const idHato: string = this.hatoActivo.idHato as string;

    const payload: RegistroFinancieroDTO[] = this.registros.map((r) => ({
      id_hato: idHato,
      id_categoria: r.id_categoria!,
      titulo: r.titulo!,
      tipo_movimiento: r.tipo_movimiento!,
      fecha: r.fecha!,
      descripcion: r.descripcion || undefined,
      monto: r.monto!,
      litros_vendidos_leche: r._esVentaLeche ? r.litros_vendidos_leche : null,
      precio_litro_leche: r._esVentaLeche ? r.precio_litro_leche : null,
    }));

    const ventasLeche = this.registros.filter(
      (r) => r._esVentaLeche && r.litros_vendidos_leche && r.precio_litro_leche && r.fecha,
    );

    this.registroService.crearRegistros(payload).subscribe({
      next: () => {
        this.guardando = false;
        this.submitted = false;

        if (ventasLeche.length > 0) {
          const primeraVenta = ventasLeche[0];
          this.ventaLecheService
            .crearVenta({
              idHato,
              fecha: primeraVenta.fecha!,
              litrosVendidos: primeraVenta.litros_vendidos_leche!,
              precioLitro: primeraVenta.precio_litro_leche!,
            })
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: (respuesta: RespuestaVentaLeche) => {
                if (respuesta.alerta && respuesta.litrosFaltantes > 0) {
                  this.alertaProduccion = true;
                  this.litrosFaltantes = respuesta.litrosFaltantes;
                  this.mensajeAlerta = respuesta.mensaje ?? '';
                  this.ventaLecheAlertaFecha = primeraVenta.fecha!;
                  this.ventaLecheAlertaLitros = respuesta.litrosFaltantes;
                  this.cdr.markForCheck();
                } else {
                  this.mostrarToast();
                }
              },
              error: () => this.mostrarToast(),
            });
        } else {
          this.mostrarToast();
        }

        this.registros = [this.nuevoRegistroVacio()];
        this.cdr.markForCheck();

        if (this.hatoActivo?.idHato) {
          this.kpiService.calcularKpis(this.hatoActivo.idHato).subscribe();
        }
      },
      error: (err) => {
        this.guardando = false;
        console.error('Error al guardar registros:', err);
        this.cdr.markForCheck();
      },
    });
  }

  private mostrarToast(): void {
    this.cdr.markForCheck();
    this.toastVisible = true;
    setTimeout(() => (this.toastVisible = false), 3500);
  }

  aceptarProduccionSugerida(): void {
    if (!this.hatoActivo?.idHato) return;
    this.agregandoProduccionSugerida = true;
    this.cdr.markForCheck();

    this.produccionService
      .crearRegistro({
        idHato: this.hatoActivo.idHato as any,
        fecha: this.ventaLecheAlertaFecha as any,
        litrosProducidos: this.litrosFaltantes,
        vacasOrdenadas: undefined,
      })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.kpiService.calcularKpis(this.hatoActivo!.idHato as string).subscribe();
          this.cerrarAlerta();
          this.mostrarToast();
        },
        error: () => {
          this.cerrarAlerta();
          this.mostrarToast();
        },
      });
  }

  rechazarProduccionSugerida(): void {
    this.cerrarAlerta();
    this.mostrarToast();
  }

  private cerrarAlerta(): void {
    this.alertaProduccion = false;
    this.litrosFaltantes = 0;
    this.mensajeAlerta = '';
    this.ventaLecheAlertaFecha = '';
    this.ventaLecheAlertaLitros = 0;
    this.agregandoProduccionSugerida = false;
    this.cdr.markForCheck();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
