# Guía de Despliegue en Fly.io

## Prerequisitos

1. Instalar Fly CLI:
   ```powershell
   # Windows PowerShell
   iwr https://fly.io/install.ps1 -useb | iex
   ```

2. Crear cuenta en Fly.io:
   ```bash
   fly auth signup
   ```

3. Iniciar sesión:
   ```bash
   fly auth login
   ```

---

## Paso 1: Crear Base de Datos MySQL

Fly.io no ofrece MySQL directamente, pero puedes usar **PlanetScale** (gratis) o crear un servicio MySQL con Docker.

### Opción A: Usar PlanetScale (Recomendado - Gratis)

1. Ve a https://planetscale.com
2. Crea una cuenta gratuita
3. Crea un proyecto: `clinica-veterinaria`
4. Crea una base de datos: `clinica_veterinaria`
5. Copia las credenciales de conexión

### Opción B: MySQL en Fly.io (Requiere configuración manual)

Puedes crear un servicio MySQL usando una imagen Docker, pero es más complejo.

---

## Paso 2: Desplegar Backend

1. **Navega a la raíz del proyecto**:
   ```bash
   cd C:\Users\MIGUEL\IdeaProjects\clinica_veternica
   ```

2. **Inicializa la app de Fly.io**:
   ```bash
   fly launch --config fly.backend.toml --name clinica-backend
   ```
   
   Cuando te pregunte:
   - **App name**: `clinica-backend` (o déjalo como está)
   - **Region**: `ord` (Chicago) o elige el más cercano
   - **Postgres**: No (estamos usando MySQL externo)
   - **Redis**: No

3. **Configura las variables de entorno**:
   ```bash
   fly secrets set -a clinica-backend \
     SPRING_PROFILES_ACTIVE=docker \
     DB_URL="jdbc:mysql://tu-host.planetscale.com:3306/clinica_veterinaria?useSSL=true&serverTimezone=UTC" \
     DB_USERNAME=tu_usuario \
     DB_PASSWORD=tu_password \
     JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970 \
     JWT_EXPIRATION=86400000 \
     MAIL_HOST=smtp.gmail.com \
     MAIL_PORT=587 \
     MAIL_USERNAME=miguelitorodriguezaranzazu@gmail.com \
     MAIL_PASSWORD=asjkgbztsxkosydo \
     MAIL_FROM_NAME="Clínica Veterinaria"
   ```

   **NOTA**: Reemplaza `tu-host.planetscale.com`, `tu_usuario` y `tu_password` con los valores reales de PlanetScale.

4. **Despliega**:
   ```bash
   fly deploy -c fly.backend.toml
   ```

5. **Obtén la URL del backend**:
   ```bash
   fly status -a clinica-backend
   ```
   
   La URL será algo como: `https://clinica-backend.fly.dev`

---

## Paso 3: Desplegar Frontend

1. **Actualiza la URL del backend en `fly.frontend.toml`**:
   
   Edita `fly.frontend.toml` y cambia:
   ```toml
   build_args = { VITE_API_URL = "https://TU-BACKEND-URL.fly.dev/api" }
   ```
   
   Reemplaza `TU-BACKEND-URL` con la URL real de tu backend.

2. **Inicializa la app de Fly.io para el frontend**:
   ```bash
   fly launch --config fly.frontend.toml --name clinica-frontend
   ```
   
   Cuando te pregunte:
   - **App name**: `clinica-frontend` (o déjalo como está)
   - **Region**: `ord` (mismo que el backend)
   - **Postgres**: No
   - **Redis**: No

3. **Despliega**:
   ```bash
   fly deploy -c fly.frontend.toml
   ```

4. **Obtén la URL del frontend**:
   ```bash
   fly status -a clinica-frontend
   ```
   
   La URL será algo como: `https://clinica-frontend.fly.dev`

---

## Comandos Útiles

### Ver logs:
```bash
# Backend
fly logs -a clinica-backend

# Frontend
fly logs -a clinica-frontend
```

### Ver estado:
```bash
# Backend
fly status -a clinica-backend

# Frontend
fly status -a clinica-frontend
```

### Ver variables de entorno:
```bash
fly secrets list -a clinica-backend
```

### Actualizar variables de entorno:
```bash
fly secrets set -a clinica-backend NUEVA_VAR=valor
```

### Reiniciar servicios:
```bash
fly apps restart -a clinica-backend
fly apps restart -a clinica-frontend
```

### Abrir SSH:
```bash
fly ssh console -a clinica-backend
```

---

## Troubleshooting

### Error: "No se pudo encontrar un Dockerfile"
- Asegúrate de estar en la raíz del proyecto
- Verifica que `Dockerfile.backend` y `Dockerfile.frontend` existan
- Usa `fly deploy -c fly.backend.toml` para especificar el archivo de configuración

### Backend no conecta a MySQL:
- Verifica que la URL de conexión sea correcta
- Asegúrate de usar `useSSL=true` para PlanetScale
- Revisa los logs: `fly logs -a clinica-backend`

### Frontend no conecta al Backend:
- Verifica que `VITE_API_URL` en `fly.frontend.toml` sea correcta
- Asegúrate de que el backend esté funcionando
- Revisa la configuración de CORS en el backend

### Error de build:
- Revisa los logs: `fly logs -a clinica-backend`
- Verifica que todas las dependencias estén en `pom.xml`
- Asegúrate de que el Dockerfile esté correcto

---

## URLs Finales

Después del despliegue:
- **Frontend**: `https://clinica-frontend.fly.dev`
- **Backend**: `https://clinica-backend.fly.dev`
- **API**: `https://clinica-backend.fly.dev/api`

---

## Notas Importantes

1. **Plan gratuito**: Fly.io ofrece un plan gratuito generoso, pero revisa los límites en https://fly.io/docs/about/pricing/

2. **Base de datos**: Recomendamos usar PlanetScale para MySQL (gratis y fácil de configurar)

3. **Variables de entorno**: Usa `fly secrets set` para configurar variables sensibles (no se muestran en los logs)

4. **Auto-deploy**: Fly.io no hace auto-deploy automático desde GitHub. Necesitas ejecutar `fly deploy` manualmente o configurar CI/CD

5. **Regiones**: Elige la región más cercana a tus usuarios para mejor rendimiento

