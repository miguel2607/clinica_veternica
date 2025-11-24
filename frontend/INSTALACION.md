# Instrucciones de Instalación - Frontend

## Requisitos Previos

- Node.js 18+ instalado
- npm o yarn
- Backend Spring Boot corriendo en http://localhost:8080

## Pasos de Instalación

1. **Navegar a la carpeta frontend:**
   ```bash
   cd frontend
   ```

2. **Instalar dependencias:**
   ```bash
   npm install
   ```

3. **Iniciar el servidor de desarrollo:**
   ```bash
   npm run dev
   ```

4. **Abrir en el navegador:**
   ```
   http://localhost:3000
   ```

## Credenciales de Prueba

Usa las credenciales de tu usuario en el sistema para iniciar sesión.

## Estructura de Roles

- **ADMIN**: Acceso completo al sistema
- **VETERINARIO**: Gestión de citas e historias clínicas
- **RECEPCIONISTA**: Gestión de citas, propietarios y mascotas
- **AUXILIAR**: Gestión de inventario
- **PROPIETARIO**: Portal para ver mascotas y citas propias

## Notas

- El frontend está configurado para conectarse automáticamente al backend en `http://localhost:8080`
- Si el backend está en otro puerto, modifica `vite.config.js` y `src/services/api.js`
- El token JWT se almacena en localStorage y se envía automáticamente en todas las peticiones

