import { Directive, ElementRef, OnInit, OnDestroy, Input, Renderer2 } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID, inject } from '@angular/core';

@Directive({
  selector: '[appRevealOnScroll]',
  standalone: true,
})
export class RevealOnScrollDirective implements OnInit, OnDestroy {
  @Input() revealDelay: number = 0; // delay
  @Input() revealDirection: 'up' | 'left' | 'right' = 'up'; // dirección

  private observer: IntersectionObserver | null = null;
  private platformId = inject(PLATFORM_ID);

  constructor(
    private el: ElementRef,
    private renderer: Renderer2,
  ) {}

  ngOnInit() {
    if (!isPlatformBrowser(this.platformId)) return;

    this.renderer.setStyle(this.el.nativeElement, 'opacity', '0');
    this.renderer.setStyle(
      this.el.nativeElement,
      'transition',
      `opacity 0.6s ease ${this.revealDelay}ms, transform 0.6s ease ${this.revealDelay}ms`,
    );

    const translateMap = {
      up: 'translateY(40px)',
      left: 'translateX(-40px)',
      right: 'translateX(40px)',
    };
    this.renderer.setStyle(this.el.nativeElement, 'transform', translateMap[this.revealDirection]);

    this.observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            this.renderer.setStyle(this.el.nativeElement, 'opacity', '1');
            this.renderer.setStyle(this.el.nativeElement, 'transform', 'translate(0)');
            this.observer?.unobserve(this.el.nativeElement);
          }
        });
      },
      { threshold: 0.12 },
    );

    this.observer.observe(this.el.nativeElement);
  }

  ngOnDestroy() {
    this.observer?.disconnect();
  }
}
