# 🚀 Guía de Despliegue en Render

Esta guía te ayudará a desplegar la aplicación Clínica Veterinaria en Render.

## 📋 Requisitos Previos

- Cuenta en [Render.com](https://render.com)
- Repositorio Git (GitHub, GitLab, o Bitbucket)
- PostgreSQL (Render ofrece base de datos PostgreSQL)

## 🗄️ Paso 1: Crear Base de Datos PostgreSQL

1. En el dashboard de Render, ve a **"New +"** → **"PostgreSQL"**
2. Configura:
   - **Name**: `clinica-veterinaria-db`
   - **Database**: `clinica_veterinaria`
   - **User**: `postgres` (o el que prefieras)
   - **Region**: Elige la más cercana
   - **PostgreSQL Version**: 15
   - **Plan**: Free (para desarrollo) o Paid (para producción)

3. **Guarda las credenciales** que Render te proporciona:
   - `Internal Database URL` (para usar dentro de Render)
   - `External Database URL` (para conexiones externas)
   - Usuario y contraseña

## 🔧 Paso 2: Configurar Variables de Entorno en Render

### Variables para el Backend (Spring Boot)

Cuando crees el servicio de Web Service, configura estas variables:

#### Base de Datos

**Opción 1: Usar DATABASE_URL de Render (Recomendado)**
Render proporciona automáticamente la variable `DATABASE_URL` cuando conectas la base de datos al servicio. El código la parsea automáticamente.

**Ejemplo de URL de Render:**
```
postgresql://clinica_veterinaria_y9xc_user:HvBjTuaa0wYRQw0v71sMj07sQOCh4uAb@dpg-d4o6fbvpm1nc73fu78o0-a/clinica_veterinaria_y9xc
```

**Variables necesarias:**
```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=postgresql://usuario:contraseña@host:puerto/base_de_datos
```

**Nota**: Si conectas la base de datos usando "Link Resource" en Render, `DATABASE_URL` se agrega automáticamente. No necesitas configurarla manualmente.

**Opción 2: Configurar manualmente**
Si prefieres configurar manualmente:

```
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://[HOST]:[PORT]/clinica_veterinaria
DB_USERNAME=[USUARIO_POSTGRES]
DB_PASSWORD=[CONTRASEÑA_POSTGRES]
```

**⚠️ IMPORTANTE**: 
- Si usas `DATABASE_URL`, Render la proporciona automáticamente cuando conectas la base de datos
- El formato de `DATABASE_URL` es: `postgresql://user:password@host:port/database`
- El código convierte automáticamente `DATABASE_URL` al formato JDBC necesario

#### JWT
```
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=86400000
```

#### Email (Gmail)
```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu_email@gmail.com
MAIL_PASSWORD=tu_contraseña_de_aplicacion
MAIL_FROM_NAME=Clínica Veterinaria
```

#### Java/Spring Boot
```
JAVA_VERSION=21
SPRING_PROFILES_ACTIVE=prod
```

## 🚀 Paso 3: Desplegar el Backend

1. En Render, ve a **"New +"** → **"Web Service"**
2. Conecta tu repositorio Git
3. Configura:
   - **Name**: `clinica-veterinaria-backend`
   - **Environment**: `Docker`
   - **Region**: La misma que la base de datos
   - **Branch**: `main` (o la rama que uses)
   - **Root Directory**: `.` (raíz del proyecto)
   - **Dockerfile Path**: `Dockerfile`
   - **Docker Context**: `.`

4. **Build Command**: (no necesario si usas Dockerfile)
5. **Start Command**: (no necesario, está en Dockerfile)

6. **Variables de Entorno**: Agrega todas las variables del Paso 2

7. **Health Check Path**: `/swagger-ui.html` o `/actuator/health`

## 🎨 Paso 4: Desplegar el Frontend

1. En Render, ve a **"New +"** → **"Static Site"**
2. Conecta tu repositorio Git
3. Configura:
   - **Name**: `clinica-veterinaria-frontend`
   - **Build Command**: `cd frontend && npm ci && npm run build`
   - **Publish Directory**: `frontend/dist`
   - **Environment**: `Node`

4. **Variables de Entorno**:
   ```
   VITE_API_URL=https://clinica-veterinaria-backend.onrender.com/api
   ```
   (Reemplaza con la URL real de tu backend)

## 📝 Paso 5: Configurar Nginx para Frontend (Opcional)

Si prefieres usar un Web Service para el frontend con Nginx:

1. Crea un **Web Service** en lugar de Static Site
2. Usa el `Dockerfile.frontend`
3. Configura:
   - **Dockerfile Path**: `Dockerfile.frontend`
   - **Variables de Entorno**:
     ```
     VITE_API_URL=https://clinica-veterinaria-backend.onrender.com/api
     ```

## 🔗 Paso 6: Configurar URLs

### Backend
- Render te dará una URL como: `https://clinica-veterinaria-backend.onrender.com`
- La API estará en: `https://clinica-veterinaria-backend.onrender.com/api`

### Frontend
- Si usas Static Site: `https://clinica-veterinaria-frontend.onrender.com`
- Si usas Web Service: `https://clinica-veterinaria-frontend.onrender.com`

### Actualizar Frontend
En el frontend, actualiza `vite.config.js` o las variables de entorno para apuntar a la URL del backend en producción.

## ⚙️ Configuración Adicional

### Health Checks
Render puede configurar health checks automáticamente. Asegúrate de que tu aplicación tenga un endpoint de health:
- `/actuator/health` (si tienes Spring Boot Actuator)
- `/swagger-ui.html` (como fallback)

### Auto-Deploy
Render puede hacer auto-deploy cuando haces push a la rama principal. Actívalo en la configuración del servicio.

### Variables de Entorno Sensibles
⚠️ **NUNCA** subas el archivo `.env` al repositorio. Render tiene su propia interfaz para variables de entorno seguras.

## 🔐 Seguridad

1. **JWT_SECRET**: Usa un secreto fuerte y único en producción
2. **DB_PASSWORD**: Render lo maneja automáticamente si usas su PostgreSQL
3. **MAIL_PASSWORD**: Usa contraseñas de aplicación de Gmail, no tu contraseña normal

## 📊 Monitoreo

Render proporciona:
- Logs en tiempo real
- Métricas de uso
- Alertas de salud

## 🆘 Solución de Problemas

### El backend no se conecta a la base de datos

**Error**: `Unable to determine Dialect without JDBC metadata`

**Solución**:
1. **Verifica que la base de datos esté conectada**:
   - En el dashboard de Render, ve a tu servicio backend
   - En la sección "Addons", verifica que la base de datos esté "Linked"
   - Si no está conectada, haz clic en "Link Resource" y selecciona tu base de datos

2. **Verifica la variable DATABASE_URL**:
   - En "Environment", busca `DATABASE_URL`
   - Debe tener formato: `postgresql://user:password@host:port/database`
   - Si no existe, reconecta la base de datos

3. **Si usas variables manuales**, verifica:
   - `DB_URL` debe tener formato: `jdbc:postgresql://host:port/database`
   - `DB_USERNAME` y `DB_PASSWORD` deben ser correctos

4. **Verifica que la base de datos esté "Available"**:
   - La base de datos debe estar en estado "Available" (no "Paused")
   - Si está pausada, reactívala

### El frontend no se conecta al backend
- Verifica que `VITE_API_URL` apunte a la URL correcta del backend
- Verifica CORS en el backend (debe permitir el dominio de Render)

### Build falla
- Verifica los logs de build en Render
- Asegúrate de que el Dockerfile esté en la raíz
- Verifica que todas las dependencias estén en `pom.xml` y `package.json`

## 📚 Recursos

- [Documentación de Render](https://render.com/docs)
- [Render PostgreSQL](https://render.com/docs/databases)
- [Render Web Services](https://render.com/docs/web-services)

