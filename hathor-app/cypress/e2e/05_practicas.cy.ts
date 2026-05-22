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
  cy.wait(2000);
};

describe('05 — Prácticas', () => {
  // ── Mis Prácticas ──────────────────────────────────────────
  describe('Mis Prácticas', () => {
    beforeEach(() => {
      doLogin();
      cy.visit('/practicas/mis-practicas');
      cy.get('[data-cy="mis-practicas-loading"]', { timeout: 20000 }).should('not.exist');
      cy.get('[data-cy="mis-practicas-header"]', { timeout: 20000 }).should('exist');
    });

    it('Carga la página correctamente', () => {
      cy.get('[data-cy="mis-practicas-header"]').should('exist');
    });

    it('Muestra las estadísticas de prácticas', () => {
      cy.get('[data-cy="mis-practicas-stats"]', { timeout: 20000 }).should('exist');
    });

    it('Muestra el grid de prácticas o estado vacío', () => {
      cy.get('body').then(($body) => {
        if ($body.find('[data-cy="practicas-grid"]').length > 0) {
          cy.get('[data-cy="practicas-grid"]').should('exist');
        } else {
          cy.get('.empty-state').should('exist');
        }
      });
    });
  });

  // ── Prácticas Sugeridas ────────────────────────────────────
  describe('Prácticas Sugeridas', () => {
    beforeEach(() => {
      doLogin();
      cy.visit('/practicas/sugeridas');
      cy.get('[data-cy="mis-practicas-loading"]', { timeout: 20000 }).should('not.exist');
      cy.get('[data-cy="mis-practicas-header"]', { timeout: 20000 }).should('exist');
    });

    it('Carga la página correctamente', () => {
      cy.get('[data-cy="mis-practicas-header"]').should('exist');
    });

    it('Muestra estadísticas completas', () => {
      cy.get('[data-cy="mis-practicas-stats"]', { timeout: 20000 }).should('exist');
      cy.get('[data-cy="stat-total"]').should('exist');
      cy.get('[data-cy="stat-pendientes"]').should('exist');
      cy.get('[data-cy="stat-en-curso"]').should('exist');
      cy.get('[data-cy="stat-completadas"]').should('exist');
    });

    it('Muestra el grid de prácticas o estado vacío', () => {
      cy.get('body').then(($body) => {
        if ($body.find('[data-cy="practicas-grid"]').length > 0) {
          cy.get('[data-cy="practicas-grid"]').should('exist');
        } else {
          cy.get('.empty-state').should('exist');
        }
      });
    });
  });

  // ── Recomendaciones ────────────────────────────────────────
  describe('Recomendaciones', () => {
    beforeEach(() => {
      doLogin();
      cy.visit('/practicas/recomendaciones');
      cy.get('[data-cy="recomendaciones-loading"]', { timeout: 20000 }).should('not.exist');
      cy.get('[data-cy="recomendaciones-header"]', { timeout: 20000 }).should('exist');
    });

    it('Carga la página correctamente', () => {
      cy.get('[data-cy="recomendaciones-header"]').should('exist');
    });

    it('Muestra el contador de no leídas', () => {
      cy.get('[data-cy="recomendaciones-no-leidas"]')
        .should('exist')
        .invoke('text')
        .should('match', /\d+/);
    });

    it('Muestra recomendaciones o estado vacío', () => {
      cy.get('body').then(($body) => {
        if ($body.find('[data-cy="recomendaciones-grid"]').length > 0) {
          cy.get('[data-cy="recomendaciones-grid"]').should('exist');
          cy.get('[data-cy="reco-card"]').should('have.length.greaterThan', 0);
        } else {
          cy.get('[data-cy="recomendaciones-empty"]').should('exist');
        }
      });
    });

    it('Cada recomendación tiene botón de marcar leída', () => {
      cy.get('body').then(($body) => {
        if ($body.find('[data-cy="reco-card"]').length > 0) {
          cy.get('[data-cy="reco-card"]').first().find('.btn-leida').should('exist');
        }
      });
    });
  });
});
