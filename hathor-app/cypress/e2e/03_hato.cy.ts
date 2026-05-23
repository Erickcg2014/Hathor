const usuarioValido = {
  email: 'loadtest_01@loadtest.com',
  password: 'LoadTest123!',
};

describe('03 — Hato', () => {
  beforeEach(() => {
    cy.visit('/login');
    cy.get('[data-cy="input-email"]').type(usuarioValido.email);
    cy.get('[data-cy="input-password"]').type(usuarioValido.password);
    cy.get('[data-cy="btn-login"]').click();
    cy.url().should('include', '/home');
    cy.visit('/hato');
    cy.get('[data-cy="hato-loading"]', { timeout: 15000 }).should('not.exist');
  });

  it('Carga la página del hato correctamente', () => {
    cy.get('[data-cy="hato-page"]').should('exist');
  });

  it('Muestra el nombre del hato', () => {
    cy.get('[data-cy="hato-nombre"]').should('exist');
    cy.get('[data-cy="hato-nombre"]').invoke('text').should('not.be.empty');
  });

  it('Muestra la barra de completitud', () => {
    cy.get('[data-cy="completitud-bar"]').should('exist');
    cy.get('[data-cy="completitud-pct"]').should('exist');
    cy.get('[data-cy="completitud-pct"]').invoke('text').should('include', '%');
  });

  it('Muestra todas las cards de información', () => {
    cy.get('[data-cy="info-grid"]').should('exist');
    cy.get('[data-cy="card-ubicacion"]').should('exist');
    cy.get('[data-cy="card-altitud"]').should('exist');
    cy.get('[data-cy="card-area"]').should('exist');
    cy.get('[data-cy="card-infraestructura"]').should('exist');
    cy.get('[data-cy="card-empleados"]').should('exist');
    cy.get('[data-cy="card-almacenamiento"]').should('exist');
  });

  it('Muestra el botón de nuevo hato', () => {
    cy.get('[data-cy="btn-nuevo-hato"]').should('exist');
  });
});
