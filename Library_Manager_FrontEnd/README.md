# Library Manager Frontend

Aplicación frontend en React + TypeScript para gestionar usuarios, libros y préstamos, consumiendo la API REST de [Library Manager Backend](https://github.com/JulianLopez11/Library_Manager_Backend).

## Tecnologías

- React 19 + TypeScript
- Vite
- Fetch API (sin librerías externas de HTTP)
- CSS plano (sin frameworks de estilos)
- Docker / Docker Compose

## Organización del proyecto

```text
src/
├── api/          # Cliente fetch y URL base del backend
├── types/        # Tipos TypeScript compartidos
├── hooks/        # Hooks (useUsers, useBooks, useLoans, ...) que consumen la API
├── components/   # Componentes de UI (formularios, tablas, navbar) por entidad
├── pages/        # Páginas (Usuarios, Libros, Préstamos)
└── styles/       # Hojas de estilo CSS
```

## Variables de entorno

| Variable         | Descripción                                                            | Valor por defecto       |
|------------------|-------------------------------------------------------------------------|--------------------------|
| `API_URL`        | URL base del backend, alcanzable desde el navegador                    | `http://localhost:8080` |
| `FRONTEND_PORT`  | Puerto en el host donde queda expuesto el frontend (solo Docker)       | `5173`                   |

`API_URL` se agrrga en el build (Vite expone al cliente las variables con prefijo `API_`, configurado en `vite.config.ts`)

## Ejecución con Docker 

Requisitos: Docker y Docker Compose.

```bash
git clone https://github.com/JulianLopez11/Library_Manager_FrontEnd.git
cd Library_Manager_FrontEnd
cp .env.example .env
docker compose up -d --build
```

La aplicación queda disponible en `http://localhost:5173` (o en el puerto que definas en `FRONTEND_PORT`).

Antes de levantar el frontend, asegúrate de tener el backend corriendo y de que `API_URL` (en tu `.env`) apunte a él, por ejemplo `http://localhost:8080` si sigues las instrucciones del repositorio del backend.

## Desarrollo local (sin Docker)

Requisitos: Node.js 20+.

```bash
git clone https://github.com/JulianLopez11/Library_Manager_FrontEnd.git
cd Library_Manager_FrontEnd
cp .env.example .env
npm install
npm run dev
```

La aplicación queda disponible en `http://localhost:5173`.

## Funcionalidades

- **Usuarios**: listar, crear, editar y eliminar.
- **Libros**: listar, crear, editar, eliminar y agregar ejemplares físicos.
- **Préstamos**: registrar un préstamo, listar préstamos por usuario y por libro, y consultar ejemplares disponibles por ISBN.

## Autor

- [Julian Camilo Lopez Barrero](https://github.com/JulianLopez11)
