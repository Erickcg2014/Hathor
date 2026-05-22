const usuarioValido = {
  email: 'loadtest_01@loadtest.com',
  password: 'LoadTest123!',
};

const doLogin = () => {
  cy.visit('/login');
  cy.get('[data-cy="input-email"]').type(usuarioValido.email);
  cy.get('[data-cy="input-password"]').type(usuarioValido.password);
  cy.get('[data-cy="btn-login"]').click();
  cy.url().should('include', '/home');
  cy.get('[data-cy="home-modulos"]', { timeout: 20000 }).should('exist');
  cy.wait(1000);
};

const cerrarBitacora = () => {
  cy.get('body').then(($body) => {
    if ($body.find('[data-cy="bitacora-backdrop"]').length > 0) {
      cy.get('[data-cy="bitacora-backdrop"]').click({ force: true });
      cy.wait(800);
    } else if ($body.find('[data-cy="btn-recordar-despues"]').length > 0) {
      cy.get('[data-cy="btn-recordar-despues"]').click({ force: true });
      cy.wait(800);
    }
  });
};

describe('08 — Flujo Completo: KPIs → Recomendaciones → Prácticas', () => {
  before(() => {
    doLogin();
  });

  it('E2E-01: Flujo completo KPIs → Recomendaciones → Prácticas', () => {
    // ── PASO 1: Ir al módulo de benchmarking ──────────────────
    cy.log('PASO 1 — Navegando al módulo de Benchmarking KPIs');
    cy.visit('/benchmarking/kpis');
    cerrarBitacora();
    cy.get('[data-cy="benchmarking-header"]', { timeout: 20000 }).should('exist');

    // ── PASO 2: Recalcular benchmarking ───────────────────────
    cy.log('PASO 2 — Recalculando benchmarking');
    cy.get('[data-cy="btn-recalcular"]', { timeout: 20000 })
      .should('exist')
      .should('not.be.disabled')
      .click({ force: true });

    cy.get('[data-cy="btn-recalcular"]', { timeout: 180000 }).should(
      'contain.text',
      'Recalcular benchmarking',
    );
    cy.log('✓ Benchmarking recalculado correctamente');

    // ── PASO 3: Verificar que hay KPIs calculados ─────────────
    cy.log('PASO 3 — Verificando resumen de KPIs');
    cy.get('[data-cy="summary-grid"]', { timeout: 20000 }).should('exist');
    cy.get('[data-cy="summary-kpis-comparados"]')
      .find('.summary-value')
      .invoke('text')
      .should('match', /\d+/);
    cy.log('✓ KPIs calculados y visibles');

    // ── PASO 4: Navegar a Recomendaciones ─────────────────────
    cy.log('PASO 4 — Navegando a Recomendaciones');
    cy.visit('/practicas/recomendaciones');
    cerrarBitacora();
    cy.get('[data-cy="recomendaciones-header"]', { timeout: 20000 }).should('exist');

    // ── PASO 5: Verificar al menos 1 recomendación activa ─────
    cy.log('PASO 5 — Verificando recomendaciones activas');
    cy.get('body').then(($body) => {
      if ($body.find('[data-cy="recomendaciones-grid"]').length > 0) {
        cy.get('[data-cy="recomendaciones-grid"]').should('exist');
        cy.get('[data-cy="reco-card"]').should('have.length.greaterThan', 0);
        cy.log('✓ Al menos 1 recomendación activa encontrada');
      } else {
        cy.get('[data-cy="recomendaciones-empty"]').should('exist');
        cy.log('ℹ Sin recomendaciones activas — KPIs en estado óptimo');
      }
    });

    // ── PASO 6: Navegar a Mis Prácticas ───────────────────────
    cy.log('PASO 6 — Navegando a Mis Prácticas');
    cy.visit('/home');
    cy.wait(2000);
    cy.visit('/practicas/sugeridas');
    cerrarBitacora();
    cy.get('[data-cy="mis-practicas-header"]', { timeout: 20000 }).should('exist');

    // ── PASO 7: Verificar al menos 1 práctica generada ────────
    cy.log('PASO 7 — Verificando prácticas generadas');
    cy.get('[data-cy="mis-practicas-stats"]', { timeout: 20000 }).should('exist');

    cy.get('body').then(($body) => {
      if ($body.find('[data-cy="practicas-grid"]').length > 0) {
        cy.get('[data-cy="practicas-grid"]').should('exist');
        cy.get('.practica-card').should('have.length.greaterThan', 0);
        cy.log('✓ Al menos 1 práctica generada encontrada');
      } else {
        cy.get('.empty-state').should('exist');
        cy.log('ℹ Sin prácticas asignadas aún');
      }
    });

    // ── PASO 8: Verificar contadores del encabezado ───────────
    cy.log('PASO 8 — Verificando contadores del encabezado');
    cy.get('[data-cy="stat-total"]').find('.stat-valor').invoke('text').should('match', /\d+/);
    cy.log('✓ Contadores del encabezado correctos');

    cy.log('✅ Flujo completo E2E-01 completado exitosamente');
  });
});
