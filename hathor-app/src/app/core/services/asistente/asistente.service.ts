import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface ChatRequestDTO {
  idHato: string;
  mensaje: string;
  contextoActivo: string;
}

export interface ChatResponseDTO {
  respuesta: string;
  historialLargo: boolean;
  tokensEstimados: number;
  conversationId: string;
}

export interface PracticaGeneradaDTO {
  nombre: string;
  descripcion: string;
  objetivo: string;
  categoria: string;
  impactoEsperado: string;
  pasos: string[];
  kpiImpactado: string;
  dificultad: string;
  duracionDias: number;
  escala: string;
  tropicaAplicable: string;
}

export interface PracticaIAResponseDTO {
  practicaGenerada: PracticaGeneradaDTO;
  idHatoPracticaCreada: string | null;
  pendienteConfirmacion: boolean;
  mensaje: string;
}

export interface MensajeHistorialDTO {
  role: 'user' | 'assistant';
  content: string;
}

export interface ChatHistorialDTO {
  mensajes: MensajeHistorialDTO[];
  tieneHistorial: boolean;
  conversationId: string;
}

@Injectable({ providedIn: 'root' })
export class AsistenteService {
  private readonly API_URL = `${environment.apiUrl}/asistente`;

  private fabExpandido = true;
  private fabTimerIniciado = false;
  constructor(private http: HttpClient) {}

  iniciarTimerFab(): void {
    if (this.fabTimerIniciado) return;
    this.fabTimerIniciado = true;

    setTimeout(() => {
      this.fabExpandido = false;
    }, 10000);
  }

  chat(request: ChatRequestDTO): Observable<ChatResponseDTO> {
    return this.http.post<ChatResponseDTO>(`${this.API_URL}/chat`, request);
  }

  generarPractica(
    idHato: string,
    codigoKpi: string,
    confirmar: boolean,
  ): Observable<PracticaIAResponseDTO> {
    return this.http.post<PracticaIAResponseDTO>(`${this.API_URL}/generar-practica`, {
      idHato,
      codigoKpi,
      confirmar,
    });
  }

  limpiarConversacion(idHato: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/conversacion/${idHato}`);
  }

  getHistorial(idHato: string): Observable<ChatHistorialDTO> {
    return this.http.get<ChatHistorialDTO>(`${this.API_URL}/historial/${idHato}`);
  }

  /**HELPERS */
  getFabExpandido(): boolean {
    return this.fabExpandido;
  }
}
