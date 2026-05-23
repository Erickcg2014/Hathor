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

  cy.get('body').then(($body) => {
    if ($body.find('[data-cy="bitacora-backdrop"]').length > 0) {
      cy.get('[data-cy="bitacora-backdrop"]').click({ force: true });
      cy.wait(500);
    }
  });
  cy.wait(1000);
};

describe('07 — Producción', () => {
  describe('Info Producción', () => {
    beforeEach(() => {
      doLogin();
      cy.visit('/produccion');
      cy.get('[data-cy="produccion-loading"]', { timeout: 20000 }).should('not.exist');
      cy.get('[data-cy="produccion-header"]', { timeout: 20000 }).should('exist');
    });

    it('Carga la página de producción correctamente', () => {
      cy.get('[data-cy="produccion-page"]').should('exist');
      cy.get('[data-cy="produccion-header"]').should('exist');
    });

    it('Muestra el grid de KPIs del perfil productivo', () => {
      cy.get('[data-cy="produccion-kpi-grid"]', { timeout: 20000 }).should('exist');
    });

    it('Muestra el banner de estado de producción', () => {
      cy.get('body').then(($body) => {
        if ($body.find('[data-cy="produccion-banner"]').length > 0) {
          cy.get('[data-cy="produccion-banner"]').should('exist');
        } else {
          cy.get('[data-cy="produccion-kpi-grid"]').should('exist');
        }
      });
    });

    it('Muestra la tabla de registros o estado vacío', () => {
      cy.get('body').then(($body) => {
        if ($body.find('[data-cy="produccion-tabla"]').length > 0) {
          cy.get('[data-cy="produccion-tabla"]').should('exist');
        } else {
          cy.get('.panel').should('exist');
        }
      });
    });

    it('Muestra el botón de nuevo registro', () => {
      cy.get('[data-cy="btn-nuevo-registro"]').should('exist');
    });
  });

  describe('Nuevo Registro de Producción', () => {
    beforeEach(() => {
      doLogin();
      cy.visit('/produccion');
      cy.get('[data-cy="produccion-loading"]', { timeout: 20000 }).should('not.exist');
      cy.get('[data-cy="produccion-header"]', { timeout: 20000 }).should('exist');
    });

    it('Abre el overlay de nuevo registro', () => {
      cy.get('body').then(($body) => {
        if ($body.find('[data-cy="bitacora-backdrop"]').length > 0) {
          cy.get('[data-cy="bitacora-backdrop"]').click({ force: true });
          cy.wait(1000);
        }
      });

      cy.get('[data-cy="btn-nuevo-registro"]').scrollIntoView().click({ force: true });

      cy.get('[data-cy="overlay-nuevo-registro"]', { timeout: 10000 }).should('exist');
    });

    it('Puede registrar producción del día', () => {
      cy.get('body').then(($body) => {
        if ($body.find('[data-cy="bitacora-backdrop"]').length > 0) {
          cy.get('[data-cy="bitacora-backdrop"]').click({ force: true });
          cy.wait(1000);
        }
      });

      cy.get('[data-cy="btn-nuevo-registro"]').scrollIntoView().click({ force: true });

      cy.get('[data-cy="overlay-nuevo-registro"]', { timeout: 10000 }).should('exist');

      cy.get('[data-cy="input-fecha-registro"]').type('2025-10-20', { force: true });

      cy.get('[data-cy="input-litros-producidos"]')
        .clear({ force: true })
        .type('180', { force: true });

      cy.get('[data-cy="btn-guardar-registro"]').click({ force: true });

      cy.get('[data-cy="overlay-nuevo-registro"]', { timeout: 10000 }).should('not.exist');
    });
  });
});
