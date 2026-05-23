import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

interface TestimonialItem {
  quote: string;
  initials: string;
  name: string;
  role: string;
  highlight: string;
}

@Component({
  selector: 'app-testimonials-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './testimonials-section.component.html',
  styleUrl: './testimonials-section.component.css'
})
export class TestimonialsSectionComponent {
  testimonials: TestimonialItem[] = [
    {
      quote: 'Antes tardaba días consolidando datos en Excel. Con Hathor veo mis KPIs al instante y puedo tomar decisiones el mismo día.',
      initials: 'CM',
      name: 'Carlos Moreno',
      role: 'Ganadero, Cundinamarca',
      highlight: '180 vacas'
    },
    {
      quote: 'Uso Hathor para llevar el análisis financiero de mis clientes. El benchmarking me permite mostrar qué deben mejorar con datos claros.',
      initials: 'AP',
      name: 'Ana Lucía Pedraza',
      role: 'Asesora financiera agropecuaria',
      highlight: '5 hatos asesorados'
    },
    {
      quote: 'Nunca había tenido visibilidad real de la rentabilidad de mi hato. Ahora puedo optimizar mes a mes con confianza.',
      initials: 'HC',
      name: 'Hernando Castillo',
      role: 'Propietario, finca La Esperanza',
      highlight: '320 vacas — Boyacá'
    }
  ];
}
