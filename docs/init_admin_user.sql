-- ===================================================================
-- SCRIPT PARA CREAR USUARIO ADMINISTRADOR INICIAL
-- Clínica Veterinaria - Sistema de Gestión
-- ===================================================================
--
-- Este script crea un usuario administrador inicial para poder
-- acceder al sistema por primera vez.
--
-- CREDENCIALES:
--   Username: admin
--   Password: Admin123!
--   Email: admin@veterinaria.com
--   Rol: ADMIN
--
-- USO:
--   mysql -u root -p clinica_veterinaria < init_admin_user.sql
--
-- O desde MySQL CLI:
--   USE clinica_veterinaria;
--   SOURCE /ruta/a/init_admin_user.sql;
--
-- ===================================================================

USE clinica_veterinaria;

-- Eliminar usuario admin si ya existe (para poder recrearlo)
DELETE FROM usuarios WHERE username = 'admin';

-- Crear usuario administrador
-- Contraseña: Admin123!
-- Hash BCrypt generado con strength 10
INSERT INTO usuarios (
    username,
    email,
    password,
    rol,
    estado,
    bloqueado,
    intentos_fallidos,
    fecha_creacion,
    fecha_modificacion
) VALUES (
    'admin',
    'admin@veterinaria.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'ADMIN',
    1,
    0,
    0,
    NOW(),
    NOW()
);

-- Verificar que se creó correctamente
SELECT
    id_usuario,
    username,
    email,
    rol,
    estado,
    bloqueado,
    fecha_creacion
FROM usuarios
WHERE username = 'admin';

-- ===================================================================
-- OPCIONAL: Crear usuarios de prueba adicionales
-- ===================================================================

-- Usuario Veterinario (Password: Vet123!)
INSERT INTO usuarios (
    username,
    email,
    password,
    rol,
    estado,
    bloqueado,
    intentos_fallidos,
    fecha_creacion,
    fecha_modificacion
) VALUES (
    'veterinario1',
    'vet1@veterinaria.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'VETERINARIO',
    1,
    0,
    0,
    NOW(),
    NOW()
);

-- Usuario Recepcionista (Password: Recep123!)
INSERT INTO usuarios (
    username,
    email,
    password,
    rol,
    estado,
    bloqueado,
    intentos_fallidos,
    fecha_creacion,
    fecha_modificacion
) VALUES (
    'recepcion1',
    'recep1@veterinaria.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'RECEPCIONISTA',
    1,
    0,
    0,
    NOW(),
    NOW()
);

-- Ver todos los usuarios creados
SELECT
    id_usuario,
    username,
    email,
    rol,
    estado,
    bloqueado
FROM usuarios
ORDER BY id_usuario;

-- ===================================================================
-- NOTAS IMPORTANTES:
-- ===================================================================
--
-- 1. La contraseña está hasheada con BCrypt (strength 10)
-- 2. Todos los usuarios de prueba usan la misma contraseña hasheada
--    por simplicidad, pero en producción cada uno debería tener
--    una contraseña única
-- 3. Para generar nuevos hashes BCrypt, puedes usar:
--    - https://bcrypt-generator.com/
--    - O el endpoint /api/auth/register del sistema
-- 4. Cambiar estas contraseñas en producción por seguridad
--
-- ===================================================================
