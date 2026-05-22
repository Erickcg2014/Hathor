export {};

Cypress.Commands.add('login' as any, (...args: any[]) => {
  const [email, password] = args;
  cy.visit('/login');
  cy.get('[data-cy="input-email"]').clear().type(email);
  cy.get('[data-cy="input-password"]').clear().type(password);
  cy.get('[data-cy="btn-login"]').click();
  cy.url().should('include', '/home');
});
