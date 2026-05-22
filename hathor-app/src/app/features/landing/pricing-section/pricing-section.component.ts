import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

interface PlanItem {
  name: string;
  price: string;
  period: string;
  subtitle: string;
  cta: string;
  features: string[];
  mutedFeatures?: string[];
  highlighted?: boolean;
}

@Component({
  selector: 'app-pricing-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pricing-section.component.html',
  styleUrl: './pricing-section.component.css'
})
export class PricingSectionComponent {
  plans: PlanItem[] = [
    {
      name: 'Gratis',
      price: '$0',
      period: 'para siempre',
      subtitle: 'Perfecto para empezar a medir tu hato.',
      cta: 'Comenzar gratis',
      features: [
        '1 hato registrado',
        'Hasta 50 animales',
        'KPIs básicos (3 indicadores)',
        'Importación de datos Excel',
        'Reporte mensual en PDF',
        'Comparación básica con el sector'
      ],
      mutedFeatures: ['Benchmarking avanzado', 'Recomendaciones personalizadas']
    },
    {
      name: 'Pro',
      price: '$89,000',
      period: '/ mes COP',
      subtitle: 'Para ganaderos que quieren resultados reales.',
      cta: 'Próximamente',
      highlighted: true,
      features: [
        'Hatos ilimitados',
        'Animales ilimitados',
        '6 KPIs completos del sector',
        'Benchmarking avanzado',
        'Recomendaciones personalizadas',
        'Exportación en múltiples formatos',
        'Historial de indicadores',
        'Soporte prioritario'
      ]
    },
    {
      name: 'Asesor',
      price: '$220,000',
      period: '/ mes COP',
      subtitle: 'Para asesores que gestionan múltiples clientes.',
      cta: 'Próximamente',
      features: [
        'Todo lo del plan Pro',
        'Hasta 20 hatos de clientes',
        'Dashboard multi-hato',
        'Reportes comparativos entre hatos',
        'Acceso para equipo de trabajo',
        'Análisis histórico completo'
      ]
    }
  ];
}
