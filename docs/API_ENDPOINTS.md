# API REST - Documentación de Endpoints

## Tabla de Contenidos
1. [Especies](#especies)
2. [Razas](#razas)
3. [Propietarios](#propietarios)
4. [Mascotas](#mascotas)
5. [Usuarios](#usuarios)
6. [Veterinarios](#veterinarios)

---

## Especies

Base URL: `/api/especies`

### 1. Crear nueva especie
- **Método:** `POST`
- **URL:** `/api/especies`
- **Descripción:** Crea una nueva especie en el sistema
- **Request Body:**
```json
{
  "nombre": "Perro",
  "descripcion": "Canis lupus familiaris",
  "activo": true
}
```
- **Respuesta:** `201 CREATED`
```json
{
  "idEspecie": 1,
  "nombre": "Perro",
  "descripcion": "Canis lupus familiaris",
  "activo": true
}
```
- **Errores:**
  - `400 Bad Request`: Datos de entrada inválidos
  - `409 Conflict`: La especie ya existe

### 2. Actualizar especie
- **Método:** `PUT`
- **URL:** `/api/especies/{id}`
- **Descripción:** Actualiza una especie existente
- **Parámetros de ruta:**
  - `id` (Long): ID de la especie
- **Request Body:**
```json
{
  "nombre": "Perro actualizado",
  "descripcion": "Nueva descripción",
  "activo": true
}
```
- **Respuesta:** `200 OK`
- **Errores:**
  - `404 Not Found`: Especie no encontrada

### 3. Buscar especie por ID
- **Método:** `GET`
- **URL:** `/api/especies/{id}`
- **Descripción:** Obtiene una especie por su ID
- **Parámetros de ruta:**
  - `id` (Long): ID de la especie
- **Respuesta:** `200 OK`
```json
{
  "idEspecie": 1,
  "nombre": "Perro",
  "descripcion": "Canis lupus familiaris",
  "activo": true
}
```
- **Errores:**
  - `404 Not Found`: Especie no encontrada

### 4. Listar todas las especies
- **Método:** `GET`
- **URL:** `/api/especies`
- **Descripción:** Obtiene todas las especies del sistema
- **Respuesta:** `200 OK`
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

### 5. Listar especies con paginación
- **Método:** `GET`
- **URL:** `/api/especies/paginadas`
- **Descripción:** Obtiene especies con paginación
- **Parámetros de query:**
  - `page` (int, opcional): Número de página (default: 0)
  - `size` (int, opcional): Tamaño de página (default: 10)
  - `sort` (string, opcional): Campo de ordenamiento (default: "nombre")
- **Ejemplo:** `/api/especies/paginadas?page=0&size=5&sort=nombre,asc`
- **Respuesta:** `200 OK`
```json
{
  "content": [...],
  "pageable": {...},
  "totalPages": 2,
  "totalElements": 15,
  "number": 0,
  "size": 5
}
```

### 6. Listar especies activas
- **Método:** `GET`
- **URL:** `/api/especies/activas`
- **Descripción:** Obtiene solo las especies activas
- **Respuesta:** `200 OK`

### 7. Buscar especies por nombre
- **Método:** `GET`
- **URL:** `/api/especies/buscar`
- **Descripción:** Busca especies por nombre (búsqueda parcial, case-insensitive)
- **Parámetros de query:**
  - `nombre` (string, requerido): Texto a buscar
- **Ejemplo:** `/api/especies/buscar?nombre=perr`
- **Respuesta:** `200 OK`

### 8. Eliminar especie (soft delete)
- **Método:** `DELETE`
- **URL:** `/api/especies/{id}`
- **Descripción:** Desactiva una especie (no la elimina físicamente)
- **Parámetros de ruta:**
  - `id` (Long): ID de la especie
- **Respuesta:** `204 NO CONTENT`
- **Errores:**
  - `404 Not Found`: Especie no encontrada
  - `422 Unprocessable Entity`: No se puede eliminar (tiene razas asociadas)

### 9. Activar especie
- **Método:** `PATCH`
- **URL:** `/api/especies/{id}/activar`
- **Descripción:** Activa una especie previamente desactivada
- **Parámetros de ruta:**
  - `id` (Long): ID de la especie
- **Respuesta:** `200 OK`

---

## Razas

Base URL: `/api/razas`

### 1. Crear nueva raza
- **Método:** `POST`
- **URL:** `/api/razas`
- **Descripción:** Crea una nueva raza en el sistema
- **Request Body:**
```json
{
  "nombre": "Labrador",
  "descripcion": "Raza grande, amigable",
  "idEspecie": 1,
  "activo": true
}
```
- **Respuesta:** `201 CREATED`
```json
{
  "idRaza": 1,
  "nombre": "Labrador",
  "descripcion": "Raza grande, amigable",
  "especie": {
    "idEspecie": 1,
    "nombre": "Perro"
  },
  "activo": true
}
```

### 2. Actualizar raza
- **Método:** `PUT`
- **URL:** `/api/razas/{id}`
- **Request Body:**
```json
{
  "nombre": "Labrador Retriever",
  "descripcion": "Raza grande, amigable y trabajadora",
  "idEspecie": 1,
  "activo": true
}
```
- **Respuesta:** `200 OK`

### 3. Buscar raza por ID
- **Método:** `GET`
- **URL:** `/api/razas/{id}`
- **Respuesta:** `200 OK`

### 4. Listar todas las razas
- **Método:** `GET`
- **URL:** `/api/razas`
- **Respuesta:** `200 OK`

### 5. Listar razas con paginación
- **Método:** `GET`
- **URL:** `/api/razas/paginadas`
- **Parámetros de query:**
  - `page`, `size`, `sort` (igual que especies)
- **Respuesta:** `200 OK`

### 6. Listar razas por especie
- **Método:** `GET`
- **URL:** `/api/razas/especie/{idEspecie}`
- **Descripción:** Obtiene todas las razas de una especie específica
- **Parámetros de ruta:**
  - `idEspecie` (Long): ID de la especie
- **Ejemplo:** `/api/razas/especie/1` (todas las razas de perros)
- **Respuesta:** `200 OK`

### 7. Listar razas activas
- **Método:** `GET`
- **URL:** `/api/razas/activas`
- **Respuesta:** `200 OK`

### 8. Listar razas activas por especie
- **Método:** `GET`
- **URL:** `/api/razas/activas/especie/{idEspecie}`
- **Descripción:** Obtiene solo las razas activas de una especie específica
- **Parámetros de ruta:**
  - `idEspecie` (Long): ID de la especie
- **Respuesta:** `200 OK`

### 9. Buscar razas por nombre
- **Método:** `GET`
- **URL:** `/api/razas/buscar`
- **Parámetros de query:**
  - `nombre` (string): Texto a buscar
- **Respuesta:** `200 OK`

### 10. Eliminar raza (soft delete)
- **Método:** `DELETE`
- **URL:** `/api/razas/{id}`
- **Respuesta:** `204 NO CONTENT`

### 11. Activar raza
- **Método:** `PATCH`
- **URL:** `/api/razas/{id}/activar`
- **Respuesta:** `200 OK`

---

## Propietarios

Base URL: `/api/propietarios`

### 1. Crear nuevo propietario
- **Método:** `POST`
- **URL:** `/api/propietarios`
- **Request Body:**
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
- **Respuesta:** `201 CREATED`
```json
{
  "idPropietario": 1,
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
- **Validaciones:**
  - Documento único por tipo de documento
  - Email único
  - Formato de email válido

### 2. Actualizar propietario
- **Método:** `PUT`
- **URL:** `/api/propietarios/{id}`
- **Respuesta:** `200 OK`

### 3. Buscar propietario por ID
- **Método:** `GET`
- **URL:** `/api/propietarios/{id}`
- **Respuesta:** `200 OK`

### 4. Listar todos los propietarios
- **Método:** `GET`
- **URL:** `/api/propietarios`
- **Respuesta:** `200 OK`

### 5. Listar propietarios con paginación
- **Método:** `GET`
- **URL:** `/api/propietarios/paginados`
- **Parámetros de query:**
  - `page`, `size`, `sort` (default sort: "nombres")
- **Respuesta:** `200 OK`

### 6. Listar propietarios activos
- **Método:** `GET`
- **URL:** `/api/propietarios/activos`
- **Respuesta:** `200 OK`

### 7. Buscar propietarios por nombre
- **Método:** `GET`
- **URL:** `/api/propietarios/buscar`
- **Descripción:** Busca propietarios por nombres o apellidos
- **Parámetros de query:**
  - `nombre` (string): Texto a buscar
- **Ejemplo:** `/api/propietarios/buscar?nombre=juan`
- **Respuesta:** `200 OK`

### 8. Buscar propietario por documento
- **Método:** `GET`
- **URL:** `/api/propietarios/documento`
- **Descripción:** Busca un propietario por tipo y número de documento
- **Parámetros de query:**
  - `tipoDocumento` (string): Tipo de documento (CC, CE, TI, etc.)
  - `numeroDocumento` (string): Número de documento
- **Ejemplo:** `/api/propietarios/documento?tipoDocumento=CC&numeroDocumento=1234567890`
- **Respuesta:** `200 OK`
- **Errores:**
  - `404 Not Found`: Propietario no encontrado

### 9. Buscar propietario por email
- **Método:** `GET`
- **URL:** `/api/propietarios/email`
- **Parámetros de query:**
  - `email` (string): Email del propietario
- **Ejemplo:** `/api/propietarios/email?email=juan.garcia@email.com`
- **Respuesta:** `200 OK`

### 10. Buscar propietarios por teléfono
- **Método:** `GET`
- **URL:** `/api/propietarios/telefono`
- **Descripción:** Busca propietarios por teléfono (búsqueda parcial)
- **Parámetros de query:**
  - `telefono` (string): Número de teléfono
- **Respuesta:** `200 OK`

### 11. Eliminar propietario (soft delete)
- **Método:** `DELETE`
- **URL:** `/api/propietarios/{id}`
- **Respuesta:** `204 NO CONTENT`
- **Errores:**
  - `422 Unprocessable Entity`: No se puede eliminar (tiene mascotas asociadas)

### 12. Activar propietario
- **Método:** `PATCH`
- **URL:** `/api/propietarios/{id}/activar`
- **Respuesta:** `200 OK`

---

## Mascotas

Base URL: `/api/mascotas`

### 1. Crear nueva mascota
- **Método:** `POST`
- **URL:** `/api/mascotas`
- **Request Body:**
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
- **Respuesta:** `201 CREATED`
```json
{
  "idMascota": 1,
  "nombre": "Max",
  "fechaNacimiento": "2020-05-15",
  "sexo": "M",
  "color": "Café y blanco",
  "pesoKg": 25.5,
  "propietario": {
    "idPropietario": 1,
    "nombres": "Juan Carlos",
    "apellidos": "García Pérez"
  },
  "especie": {
    "idEspecie": 1,
    "nombre": "Perro"
  },
  "raza": {
    "idRaza": 1,
    "nombre": "Labrador"
  },
  "microchip": "982000123456789",
  "observaciones": "Muy activo, le gusta jugar",
  "activo": true
}
```
- **Validaciones:**
  - El propietario debe existir
  - La especie debe existir
  - La raza debe existir y pertenecer a la especie seleccionada
  - El microchip debe ser único (si se proporciona)

### 2. Actualizar mascota
- **Método:** `PUT`
- **URL:** `/api/mascotas/{id}`
- **Respuesta:** `200 OK`

### 3. Buscar mascota por ID
- **Método:** `GET`
- **URL:** `/api/mascotas/{id}`
- **Respuesta:** `200 OK`

### 4. Listar todas las mascotas
- **Método:** `GET`
- **URL:** `/api/mascotas`
- **Respuesta:** `200 OK`

### 5. Listar mascotas con paginación
- **Método:** `GET`
- **URL:** `/api/mascotas/paginadas`
- **Parámetros de query:**
  - `page`, `size`, `sort` (default sort: "nombre")
- **Respuesta:** `200 OK`

### 6. Listar mascotas activas
- **Método:** `GET`
- **URL:** `/api/mascotas/activas`
- **Respuesta:** `200 OK`

### 7. Listar mascotas por propietario
- **Método:** `GET`
- **URL:** `/api/mascotas/propietario/{idPropietario}`
- **Descripción:** Obtiene todas las mascotas de un propietario
- **Parámetros de ruta:**
  - `idPropietario` (Long): ID del propietario
- **Ejemplo:** `/api/mascotas/propietario/1`
- **Respuesta:** `200 OK`

### 8. Listar mascotas por especie
- **Método:** `GET`
- **URL:** `/api/mascotas/especie/{idEspecie}`
- **Descripción:** Obtiene todas las mascotas de una especie
- **Parámetros de ruta:**
  - `idEspecie` (Long): ID de la especie
- **Respuesta:** `200 OK`

### 9. Listar mascotas por raza
- **Método:** `GET`
- **URL:** `/api/mascotas/raza/{idRaza}`
- **Descripción:** Obtiene todas las mascotas de una raza
- **Parámetros de ruta:**
  - `idRaza` (Long): ID de la raza
- **Respuesta:** `200 OK`

### 10. Buscar mascotas por nombre
- **Método:** `GET`
- **URL:** `/api/mascotas/buscar`
- **Parámetros de query:**
  - `nombre` (string): Texto a buscar
- **Respuesta:** `200 OK`

### 11. Eliminar mascota (soft delete)
- **Método:** `DELETE`
- **URL:** `/api/mascotas/{id}`
- **Respuesta:** `204 NO CONTENT`

### 12. Activar mascota
- **Método:** `PATCH`
- **URL:** `/api/mascotas/{id}/activar`
- **Respuesta:** `200 OK`

---

## Usuarios

Base URL: `/api/usuarios`

### 1. Crear nuevo usuario
- **Método:** `POST`
- **URL:** `/api/usuarios`
- **Request Body:**
```json
{
  "username": "admin",
  "password": "Admin123!",
  "email": "admin@veterinaria.com",
  "rol": "ADMIN",
  "estado": true
}
```
- **Respuesta:** `201 CREATED`
```json
{
  "idUsuario": 1,
  "username": "admin",
  "email": "admin@veterinaria.com",
  "rol": "ADMIN",
  "estado": true,
  "bloqueado": false,
  "intentosFallidos": 0
}
```
- **Nota:** La contraseña se encripta con BCrypt automáticamente
- **Validaciones:**
  - Username único
  - Email único
  - Formato de email válido

### 2. Actualizar usuario
- **Método:** `PUT`
- **URL:** `/api/usuarios/{id}`
- **Respuesta:** `200 OK`

### 3. Buscar usuario por ID
- **Método:** `GET`
- **URL:** `/api/usuarios/{id}`
- **Respuesta:** `200 OK`

### 4. Buscar usuario por username
- **Método:** `GET`
- **URL:** `/api/usuarios/username/{username}`
- **Parámetros de ruta:**
  - `username` (string): Nombre de usuario
- **Ejemplo:** `/api/usuarios/username/admin`
- **Respuesta:** `200 OK`

### 5. Buscar usuario por email
- **Método:** `GET`
- **URL:** `/api/usuarios/email`
- **Parámetros de query:**
  - `email` (string): Email del usuario
- **Respuesta:** `200 OK`

### 6. Listar todos los usuarios
- **Método:** `GET`
- **URL:** `/api/usuarios`
- **Respuesta:** `200 OK`

### 7. Listar usuarios con paginación
- **Método:** `GET`
- **URL:** `/api/usuarios/paginados`
- **Parámetros de query:**
  - `page`, `size`, `sort` (default sort: "username")
- **Respuesta:** `200 OK`

### 8. Listar usuarios por rol
- **Método:** `GET`
- **URL:** `/api/usuarios/rol/{rol}`
- **Descripción:** Obtiene todos los usuarios de un rol específico
- **Parámetros de ruta:**
  - `rol` (string): Rol del usuario (ADMIN, VETERINARIO, RECEPCIONISTA, etc.)
- **Ejemplo:** `/api/usuarios/rol/ADMIN`
- **Respuesta:** `200 OK`

### 9. Listar usuarios por estado
- **Método:** `GET`
- **URL:** `/api/usuarios/estado/{estado}`
- **Descripción:** Obtiene usuarios activos o inactivos
- **Parámetros de ruta:**
  - `estado` (boolean): true para activos, false para inactivos
- **Ejemplo:** `/api/usuarios/estado/true`
- **Respuesta:** `200 OK`

### 10. Cambiar contraseña
- **Método:** `PATCH`
- **URL:** `/api/usuarios/{id}/cambiar-password`
- **Descripción:** Permite al usuario cambiar su contraseña
- **Request Body:**
```json
{
  "passwordActual": "Admin123!",
  "passwordNueva": "NewAdmin456!"
}
```
- **Respuesta:** `204 NO CONTENT`
- **Validaciones:**
  - La contraseña actual debe ser correcta
  - La nueva contraseña debe ser diferente a la actual
- **Errores:**
  - `401 Unauthorized`: Contraseña actual incorrecta
  - `400 Bad Request`: Nueva contraseña igual a la actual

### 11. Resetear contraseña (Admin)
- **Método:** `PATCH`
- **URL:** `/api/usuarios/{id}/resetear-password`
- **Descripción:** Permite a un administrador resetear la contraseña de un usuario
- **Request Body:**
```json
{
  "nuevaPassword": "Temp123!"
}
```
- **Respuesta:** `204 NO CONTENT`

### 12. Bloquear usuario
- **Método:** `PATCH`
- **URL:** `/api/usuarios/{id}/bloquear`
- **Descripción:** Bloquea un usuario del sistema
- **Request Body (opcional):**
```json
{
  "motivo": "Múltiples intentos fallidos de inicio de sesión"
}
```
- **Respuesta:** `204 NO CONTENT`
- **Nota:** Si no se proporciona motivo, se usa "Bloqueado por administrador"

### 13. Desbloquear usuario
- **Método:** `PATCH`
- **URL:** `/api/usuarios/{id}/desbloquear`
- **Descripción:** Desbloquea un usuario previamente bloqueado
- **Respuesta:** `204 NO CONTENT`

### 14. Activar usuario
- **Método:** `PATCH`
- **URL:** `/api/usuarios/{id}/activar`
- **Descripción:** Activa un usuario desactivado
- **Respuesta:** `204 NO CONTENT`

### 15. Desactivar usuario
- **Método:** `PATCH`
- **URL:** `/api/usuarios/{id}/desactivar`
- **Descripción:** Desactiva un usuario (no lo elimina)
- **Respuesta:** `204 NO CONTENT`

---

## Veterinarios

Base URL: `/api/veterinarios`

### 1. Crear nuevo veterinario
- **Método:** `POST`
- **URL:** `/api/veterinarios`
- **Request Body:**
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
- **Respuesta:** `201 CREATED`
```json
{
  "idVeterinario": 1,
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
- **Validaciones:**
  - Registro profesional único
  - Email único
  - Formato de email válido

### 2. Actualizar veterinario
- **Método:** `PUT`
- **URL:** `/api/veterinarios/{id}`
- **Respuesta:** `200 OK`

### 3. Buscar veterinario por ID
- **Método:** `GET`
- **URL:** `/api/veterinarios/{id}`
- **Respuesta:** `200 OK`

### 4. Buscar veterinario por registro profesional
- **Método:** `GET`
- **URL:** `/api/veterinarios/registro/{registroProfesional}`
- **Descripción:** Busca un veterinario por su registro profesional
- **Parámetros de ruta:**
  - `registroProfesional` (string): Registro profesional del veterinario
- **Ejemplo:** `/api/veterinarios/registro/MV-12345`
- **Respuesta:** `200 OK`
- **Errores:**
  - `404 Not Found`: Veterinario no encontrado

### 5. Listar todos los veterinarios
- **Método:** `GET`
- **URL:** `/api/veterinarios`
- **Respuesta:** `200 OK`

### 6. Listar veterinarios con paginación
- **Método:** `GET`
- **URL:** `/api/veterinarios/paginados`
- **Parámetros de query:**
  - `page`, `size`, `sort` (default sort: "nombres")
- **Respuesta:** `200 OK`

### 7. Listar veterinarios activos
- **Método:** `GET`
- **URL:** `/api/veterinarios/activos`
- **Respuesta:** `200 OK`

### 8. Listar veterinarios disponibles
- **Método:** `GET`
- **URL:** `/api/veterinarios/disponibles`
- **Descripción:** Obtiene solo los veterinarios marcados como disponibles para atención
- **Respuesta:** `200 OK`

### 9. Listar veterinarios por especialidad
- **Método:** `GET`
- **URL:** `/api/veterinarios/especialidad`
- **Descripción:** Busca veterinarios por especialidad (búsqueda parcial, case-insensitive)
- **Parámetros de query:**
  - `especialidad` (string): Especialidad a buscar
- **Ejemplo:** `/api/veterinarios/especialidad?especialidad=cirug`
- **Respuesta:** `200 OK`

### 10. Buscar veterinarios por nombre
- **Método:** `GET`
- **URL:** `/api/veterinarios/buscar`
- **Descripción:** Busca veterinarios por nombres o apellidos
- **Parámetros de query:**
  - `nombre` (string): Texto a buscar
- **Respuesta:** `200 OK`

### 11. Eliminar veterinario (soft delete)
- **Método:** `DELETE`
- **URL:** `/api/veterinarios/{id}`
- **Respuesta:** `204 NO CONTENT`

### 12. Activar veterinario
- **Método:** `PATCH`
- **URL:** `/api/veterinarios/{id}/activar`
- **Respuesta:** `200 OK`

---

## Códigos de Estado HTTP

### Respuestas Exitosas
- `200 OK`: Solicitud exitosa (GET, PUT, PATCH)
- `201 CREATED`: Recurso creado exitosamente (POST)
- `204 NO CONTENT`: Solicitud exitosa sin contenido de respuesta (DELETE, algunos PATCH)

### Errores del Cliente
- `400 BAD REQUEST`: Datos de entrada inválidos o validación fallida
- `401 UNAUTHORIZED`: Autenticación requerida o credenciales inválidas
- `404 NOT FOUND`: Recurso no encontrado
- `409 CONFLICT`: Conflicto con el estado actual (ej: recurso duplicado)
- `422 UNPROCESSABLE ENTITY`: No se puede procesar (ej: violación de reglas de negocio)

### Errores del Servidor
- `500 INTERNAL SERVER ERROR`: Error interno del servidor

---

## Formato de Errores

Todas las respuestas de error siguen el siguiente formato:

```json
{
  "timestamp": "2025-11-03T19:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Descripción del error",
  "path": "/api/especies",
  "validationErrors": {
    "nombre": "El nombre no puede estar vacío",
    "descripcion": "La descripción debe tener al menos 5 caracteres"
  },
  "errors": ["Error 1", "Error 2"],
  "traceId": "abc123def456"
}
```

### Campos del objeto de error:
- `timestamp`: Fecha y hora del error
- `status`: Código HTTP del error
- `error`: Nombre del error HTTP
- `message`: Mensaje descriptivo del error
- `path`: Ruta del endpoint que generó el error
- `validationErrors`: Errores de validación por campo (opcional)
- `errors`: Lista de errores múltiples (opcional)
- `traceId`: ID de rastreo del error (opcional)

---

## Notas de Implementación

### Validaciones Comunes
- Todos los endpoints POST y PUT validan automáticamente los datos de entrada con `@Valid`
- Los campos requeridos están marcados con `@NotNull` o `@NotBlank` en los DTOs
- Los emails se validan con `@Email`
- Las fechas deben estar en formato ISO-8601: `YYYY-MM-DD`

### Paginación
- Por defecto, todas las listas paginadas retornan 10 elementos por página
- Los resultados se ordenan por un campo lógico según el recurso
- Se puede personalizar con parámetros `page`, `size` y `sort`

### Soft Delete
- Todos los endpoints DELETE implementan eliminación lógica (soft delete)
- Los recursos no se eliminan físicamente, solo se marcan como inactivos
- Se pueden reactivar con los endpoints PATCH `/{id}/activar`

### Búsquedas
- Todas las búsquedas por texto son case-insensitive
- Las búsquedas parciales (LIKE) buscan el texto en cualquier parte del campo
- Las búsquedas por nombre en propietarios y veterinarios buscan en nombres Y apellidos

### Seguridad (Pendiente de implementar en FASE 11)
- Todos los endpoints requerirán autenticación JWT
- Los permisos serán controlados por roles (ADMIN, VETERINARIO, RECEPCIONISTA)
- Algunos endpoints estarán restringidos solo para administradores

---

## Swagger UI

Una vez el proyecto esté ejecutándose, puede acceder a la documentación interactiva en:

**URL:** `http://localhost:8080/swagger-ui.html`

Swagger UI permite:
- Ver todos los endpoints disponibles
- Probar los endpoints directamente desde el navegador
- Ver los esquemas de request y response
- Ver los códigos de estado y errores posibles
