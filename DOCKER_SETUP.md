# 🐳 Guía de Dockerización - Clínica Veterinaria

Esta guía te ayudará a ejecutar el proyecto completo usando Docker.

## 📋 Requisitos Previos

- Docker Desktop instalado y ejecutándose
- Docker Compose v2 o superior
- Al menos 4GB de RAM disponibles
- Puertos libres: 80, 8080, 5432

## 🚀 Inicio Rápido

### 1. Configurar Variables de Entorno

Copia el archivo de ejemplo y configura tus variables:

```bash
cp .env.example .env
```

Edita el archivo `.env` y configura:
- **POSTGRES_PASSWORD**: Contraseña para PostgreSQL (por defecto: `kepler2607`)
- **MAIL_USERNAME**: Tu email de Gmail
- **MAIL_PASSWORD**: Tu contraseña de aplicación de Gmail (no la contraseña normal)

> **Nota sobre Gmail**: Para usar Gmail, necesitas generar una "Contraseña de aplicación" desde tu cuenta de Google:
> 1. Ve a https://myaccount.google.com/apppasswords
> 2. Genera una contraseña de aplicación
> 3. Úsala en `MAIL_PASSWORD`

### 2. Construir y Ejecutar

#### Producción (Recomendado)

```bash
docker-compose up -d --build
```

#### Desarrollo

```bash
docker-compose -f docker-compose.dev.yml up -d --build
```

### 3. Verificar que Todo Funciona

```bash
# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
```

### 4. Acceder a la Aplicación

- **Frontend**: http://localhost (puerto 80)
- **Backend API**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **PostgreSQL**: localhost:5432

## 🛠️ Comandos Útiles

### Detener los Contenedores

```bash
docker-compose down
```

### Detener y Eliminar Volúmenes (⚠️ Elimina la base de datos)

```bash
docker-compose down -v
```

### Reconstruir Imágenes

```bash
docker-compose build --no-cache
```

### Ver Estado de los Contenedores

```bash
docker-compose ps
```

### Ejecutar Comandos en un Contenedor

```bash
# Acceder a la base de datos PostgreSQL
docker-compose exec postgres psql -U postgres -d clinica_veterinaria

# Ver logs del backend
docker-compose logs -f backend

# Reiniciar un servicio específico
docker-compose restart backend
```

## 📁 Estructura de Servicios

### PostgreSQL (Base de Datos)
- **Puerto**: 5432
- **Usuario**: postgres
- **Base de datos**: clinica_veterinaria
- **Volumen**: `postgres_data` (persistente)

### Backend (Spring Boot)
- **Puerto**: 8080
- **Perfil**: docker
- **Health Check**: http://localhost:8080/swagger-ui.html

### Frontend (React + Nginx)
- **Puerto**: 80
- **Proxy API**: /api → backend:8080/api
- **Health Check**: http://localhost/

## 🔧 Solución de Problemas

### El backend no inicia

1. Verifica que PostgreSQL esté corriendo:
   ```bash
   docker-compose ps postgres
   ```

2. Revisa los logs:
   ```bash
   docker-compose logs backend
   ```

3. Verifica las variables de entorno en `.env`

### El frontend no se conecta al backend

1. Verifica que el backend esté corriendo:
   ```bash
   curl http://localhost:8080/api/health
   ```

2. Revisa la configuración de Nginx:
   ```bash
   docker-compose exec frontend cat /etc/nginx/conf.d/default.conf
   ```

### La base de datos no persiste

Los datos se guardan en un volumen de Docker. Para verificar:

```bash
docker volume ls
docker volume inspect clinica_veternica_postgres_data
```

### Limpiar Todo y Empezar de Nuevo

```bash
# Detener y eliminar contenedores, redes y volúmenes
docker-compose down -v

# Eliminar imágenes
docker-compose rm -f

# Reconstruir desde cero
docker-compose build --no-cache
docker-compose up -d
```

## 🔐 Seguridad

- ⚠️ **NUNCA** subas el archivo `.env` al repositorio
- ⚠️ Cambia las contraseñas por defecto en producción
- ⚠️ Usa contraseñas de aplicación para Gmail, no tu contraseña normal
- ⚠️ Configura firewall para limitar acceso a los puertos expuestos

## 📝 Notas Adicionales

- La base de datos se inicializa automáticamente con Hibernate (`ddl-auto=update`)
- El frontend se construye en tiempo de build, no en tiempo de ejecución
- Los health checks aseguran que los servicios estén listos antes de iniciar dependencias
- Los logs se pueden ver en tiempo real con `docker-compose logs -f`

## 🆘 Soporte

Si encuentras problemas:
1. Revisa los logs: `docker-compose logs -f`
2. Verifica que los puertos no estén ocupados
3. Asegúrate de tener Docker Desktop ejecutándose
4. Verifica que las variables de entorno estén correctamente configuradas

