import './commands';

Cypress.on('uncaught:exception', (err) => {
  if (
    err.message.includes('Failed to fetch dynamically imported module') ||
    err.message.includes('Importing a module script failed') ||
    err.message.includes('@ng/component') ||
    err.message.includes("Cannot read properties of undefined (reading 'frame')")
  ) {
    return false;
  }
  return true;
});
