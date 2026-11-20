/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: '#2563EB',
        background: '#F8FAFC',
        surface: '#FFFFFF',
        mainText: '#0F172A',
        secondaryText: '#64748B',
        success: '#16A34A',
        warning: '#F59E0B',
        danger: '#DC2626',
        border: '#E2E8F0',
      },
    },
  },
  plugins: [],
};
