# Guía de Pruebas del Sistema - Clínica Veterinaria

## Requisitos Previos

### 1. Verificar MySQL
Asegúrate de que MySQL esté corriendo:

```bash
# Verificar si MySQL está corriendo
mysql -u root -p

# Si está corriendo, deberías ver:
# mysql>
```

**Credenciales configuradas:**
- Host: `localhost:3306`
- Usuario: `root`
- Contraseña: `root`
- Base de datos: `clinica_veterinaria` (se crea automáticamente)

---

## Paso 1: Iniciar la Aplicación

### Opción A: Desde Maven
```bash
cd C:\Users\MIGUEL\IdeaProjects\clinica_veternica
mvn spring-boot:run
```

### Opción B: Desde IDE (IntelliJ/Eclipse)
1. Abrir el proyecto
2. Ejecutar la clase `ClinicaVeternicaApplication.java`
3. Hacer clic en "Run" o presionar Shift+F10

### Verificar que inició correctamente
Deberías ver en la consola:
```
Started ClinicaVeternicaApplication in X seconds
```

---

## Paso 2: Verificar Tablas Creadas

### Conectar a MySQL y verificar:
```sql
-- Conectar a MySQL
mysql -u root -p

-- Usar la base de datos
USE clinica_veterinaria;

-- Ver todas las tablas creadas
SHOW TABLES;
```

**Tablas que deberías ver (46 tablas):**
```
+--------------------------------------+
| Tables_in_clinica_veterinaria        |
+--------------------------------------+
| citas                                |
| citaciones                           |
| especies                             |
| estudiantes                          |
| evoluciones_clinicas                 |
| historias_clinicas                   |
| horarios                             |
| insumos                              |
| inventarios                          |
| mascotas                             |
| movimientos_inventario               |
| notificaciones                       |
| personal                             |
| propietarios                         |
| proveedores                          |
| razas                                |
| recetas_medicas                      |
| servicios                            |
| tipos_insumo                         |
| tratamientos                         |
| usuarios                             |
| veterinarios                         |
| ... (y más)                          |
+--------------------------------------+
```

### Verificar la estructura de la tabla usuarios:
```sql
DESCRIBE usuarios;
```

Deberías ver:
```
+-------------------+--------------+
| Field             | Type         |
+-------------------+--------------+
| id_usuario        | bigint       |
| username          | varchar(50)  |
| password          | varchar(255) |
| email             | varchar(100) |
| rol               | varchar(20)  |
| estado            | tinyint(1)   |
| bloqueado         | tinyint(1)   |
| intentos_fallidos | int          |
| fecha_bloqueo     | datetime(6)  |
| motivo_bloqueo    | varchar(500) |
| ultimo_login      | datetime(6)  |
| fecha_creacion    | datetime(6)  |
| fecha_modificacion| datetime(6)  |
+-------------------+--------------+
```

---

## Paso 3: Acceder a Swagger UI

### URL de Swagger:
```
http://localhost:8080/swagger-ui.html
```

**¿Qué verás en Swagger?**
- Lista de todos los endpoints organizados por controladores
- Documentación interactiva de cada endpoint
- Posibilidad de probar los endpoints directamente desde el navegador

**Controladores disponibles:**
1. **Autenticación** - `/api/auth`
2. **Especies** - `/api/especies`
3. **Razas** - `/api/razas`
4. **Propietarios** - `/api/propietarios`
5. **Mascotas** - `/api/mascotas`
6. **Usuarios** - `/api/usuarios`
7. **Veterinarios** - `/api/veterinarios`

---

## Paso 4: Crear el Primer Usuario (IMPORTANTE)

### Opción A: Usando Swagger UI

1. Ir a: `http://localhost:8080/swagger-ui.html`
2. Buscar el controlador **"Autenticación"**
3. Expandir `POST /api/auth/register`
4. Hacer clic en **"Try it out"**
5. Pegar este JSON en el cuerpo:

```json
{
  "username": "admin",
  "email": "admin@veterinaria.com",
  "password": "Admin123!",
  "rol": "ADMIN"
}
```

6. Hacer clic en **"Execute"**
7. Verificar respuesta `201 Created`

### Opción B: Usando cURL

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@veterinaria.com",
    "password": "Admin123!",
    "rol": "ADMIN"
  }'
```

### Opción C: Usando Postman
1. Crear nueva petición POST
2. URL: `http://localhost:8080/api/auth/register`
3. Headers: `Content-Type: application/json`
4. Body (raw JSON):
```json
{
  "username": "admin",
  "email": "admin@veterinaria.com",
  "password": "Admin123!",
  "rol": "ADMIN"
}
```

### Opción D: Insertar directamente en MySQL

```sql
-- Conectar a MySQL
USE clinica_veterinaria;

-- Insertar usuario admin (contraseña: Admin123!)
-- La contraseña está encriptada con BCrypt
INSERT INTO usuarios (username, email, password, rol, estado, bloqueado, intentos_fallidos, fecha_creacion, fecha_modificacion)
VALUES (
  'admin',
  'admin@veterinaria.com',
  '$2a$10$xJ3kIJ8KZ5J7Z5Z5Z5Z5ZOu0VYmY9H9H9H9H9H9H9H9H9H9H9H9H9H',
  'ADMIN',
  1,
  0,
  0,
  NOW(),
  NOW()
);

-- NOTA: Esta contraseña BCrypt es un ejemplo, mejor usar el endpoint de registro
```

---

## Paso 5: Iniciar Sesión (Login)

### Usando Swagger UI:

1. Ir a `POST /api/auth/login`
2. Click en **"Try it out"**
3. Usar estas credenciales:

```json
{
  "username": "admin",
  "password": "Admin123!"
}
```

4. Click en **"Execute"**

**Respuesta esperada (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTczMDY3...",
  "type": "Bearer",
  "idUsuario": 1,
  "username": "admin",
  "email": "admin@veterinaria.com",
  "rol": "ADMIN",
  "expiresIn": 86400000
}
```

### **IMPORTANTE: Copiar el token**
Copia el valor completo del campo `token`, lo necesitarás para las siguientes pruebas.

---

## Paso 6: Autenticarse en Swagger

Una vez que tengas el token, debes configurarlo en Swagger para acceder a los endpoints protegidos:

1. En Swagger UI, busca el botón **"Authorize"** (candado verde, arriba a la derecha)
2. Click en **"Authorize"**
3. En el campo `Value`, pegar:
```
Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTczMDY3...
```
(Reemplazar con tu token completo)

4. Click en **"Authorize"**
5. Click en **"Close"**

**Ahora puedes usar todos los endpoints protegidos.**

---

## Paso 7: Probar los Endpoints REST

### A. Crear una Especie

**Endpoint:** `POST /api/especies`

```json
{
  "nombre": "Perro",
  "descripcion": "Canis lupus familiaris",
  "activo": true
}
```

**Respuesta esperada (201 Created):**
```json
{
  "idEspecie": 1,
  "nombre": "Perro",
  "descripcion": "Canis lupus familiaris",
  "activo": true
}
```

### B. Crear otra Especie (Gato)

```json
{
  "nombre": "Gato",
  "descripcion": "Felis catus",
  "activo": true
}
```

### C. Listar todas las Especies

**Endpoint:** `GET /api/especies`

**Respuesta esperada (200 OK):**
```json
[
  {
    "idEspecie": 1,
    "nombre": "Perro",
    "descripcion": "Canis lupus familiaris",
    "activo": true
  },
  {
    "idEspecie": 2,
    "nombre": "Gato",
    "descripcion": "Felis catus",
    "activo": true
  }
]
```

### D. Crear una Raza

**Endpoint:** `POST /api/razas`

```json
{
  "nombre": "Labrador",
  "descripcion": "Perro grande, amigable y trabajador",
  "idEspecie": 1,
  "activo": true
}
```

### E. Crear un Propietario

**Endpoint:** `POST /api/propietarios`

```json
{
  "tipoDocumento": "CC",
  "documento": "1234567890",
  "nombres": "Juan Carlos",
  "apellidos": "García Pérez",
  "telefono": "+57 300 1234567",
  "email": "juan.garcia@email.com",
  "direccion": "Calle 123 #45-67",
  "ciudad": "Bogotá",
  "activo": true
}
```

### F. Crear una Mascota

**Endpoint:** `POST /api/mascotas`

```json
{
  "nombre": "Max",
  "fechaNacimiento": "2020-05-15",
  "sexo": "M",
  "color": "Café y blanco",
  "pesoKg": 25.5,
  "idPropietario": 1,
  "idEspecie": 1,
  "idRaza": 1,
  "microchip": "982000123456789",
  "observaciones": "Muy activo, le gusta jugar",
  "activo": true
}
```

### G. Crear un Veterinario

**Endpoint:** `POST /api/veterinarios`

```json
{
  "nombres": "María Elena",
  "apellidos": "Rodríguez López",
  "registroProfesional": "MV-12345",
  "especialidad": "Cirugía",
  "telefono": "+57 310 9876543",
  "email": "maria.rodriguez@veterinaria.com",
  "disponible": true,
  "activo": true
}
```

---

## Paso 8: Verificar Datos en MySQL

```sql
USE clinica_veterinaria;

-- Ver usuarios
SELECT * FROM usuarios;

-- Ver especies
SELECT * FROM especies;

-- Ver razas con sus especies
SELECT r.nombre AS raza, e.nombre AS especie
FROM razas r
JOIN especies e ON r.id_especie = e.id_especie;

-- Ver propietarios
SELECT * FROM propietarios;

-- Ver mascotas con toda su información
SELECT
  m.nombre AS mascota,
  m.sexo,
  m.peso_kg,
  e.nombre AS especie,
  r.nombre AS raza,
  CONCAT(p.nombres, ' ', p.apellidos) AS propietario
FROM mascotas m
JOIN especies e ON m.id_especie = e.id_especie
LEFT JOIN razas r ON m.id_raza = r.id_raza
JOIN propietarios p ON m.id_propietario = p.id_propietario;

-- Ver veterinarios
SELECT * FROM veterinarios;
```

---

## Paso 9: Probar Funcionalidades de Seguridad

### A. Probar endpoint sin autenticación (debe fallar)

**Endpoint:** `GET /api/especies`
**Sin header Authorization**

**Respuesta esperada (401 Unauthorized):**
```json
{
  "timestamp": "2025-11-03T20:10:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Error de autenticación: Full authentication is required...",
  "path": "/api/especies"
}
```

### B. Probar token inválido (debe fallar)

**Header:** `Authorization: Bearer token_invalido`

**Respuesta esperada (401 Unauthorized)**

### C. Probar login con credenciales incorrectas

```json
{
  "username": "admin",
  "password": "password_incorrecto"
}
```

**Respuesta esperada (401 Unauthorized):**
```json
{
  "timestamp": "2025-11-03T20:10:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Credenciales inválidas",
  "path": "/api/auth/login"
}
```

### D. Probar bloqueo automático

Intentar login 5 veces con contraseña incorrecta:
- Intentos 1-4: Devuelve 401 "Credenciales inválidas"
- Intento 5: Bloquea el usuario automáticamente

Verificar en MySQL:
```sql
SELECT username, bloqueado, intentos_fallidos, motivo_bloqueo
FROM usuarios
WHERE username = 'admin';
```

Deberías ver:
```
bloqueado = 1
intentos_fallidos = 5
motivo_bloqueo = "Bloqueado automáticamente por múltiples intentos fallidos de inicio de sesión"
```

---

## Paso 10: Probar Validaciones

### A. Crear especie con nombre vacío (debe fallar)

```json
{
  "nombre": "",
  "descripcion": "Test",
  "activo": true
}
```

**Respuesta esperada (400 Bad Request):**
```json
{
  "timestamp": "2025-11-03T20:10:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación",
  "path": "/api/especies",
  "validationErrors": {
    "nombre": "El nombre no puede estar vacío"
  }
}
```

### B. Crear especie duplicada (debe fallar)

Intentar crear "Perro" dos veces.

**Respuesta esperada (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Ya existe una especie con el nombre: Perro"
}
```

### C. Crear raza con especie inexistente (debe fallar)

```json
{
  "nombre": "Test Raza",
  "descripcion": "Test",
  "idEspecie": 999,
  "activo": true
}
```

**Respuesta esperada (404 Not Found):**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Especie no encontrada con id: 999"
}
```

---

## Paso 11: Probar Paginación

**Endpoint:** `GET /api/especies/paginadas?page=0&size=2&sort=nombre,asc`

**Respuesta esperada:**
```json
{
  "content": [
    { "idEspecie": 2, "nombre": "Gato", ... },
    { "idEspecie": 1, "nombre": "Perro", ... }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 2
  },
  "totalPages": 1,
  "totalElements": 2,
  "last": true,
  "first": true
}
```

---

## Paso 12: Probar Búsquedas

### A. Buscar especies por nombre
`GET /api/especies/buscar?nombre=perr`

### B. Buscar propietario por documento
`GET /api/propietarios/documento?tipoDocumento=CC&numeroDocumento=1234567890`

### C. Buscar mascotas por propietario
`GET /api/mascotas/propietario/1`

### D. Buscar veterinario por registro profesional
`GET /api/veterinarios/registro/MV-12345`

---

## Resumen de URLs Importantes

| Descripción | URL |
|-------------|-----|
| **Aplicación corriendo** | http://localhost:8080 |
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **API Docs JSON** | http://localhost:8080/api-docs |
| **Login** | POST http://localhost:8080/api/auth/login |
| **Register** | POST http://localhost:8080/api/auth/register |
| **Especies** | http://localhost:8080/api/especies |
| **Razas** | http://localhost:8080/api/razas |
| **Propietarios** | http://localhost:8080/api/propietarios |
| **Mascotas** | http://localhost:8080/api/mascotas |
| **Usuarios** | http://localhost:8080/api/usuarios |
| **Veterinarios** | http://localhost:8080/api/veterinarios |

---

## Solución de Problemas

### Error: "Cannot connect to database"
- Verificar que MySQL esté corriendo
- Verificar credenciales en `application.properties`
- Verificar puerto 3306

### Error: "Port 8080 already in use"
- Cambiar puerto en `application.properties`: `server.port=8081`
- O detener la aplicación que usa el puerto 8080

### Error: "JWT token expired"
- El token dura 24 horas
- Hacer login nuevamente para obtener un nuevo token

### No se ven las tablas en MySQL
- Verificar que la aplicación haya iniciado correctamente
- Revisar logs en consola buscando errores
- Verificar `spring.jpa.hibernate.ddl-auto=update` en properties

---

## Próximos Pasos

Una vez que hayas probado todo esto exitosamente, estarás listo para:

1. **Implementar módulo de Inventario**
   - Servicios y controladores para Insumos
   - Servicios y controladores para Proveedores
   - Servicios y controladores para Movimientos de Inventario

2. **Continuar con el plan de implementación**
   - Módulos de agenda (citas, horarios)
   - Módulos clínicos (historias, evoluciones, tratamientos)
   - Módulos de facturación

3. **Agregar más funcionalidades**
   - Reportes
   - Dashboard
   - Notificaciones
   - Exportación de datos

---

## Checklist de Pruebas Básicas

- [ ] MySQL corriendo y accesible
- [ ] Aplicación inicia sin errores
- [ ] Swagger UI accesible
- [ ] Tablas creadas en MySQL (46 tablas)
- [ ] Registro de usuario admin exitoso
- [ ] Login exitoso y token recibido
- [ ] Token configurado en Swagger (Authorize)
- [ ] Crear especie exitosamente
- [ ] Crear raza exitosamente
- [ ] Crear propietario exitosamente
- [ ] Crear mascota exitosamente
- [ ] Listar especies funciona
- [ ] Búsqueda por nombre funciona
- [ ] Paginación funciona
- [ ] Validaciones funcionan (nombre vacío = error)
- [ ] Seguridad funciona (sin token = 401)
- [ ] Bloqueo automático después de 5 intentos

---

¡Buena suerte con las pruebas! 🎉
