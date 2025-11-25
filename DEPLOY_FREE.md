# Guía de Despliegue Gratuito con MySQL

## Opciones de Hosting Gratuito con MySQL

### 🚀 Opción 1: Railway (RECOMENDADO)
**Ventajas:**
- ✅ Soporta MySQL nativo
- ✅ $5 de crédito gratis mensual (suficiente para proyectos pequeños)
- ✅ Soporta Docker
- ✅ Despliegue automático desde GitHub
- ✅ Base de datos MySQL incluida

**Desventajas:**
- Después de $5, cobra por uso
- Los servicios se duermen después de inactividad

**URL**: https://railway.app

---

### 🚀 Opción 2: Fly.io
**Ventajas:**
- ✅ Plan gratuito generoso
- ✅ Soporta MySQL (usando imagen Docker)
- ✅ Soporta Docker nativo
- ✅ Despliegue automático desde GitHub
- ✅ No se duerme automáticamente

**Desventajas:**
- Configuración un poco más compleja
- Requiere tarjeta de crédito (pero no cobra en el plan gratuito)

**URL**: https://fly.io

---

### 🚀 Opción 3: PlanetScale (Solo Base de Datos)
**Ventajas:**
- ✅ MySQL serverless gratuito
- ✅ Escalable automáticamente
- ✅ Plan gratuito generoso
- ✅ No requiere mantenimiento

**Desventajas:**
- Solo base de datos (necesitas otro servicio para backend/frontend)
- Algunas limitaciones en el plan gratuito

**URL**: https://planetscale.com

**Combinación recomendada**: PlanetScale (BD) + Render (Backend/Frontend)

---

## 🎯 Guía Completa: Railway (Opción Recomendada)

### Paso 1: Crear Cuenta en Railway

1. Ve a https://railway.app
2. Haz clic en **"Start a New Project"**
3. Conecta tu cuenta de GitHub
4. Autoriza Railway para acceder a tus repositorios

---

### Paso 2: Crear Base de Datos MySQL

1. En el dashboard de Railway, haz clic en **"New Project"**
2. Selecciona **"Provision MySQL"**
3. Configuración:
   - **Name**: `clinica-veterinaria-db`
   - Railway creará automáticamente las variables de entorno
4. Haz clic en **"Add"**
5. **IMPORTANTE**: Copia las variables de entorno que Railway genera:
   - `MYSQLDATABASE`
   - `MYSQLUSER`
   - `MYSQLPASSWORD`
   - `MYSQLHOST`
   - `MYSQLPORT`

---

### Paso 3: Crear Servicio Backend

1. En el mismo proyecto, haz clic en **"New"** → **"GitHub Repo"**
2. Selecciona tu repositorio `clinica_veternica`
3. Railway detectará automáticamente que es un proyecto con Docker
4. Configuración:
   - **Name**: `clinica-backend`
   - **Root Directory**: (dejar vacío)
   - **Dockerfile Path**: `Dockerfile.backend`
   - **Watch Paths**: (dejar vacío)

### Variables de Entorno del Backend:

Agrega estas variables de entorno en Railway (Settings → Variables):

```
SPRING_PROFILES_ACTIVE=docker
DB_URL=jdbc:mysql://${MYSQLHOST}:${MYSQLPORT}/${MYSQLDATABASE}?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=${MYSQLUSER}
DB_PASSWORD=${MYSQLPASSWORD}
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=86400000
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=miguelitorodriguezaranzazu@gmail.com
MAIL_PASSWORD=asjkgbztsxkosydo
MAIL_FROM_NAME=Clínica Veterinaria
```

**NOTA**: Railway permite usar variables de referencia como `${MYSQLHOST}` que se resuelven automáticamente.

### Configurar Puerto:

En Settings → Networking:
- **Port**: `8080`
- Railway generará automáticamente una URL pública

---

### Paso 4: Crear Servicio Frontend

1. En el mismo proyecto, haz clic en **"New"** → **"GitHub Repo"**
2. Selecciona el mismo repositorio `clinica_veternica`
3. Configuración:
   - **Name**: `clinica-frontend`
   - **Root Directory**: (dejar vacío)
   - **Dockerfile Path**: `Dockerfile.frontend`
   - **Watch Paths**: (dejar vacío)

### Variables de Entorno del Frontend:

Agrega esta variable de entorno:

```
VITE_API_URL=https://tu-backend-url.railway.app/api
```

**NOTA**: Reemplaza `tu-backend-url.railway.app` con la URL real de tu servicio backend en Railway.

### Configurar Puerto:

En Settings → Networking:
- **Port**: `80`
- Railway generará automáticamente una URL pública

---

## 🎯 Guía Alternativa: Fly.io

### Paso 1: Instalar Fly CLI

```bash
# Windows (PowerShell)
iwr https://fly.io/install.ps1 -useb | iex

# O descarga desde: https://fly.io/docs/hands-on/install-flyctl/
```

### Paso 2: Crear Cuenta

```bash
fly auth signup
```

### Paso 3: Crear Base de Datos MySQL

```bash
fly postgres create --name clinica-veterinaria-db --region ord
```

**NOTA**: Fly.io usa PostgreSQL por defecto. Para MySQL, necesitas usar una imagen Docker personalizada o usar PlanetScale.

### Paso 4: Desplegar Backend

1. Crea un archivo `fly.toml` en la raíz del proyecto:

```toml
app = "clinica-backend"
primary_region = "ord"

[build]
  dockerfile = "Dockerfile.backend"

[env]
  SPRING_PROFILES_ACTIVE = "docker"
  PORT = "8080"

[[services]]
  internal_port = 8080
  protocol = "tcp"

  [[services.ports]]
    port = 80
    handlers = ["http"]
    force_https = true

  [[services.ports]]
    port = 443
    handlers = ["tls", "http"]
```

2. Despliega:

```bash
fly deploy
```

---

## 🎯 Guía Alternativa: PlanetScale + Render

### Paso 1: Crear Base de Datos en PlanetScale

1. Ve a https://planetscale.com
2. Crea una cuenta gratuita
3. Crea un nuevo proyecto: `clinica-veterinaria`
4. Crea una base de datos: `clinica_veterinaria`
5. Copia las credenciales de conexión

### Paso 2: Desplegar Backend en Render

Sigue la guía de Render pero usa las credenciales de PlanetScale en lugar de PostgreSQL.

**Variables de Entorno en Render:**

```
DB_URL=jdbc:mysql://tu-host.planetscale.com:3306/clinica_veterinaria?useSSL=true&serverTimezone=UTC
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_password
```

---

## 📝 Notas Importantes

### Railway:
- ✅ Mejor opción para empezar
- ✅ Soporta MySQL nativo
- ✅ Fácil de configurar
- ⚠️ $5 gratis/mes, luego cobra por uso

### Fly.io:
- ✅ Plan gratuito generoso
- ✅ No se duerme
- ⚠️ Configuración más compleja
- ⚠️ Requiere CLI

### PlanetScale:
- ✅ MySQL serverless excelente
- ✅ Plan gratuito generoso
- ⚠️ Solo base de datos (necesitas otro servicio)

---

## 🔧 Troubleshooting

### Backend no conecta a MySQL:
- Verifica que las variables de entorno estén correctas
- Asegúrate de que la URL de conexión use SSL (`useSSL=true`)
- Revisa los logs en Railway/Fly.io

### Frontend no conecta al Backend:
- Verifica que `VITE_API_URL` apunte a la URL correcta
- Asegúrate de que el backend esté funcionando
- Revisa la configuración de CORS en el backend

### Servicios se duermen:
- Railway: Los servicios se duermen después de inactividad (normal en plan gratuito)
- Fly.io: No se duerme automáticamente
- Render: Se duerme después de 15 minutos

---

## 🚀 Recomendación Final

**Para empezar rápido**: Usa **Railway**
- Más fácil de configurar
- Soporta MySQL nativo
- $5 gratis al mes es suficiente para desarrollo/pruebas

**Para producción**: Considera **Fly.io** o **PlanetScale + Render**
- Más recursos gratuitos
- Mejor rendimiento
- Más escalable

