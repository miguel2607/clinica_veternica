-- ===================================================================
-- SCRIPT PARA ELIMINAR TABLAS HUÉRFANAS - CLINICA VETERINARIA
-- ===================================================================
-- Ejecutar este script SOLO si estás seguro de que no tienes datos importantes
-- en estas tablas.
--
-- Estas tablas corresponden a entidades que fueron eliminadas del código
-- pero que todavía existen en la base de datos.
-- ===================================================================

USE clinica_veterinaria;

-- Deshabilitar verificación de foreign keys temporalmente
SET FOREIGN_KEY_CHECKS = 0;

-- ===================================================================
-- MÓDULO: FACTURACIÓN (ELIMINADO)
-- ===================================================================

-- Eliminar tabla de detalles de factura
DROP TABLE IF EXISTS detalles_factura;
DROP TABLE IF EXISTS detalle_factura;

-- Eliminar tabla de facturas
DROP TABLE IF EXISTS facturas;
DROP TABLE IF EXISTS factura;

-- Eliminar tabla de pagos
DROP TABLE IF EXISTS pagos;
DROP TABLE IF EXISTS pago;

-- ===================================================================
-- MÓDULO: CLÍNICO (TABLAS ELIMINADAS)
-- ===================================================================

-- Eliminar tabla de recetas médicas
DROP TABLE IF EXISTS recetas_medicas;
DROP TABLE IF EXISTS receta_medica;

-- Eliminar tabla de tratamientos
DROP TABLE IF EXISTS tratamientos;
DROP TABLE IF EXISTS tratamiento;

-- ===================================================================
-- MÓDULO: INVENTARIO (TABLAS ELIMINADAS)
-- ===================================================================

-- Eliminar tabla de movimientos de inventario
DROP TABLE IF EXISTS movimientos_inventario;
DROP TABLE IF EXISTS movimiento_inventario;

-- Eliminar tabla de proveedores
DROP TABLE IF EXISTS proveedores;
DROP TABLE IF EXISTS proveedor;

-- ===================================================================
-- OTRAS POSIBLES TABLAS HUÉRFANAS
-- ===================================================================

-- Eliminar tabla de método de pago (si es entidad y no ENUM)
DROP TABLE IF EXISTS metodos_pago;
DROP TABLE IF EXISTS metodo_pago;

-- Habilitar verificación de foreign keys nuevamente
SET FOREIGN_KEY_CHECKS = 1;

-- ===================================================================
-- VERIFICAR TABLAS EXISTENTES
-- ===================================================================

-- Ver todas las tablas que quedan en la base de datos
SELECT TABLE_NAME
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'clinica_veterinaria'
ORDER BY TABLE_NAME;

-- ===================================================================
-- RESULTADO ESPERADO
-- ===================================================================
-- Deberías ver SOLO las siguientes tablas (aprox. 20-25 tablas):
--
-- Tabla                     | Entidad
-- --------------------------|------------------
-- administrador             | Administrador
-- auxiliar_veterinario      | AuxiliarVeterinario
-- citas                     | Cita
-- comunicaciones            | Comunicacion
-- especies                  | Especie
-- evolucion_clinica         | EvolucionClinica
-- historias_clinicas        | HistoriaClinica
-- horarios                  | Horario
-- insumos                   | Insumo
-- inventarios               | Inventario
-- mascotas                  | Mascota
-- personal                  | Personal (abstracta)
-- propietarios              | Propietario
-- razas                     | Raza
-- recepcionista             | Recepcionista
-- servicios                 | Servicio
-- tipo_insumo               | TipoInsumo
-- usuarios                  | Usuario
-- vacunaciones              | Vacunacion
-- veterinario               | Veterinario
-- ===================================================================
