import { Component } from '@angular/core';
import { NavbarComponent } from './navbar/navbar.component';
import { HeroSectionComponent } from './hero-section/hero-section.component';
import { FeaturesSectionComponent } from './features-section/features-section.component';
import { ProblemSectionComponent } from './problem-section/problem-section.component';
import { HowItWorksSectionComponent } from './how-it-works-section/how-it-works-section.component';
import { BenefitsSectionComponent } from './benefits-section/benefits-section.component';
import { BenchmarkingSectionComponent } from './benchmarking-section/benchmarking-section.component';
import { KpiPreviewSectionComponent } from './kpi-preview-section/kpi-preview-section.component';
import { ExcelImportSectionComponent } from './excel-import-section/excel-import-section.component';
import { TestimonialsSectionComponent } from './testimonials-section/testimonials-section.component';
import { PricingSectionComponent } from './pricing-section/pricing-section.component';
import { CtaSectionComponent } from './cta-section/cta-section.component';
import { FooterComponent } from './footer/footer.component';
import { RevealOnScrollDirective } from '../../core/directives/reveal-on-scroll.directive';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [
    RevealOnScrollDirective,
    NavbarComponent,
    HeroSectionComponent,
    FeaturesSectionComponent,
    ProblemSectionComponent,
    HowItWorksSectionComponent,
    BenefitsSectionComponent,
    BenchmarkingSectionComponent,
    KpiPreviewSectionComponent,
    ExcelImportSectionComponent,
    TestimonialsSectionComponent,
    PricingSectionComponent,
    CtaSectionComponent,
    FooterComponent,
  ],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css',
})
export class LandingComponent {}
