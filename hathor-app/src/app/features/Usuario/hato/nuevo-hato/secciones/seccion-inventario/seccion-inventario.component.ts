import { Component, OnInit, Input, Output, EventEmitter, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormsModule,
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  FormArray,
  Validators,
} from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import {
  Hato,
  HatoService,
  InfraestructuraBasicaDTO,
} from '../../../../../../core/services/hato/hato.service';
import {
  CategoriaInventarioService,
  CategoriaInventario,
} from '../../../../../../core/services/inventario/categoria-inventario.service';
import {
  CategoriaGanadoService,
  CategoriaGanado,
} from '../../../../../../core/services/inventario/categoria-ganado.service';
import { RazaService, Raza } from '../../../../../../core/services/inventario/raza.service';
import {
  InventarioGeneralService,
  InventarioGeneralDTO,
} from '../../../../../../core/services/inventario/inventario-general.service';
import {
  InventarioGanadoService,
  InventarioGanadoDTO,
} from '../../../../../../core/services/inventario/inventario-ganado.service';

import { SpinnerComponent } from '../../../../../../shared/spinner/spinner.component';

interface ResumenInventarioItem {
  nombreCategoriaPadre: string;
  nombreSubcategoria: string;
  cantidad: number;
  valorUnitario: number;
  total: number;
}

interface ResumenGanadoItem {
  nombreRaza: string;
  tipoRaza: string;
  nombreCategoria: string;
  cantidad: number;
  edadPromedioMeses: number;
  valorUnitario: number;
  total: number;
}

@Component({
  selector: 'app-seccion-inventario',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, SpinnerComponent],
  templateUrl: './seccion-inventario.component.html',
  styleUrl: './seccion-inventario.component.css',
})
export class SeccionInventarioComponent implements OnInit {
  @Input() hatoActual: Hato | null = null;
  @Input() modoReadonly = false;

  @Output() seccionCompletada = new EventEmitter<Hato>();
  @Output() irSiguiente = new EventEmitter<void>();
  @Output() irAnterior = new EventEmitter<void>();

  categoriasInventario: CategoriaInventario[] = [];
  categoriasGanado: CategoriaGanado[] = [];
  loadingCatalogos = false;
  loadingGuardar = false;
  error = '';

  inventarioGuardado: any[] = [];
  ganadoGuardado: any[] = [];
  loadingReadonly = false;

  tiposRaza: string[] = [];
  razasSeleccionadas = new Map<string, Set<number>>();

  formInfraestructura!: FormGroup;

  seccionesInventarioAbiertas = new Set<number>();
  inventarioPorCategoria = new Map<number, FormArray>();

  tiposRazaAbiertos = new Set<string>();
  ganadoPorTipo = new Map<string, { raza: Raza; filas: FormArray }[]>();
  razasPorTipo = new Map<string, Raza[]>();

  categoriasParent: CategoriaInventario[] = [];

  razasPorFila: Raza[][] = [];

  ganadoFormArray!: FormArray;

  get totalInventarioGeneral(): number {
    let total = 0;
    this.inventarioPorCategoria.forEach((formArray) => {
      formArray.controls.forEach((ctrl) => {
        total += (ctrl.get('cantidad')?.value ?? 0) * (ctrl.get('valorUnitario')?.value ?? 0);
      });
    });
    return total;
  }

  get totalInventarioGanado(): number {
    let total = 0;
    this.ganadoPorTipo.forEach((razasConFilas) => {
      razasConFilas.forEach((entry) => {
        entry.filas.controls.forEach((ctrl) => {
          total += (ctrl.get('cantidad')?.value ?? 0) * (ctrl.get('valorUnitario')?.value ?? 0);
        });
      });
    });
    return total;
  }

  get seccionValida(): boolean {
    const tieneInventario = this.totalInventarioGeneral > 0;
    const tieneGanado = this.totalInventarioGanado > 0;
    return this.formInfraestructura.valid && tieneInventario && tieneGanado;
  }

  constructor(
    private fb: FormBuilder,
    private hatoService: HatoService,
    private categoriaInventarioService: CategoriaInventarioService,
    private categoriaGanadoService: CategoriaGanadoService,
    private razaService: RazaService,
    private inventarioGeneralService: InventarioGeneralService,
    private inventarioGanadoService: InventarioGanadoService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.initForms();
    if (!this.modoReadonly) {
      this.cargarCatalogos();
    } else {
      this.cargarDatosGuardados();
    }
  }

  private initForms(): void {
    this.formInfraestructura = this.fb.group({
      cantCorrales: [null, [Validators.required, Validators.min(0)]],
      cantSalasOrdenio: [null, [Validators.required, Validators.min(0)]],
      capacidadAlmacenarLeche: [null, [Validators.required, Validators.min(0)]],
      cantEmpleadosPermanentes: [null, [Validators.required, Validators.min(0)]],
      cantEmpleadosTemporales: [null, [Validators.required, Validators.min(0)]],
    });
  }

  private async cargarDatosGuardados(): Promise<void> {
    if (!this.hatoActual?.idHato) return;
    this.loadingReadonly = true;
    try {
      const [inventario, ganado] = await Promise.all([
        firstValueFrom(this.inventarioGeneralService.getByHato(this.hatoActual.idHato)),
        firstValueFrom(this.inventarioGanadoService.getByHato(this.hatoActual.idHato)),
      ]);
      this.inventarioGuardado = inventario;
      this.ganadoGuardado = ganado;
    } catch {
    } finally {
      this.loadingReadonly = false;
      this.cdr.markForCheck();
    }
  }

  private async cargarCatalogos(): Promise<void> {
    this.loadingCatalogos = true;
    try {
      const [categorias, categoriasGanado, tiposRaza] = await Promise.all([
        firstValueFrom(this.categoriaInventarioService.getCategorias()),
        firstValueFrom(this.categoriaGanadoService.getCategorias()),
        firstValueFrom(this.razaService.getTiposRaza()),
      ]);

      this.categoriasInventario = categorias;
      this.categoriasGanado = categoriasGanado;
      this.tiposRaza = tiposRaza;
      this.categoriasParent = categorias.filter((c) => c.tipo === 'PADRE');

      this.categoriasParent.forEach((padre) => {
        this.inventarioPorCategoria.set(padre.idCategoriaInventario, this.fb.array([]));
      });

      tiposRaza.forEach((tipo) => {
        this.ganadoPorTipo.set(tipo, []);
        this.razasPorTipo.set(tipo, []);
      });
    } catch (err) {
      console.error('❌ Error en cargarCatalogos:', err);
      this.error = 'Error al cargar catálogos. Recarga la página.';
    } finally {
      this.loadingCatalogos = false;
      this.cdr.markForCheck();
    }
  }

  // ===== INVENTARIO GENERAL =====
  // ===== ACORDEÓN INVENTARIO =====

  toggleSeccionInventario(idPadre: number): void {
    const idPadreNum = Number(idPadre);

    if (this.seccionesInventarioAbiertas.has(idPadreNum)) {
      this.seccionesInventarioAbiertas.delete(idPadreNum);
      this.cdr.markForCheck();
      return;
    }

    this.seccionesInventarioAbiertas.add(idPadreNum);

    if (!this.inventarioPorCategoria.has(idPadreNum)) {
      this.inventarioPorCategoria.set(idPadreNum, this.fb.array([]));
    }

    const formArray = this.inventarioPorCategoria.get(idPadreNum)!;

    if (formArray.length === 0) {
      const subcategorias = this.getSubcategorias(idPadreNum);

      subcategorias.forEach((sub) => {
        formArray.push(
          this.fb.group({
            idCategoriaInventario: [sub.idCategoriaInventario],
            nombreSubcategoria: [sub.nombre],
            cantidad: [null, [Validators.min(0)]],
            valorUnitario: [null, [Validators.min(0)]],
            descripcion: [''],
          }),
        );
      });
    }

    this.cdr.markForCheck();
  }

  estaAbiertaInventario(idPadre: number): boolean {
    return this.seccionesInventarioAbiertas.has(Number(idPadre));
  }

  getSubcategorias(idPadre: number): CategoriaInventario[] {
    return this.categoriasInventario.filter(
      (c) => c.tipo === 'HIJA' && c.categoriaPadre?.idCategoriaInventario === idPadre,
    );
  }

  getFormArrayInventario(idPadre: number): FormArray {
    return this.inventarioPorCategoria.get(Number(idPadre)) ?? this.fb.array([]);
  }

  getFilaInventario(idPadre: number, index: number): FormGroup {
    return this.getFormArrayInventario(Number(idPadre)).at(index) as FormGroup;
  }

  // ===== ACORDEÓN GANADO =====
  // ===== INVENTARIO GANADO =====

  async toggleSeccionGanado(tipo: string): Promise<void> {
    if (this.tiposRazaAbiertos.has(tipo)) {
      this.tiposRazaAbiertos.delete(tipo);
      this.cdr.markForCheck();
      return;
    }

    this.tiposRazaAbiertos.add(tipo);

    if (!this.razasPorTipo.get(tipo)?.length) {
      try {
        const razas = await firstValueFrom(this.razaService.getRazasPorTipo(tipo));
        this.razasPorTipo.set(tipo, razas);

        const razasConFilas = razas.map((raza) => ({
          raza,
          filas: this.fb.array([this.crearFilaGanado()]),
        }));

        this.ganadoPorTipo.set(tipo, razasConFilas);
      } catch {
        this.error = 'Error al cargar razas.';
      }
    }

    this.cdr.markForCheck();
  }
  toggleRaza(tipo: string, idRaza: number): void {
    if (!this.razasSeleccionadas.has(tipo)) {
      this.razasSeleccionadas.set(tipo, new Set());
    }
    const set = this.razasSeleccionadas.get(tipo)!;
    if (set.has(idRaza)) {
      set.delete(idRaza);
      const entry = this.ganadoPorTipo.get(tipo)?.find((e) => e.raza.idRaza === idRaza);
      if (entry) entry.filas = this.fb.array([this.crearFilaGanado()]);
    } else {
      set.add(idRaza);
    }
    this.cdr.markForCheck();
  }

  isRazaSeleccionada(tipo: string, idRaza: number): boolean {
    return this.razasSeleccionadas.get(tipo)?.has(idRaza) ?? false;
  }

  private crearFilaGanado(): FormGroup {
    return this.fb.group({
      idCategoria: [null, Validators.required],
      cantidad: [null, [Validators.min(1)]],
      edadPromedioMeses: [null, [Validators.min(0)]],
      valorUnitario: [null, [Validators.min(0)]],
    });
  }

  // === GANADO ===
  agregarFilaGanado(tipo: string, idRaza: number): void {
    const razasConFilas = this.ganadoPorTipo.get(tipo);
    if (!razasConFilas) return;
    const entry = razasConFilas.find((r) => r.raza.idRaza === idRaza);
    if (!entry) return;
    entry.filas.push(this.crearFilaGanado());
    this.cdr.markForCheck();
  }

  eliminarFilaGanado(tipo: string, idRaza: number, index: number): void {
    const razasConFilas = this.ganadoPorTipo.get(tipo);
    if (!razasConFilas) return;
    const entry = razasConFilas.find((r) => r.raza.idRaza === idRaza);
    if (!entry || entry.filas.length <= 1) return;
    entry.filas.removeAt(index);
    this.cdr.markForCheck();
  }

  getFilasGanado(tipo: string, idRaza: number): FormArray {
    const razasConFilas = this.ganadoPorTipo.get(tipo);
    if (!razasConFilas) return this.fb.array([]);
    return razasConFilas.find((r) => r.raza.idRaza === idRaza)?.filas ?? this.fb.array([]);
  }

  estaAbiertoGanado(tipo: string): boolean {
    return this.tiposRazaAbiertos.has(tipo);
  }

  getFilaGanado(tipo: string, idRaza: number, index: number): FormGroup {
    const entry = this.ganadoPorTipo.get(tipo)?.find((e) => e.raza.idRaza === idRaza);
    return entry?.filas.at(index) as FormGroup;
  }

  // ===== HELPERS NOMBRES =====

  getNombreCategoria(id: number): string {
    return this.categoriasInventario.find((c) => c.idCategoriaInventario === id)?.nombre ?? '-';
  }

  getTotalSeccionInventario(idPadre: number): number {
    const formArray = this.getFormArrayInventario(Number(idPadre));
    return formArray.controls.reduce((acc, ctrl) => {
      return acc + (ctrl.get('cantidad')?.value ?? 0) * (ctrl.get('valorUnitario')?.value ?? 0);
    }, 0);
  }

  getTotalSeccionGanado(tipo: string): number {
    const razasConFilas = this.ganadoPorTipo.get(tipo);
    if (!razasConFilas) return 0;
    return razasConFilas.reduce((total, entry) => {
      return (
        total +
        entry.filas.controls.reduce((acc, ctrl) => {
          return acc + (ctrl.get('cantidad')?.value ?? 0) * (ctrl.get('valorUnitario')?.value ?? 0);
        }, 0)
      );
    }, 0);
  }
  // == GETTERS RESUMEN ==
  get resumenInfraestructura() {
    const v = this.formInfraestructura.value;
    return [
      { label: 'Corrales', valor: v.cantCorrales, sufijo: 'unidades' },
      { label: 'Salas de ordeño', valor: v.cantSalasOrdenio, sufijo: 'unidades' },
      { label: 'Cap. almacenamiento', valor: v.capacidadAlmacenarLeche, sufijo: 'litros' },
      { label: 'Empleados permanentes', valor: v.cantEmpleadosPermanentes, sufijo: 'personas' },
      { label: 'Empleados temporales', valor: v.cantEmpleadosTemporales, sufijo: 'personas' },
    ].filter((item) => item.valor !== null && item.valor !== undefined && item.valor !== '');
  }

  get resumenInventario(): ResumenInventarioItem[] {
    const items: ResumenInventarioItem[] = [];

    this.inventarioPorCategoria.forEach((formArray, idPadre) => {
      const padre = this.categoriasParent.find((p) => p.idCategoriaInventario === idPadre);
      if (!padre) return;

      formArray.controls.forEach((ctrl) => {
        const v = ctrl.value;
        if (!v.cantidad || !v.valorUnitario) return;
        items.push({
          nombreCategoriaPadre: padre.nombre,
          nombreSubcategoria: v.nombreSubcategoria,
          cantidad: v.cantidad,
          valorUnitario: v.valorUnitario,
          total: v.cantidad * v.valorUnitario,
        });
      });
    });

    return items;
  }

  get resumenGanado(): ResumenGanadoItem[] {
    const items: ResumenGanadoItem[] = [];

    this.ganadoPorTipo.forEach((razasConFilas, tipo) => {
      razasConFilas.forEach((entry) => {
        entry.filas.controls.forEach((ctrl) => {
          const v = ctrl.value;
          if (!v.cantidad) return;
          const categoria = this.categoriasGanado.find((c) => c.idCategoria === v.idCategoria);
          items.push({
            nombreRaza: entry.raza.nombre,
            tipoRaza: tipo,
            nombreCategoria: categoria?.nombreCategoria ?? '-',
            cantidad: v.cantidad,
            edadPromedioMeses: v.edadPromedioMeses ?? 0,
            valorUnitario: v.valorUnitario ?? 0,
            total: (v.cantidad ?? 0) * (v.valorUnitario ?? 0),
          });
        });
      });
    });

    return items;
  }

  get totalGeneral(): number {
    return this.totalInventarioGeneral + this.totalInventarioGanado;
  }

  get tieneResumen(): boolean {
    return (
      this.resumenInfraestructura.length > 0 ||
      this.resumenInventario.length > 0 ||
      this.resumenGanado.length > 0
    );
  }
  // ===== GUARDAR =====

  async guardar(): Promise<void> {
    if (!this.seccionValida || !this.hatoActual?.idHato) return;

    this.loadingGuardar = true;
    this.error = '';

    try {
      // 1. Infraestructura básica
      const infraDto: InfraestructuraBasicaDTO = this.formInfraestructura.value;
      await firstValueFrom(
        this.hatoService.actualizarInfraestructuraBasica(this.hatoActual.idHato!, infraDto),
      );

      // 2. Inventario general
      for (const [idPadre, formArray] of this.inventarioPorCategoria) {
        for (const ctrl of formArray.controls) {
          const v = ctrl.value;
          if (!v.cantidad || !v.valorUnitario) continue;
          const dto: InventarioGeneralDTO = {
            idHato: this.hatoActual.idHato!,
            idCategoriaInventario: v.idCategoriaInventario,
            nombreItem: v.nombreSubcategoria,
            cantidad: v.cantidad,
            valorUnitario: v.valorUnitario,
            descripcion: v.descripcion,
          };
          await firstValueFrom(this.inventarioGeneralService.crearItem(dto));
        }
      }

      // 3. Inventario ganado
      for (const [tipo, razasConFilas] of this.ganadoPorTipo) {
        for (const entry of razasConFilas) {
          for (const ctrl of entry.filas.controls) {
            const v = ctrl.value;
            if (!v.cantidad || !v.idCategoria) continue;
            const dto: InventarioGanadoDTO = {
              idHato: this.hatoActual.idHato!,
              idRaza: entry.raza.idRaza,
              idCategoria: v.idCategoria,
              cantidad: v.cantidad,
              edadPromedioMeses: v.edadPromedioMeses ?? 0,
              valorUnitario: v.valorUnitario ?? 0,
            };
            await firstValueFrom(this.inventarioGanadoService.crearRegistro(dto));
          }
        }
      }

      // 4. Actualizar completitud a 50%
      const hatoActualizado = await firstValueFrom(
        this.hatoService.actualizarCompletitud(this.hatoActual.idHato!, 50),
      );

      this.seccionCompletada.emit(hatoActualizado);
    } catch {
      this.error = 'Error al guardar. Intenta de nuevo.';
      this.cdr.markForCheck();
    } finally {
      this.loadingGuardar = false;
      this.cdr.markForCheck();
    }
  }
}
