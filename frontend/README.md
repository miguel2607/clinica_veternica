# Frontend - Clínica Veterinaria

Frontend desarrollado con React + Vite + Tailwind CSS que consume todas las APIs del backend.

## 🚀 Instalación

```bash
cd frontend
npm install
```

## 🏃 Ejecutar en Desarrollo

```bash
npm run dev
```

La aplicación estará disponible en: http://localhost:3000

## 📦 Build para Producción

```bash
npm run build
```

## 🎨 Características

- ✅ React 18 con Vite
- ✅ Tailwind CSS para estilos
- ✅ React Router para navegación
- ✅ Context API para autenticación
- ✅ Axios para llamadas API
- ✅ Diseño responsive
- ✅ Diferentes perspectivas según rol:
  - **Administrador**: Gestión completa del sistema
  - **Veterinario**: Citas y historias clínicas
  - **Recepcionista**: Gestión de citas, propietarios y mascotas
  - **Auxiliar**: Gestión de inventario
  - **Propietario**: Portal para ver sus mascotas y citas

## 📁 Estructura

```
frontend/
├── src/
│   ├── components/      # Componentes reutilizables
│   ├── context/         # Context API (Auth)
│   ├── layouts/         # Layouts por rol
│   ├── pages/           # Páginas de la aplicación
│   │   ├── admin/       # Páginas de administrador
│   │   ├── veterinario/ # Páginas de veterinario
│   │   ├── recepcionista/ # Páginas de recepcionista
│   │   ├── auxiliar/    # Páginas de auxiliar
│   │   └── propietario/ # Páginas de propietario
│   ├── services/        # Servicios API
│   └── App.jsx          # Componente principal
```

## 🔐 Autenticación

El sistema usa JWT tokens almacenados en localStorage. El token se envía automáticamente en todas las peticiones.

## 🌐 API Base URL

Por defecto: `http://localhost:8080/api`

Configurado en: `src/services/api.js`

