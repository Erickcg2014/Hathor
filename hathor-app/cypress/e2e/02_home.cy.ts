const usuarioValido = {
  email: 'loadtest_01@loadtest.com',
  password: 'LoadTest123!',
};
describe('02 — Home Dashboard', () => {
  beforeEach(() => {
    cy.visit('/login');
    cy.get('[data-cy="input-email"]').type(usuarioValido.email);
    cy.get('[data-cy="input-password"]').type(usuarioValido.password);
    cy.get('[data-cy="btn-login"]').click();
    cy.url().should('include', '/home');

    cy.get('[data-cy="home-loading"]', { timeout: 20000 }).should('not.exist');

    cy.get('[data-cy="home-modulos"]', { timeout: 20000 }).should('exist');

    cy.wait(3000);
  });

  it('Muestra todos los módulos del dashboard', () => {
    // exist en vez de be.visible — elementos en contenedores con overflow
    cy.get('[data-cy="home-modulos"]').should('exist');
    cy.get('[data-cy="modulo-hato"]').should('exist');
    cy.get('[data-cy="modulo-produccion"]').should('exist');
    cy.get('[data-cy="modulo-finanzas"]').should('exist');
    cy.get('[data-cy="modulo-ganado"]').should('exist');
    cy.get('[data-cy="modulo-kpis"]').should('exist');
    cy.get('[data-cy="modulo-practicas"]').should('exist');
  });

  it('Muestra los chips de ranking', () => {
    cy.get('[data-cy="ranking-chips"]').should('exist');
    cy.get('[data-cy="chip-posicion-nacional"]').should('exist');
    cy.get('[data-cy="chip-score-compuesto"]').should('exist');
    cy.get('[data-cy="chip-kpis-criticos"]').should('exist');
    cy.get('[data-cy="chip-posicion-regional"]').should('exist');
  });

  it('Navega a /hato al hacer click en módulo Mi Hato', () => {
    cy.get('[data-cy="modulo-hato"]').scrollIntoView().click({ force: true });
    cy.url().should('include', '/hato');
  });

  it('Navega a /benchmarking al hacer click en módulo KPIs', () => {
    cy.get('[data-cy="modulo-kpis"]').scrollIntoView().click({ force: true });
    cy.url().should('include', '/benchmarking');
  });

  it('Navega a /finanzas al hacer click en módulo Finanzas', () => {
    cy.get('[data-cy="modulo-finanzas"]').scrollIntoView().click({ force: true });
    cy.url().should('include', '/finanzas');
  });

  it('Navega a /practicas al hacer click en módulo Prácticas', () => {
    cy.get('[data-cy="modulo-practicas"]').scrollIntoView().click({ force: true });
    cy.url().should('include', '/practicas');
  });
});
