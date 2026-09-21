import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  // Por defecto Vite solo expone al cliente las variables con prefijo VITE_;
  // se cambia a API_ para poder usar API_URL sin ese prefijo.
  envPrefix: 'API_',
})
