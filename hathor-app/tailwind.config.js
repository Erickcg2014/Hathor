/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        hathor: {
          green: '#BFF102',
          text: '#333333',
          placeholder: '#CCCCCC',
        }
      },
      fontSize: {
        'display': ['3.5rem', { lineHeight: '1.2' }],
        'headline': ['2.5rem', { lineHeight: '1.3' }],
      }
    },
  },
  plugins: [],
}
