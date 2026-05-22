import { Component, OnInit, Input, Output, EventEmitter, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormsModule,
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import {
  HatoService,
  RegistroHatoDTO,
  Hato,
} from '../../../../../../core/services/hato/hato.service';
import { HatoStateService } from '../../../../../../core/services/ui-state/hato-state.service';
import { SpinnerComponent } from '../../../../../../shared/spinner/spinner.component';

@Component({
  selector: 'app-seccion-info-hato',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, SpinnerComponent],
  templateUrl: './seccion-info-hato.component.html',
  styleUrl: './seccion-info-hato.component.css',
})
export class SeccionInfoHatoComponent implements OnInit {
  @Input() hatoActual: Hato | null = null;
  @Input() modoReadonly = false;

  @Output() seccionCompletada = new EventEmitter<Hato>();
  @Output() irSiguiente = new EventEmitter<void>();
  @Output() irAnterior = new EventEmitter<void>();

  form!: FormGroup;
  loadingGuardar = false;
  error = '';

  tiposHato = ['Lechero', 'Doble propósito'];
  departamentos: string[] = [];
  ciudadesFiltradas: { nombre: string; latitud: number; longitud: number; altitud: number }[] = [];
  private colombiaData: any[] = [];

  constructor(
    private fb: FormBuilder,
    private hatoService: HatoService,
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.cargarColombia();
  }

  private cargarColombia(): void {
    this.http.get<any[]>('assets/data/colombia_data.json').subscribe({
      next: (data) => {
        this.colombiaData = data;
        this.departamentos = data.map((d) => d.departamento);
        if (this.hatoActual) {
          const found = data.find((d) => d.departamento === this.hatoActual!.departamento);
          this.ciudadesFiltradas = found ? found.ciudades : [];
        }
        this.cdr.markForCheck();
      },
    });
  }

  private initForm(): void {
    this.form = this.fb.group({
      nombreHato: ['', [Validators.required, Validators.minLength(2)]],
      departamento: ['', Validators.required],
      ciudad: ['', Validators.required],
      altitud: [{ value: '', disabled: true }],
      tropico: [{ value: '', disabled: true }],
      tipoHato: ['', Validators.required],
      areaHato: [null, [Validators.required, Validators.min(0)]],
      areaPastoreo: [null, [Validators.required, Validators.min(0)]],
    });

    if (this.hatoActual) {
      this.form.patchValue({
        nombreHato: this.hatoActual.nombreHato,
        departamento: this.hatoActual.departamento,
        ciudad: this.hatoActual.ciudad,
        altitud: this.hatoActual.altitud,
        tropico: this.hatoActual.tropico,
        tipoHato: this.hatoActual.tipoHato,
        areaHato: this.hatoActual.areaHato,
        areaPastoreo: this.hatoActual.areaPastoreo,
      });
    }

    // Listener departamento
    this.form.get('departamento')!.valueChanges.subscribe((dep) => {
      const found = this.colombiaData.find((d) => d.departamento === dep);
      this.ciudadesFiltradas = found ? found.ciudades : [];
      this.form.get('ciudad')!.setValue('');
      this.form.get('altitud')!.setValue('');
      this.form.get('tropico')!.setValue('');
      this.cdr.markForCheck();
    });

    // Listener ciudad — autocompletar altitud y trópico
    this.form.get('ciudad')!.valueChanges.subscribe((nombreCiudad) => {
      const ciudad = this.ciudadesFiltradas.find((c) => c.nombre === nombreCiudad);
      if (!ciudad) return;
      const altitud = ciudad.altitud;
      let tropico = '';
      if (altitud < 1000) tropico = 'CALIDO';
      else if (altitud < 2000) tropico = 'TEMPLADO';
      else tropico = 'FRIO';
      this.form.get('altitud')!.setValue(altitud);
      this.form.get('tropico')!.setValue(tropico);
      this.cdr.markForCheck();
    });
  }

  async guardar(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.cdr.markForCheck();
      return;
    }

    this.loadingGuardar = true;
    this.error = '';

    try {
      const v = this.form.getRawValue();

      // Buscar coordenadas de la ciudad seleccionada
      const ciudadData = this.ciudadesFiltradas.find((c) => c.nombre === v.ciudad);

      const dto: RegistroHatoDTO = {
        nombreHato: v.nombreHato,
        departamento: v.departamento,
        ciudad: v.ciudad,
        altitud: v.altitud,
        tropico: v.tropico,
        tipoHato: v.tipoHato,
        areaHato: v.areaHato,
        areaPastoreo: v.areaPastoreo,
        cantCorrales: 0,
        cantSalasOrdenio: 0,
        capacidadAlmacenarLeche: 0,
        cantEmpleadosPermanentes: 0,
        cantEmpleadosTemporales: 0,
        latitud: ciudadData?.latitud,
        longitud: ciudadData?.longitud,
      };

      const hatoCreado = await firstValueFrom(this.hatoService.crearHato(dto));
      this.seccionCompletada.emit(hatoCreado);
    } catch {
      this.error = 'Error al guardar. Intenta de nuevo.';
      this.cdr.markForCheck();
    } finally {
      this.loadingGuardar = false;
      this.cdr.markForCheck();
    }
  }

  continuar(): void {
    this.irSiguiente.emit();
  }
}
