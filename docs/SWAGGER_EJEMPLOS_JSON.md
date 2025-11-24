# EJEMPLOS JSON PARA SWAGGER - CLÍNICA VETERINARIA

**Documento de referencia rápida para copiar y pegar en Swagger UI**
**Fecha:** 2025-11-19
**Versión:** 1.0

---

## 📋 TABLA DE CONTENIDOS

1. [Autenticación](#autenticación)
2. [Citas](#citas)
3. [Mascotas](#mascotas)
4. [Propietarios](#propietarios)
5. [Veterinarios](#veterinarios)
6. [Historias Clínicas](#historias-clínicas)
7. [Servicios](#servicios)
8. [Inventario](#inventario)
9. [Notificaciones](#notificaciones)
10. [Reportes (Facades)](#reportes-facades)

---

## 🔐 AUTENTICACIÓN

### POST /api/auth/login
**Descripción:** Iniciar sesión y obtener token JWT

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response 200 OK:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "admin",
  "email": "admin@clinica.com",
  "rol": "ADMIN",
  "expiresIn": 86400000
}
```

---

### POST /api/auth/register
**Descripción:** Registrar nuevo usuario

**Request Body:**
```json
{
  "username": "veterinario1",
  "email": "vet1@clinica.com",
  "password": "VetPass123!",
  "nombres": "Carlos",
  "apellidos": "Rodríguez",
  "rol": "VETERINARIO"
}
```

**Response 201 Created:**
```json
{
  "idUsuario": 1,
  "username": "veterinario1",
  "email": "vet1@clinica.com",
  "nombres": "Carlos",
  "apellidos": "Rodríguez",
  "rol": "VETERINARIO",
  "activo": true,
  "bloqueado": false
}
```

---

## 📅 CITAS

### POST /api/citas
**Descripción:** Crear nueva cita (usa Builder Pattern)

**Request Body:**
```json
{
  "idMascota": 1,
  "idVeterinario": 1,
  "idServicio": 1,
  "fechaCita": "2025-11-20",
  "horaCita": "10:30:00",
  "motivo": "Consulta general - Revisión de rutina",
  "observaciones": "Mascota muy activa, requiere paciencia",
  "esEmergencia": false
}
```

**Response 201 Created:**
```json
{
  "idCita": 1,
  "mascota": {
    "idMascota": 1,
    "nombre": "Max",
    "especie": "Canino",
    "raza": "Labrador"
  },
  "veterinario": {
    "idPersonal": 1,
    "nombreCompleto": "Dr. Carlos Rodríguez",
    "especialidad": "Medicina General"
  },
  "servicio": {
    "idServicio": 1,
    "nombre": "Consulta General",
    "categoria": "CLINICO"
  },
  "fechaCita": "2025-11-20",
  "horaCita": "10:30:00",
  "estado": "PROGRAMADA",
  "motivoConsulta": "Consulta general - Revisión de rutina",
  "observaciones": "Mascota muy activa, requiere paciencia",
  "esEmergencia": false,
  "precioFinal": 50000.00
}
```

---

### PUT /api/citas/{id}/confirmar
**Descripción:** Confirmar cita (usa Mediator Pattern)

**Path Parameter:** `id=1`

**Response 200 OK:**
```json
{
  "idCita": 1,
  "estado": "CONFIRMADA",
  "fechaConfirmacion": "2025-11-19T14:30:00",
  "mensaje": "Cita confirmada exitosamente"
}
```

---

### PUT /api/citas/{id}/cancelar
**Descripción:** Cancelar cita

**Path Parameter:** `id=1`
**Query Parameters:**
- `motivo`: Mascota enferma
- `usuario`: admin

**Response 200 OK:**
```json
{
  "idCita": 1,
  "estado": "CANCELADA",
  "fechaCancelacion": "2025-11-19T15:00:00",
  "motivoCancelacion": "Mascota enferma",
  "canceladaPor": "admin"
}
```

---

### GET /api/citas/programadas
**Descripción:** Listar todas las citas programadas

**Response 200 OK:**
```json
[
  {
    "idCita": 1,
    "mascota": {
      "nombre": "Max",
      "propietario": "Juan Pérez"
    },
    "veterinario": {
      "nombreCompleto": "Dr. Carlos Rodríguez"
    },
    "fechaCita": "2025-11-20",
    "horaCita": "10:30:00",
    "estado": "PROGRAMADA"
  },
  {
    "idCita": 2,
    "mascota": {
      "nombre": "Luna",
      "propietario": "María González"
    },
    "veterinario": {
      "nombreCompleto": "Dra. Ana Martínez"
    },
    "fechaCita": "2025-11-20",
    "horaCita": "11:00:00",
    "estado": "CONFIRMADA"
  }
]
```

---

## 🐾 MASCOTAS

### POST /api/mascotas
**Descripción:** Registrar nueva mascota

**Request Body:**
```json
{
  "nombre": "Max",
  "sexo": "Macho",
  "fechaNacimiento": "2020-05-15",
  "color": "Dorado",
  "peso": 25.5,
  "idPropietario": 1,
  "idEspecie": 1,
  "idRaza": 1,
  "microchip": "MX123456789",
  "observaciones": "Muy juguetón, le encanta el agua"
}
```

**Response 201 Created:**
```json
{
  "idMascota": 1,
  "nombre": "Max",
  "sexo": "Macho",
  "fechaNacimiento": "2020-05-15",
  "edad": "4 años, 6 meses",
  "color": "Dorado",
  "peso": 25.5,
  "especie": {
    "idEspecie": 1,
    "nombre": "Canino"
  },
  "raza": {
    "idRaza": 1,
    "nombre": "Labrador Retriever"
  },
  "propietario": {
    "idPropietario": 1,
    "nombreCompleto": "Juan Pérez",
    "telefono": "3001234567",
    "email": "juan@example.com"
  },
  "microchip": "MX123456789",
  "observaciones": "Muy juguetón, le encanta el agua",
  "activo": true,
  "tieneHistoriaClinica": false,
  "fechaRegistro": "2025-11-19"
}
```

---

### GET /api/mascotas/{id}
**Descripción:** Obtener mascota por ID

**Path Parameter:** `id=1`

**Response 200 OK:**
```json
{
  "idMascota": 1,
  "nombre": "Max",
  "sexo": "Macho",
  "edad": "4 años, 6 meses",
  "especie": "Canino",
  "raza": "Labrador Retriever",
  "peso": 25.5,
  "propietario": {
    "nombreCompleto": "Juan Pérez",
    "telefono": "3001234567"
  },
  "ultimaCita": {
    "fecha": "2025-10-15",
    "motivo": "Vacunación anual"
  },
  "activo": true
}
```

---

### GET /api/mascotas/propietario/{idPropietario}
**Descripción:** Listar mascotas de un propietario

**Path Parameter:** `idPropietario=1`

**Response 200 OK:**
```json
[
  {
    "idMascota": 1,
    "nombre": "Max",
    "especie": "Canino",
    "raza": "Labrador",
    "edad": "4 años",
    "activo": true
  },
  {
    "idMascota": 2,
    "nombre": "Luna",
    "especie": "Felino",
    "raza": "Persa",
    "edad": "2 años",
    "activo": true
  }
]
```

---

## 👤 PROPIETARIOS

### POST /api/propietarios
**Descripción:** Registrar nuevo propietario

**Request Body:**
```json
{
  "tipoDocumento": "CC",
  "documento": "1234567890",
  "nombres": "Juan",
  "apellidos": "Pérez García",
  "email": "juan.perez@example.com",
  "telefono": "3001234567",
  "direccion": "Calle 123 #45-67, Apto 301",
  "ciudad": "Bogotá",
  "activo": true
}
```

**Response 201 Created:**
```json
{
  "idPropietario": 1,
  "tipoDocumento": "CC",
  "documento": "1234567890",
  "nombres": "Juan",
  "apellidos": "Pérez García",
  "nombreCompleto": "Juan Pérez García",
  "email": "juan.perez@example.com",
  "telefono": "3001234567",
  "direccion": "Calle 123 #45-67, Apto 301",
  "ciudad": "Bogotá",
  "activo": true,
  "cantidadMascotas": 0,
  "fechaRegistro": "2025-11-19"
}
```

---

## 👨‍⚕️ VETERINARIOS

### POST /api/veterinarios
**Descripción:** Registrar nuevo veterinario

**Request Body:**
```json
{
  "tipoDocumento": "CC",
  "documento": "9876543210",
  "nombres": "Carlos Andrés",
  "apellidos": "Rodríguez López",
  "email": "carlos.rodriguez@clinica.com",
  "telefono": "3009876543",
  "especialidad": "Medicina General",
  "tarjetaProfesional": "TP-12345",
  "usuario": {
    "username": "crodriguez",
    "password": "VetPass123!",
    "rol": "VETERINARIO"
  }
}
```

**Response 201 Created:**
```json
{
  "idPersonal": 1,
  "tipoDocumento": "CC",
  "documento": "9876543210",
  "nombreCompleto": "Carlos Andrés Rodríguez López",
  "email": "carlos.rodriguez@clinica.com",
  "telefono": "3009876543",
  "especialidad": "Medicina General",
  "tarjetaProfesional": "TP-12345",
  "usuario": {
    "username": "crodriguez",
    "rol": "VETERINARIO",
    "activo": true
  },
  "activo": true
}
```

---

## 📋 HISTORIAS CLÍNICAS

### POST /api/historias-clinicas
**Descripción:** Crear historia clínica (usa Builder Pattern)

**Request Body:**
```json
{
  "idMascota": 1,
  "numeroHistoria": "HC-2025-001",
  "alergias": "Ninguna conocida",
  "enfermedadesCronicas": "Ninguna",
  "medicamentosActuales": "Ninguno",
  "observaciones": "Mascota saludable, sin antecedentes médicos relevantes"
}
```

**Response 201 Created:**
```json
{
  "idHistoriaClinica": 1,
  "numeroHistoria": "HC-2025-001",
  "mascota": {
    "idMascota": 1,
    "nombre": "Max",
    "propietario": "Juan Pérez"
  },
  "alergias": "Ninguna conocida",
  "enfermedadesCronicas": "Ninguna",
  "medicamentosActuales": "Ninguno",
  "observaciones": "Mascota saludable, sin antecedentes médicos relevantes",
  "activo": true,
  "fechaCreacion": "2025-11-19T14:30:00",
  "cantidadEvolucionesclincase": 0,
  "cantidadVacunaciones": 0
}
```

---

### PUT /api/historias-clinicas/{id}
**Descripción:** Actualizar historia clínica (usa Proxy Pattern para control de acceso + Memento Pattern para guardar estado)

**Path Parameter:** `id=1`

**Request Body:**
```json
{
  "alergias": "Alérgico a penicilina",
  "enfermedadesCronicas": "Displasia de cadera leve",
  "medicamentosActuales": "Condroprotector oral (1 tableta/día)",
  "observaciones": "Se detectó displasia en radiografía. Tratamiento preventivo iniciado."
}
```

**Response 200 OK:**
```json
{
  "idHistoriaClinica": 1,
  "numeroHistoria": "HC-2025-001",
  "alergias": "Alérgico a penicilina",
  "enfermedadesCronicas": "Displasia de cadera leve",
  "medicamentosActuales": "Condroprotector oral (1 tableta/día)",
  "observaciones": "Se detectó displasia en radiografía. Tratamiento preventivo iniciado.",
  "fechaUltimaModificacion": "2025-11-19T15:00:00",
  "mementosGuardados": 1,
  "mensaje": "Historia clínica actualizada. Estado anterior guardado en Memento."
}
```

---

### POST /api/historias-clinicas/{id}/restaurar-memento
**Descripción:** Restaurar estado anterior de historia clínica (Memento Pattern)

**Path Parameter:** `id=1`

**Response 200 OK:**
```json
{
  "mensaje": "Historia clínica restaurada al estado anterior",
  "numeroHistoria": "HC-2025-001",
  "fechaRestauracion": "2025-11-19T15:30:00",
  "estadoRestaurado": {
    "alergias": "Ninguna conocida",
    "enfermedadesCronicas": "Ninguna",
    "medicamentosActuales": "Ninguno"
  }
}
```

---

## 🏥 SERVICIOS

### POST /api/servicios
**Descripción:** Crear nuevo servicio (usa Factory Pattern)

**Request Body:**
```json
{
  "nombre": "Consulta General",
  "descripcion": "Consulta médica veterinaria general",
  "categoria": "CLINICO",
  "precio": 50000.00,
  "duracion": 30,
  "activo": true
}
```

**Response 201 Created:**
```json
{
  "idServicio": 1,
  "nombre": "Consulta General",
  "descripcion": "Consulta médica veterinaria general",
  "categoria": "CLINICO",
  "precio": 50000.00,
  "duracion": 30,
  "activo": true,
  "requiereAnestesia": false,
  "requiereAyuno": false,
  "disponibleEmergencias": true,
  "factoryUsada": "ServicioClinicoFactory"
}
```

---

### POST /api/servicios/factory
**Descripción:** Crear servicio usando Factory Pattern explícitamente

**Request Body:**
```json
{
  "nombre": "Cirugía de Esterilización",
  "descripcion": "Procedimiento quirúrgico de esterilización",
  "categoria": "QUIRURGICO",
  "precio": 350000.00
}
```

**Response 201 Created:**
```json
{
  "idServicio": 2,
  "nombre": "Cirugía de Esterilización",
  "categoria": "QUIRURGICO",
  "precio": 350000.00,
  "duracion": 120,
  "requiereAnestesia": true,
  "requiereAyuno": true,
  "horasAyunoRequeridas": 8,
  "requiereHospitalizacion": true,
  "disponibleEmergencias": false,
  "factoryMessage": "Servicio creado con ServicioQuirurgicoFactory"
}
```

---

## 📦 INVENTARIO

### POST /api/inventario
**Descripción:** Registrar nuevo inventario

**Request Body:**
```json
{
  "idInsumo": 1,
  "cantidad": 100,
  "stockMinimo": 20,
  "ubicacion": "Estante A3",
  "lote": "LOTE-2025-001",
  "fechaVencimiento": "2026-12-31"
}
```

**Response 201 Created:**
```json
{
  "idInventario": 1,
  "insumo": {
    "idInsumo": 1,
    "nombre": "Jeringas 5ml",
    "tipoInsumo": "Material Médico"
  },
  "cantidad": 100,
  "stockMinimo": 20,
  "ubicacion": "Estante A3",
  "lote": "LOTE-2025-001",
  "fechaVencimiento": "2026-12-31",
  "precioUnitario": 1500.00,
  "valorTotal": 150000.00,
  "estadoStock": "NORMAL",
  "alertaStockBajo": false
}
```

---

### GET /api/inventario/stock-bajo
**Descripción:** Listar inventario con stock bajo

**Response 200 OK:**
```json
[
  {
    "idInventario": 5,
    "insumo": "Guantes Quirúrgicos",
    "cantidadActual": 15,
    "stockMinimo": 50,
    "estadoStock": "CRITICO",
    "ubicacion": "Estante B1",
    "alertaStockBajo": true,
    "diferencia": -35
  },
  {
    "idInventario": 8,
    "insumo": "Antibiótico Amoxicilina",
    "cantidadActual": 18,
    "stockMinimo": 30,
    "estadoStock": "BAJO",
    "ubicacion": "Refrigerador 1",
    "alertaStockBajo": true,
    "diferencia": -12
  }
]
```

---

## 📬 NOTIFICACIONES

### POST /api/notificaciones
**Descripción:** Crear notificación (usa Abstract Factory Pattern)

**Request Body:**
```json
{
  "usuario": "juan.perez",
  "canal": "EMAIL",
  "destinatario": "juan.perez@example.com",
  "asunto": "Recordatorio de Cita",
  "mensaje": "Estimado Juan, le recordamos su cita para Max el día 20/11/2025 a las 10:30 AM.",
  "motivo": "RECORDATORIO_CITA"
}
```

**Response 201 Created:**
```json
{
  "idNotificacion": 1,
  "usuario": "juan.perez",
  "canal": "EMAIL",
  "destinatario": "juan.perez@example.com",
  "asunto": "Recordatorio de Cita",
  "mensaje": "Estimado Juan, le recordamos su cita para Max el día 20/11/2025 a las 10:30 AM.",
  "motivo": "RECORDATORIO_CITA",
  "enviada": false,
  "fechaCreacion": "2025-11-19T14:30:00",
  "factoryUsada": "EmailNotificacionFactory"
}
```

---

### POST /api/notificaciones/{id}/enviar
**Descripción:** Enviar notificación (usa Abstract Factory: Validador + Mensaje + Enviador)

**Path Parameter:** `id=1`

**Response 200 OK:**
```json
{
  "idNotificacion": 1,
  "enviada": true,
  "fechaEnvio": "2025-11-19T14:35:00",
  "idEnvioExterno": "EMAIL-1234567890",
  "mensaje": "Notificación enviada exitosamente por EMAIL",
  "validadorUsado": "EmailValidador",
  "enviadorUsado": "EmailEnviador"
}
```

---

## 📊 REPORTES (FACADES)

### GET /api/reportes/citas
**Descripción:** Generar reporte de citas (Facade Pattern)

**Query Parameters:**
- `fechaInicio`: 2025-11-01
- `fechaFin`: 2025-11-30

**Response 200 OK:**
```json
{
  "fechaInicio": "2025-11-01",
  "fechaFin": "2025-11-30",
  "totalCitas": 45,
  "citasAtendidas": 30,
  "citasProgramadas": 10,
  "citasCanceladas": 5,
  "porcentajeEfectividad": 66.67,
  "citas": [
    {
      "idCita": 1,
      "fecha": "2025-11-15",
      "mascota": "Max",
      "veterinario": "Dr. Carlos Rodríguez",
      "estado": "ATENDIDA"
    }
  ]
}
```

---

### GET /api/reportes/inventario
**Descripción:** Generar reporte de inventario

**Response 200 OK:**
```json
{
  "totalItems": 25,
  "valorTotalInventario": 15750000.00,
  "stockBajo": [
    {
      "insumo": "Guantes Quirúrgicos",
      "cantidadActual": 15,
      "stockMinimo": 50
    }
  ],
  "totalStockBajo": 3,
  "inventarios": [
    {
      "insumo": "Jeringas 5ml",
      "cantidad": 100,
      "valorTotal": 150000.00
    }
  ]
}
```

---

### GET /api/reportes/veterinarios
**Descripción:** Generar reporte de veterinarios (Facade Pattern)

**Query Parameters:**
- `fechaInicio`: 2025-11-01
- `fechaFin`: 2025-11-30

**Response 200 OK:**
```json
{
  "fechaInicio": "2025-11-01",
  "fechaFin": "2025-11-30",
  "totalAtenciones": 45,
  "estadisticasPorVeterinario": [
    {
      "idVeterinario": 1,
      "nombreVeterinario": "Dr. Carlos Rodríguez",
      "especialidad": "Medicina General",
      "totalCitasAtendidas": 20,
      "totalCitasProgramadas": 5,
      "porcentajeEfectividad": 80.0
    },
    {
      "idVeterinario": 2,
      "nombreVeterinario": "Dra. Ana Martínez",
      "especialidad": "Cirugía",
      "totalCitasAtendidas": 10,
      "totalCitasProgramadas": 5,
      "porcentajeEfectividad": 66.67
    }
  ]
}
```

---

### POST /api/reportes/builder/citas
**Descripción:** Generar reporte usando ReporteBuilder (Builder + Bridge Pattern)

**Request Body:**
```json
{
  "fechaInicio": "2025-11-01",
  "fechaFin": "2025-11-30",
  "formato": "PDF",
  "incluirGraficos": true
}
```

**Response 200 OK:**
```json
{
  "tipoReporte": "CITAS",
  "formato": "PDF",
  "titulo": "Reporte de Citas (2025-11-01 - 2025-11-30)",
  "fechaGeneracion": "2025-11-19T15:00:00",
  "incluirGraficos": true,
  "incluirResumen": true,
  "incluirDetalles": true,
  "columnas": ["Fecha", "Hora", "Mascota", "Veterinario", "Estado"],
  "implementor": "ReportePDFImpl",
  "mensaje": "Reporte generado exitosamente con Builder Pattern",
  "bridgePattern": "Usando Bridge para formato PDF"
}
```

---

## 🔍 BÚSQUEDAS (FACADE)

### GET /api/busquedas/global
**Descripción:** Búsqueda global en todo el sistema (Facade Pattern)

**Query Parameter:** `termino=Max`

**Response 200 OK:**
```json
{
  "termino": "Max",
  "resultadosEncontrados": 3,
  "mascotas": [
    {
      "idMascota": 1,
      "nombre": "Max",
      "propietario": "Juan Pérez",
      "especie": "Canino"
    }
  ],
  "propietarios": [
    {
      "idPropietario": 5,
      "nombreCompleto": "Maxwell Torres",
      "documento": "1234567890"
    }
  ],
  "citas": [
    {
      "idCita": 12,
      "mascota": "Max",
      "fecha": "2025-11-20",
      "estado": "PROGRAMADA"
    }
  ]
}
```

---

## 📈 DASHBOARD (FACADE)

### GET /api/dashboard/estadisticas
**Descripción:** Obtener estadísticas del dashboard (Facade Pattern)

**Response 200 OK:**
```json
{
  "fecha": "2025-11-19",
  "citasHoy": 8,
  "citasProgramadas": 12,
  "citasAtendidas": 35,
  "citasCanceladas": 3,
  "mascotasRegistradas": 150,
  "mascotasActivas": 142,
  "propietariosRegistrados": 85,
  "propietariosActivos": 80,
  "veterinariosActivos": 5,
  "serviciosActivos": 15,
  "stockBajo": 3,
  "ingresosMes": 15750000.00,
  "citasMes": 45
}
```

---

## 🛠️ NOTAS TÉCNICAS

### Patrones de Diseño Implementados

1. **Builder Pattern**:
   - `CitaBuilder` para construcción de citas
   - `HistoriaClinicaBuilder` para historias clínicas
   - `ReporteBuilder` para reportes complejos

2. **Factory Pattern**:
   - `ServicioFactory` para creación de servicios por categoría
   - 4 factories específicas: Clínico, Quirúrgico, Estético, Emergencia

3. **Abstract Factory Pattern**:
   - `NotificacionFactory` para sistema de notificaciones multicanal
   - Crea: Validador + Mensaje + Enviador

4. **Singleton Pattern**:
   - `AuditLogger` para logging centralizado
   - `ConfigurationManager` para configuraciones globales

5. **Facade Pattern**:
   - `CitaFacadeService` para operaciones complejas de citas
   - `DashboardFacadeService` para estadísticas
   - `BusquedaFacadeService` para búsquedas multi-entidad
   - `ReporteFacadeService` para generación de reportes

6. **Proxy Pattern**:
   - `HistoriaClinicaProxy` para control de acceso (Protection Proxy)
   - `CachedServiceProxy` para caché (Virtual Proxy)
   - `InventarioProxy` para auditoría y seguridad

7. **Decorator Pattern**:
   - `ServicioDecorator` para recargos dinámicos (urgencia, domicilio, seguros)

8. **Observer Pattern**:
   - `CitaSubject` + 3 observers: Auditoría, Notificación, Recordatorio

9. **Mediator Pattern**:
   - `CitaMediator` para coordinar operaciones de citas

10. **Chain of Responsibility**:
    - `ValidacionHandler` para validaciones en cadena

11. **Template Method**:
    - `AtencionTemplate` para flujos de atención (Consulta, Cirugía, Emergencia)

12. **Memento Pattern**:
    - `HistoriaClinicaMemento` para undo/redo de historias clínicas

13. **Bridge Pattern**:
    - `ReporteImplementor` para separar abstracción de reportes de implementación (PDF/Excel/JSON)

### Headers Requeridos

```
Authorization: Bearer {token}
Content-Type: application/json
Accept: application/json
```

### Códigos de Estado HTTP

- `200 OK`: Operación exitosa
- `201 Created`: Recurso creado
- `204 No Content`: Operación exitosa sin contenido
- `400 Bad Request`: Datos inválidos
- `401 Unauthorized`: No autenticado
- `403 Forbidden`: Sin permisos
- `404 Not Found`: Recurso no encontrado
- `409 Conflict`: Conflicto (duplicado)
- `500 Internal Server Error`: Error del servidor

### Roles Disponibles

- `ADMIN`: Acceso total
- `VETERINARIO`: Gestión clínica completa
- `RECEPCIONISTA`: Gestión de citas y clientes
- `AUXILIAR_VETERINARIO`: Apoyo en procedimientos
- `PROPIETARIO`: Acceso limitado a sus mascotas

---

**📝 Nota:** Este documento contiene ejemplos listos para copiar y pegar en Swagger UI durante la presentación. Todos los endpoints están documentados con anotaciones Swagger en el código fuente.

**🔗 URL Swagger:** http://localhost:8080/swagger-ui.html

**Última actualización:** 2025-11-19
**Versión del documento:** 1.0
