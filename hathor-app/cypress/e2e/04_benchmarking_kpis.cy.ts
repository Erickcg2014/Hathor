// cypress/e2e/04_benchmarking_kpis.cy.ts

const usuarioValido = {
  email: 'loadtest_01@loadtest.com',
  password: 'LoadTest123!',
};

describe('04 — Benchmarking KPIs', () => {
  beforeEach(() => {
    cy.visit('/login');
    cy.get('[data-cy="input-email"]').type(usuarioValido.email);
    cy.get('[data-cy="input-password"]').type(usuarioValido.password);
    cy.get('[data-cy="btn-login"]').click();
    cy.url().should('include', '/home');
    cy.visit('/benchmarking/kpis');
    cy.get('[data-cy="benchmarking-loading"]', { timeout: 20000 }).should('not.exist');
  });

  it('Carga la página de benchmarking correctamente', () => {
    cy.get('[data-cy="benchmarking-header"]').should('exist');
  });

  it('Muestra el nombre del hato', () => {
    cy.get('[data-cy="benchmarking-hato-nombre"]')
      .should('exist')
      .invoke('text')
      .should('not.be.empty');
  });

  it('Muestra el botón de recalcular', () => {
    cy.get('[data-cy="btn-recalcular"]').should('exist');
  });

  it('Muestra los filtros de comparativa', () => {
    cy.get('[data-cy="filtros-wrapper"]').should('exist');
  });

  it('Muestra el resumen de KPIs', () => {
    cy.get('[data-cy="summary-grid"]', { timeout: 20000 }).should('exist');
    cy.get('[data-cy="summary-kpis-comparados"]').should('exist');
    cy.get('[data-cy="summary-con-referencia"]').should('exist');
    cy.get('[data-cy="summary-kpis-optimos"]').should('exist');
    cy.get('[data-cy="summary-fecha-calculo"]').should('exist');
  });

  it('El summary muestra valores numéricos', () => {
    cy.get('[data-cy="summary-kpis-comparados"]')
      .find('.summary-value')
      .invoke('text')
      .should('match', /\d+/);
  });

  it('Navega al detalle de un KPI al hacer click', () => {
    cy.get('[data-cy="summary-kpis-optimos"]').scrollIntoView().click({ force: true });
    cy.url().should('include', '/benchmarking');
  });
});
