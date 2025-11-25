# Dockerfile para el frontend React
FROM node:20-alpine AS build

# Establecer directorio de trabajo
WORKDIR /app

# Argumento para la URL de la API
ARG VITE_API_URL=http://localhost:8080/api
ENV VITE_API_URL=$VITE_API_URL

# Copiar archivos de configuración
COPY frontend/package*.json ./

# Instalar dependencias
RUN npm ci

# Copiar código fuente
COPY frontend/ .

# Construir la aplicación
RUN npm run build

# Imagen de producción con Nginx
FROM nginx:alpine

# Instalar wget para health checks
RUN apk add --no-cache wget

# Copiar archivos construidos
COPY --from=build /app/dist /usr/share/nginx/html

# Copiar configuración de Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exponer puerto
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost/ || exit 1

# Iniciar Nginx
CMD ["nginx", "-g", "daemon off;"]

