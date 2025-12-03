-- Script de inicialización para PostgreSQL en Docker
-- Este script se ejecuta automáticamente cuando se crea el contenedor por primera vez

-- Crear base de datos si no existe (PostgreSQL no tiene IF NOT EXISTS en CREATE DATABASE)
-- La base de datos se crea automáticamente por la variable POSTGRES_DB en docker-compose.yml

-- Nota: Las tablas se crearán automáticamente por Hibernate con ddl-auto=update
-- Si necesitas ejecutar scripts SQL adicionales, puedes agregarlos aquí

-- Ejemplo: Crear extensiones si son necesarias
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

