# Guía para Limpiar Tablas Huérfanas

## ¿Por qué hay tablas sin uso?

Cuando eliminaste las entidades del código fuente (RecetaMedica, Factura, Pago, etc.), Hibernate **no eliminó automáticamente** las tablas de la base de datos porque estás usando `spring.jpa.hibernate.ddl-auto=update`.

## Opciones para Limpiar

### ✅ **OPCIÓN 1: Ejecutar script SQL (RECOMENDADO)**

**Pasos:**
1. Abre tu cliente MySQL (XAMPP → MySQL Admin o phpMyAdmin)
2. Selecciona la base de datos `clinica_veterinaria`
3. Ejecuta el archivo: `scripts/limpiar_tablas_huerfanas.sql`

**Ventajas:**
- No pierdes datos de otras tablas
- Elimina solo tablas específicas
- Puedes revisar el script antes de ejecutar

---

### 🔄 **OPCIÓN 2: Recrear toda la base de datos**

**⚠️ ADVERTENCIA: Esto borrará TODOS los datos**

**Pasos:**

1. **Cambiar temporalmente `application.properties`:**
   ```properties
   # Cambiar de:
  // spring.jpa.hibernate.ddl-auto=update

   # A:
  // spring.jpa.hibernate.ddl-auto=create-drop
   ```

2. **Iniciar la aplicación:**
   ```bash
   mvn spring-boot:run
   ```

3. **La aplicación creará todas las tablas desde cero** (sin tablas huérfanas)

4. **IMPORTANTE: Después detén la aplicación y vuelve a cambiar a `update`:**
   ```properties
   spring.jpa.hibernate.ddl-auto=update
   ```

**Ventajas:**
- Base de datos completamente limpia
- Sin tablas huérfanas
- Estructura actualizada al 100%

**Desventajas:**
- ⚠️ **PIERDES TODOS LOS DATOS**
- Debes volver a crear usuarios y datos de prueba

---

### 📊 **OPCIÓN 3: Verificar tablas existentes sin eliminar**

Si solo quieres **ver** qué tablas hay sin eliminar nada:

```sql
USE clinica_veterinaria;

-- Ver todas las tablas
SHOW TABLES;

-- Ver estructura de una tabla específica
DESCRIBE recetas_medicas;
```

---

## Tablas que DEBEN existir (20 tablas)

Después de limpiar, deberías tener SOLO estas tablas:

### 👤 **Módulo: Usuarios y Personal (7 tablas)**
- `usuarios`
- `personal`
- `veterinario`
- `administrador`
- `recepcionista`
- `auxiliar_veterinario`
- `propietarios`

### 🐾 **Módulo: Pacientes (3 tablas)**
- `mascotas`
- `especies`
- `razas`

### 📅 **Módulo: Agenda (3 tablas)**
- `citas`
- `servicios`
- `horarios`

### 🏥 **Módulo: Clínico (3 tablas)**
- `historias_clinicas`
- `evolucion_clinica`
- `vacunaciones`

### 📦 **Módulo: Inventario (3 tablas)**
- `insumos`
- `tipo_insumo`
- `inventarios`

### 📨 **Módulo: Comunicación (1 tabla)**
- `comunicaciones`

---

## Tablas que DEBEN eliminarse (8 tablas)

Estas tablas corresponden a entidades eliminadas del código:

### ❌ **Módulo: Facturación (ELIMINADO)**
- `facturas`
- `detalles_factura` / `detalle_factura`
- `pagos`
- `metodos_pago` (si existe como tabla)

### ❌ **Módulo: Clínico (ELIMINADO)**
- `recetas_medicas` / `receta_medica`
- `tratamientos`

### ❌ **Módulo: Inventario (ELIMINADO)**
- `movimientos_inventario` / `movimiento_inventario`
- `proveedores`

---

## ¿Cómo verificar el resultado?

Después de ejecutar cualquier opción:

```sql
USE clinica_veterinaria;

-- Contar tablas existentes
SELECT COUNT(*) AS total_tablas
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'clinica_veterinaria';

-- Debería mostrar: 20 tablas (aprox.)
```

---

## Recomendación Final

Para **DESARROLLO**: Usa **OPCIÓN 2** (recrear toda la BD) para garantizar que todo esté limpio.

Para **PRODUCCIÓN** (cuando tengas datos reales): Usa **OPCIÓN 1** (script SQL) para eliminar solo las tablas necesarias.
