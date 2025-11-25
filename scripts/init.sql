-- Script de inicialización para MySQL en Docker
-- Este script se ejecuta automáticamente cuando se crea el contenedor por primera vez

-- Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS clinica_veterinaria CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE clinica_veterinaria;

-- Nota: Las tablas se crearán automáticamente por Hibernate con ddl-auto=update
-- Si necesitas ejecutar scripts SQL adicionales, puedes agregarlos aquí

