describe('01 — Login', () => {
  beforeEach(() => {
    cy.fixture('usuarios').as('usuarios');
  });

  it('Muestra la página de login correctamente', () => {
    cy.visit('/login');
    cy.get('[data-cy="input-email"]').should('be.visible');
    cy.get('[data-cy="input-password"]').should('be.visible');
    cy.get('[data-cy="btn-login"]').should('be.visible');
  });

  it('Muestra error con credenciales incorrectas', () => {
    cy.visit('/login');
    cy.get('[data-cy="input-email"]').type('noexiste@test.com');
    cy.get('[data-cy="input-password"]').type('wrongpassword');
    cy.get('[data-cy="btn-login"]').click();
    cy.get('[data-cy="error-message"]').should('be.visible');
    cy.url().should('include', '/login');
  });

  it('Login exitoso redirige al home', () => {
    cy.get('@usuarios').then((u: any) => {
      cy.visit('/login');
      cy.get('[data-cy="input-email"]').type(u.usuarioValido.email);
      cy.get('[data-cy="input-password"]').type(u.usuarioValido.password);
      cy.get('[data-cy="btn-login"]').click();
      cy.url().should('include', '/home');
    });
  });

  it('Botón deshabilitado con formulario vacío', () => {
    cy.visit('/login');
    cy.get('[data-cy="btn-login"]').should('be.disabled');
  });

  it('Link de registro navega a /register', () => {
    cy.visit('/login');
    cy.get('[data-cy="link-register"]').click();
    cy.url().should('include', '/register');
  });
});
