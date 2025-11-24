-- ===================================================================
-- SCRIPT PARA ELIMINAR FOREIGN KEY HUÉRFANA - EJECUTAR MANUALMENTE
-- ===================================================================
-- Ejecuta este script en la base de datos clinica_veterinaria_dev
-- antes de iniciar la aplicación Spring Boot
-- ===================================================================

USE clinica_veterinaria_dev;

SET FOREIGN_KEY_CHECKS = 0;

-- Eliminar foreign key FKg0hjg8qvublti8scc9dtvccmb si existe
SET @db_name = DATABASE();
SET @fk_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
    WHERE CONSTRAINT_SCHEMA = @db_name
      AND TABLE_NAME = 'insumos'
      AND CONSTRAINT_NAME = 'FKg0hjg8qvublti8scc9dtvccmb'
      AND CONSTRAINT_TYPE = 'FOREIGN KEY'
);

SET @sql = IF(@fk_exists > 0, 
    'ALTER TABLE insumos DROP FOREIGN KEY FKg0hjg8qvublti8scc9dtvccmb;', 
    'SELECT "Foreign key no existe" AS mensaje;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Eliminar columna id_proveedor si existe
SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = @db_name
      AND TABLE_NAME = 'insumos'
      AND COLUMN_NAME = 'id_proveedor'
);

SET @sql = IF(@col_exists > 0, 
    'ALTER TABLE insumos DROP COLUMN id_proveedor;', 
    'SELECT "Columna no existe" AS mensaje;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Eliminar tabla proveedores si existe
DROP TABLE IF EXISTS proveedores;
DROP TABLE IF EXISTS proveedor;

SET FOREIGN_KEY_CHECKS = 1;

SELECT 'Script ejecutado correctamente' AS resultado;

