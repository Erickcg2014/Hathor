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

describe('06 — Finanzas', () => {
  // ── Info Finanzas ──────────────────────────────────────────
  describe('Info Finanzas', () => {
    beforeEach(() => {
      doLogin();
      cy.visit('/finanzas');
      cerrarBitacora();
      cy.get('[data-cy="finanzas-loading"]', { timeout: 20000 }).should('not.exist');
      cy.get('[data-cy="finanzas-header"]', { timeout: 20000 }).should('exist');
    });

    it('Carga la página correctamente', () => {
      cy.get('[data-cy="finanzas-header"]').should('exist');
    });

    it('Muestra el grid de KPIs financieros', () => {
      cy.get('[data-cy="finanzas-kpi-grid"]').should('exist');
      cy.get('[data-cy="kpi-registros-actuales"]').should('exist');
      cy.get('[data-cy="kpi-registros-historicos"]').should('exist');
      cy.get('[data-cy="kpi-total-ingresos"]').should('exist');
      cy.get('[data-cy="kpi-total-egresos"]').should('exist');
    });

    it('Muestra la tabla de registros o estado vacío', () => {
      cy.get('body').then(($body) => {
        if ($body.find('[data-cy="finanzas-tabla"]').length > 0) {
          cy.get('[data-cy="finanzas-tabla"]').should('exist');
        } else {
          cy.get('.empty-section').should('exist');
        }
      });
    });
  });

  // ── Registro Manual ────────────────────────────────────────
  describe('Registro Financiero Manual', () => {
    beforeEach(() => {
      doLogin();
      cy.visit('/finanzas/registro');
      cy.wait(2000);
      cerrarBitacora();
      cy.wait(1000);
      cy.get('[data-cy="modo-toggle"]', { timeout: 20000 }).should('exist');
    });

    it('Carga la página de registro correctamente', () => {
      cy.get('[data-cy="registro-header"]').should('exist');
      cy.get('[data-cy="modo-toggle"]').should('exist');
    });

    it('Muestra los botones de modo', () => {
      cy.get('[data-cy="btn-modo-manual"]').should('exist');
      cy.get('[data-cy="btn-modo-excel"]').should('exist');
    });

    it('Puede crear un registro financiero manual', () => {
      cy.get('[data-cy="btn-modo-manual"]').click({ force: true });
      cy.wait(500);
      cy.get('[data-cy="registro-card"]').should('exist');

      cy.get('[data-cy="input-titulo"]')
        .clear({ force: true })
        .type('Registro de prueba Cypress', { force: true });

      cy.get('[data-cy="btn-tipo-ingreso"]').click({ force: true });
      cy.wait(300);

      cy.get('select').first().select(1, { force: true });
      cy.wait(300);

      cy.get('[data-cy="input-fecha"]').type('2025-10-15', { force: true });

      cy.get('[data-cy="input-monto"]').clear({ force: true }).type('500000', { force: true });

      cy.get('[data-cy="btn-guardar"]').click({ force: true });

      cy.wait(1000);
      cy.get('body').then(($body) => {
        if ($body.find('.toast').length > 0) {
          cy.get('.toast').should('exist');
        } else {
          cy.get('[data-cy="input-titulo"]').invoke('val').should('eq', '');
        }
      });
    });
  });
});
