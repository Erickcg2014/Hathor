import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  ViewChild,
  ElementRef,
  AfterViewChecked,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, NavigationEnd } from '@angular/router';
import { Subject, takeUntil, filter } from 'rxjs';

import { HatoStateService } from '../../core/services/ui-state/hato-state.service';
import {
  AsistenteService,
  ChatResponseDTO,
  ChatHistorialDTO,
} from '../../core/services/asistente/asistente.service';
import { SpinnerComponent } from '../spinner/spinner.component';
import { marked } from 'marked';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

export interface MensajeChat {
  role: 'user' | 'assistant';
  content: string;
  contentHtml?: SafeHtml;
  timestamp: Date;
}

@Component({
  selector: 'app-asistente-chat',
  standalone: true,
  imports: [CommonModule, FormsModule, SpinnerComponent],
  templateUrl: './asistente-chat.component.html',
  styleUrl: './asistente-chat.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AsistenteChatComponent implements OnInit, OnDestroy, AfterViewChecked {
  @ViewChild('mensajesContainer') mensajesContainer!: ElementRef;

  // ── Estado del panel ──────────────────────────────────────────────────
  abierto = false;
  minimizado = false;
  fabExpandido = true;

  // ── Estado del chat ───────────────────────────────────────────────────
  mensajes: MensajeChat[] = [];
  inputMensaje = '';
  enviando = false;
  historialLargo = false;
  error = '';
  cargandoHistorial = false;

  // ── Contexto activo según ruta ────────────────────────────────────────
  contextoActivo = 'general';

  // ── Hato activo ───────────────────────────────────────────────────────
  idHatoActivo: string | null = null;
  nombreHato = '';

  private destroy$ = new Subject<void>();
  private debeScrollear = false;

  constructor(
    private hatoState: HatoStateService,
    private asistenteService: AsistenteService,
    private router: Router,
    private sanitizer: DomSanitizer,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.asistenteService.iniciarTimerFab();

    const interval = setInterval(() => {
      const nuevo = this.asistenteService.getFabExpandido();
      if (nuevo !== this.fabExpandido) {
        this.fabExpandido = nuevo;
        this.cdr.markForCheck();
      }
      if (!nuevo) clearInterval(interval);
    }, 500);
    // Escuchar hato activo
    this.hatoState.hatoActivo$.pipe(takeUntil(this.destroy$)).subscribe((hato) => {
      this.idHatoActivo = (hato?.idHato as string) ?? null;
      this.nombreHato = hato?.nombreHato ?? '';
      this.cdr.markForCheck();
    });

    // Detectar contexto según ruta activa
    this.router.events
      .pipe(
        takeUntil(this.destroy$),
        filter((e) => e instanceof NavigationEnd),
      )
      .subscribe((e: any) => {
        this.contextoActivo = this.inferirContexto(e.urlAfterRedirects);
        this.cdr.markForCheck();
      });

    // Contexto inicial
    this.contextoActivo = this.inferirContexto(this.router.url);

    this.mensajes.push({
      role: 'assistant',
      content: '¡Hola! Soy Hathor AI 🐄...',
      contentHtml: this.parsearMarkdown(
        '¡Hola! Soy Hathor AI 🐄 Tu asistente de ganadería lechera. Puedo ayudarte a entender tus KPIs, compararte con otros hatos y generar prácticas de mejora personalizadas. ¿En qué te ayudo?',
      ),
      timestamp: new Date(),
    });
  }

  ngAfterViewChecked(): void {
    if (this.debeScrollear) {
      this.scrollAlFinal();
      this.debeScrollear = false;
    }
  }

  // ── Acciones del panel ────────────────────────────────────────────────

  togglePanel(): void {
    this.abierto = !this.abierto;
    if (this.abierto) {
      this.minimizado = false;
      this.debeScrollear = true;
      // Cargar historial al abrir si hay hato activo
      if (this.idHatoActivo) {
        this.cargarHistorialPrevio();
      }
    }
    this.cdr.markForCheck();
  }

  minimizar(): void {
    this.minimizado = true;
    this.cdr.markForCheck();
  }

  restaurar(): void {
    this.minimizado = false;
    this.debeScrollear = true;
    this.cdr.markForCheck();
  }

  cerrar(): void {
    this.abierto = false;
    this.cdr.markForCheck();
  }

  // ── Envío de mensaje ──────────────────────────────────────────────────

  enviar(): void {
    const mensaje = this.inputMensaje.trim();
    if (!mensaje || this.enviando || !this.idHatoActivo) return;

    // Agregar mensaje del usuario
    this.mensajes.push({
      role: 'user',
      content: mensaje,
      timestamp: new Date(),
    });

    this.inputMensaje = '';
    this.enviando = true;
    this.error = '';
    this.debeScrollear = true;
    this.cdr.markForCheck();

    this.asistenteService
      .chat({
        idHato: this.idHatoActivo,
        mensaje,
        contextoActivo: this.contextoActivo,
      })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res: ChatResponseDTO) => {
          this.mensajes.push({
            role: 'assistant',
            content: res.respuesta,
            contentHtml: this.parsearMarkdown(res.respuesta),
            timestamp: new Date(),
          });
          this.historialLargo = res.historialLargo;
          this.enviando = false;
          this.debeScrollear = true;
          this.cdr.markForCheck();
        },
        error: () => {
          this.error = 'Error al conectar con el asistente. Intenta de nuevo.';
          this.enviando = false;
          this.cdr.markForCheck();
        },
      });
  }

  limpiarConversacion(): void {
    if (!this.idHatoActivo) return;
    this.asistenteService
      .limpiarConversacion(this.idHatoActivo)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.mensajes = [
            {
              role: 'assistant',
              content: 'Conversación reiniciada. ¿En qué te puedo ayudar?',
              timestamp: new Date(),
            },
          ];
          this.historialLargo = false;
          this.cdr.markForCheck();
        },
      });
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.enviar();
    }
  }

  // ── Helpers ───────────────────────────────────────────────────────────
  private parsearMarkdown(texto: string): SafeHtml {
    const html = marked.parse(texto) as string;
    return this.sanitizer.bypassSecurityTrustHtml(html);
  }

  private inferirContexto(url: string): string {
    if (url.includes('/benchmarking')) return 'benchmarking';
    if (url.includes('/finanzas')) return 'finanzas';
    if (url.includes('/practicas')) return 'practicas';
    if (url.includes('/kpis') || url.includes('/benchmarking/kpis')) return 'kpis';
    return 'general';
  }

  private scrollAlFinal(): void {
    try {
      const el = this.mensajesContainer?.nativeElement;
      if (el) el.scrollTop = el.scrollHeight;
    } catch {}
  }

  formatearHora(fecha: Date): string {
    return fecha.toLocaleTimeString('es-CO', {
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  private cargarHistorialPrevio(): void {
    if (!this.idHatoActivo) return;

    this.cargandoHistorial = true;
    this.cdr.markForCheck();

    this.asistenteService
      .getHistorial(this.idHatoActivo)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (historial: ChatHistorialDTO) => {
          if (historial.tieneHistorial && historial.mensajes.length > 0) {
            // Reemplaza los mensajes actuales con el historial recuperado
            this.mensajes = historial.mensajes.map((m) => ({
              role: m.role,
              content: m.content,
              contentHtml: m.role === 'assistant' ? this.parsearMarkdown(m.content) : undefined,
              timestamp: new Date(),
            }));
          }
          // Si no hay historial, los mensajes actuales
          this.cargandoHistorial = false;
          this.debeScrollear = true;
          this.cdr.markForCheck();
        },
        error: () => {
          this.cargandoHistorial = false;
          this.cdr.markForCheck();
        },
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
