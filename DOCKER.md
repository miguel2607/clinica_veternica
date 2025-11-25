# Dockerización - Clínica Veterinaria

Este documento explica cómo usar Docker para ejecutar la aplicación Clínica Veterinaria.

## Requisitos Previos

- Docker Desktop instalado (Windows/Mac) o Docker Engine + Docker Compose (Linux)
- Al menos 4GB de RAM disponible
- Puertos 80, 8080 y 3306 disponibles

## Estructura de Archivos Docker

- `Dockerfile.backend`: Imagen del backend Spring Boot
- `Dockerfile.frontend`: Imagen del frontend React
- `docker-compose.yml`: Configuración principal
- `docker-compose.dev.yml`: Configuración para desarrollo
- `nginx.conf`: Configuración de Nginx para el frontend
- `.dockerignore`: Archivos a excluir del build

## Uso Rápido

### Uso Principal

```bash
# Construir y levantar todos los servicios
docker compose up -d

# Ver logs
docker compose logs -f

# Detener servicios
docker compose down

# Detener y eliminar volúmenes (¡CUIDADO! Elimina la base de datos)
docker compose down -v
```

### Desarrollo

```bash
# Construir y levantar servicios de desarrollo
docker compose -f docker-compose.dev.yml up -d

# Ver logs
docker compose -f docker-compose.dev.yml logs -f

# Detener servicios
docker compose -f docker-compose.dev.yml down
```

## Servicios

### MySQL (Base de Datos)
- **Puerto**: 3306
- **Usuario root**: `root` / sin contraseña (desarrollo)
- **Base de datos**: `clinica_veterinaria` / `clinica_veterinaria_dev` (desarrollo)
- **Volumen**: `mysql_data` (persistencia de datos)

### Backend (Spring Boot)
- **Puerto**: 8080
- **URL**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

### Frontend (React + Nginx)
- **Puerto**: 80 / 3000 (desarrollo)
- **URL**: http://localhost / http://localhost:3000 (desarrollo)

## Variables de Entorno

### Crear archivo .env

1. **Copia el archivo de ejemplo**:
   ```bash
   cp .env.example .env
   ```

2. **Edita el archivo `.env`** con tus valores personalizados:
   ```env
   # Base de datos
   MYSQL_ROOT_PASSWORD=tu_password_seguro
   MYSQL_DATABASE=clinica_veterinaria
   MYSQL_USER=clinica_user
   MYSQL_PASSWORD=tu_password_seguro

   # Backend
   DB_USERNAME=clinica_user
   DB_PASSWORD=tu_password_seguro

   # JWT
   JWT_SECRET=tu_clave_secreta_muy_larga_y_segura
   JWT_EXPIRATION=86400000
   ```

3. **Importante**: El archivo `.env` está en `.gitignore`, así que no se subirá a Git. Esto es correcto porque contiene información sensible.

### Generar una clave JWT segura

Para generar una clave secreta segura para JWT, puedes usar:

**Linux/Mac:**
```bash
openssl rand -hex 32
```

**Windows (PowerShell):**
```powershell
-join ((48..57) + (65..90) + (97..122) | Get-Random -Count 64 | ForEach-Object {[char]$_})
```

O simplemente usa un generador online de claves aleatorias.

## Comandos Útiles

### Construir imágenes manualmente

```bash
# Backend
docker build -f Dockerfile.backend -t clinica-backend .

# Frontend
docker build -f Dockerfile.frontend -t clinica-frontend .
```

### Ver logs de un servicio específico

```bash
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f mysql
```

### Ejecutar comandos en contenedores

```bash
# Acceder al contenedor del backend
docker compose exec backend sh

# Acceder a MySQL
docker compose exec mysql mysql -u root -p

# Reiniciar un servicio específico
docker compose restart backend
```

### Limpiar todo

```bash
# Detener y eliminar contenedores
docker compose down

# Eliminar imágenes
docker compose down --rmi all

# Eliminar todo incluyendo volúmenes
docker compose down -v --rmi all
```

## Solución de Problemas

### El backend no se conecta a MySQL

1. Verifica que MySQL esté saludable: `docker compose ps`
2. Revisa los logs: `docker compose logs mysql`
3. Verifica las variables de entorno en `docker-compose.yml`

### El frontend no se conecta al backend

1. Verifica que el backend esté corriendo: `docker compose ps`
2. Revisa la configuración de `nginx.conf`
3. Verifica que la URL de la API en el frontend sea correcta

### Puerto ya en uso

Si algún puerto está en uso, puedes cambiarlo en `docker-compose.yml`:

```yaml
ports:
  - "8081:8080"  # Cambiar puerto externo
```

### Reconstruir después de cambios

```bash
# Reconstruir sin caché
docker compose build --no-cache

# Reconstruir y levantar
docker compose up -d --build
```

## Notas

- La primera vez que ejecutes `docker compose up`, puede tardar varios minutos en descargar las imágenes y construir los contenedores.
- Los datos de MySQL se persisten en volúmenes Docker, por lo que no se perderán al reiniciar los contenedores.
- Para desarrollo, considera usar `docker-compose.dev.yml` que tiene configuraciones más permisivas.
- Este proyecto usa **Docker Compose v2** (usa `docker compose` con espacio, no `docker-compose` con guion).

