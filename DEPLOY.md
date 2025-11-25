# Guía de Despliegue Gratuito - Clínica Veterinaria

Esta guía te ayudará a desplegar tu aplicación de forma completamente gratuita.

## Opciones Gratuitas Recomendadas

### 🥇 Opción 1: Render (Recomendado - Más Fácil)

**Ventajas:**
- ✅ Plan gratuito generoso
- ✅ MySQL gratuito incluido
- ✅ Despliegue automático desde GitHub
- ✅ SSL/HTTPS gratuito
- ✅ Sin tarjeta de crédito requerida

**Límites del plan gratuito:**
- Servicios web: Se duermen después de 15 minutos de inactividad
- Base de datos: 90 días de datos persistentes
- 750 horas/mes de tiempo de ejecución

#### Pasos para desplegar en Render:

##### 1. Preparar el repositorio

Asegúrate de que tu código esté en GitHub:
```bash
git add .
git commit -m "Preparar para despliegue"
git push origin main
```

##### 2. Crear Base de Datos MySQL en Render

1. Ve a [render.com](https://render.com) y crea una cuenta
2. Click en **"New +"** → **"PostgreSQL"** (Render no tiene MySQL gratis, usa PostgreSQL o crea MySQL en otro servicio)
3. O mejor: Usa **PlanetScale** (MySQL gratuito) o **Supabase** (PostgreSQL gratuito)

**Alternativa: Usar PlanetScale para MySQL (Gratis):**
1. Ve a [planetscale.com](https://planetscale.com)
2. Crea una cuenta gratuita
3. Crea una nueva base de datos
4. Obtén las credenciales de conexión

##### 3. Desplegar Backend en Render

1. En Render, click **"New +"** → **"Web Service"**
2. Conecta tu repositorio de GitHub
3. Configuración:
   - **Name**: `clinica-backend`
   - **Environment**: `Docker`
   - **Build Command**: (dejar vacío, Render detecta Dockerfile)
   - **Start Command**: (dejar vacío)
   - **Dockerfile Path**: `Dockerfile.backend`

4. **Variables de Entorno**:
   ```
   SPRING_PROFILES_ACTIVE=production
   DB_URL=jdbc:mysql://TU_HOST:3306/TU_DATABASE?useSSL=true&serverTimezone=UTC
   DB_USERNAME=tu_usuario
   DB_PASSWORD=tu_password
   JWT_SECRET=tu_jwt_secret_muy_largo
   JWT_EXPIRATION=86400000
   ```

5. Click **"Create Web Service"**

##### 4. Desplegar Frontend en Render

1. En Render, click **"New +"** → **"Static Site"**
2. Conecta tu repositorio
3. Configuración:
   - **Name**: `clinica-frontend`
   - **Build Command**: `cd frontend && npm install && npm run build`
   - **Publish Directory**: `frontend/dist`
   - **Environment Variables**:
     ```
     VITE_API_URL=https://tu-backend-url.onrender.com/api
     ```

4. Click **"Create Static Site"**

---

### 🥈 Opción 2: Railway (Muy Fácil)

**Ventajas:**
- ✅ $5 de créditos gratis/mes
- ✅ Despliegue automático desde GitHub
- ✅ MySQL incluido
- ✅ SSL gratuito

**Pasos:**

1. Ve a [railway.app](https://railway.app)
2. Crea cuenta con GitHub
3. Click **"New Project"** → **"Deploy from GitHub repo"**
4. Selecciona tu repositorio
5. Railway detectará automáticamente los servicios Docker
6. Agrega una base de datos MySQL desde el panel
7. Configura las variables de entorno

---

### 🥉 Opción 3: Fly.io (Para usuarios avanzados)

**Ventajas:**
- ✅ Plan gratuito generoso
- ✅ Múltiples regiones
- ✅ Excelente para Docker

**Pasos:**

1. Instala Fly CLI: `curl -L https://fly.io/install.sh | sh`
2. Login: `fly auth login`
3. Crea app: `fly launch`
4. Despliega: `fly deploy`

---

### 🔄 Opción 4: Separar Frontend y Backend

**Frontend (Gratis):**
- **Vercel**: [vercel.com](https://vercel.com) - Excelente para React
- **Netlify**: [netlify.com](https://netlify.com) - Muy fácil de usar

**Backend (Gratis):**
- **Render**: Web Service gratuito
- **Railway**: $5 créditos/mes

**Base de Datos (Gratis):**
- **PlanetScale**: MySQL gratuito - [planetscale.com](https://planetscale.com)
- **Supabase**: PostgreSQL gratuito - [supabase.com](https://supabase.com)
- **MongoDB Atlas**: MongoDB gratuito - [mongodb.com/cloud/atlas](https://mongodb.com/cloud/atlas)

---

## Configuración Necesaria para Despliegue

### 1. Ajustar Dockerfile para Producción

El `Dockerfile.backend` ya está configurado, pero asegúrate de que use variables de entorno.

### 2. Variables de Entorno Requeridas

Crea un archivo `.env.production` o configura en la plataforma:

```env
# Base de Datos
DB_URL=jdbc:mysql://host:puerto/database?useSSL=true&serverTimezone=UTC
DB_USERNAME=usuario
DB_PASSWORD=password

# JWT
JWT_SECRET=clave_secreta_muy_larga_y_segura
JWT_EXPIRATION=86400000

# Spring Profile
SPRING_PROFILES_ACTIVE=production
```

### 3. Configurar CORS en Backend

Si separas frontend y backend, asegúrate de configurar CORS en Spring Boot:

```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("https://tu-frontend.vercel.app", "https://tu-frontend.netlify.app")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true);
            }
        };
    }
}
```

### 4. Actualizar API URL en Frontend

En `frontend/src/services/api.js`, asegúrate de usar la variable de entorno:

```javascript
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
```

---

## Recomendación Final

**Para empezar rápido:** Usa **Render** para backend y frontend, y **PlanetScale** para MySQL.

**Para mejor rendimiento:** Usa **Vercel** para frontend, **Railway** para backend, y **PlanetScale** para MySQL.

---

## Troubleshooting

### El backend no se conecta a la base de datos
- Verifica que la URL de conexión sea correcta
- Asegúrate de que la base de datos permita conexiones externas
- Verifica las credenciales

### El frontend no se conecta al backend
- Verifica la variable `VITE_API_URL`
- Configura CORS en el backend
- Verifica que el backend esté accesible públicamente

### Error de build en Render/Railway
- Revisa los logs de build
- Verifica que los Dockerfiles estén en la raíz
- Asegúrate de que todas las dependencias estén en los archivos de configuración

---

## Recursos Útiles

- [Render Docs](https://render.com/docs)
- [Railway Docs](https://docs.railway.app)
- [Vercel Docs](https://vercel.com/docs)
- [PlanetScale Docs](https://planetscale.com/docs)

