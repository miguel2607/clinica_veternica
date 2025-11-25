# ANÁLISIS DEL FRONTEND POR ROL - CLÍNICA VETERINARIA

## Fecha de Análisis
**Fecha:** 24 de Noviembre de 2025

---

## RESUMEN EJECUTIVO

Este documento presenta un análisis detallado del estado actual del frontend para cada rol del sistema de Clínica Veterinaria, comparando las funcionalidades implementadas con los permisos y endpoints disponibles en el backend.

### Estado General:
- **ADMIN**: ✅ Completamente implementado (16 páginas)
- **VETERINARIO**: ⚠️ Implementación parcial (5 de 11 páginas)
- **RECEPCIONISTA**: ⚠️ Implementación parcial (4 de 9 páginas)
- **AUXILIAR**: ⚠️ Implementación limitada (2 de 8 páginas)
- **PROPIETARIO**: ⚠️ Implementación básica (4 de 8 páginas)

---

## 1. ROL ADMINISTRADOR (ADMIN)

### ✅ PÁGINAS IMPLEMENTADAS (16 páginas)

#### Dashboard y Reportes
1. **Dashboard** (`/admin/dashboard`)
   - Vista general del sistema

2. **Reportes** (`/admin/reportes`)
   - Generación de reportes diversos

#### Gestión de Usuarios
3. **Usuarios** (`/admin/usuarios`)
   - CRUD completo de usuarios del sistema

4. **Veterinarios** (`/admin/veterinarios`)
   - Gestión específica de veterinarios

5. **Propietarios** (`/admin/propietarios`)
   - Gestión de propietarios/clientes

#### Gestión de Pacientes
6. **Mascotas** (`/admin/mascotas`)
   - CRUD de mascotas

7. **Especies** (`/admin/especies`)
   - Gestión de especies animales

8. **Razas** (`/admin/razas`)
   - Gestión de razas por especie

#### Gestión de Servicios y Citas
9. **Servicios** (`/admin/servicios`)
   - Catálogo de servicios veterinarios

10. **Horarios** (`/admin/horarios`)
    - Gestión de horarios de veterinarios

11. **Citas** (`/admin/citas`)
    - CRUD y gestión de citas

#### Gestión Clínica
12. **Historias Clínicas** (`/admin/historias-clinicas`)
    - Gestión de historias clínicas

13. **Vacunaciones** (`/admin/vacunaciones`)
    - Registro y seguimiento de vacunaciones

#### Inventario
14. **Inventario** (`/admin/inventario`)
    - Vista del inventario general

15. **Tipos de Insumo** (`/admin/tipos-insumo`)
    - Gestión de categorías de insumos

16. **Insumos** (`/admin/insumos`)
    - CRUD de insumos

#### Comunicaciones
17. **Notificaciones** (`/admin/notificaciones`)
    - Sistema de notificaciones

### ✅ ESTADO: COMPLETO
El rol de Administrador tiene acceso a todas las funcionalidades necesarias según los permisos del backend.

---

## 2. ROL VETERINARIO

### ✅ PÁGINAS IMPLEMENTADAS (5 páginas)

1. **Dashboard** (`/veterinario/dashboard`)
   - Vista general del veterinario

2. **Mis Citas** (`/veterinario/citas`)
   - Citas asignadas al veterinario

3. **Historias Clínicas** (`/veterinario/historias`)
   - Gestión de historias clínicas

4. **Vacunaciones** (`/veterinario/vacunaciones`)
   - Registro de vacunaciones

5. **Notificaciones** (`/veterinario/notificaciones`)
   - Ver notificaciones del sistema

### ❌ PÁGINAS FALTANTES (6 páginas recomendadas)

#### PRIORIDAD ALTA

6. **Mis Horarios** (`/veterinario/horarios`)
   - **Backend:** `GET /api/horarios/veterinario/{idVeterinario}` ✅
   - **Justificación:** El veterinario necesita ver y gestionar su disponibilidad
   - **Funcionalidades:**
     - Ver horarios asignados
     - Solicitar cambios de horario
     - Ver disponibilidad semanal

7. **Mis Pacientes/Mascotas** (`/veterinario/mascotas`)
   - **Backend:** `GET /api/mascotas`, `POST /api/mascotas`, `PUT /api/mascotas/{id}` ✅
   - **Justificación:** Necesita buscar y ver información de las mascotas que atiende
   - **Funcionalidades:**
     - Buscar mascotas por nombre
     - Ver historial de atenciones
     - Ver próximas citas de la mascota
     - Actualizar información de la mascota

8. **Evoluciones Clínicas** (`/veterinario/evoluciones`)
   - **Backend:** `POST /api/evoluciones-clinicas`, `GET /api/evoluciones-clinicas/historia-clinica/{id}` ✅
   - **Justificación:** Parte fundamental del seguimiento clínico
   - **Funcionalidades:**
     - Crear evoluciones/seguimientos
     - Ver evoluciones previas
     - Adjuntar observaciones

#### PRIORIDAD MEDIA

9. **Propietarios** (`/veterinario/propietarios`)
   - **Backend:** `GET /api/propietarios` ✅
   - **Justificación:** Necesita ver información de contacto de los dueños
   - **Funcionalidades:**
     - Buscar propietarios
     - Ver datos de contacto
     - Ver mascotas del propietario

10. **Insumos/Inventario** (`/veterinario/insumos`)
    - **Backend:** `GET /api/inventario/insumos`, `POST /api/inventario/insumos`, `PUT /api/inventario/insumos/{id}` ✅
    - **Justificación:** Necesita consultar disponibilidad de medicamentos e insumos
    - **Funcionalidades:**
      - Ver inventario disponible
      - Buscar insumos/medicamentos
      - Ver stock en tiempo real
      - Reportar uso de insumos

11. **Mis Reportes** (`/veterinario/reportes`)
    - **Backend:** `GET /api/facade/reportes/veterinarios` ✅
    - **Justificación:** Ver estadísticas de sus atenciones
    - **Funcionalidades:**
      - Reporte de atenciones realizadas
      - Estadísticas de citas
      - Casos atendidos por tipo

### 📊 ESTADO: 45% COMPLETADO (5 de 11 páginas)

---

## 3. ROL RECEPCIONISTA

### ✅ PÁGINAS IMPLEMENTADAS (4 páginas)

1. **Dashboard** (`/recepcionista/dashboard`)
   - Vista general de recepción

2. **Citas** (`/recepcionista/citas`)
   - Gestión de citas

3. **Propietarios** (`/recepcionista/propietarios`)
   - Gestión de propietarios

4. **Mascotas** (`/recepcionista/mascotas`)
   - Gestión de mascotas

### ❌ PÁGINAS FALTANTES (5 páginas recomendadas)

#### PRIORIDAD ALTA

5. **Servicios** (`/recepcionista/servicios`)
   - **Backend:** `GET /api/servicios/activos` ✅
   - **Justificación:** Necesita consultar servicios al agendar citas
   - **Funcionalidades:**
     - Ver catálogo de servicios
     - Ver precios
     - Ver duración de servicios
     - Filtrar por categoría

6. **Horarios de Veterinarios** (`/recepcionista/horarios`)
   - **Backend:** `GET /api/horarios`, `GET /api/horarios/veterinario/{id}` ✅
   - **Justificación:** Esencial para programar citas correctamente
   - **Funcionalidades:**
     - Ver disponibilidad de veterinarios
     - Ver horarios por día/semana
     - Buscar horarios disponibles

7. **Especies y Razas** (`/recepcionista/especies-razas`)
   - **Backend:** `GET /api/especies/activas`, `GET /api/razas/especie/{id}` ✅
   - **Justificación:** Necesario al registrar nuevas mascotas
   - **Funcionalidades:**
     - Consultar especies disponibles
     - Consultar razas por especie
     - Usado en formularios de registro

#### PRIORIDAD MEDIA

8. **Notificaciones** (`/recepcionista/notificaciones`)
   - **Backend:** `POST /api/notificaciones`, `GET /api/notificaciones` ✅
   - **Justificación:** Enviar recordatorios de citas a propietarios
   - **Funcionalidades:**
     - Enviar notificaciones a propietarios
     - Ver historial de notificaciones enviadas
     - Recordatorios automáticos de citas

9. **Registro Rápido** (`/recepcionista/registro-completo`)
   - **Backend:** `POST /api/facade/mascotas/registro-completo` ✅
   - **Justificación:** Agilizar el registro de nuevos clientes
   - **Funcionalidades:**
     - Formulario unificado: Propietario + Mascota + Historia Clínica
     - Registro rápido en recepción
     - Reducir tiempo de espera

### 📊 ESTADO: 44% COMPLETADO (4 de 9 páginas)

---

## 4. ROL AUXILIAR VETERINARIO

### ✅ PÁGINAS IMPLEMENTADAS (2 páginas)

1. **Dashboard** (`/auxiliar/dashboard`)
   - Vista general del auxiliar

2. **Inventario** (`/auxiliar/inventario`)
   - Vista del inventario

### ❌ PÁGINAS FALTANTES (6 páginas recomendadas)

#### PRIORIDAD ALTA

3. **Gestión de Insumos** (`/auxiliar/insumos`)
   - **Backend:** `POST /api/inventario/insumos`, `PUT /api/inventario/insumos/{id}`, `GET /api/inventario/insumos/stock-bajo` ✅
   - **Justificación:** Responsable principal del inventario
   - **Funcionalidades:**
     - CRUD de insumos
     - Actualizar cantidades
     - Ver alertas de stock bajo
     - Registrar entradas/salidas

4. **Tipos de Insumo** (`/auxiliar/tipos-insumo`)
   - **Backend:** `POST /api/inventario/tipos-insumo`, `PUT /api/inventario/tipos-insumo/{id}` ✅
   - **Justificación:** Necesita gestionar categorías de insumos
   - **Funcionalidades:**
     - CRUD de tipos de insumo
     - Categorización de inventario

5. **Historias Clínicas** (`/auxiliar/historias`)
   - **Backend:** `PUT /api/historias-clinicas/{id}` ✅
   - **Justificación:** Apoyo en actualización de historias clínicas
   - **Funcionalidades:**
     - Ver historias clínicas
     - Actualizar información básica
     - Agregar notas de apoyo

6. **Evoluciones Clínicas** (`/auxiliar/evoluciones`)
   - **Backend:** `POST /api/evoluciones-clinicas` ✅
   - **Justificación:** Registrar seguimientos y evoluciones
   - **Funcionalidades:**
     - Crear evoluciones clínicas
     - Ver evoluciones previas
     - Registrar signos vitales

#### PRIORIDAD MEDIA

7. **Vacunaciones** (`/auxiliar/vacunaciones`)
   - **Backend:** `POST /api/vacunaciones` ✅
   - **Justificación:** Aplicar y registrar vacunas
   - **Funcionalidades:**
     - Registrar vacunaciones aplicadas
     - Ver calendario de vacunaciones
     - Alertas de vacunas pendientes

8. **Alertas Médicas** (`/auxiliar/alertas`)
   - **Backend:** `GET /api/facade/mascotas/alertas-medicas` ✅
   - **Justificación:** Monitorear alertas del sistema
   - **Funcionalidades:**
     - Ver alertas de vacunas pendientes
     - Ver seguimientos pendientes
     - Stock crítico de medicamentos

### 📊 ESTADO: 25% COMPLETADO (2 de 8 páginas)

---

## 5. ROL PROPIETARIO

### ✅ PÁGINAS IMPLEMENTADAS (4 páginas)

1. **Dashboard** (`/propietario/dashboard`)
   - Vista general del propietario

2. **Mis Mascotas** (`/propietario/mascotas`)
   - Ver y gestionar sus mascotas

3. **Mis Citas** (`/propietario/citas`)
   - Ver y gestionar sus citas

4. **Vacunaciones** (`/propietario/vacunaciones`)
   - Ver vacunaciones de sus mascotas

### ❌ PÁGINAS FALTANTES (4 páginas recomendadas)

#### PRIORIDAD ALTA

5. **Historias Clínicas** (`/propietario/historias`)
   - **Backend:** `GET /api/historias-clinicas/mascota/{idMascota}` (con permisos PROPIETARIO) ✅
   - **Justificación:** Derecho a ver el historial médico de sus mascotas
   - **Funcionalidades:**
     - Ver historias clínicas de sus mascotas
     - Ver evoluciones y tratamientos
     - Descargar historias clínicas (PDF)

6. **Mi Perfil** (`/propietario/perfil`)
   - **Backend:** `GET /api/propietarios/mi-perfil`, `PUT /api/propietarios/{id}` ✅
   - **Justificación:** Actualizar su información personal
   - **Funcionalidades:**
     - Ver/editar información personal
     - Actualizar datos de contacto
     - Cambiar contraseña
     - Gestionar preferencias de notificaciones

#### PRIORIDAD MEDIA

7. **Notificaciones** (`/propietario/notificaciones`)
   - **Backend:** `GET /api/notificaciones/usuario/{idUsuario}` ✅
   - **Justificación:** Ver recordatorios y avisos importantes
   - **Funcionalidades:**
     - Ver notificaciones recibidas
     - Recordatorios de citas
     - Alertas de vacunación
     - Mensajes de la clínica

8. **Agendar Cita** (`/propietario/agendar-cita`)
   - **Backend:** `POST /api/citas`, `GET /api/servicios/activos`, `GET /api/horarios` ✅
   - **Justificación:** Permitir auto-agendamiento de citas
   - **Funcionalidades:**
     - Ver servicios disponibles
     - Ver horarios disponibles
     - Agendar cita online
     - Seleccionar veterinario preferido

### 📊 ESTADO: 50% COMPLETADO (4 de 8 páginas)

---

## RESUMEN DE PRIORIDADES DE IMPLEMENTACIÓN

### 🔴 PRIORIDAD CRÍTICA (Completar primero)

1. **Veterinario - Mis Horarios**
   - Impacto: Alto - El veterinario debe poder ver su agenda

2. **Recepcionista - Horarios de Veterinarios**
   - Impacto: Alto - Esencial para agendar citas correctamente

3. **Recepcionista - Servicios**
   - Impacto: Alto - Necesario para asignar servicios a citas

4. **Auxiliar - Gestión de Insumos**
   - Impacto: Alto - Función principal del rol

5. **Propietario - Mi Perfil**
   - Impacto: Alto - Funcionalidad básica de autogestión

### 🟡 PRIORIDAD ALTA (Completar segundo)

6. **Veterinario - Mis Pacientes/Mascotas**
7. **Veterinario - Evoluciones Clínicas**
8. **Auxiliar - Tipos de Insumo**
9. **Auxiliar - Historias Clínicas (vista auxiliar)**
10. **Propietario - Historias Clínicas (vista lectura)**
11. **Recepcionista - Especies y Razas**

### 🟢 PRIORIDAD MEDIA (Mejoras incrementales)

12. **Veterinario - Propietarios**
13. **Veterinario - Insumos/Inventario**
14. **Veterinario - Mis Reportes**
15. **Recepcionista - Notificaciones**
16. **Recepcionista - Registro Rápido**
17. **Auxiliar - Evoluciones Clínicas**
18. **Auxiliar - Vacunaciones**
19. **Auxiliar - Alertas Médicas**
20. **Propietario - Notificaciones**
21. **Propietario - Agendar Cita**

---

## ESTADÍSTICAS GENERALES

### Páginas Implementadas vs Necesarias

| Rol | Implementadas | Necesarias | % Completado | Faltantes |
|-----|---------------|------------|--------------|-----------|
| **ADMIN** | 16 | 16 | 100% | 0 |
| **VETERINARIO** | 5 | 11 | 45% | 6 |
| **RECEPCIONISTA** | 4 | 9 | 44% | 5 |
| **AUXILIAR** | 2 | 8 | 25% | 6 |
| **PROPIETARIO** | 4 | 8 | 50% | 4 |
| **TOTAL** | 31 | 52 | 60% | 21 |

### Distribución de Prioridades

- **Prioridad Crítica:** 5 páginas (24%)
- **Prioridad Alta:** 6 páginas (29%)
- **Prioridad Media:** 10 páginas (47%)

---

## ENDPOINTS DEL BACKEND DISPONIBLES NO UTILIZADOS

### Por Módulo

#### 1. Módulo de Búsquedas (Facade)
- `GET /api/facade/busquedas/global` - Búsqueda global en el sistema
  - **Uso sugerido:** Barra de búsqueda global en todos los roles
  - **Beneficio:** Búsqueda unificada de mascotas, propietarios, citas

#### 2. Módulo de Operaciones Complejas (Facade)
- `GET /api/facade/mascotas/{id}/completa` - Información completa de mascota
  - **Uso sugerido:** Vista detallada de mascota (Veterinario, Recepcionista)
  - **Beneficio:** Datos + Historia + Citas en una sola petición

- `POST /api/facade/mascotas/registro-completo` - Registro completo
  - **Uso sugerido:** Página de registro rápido (Recepcionista)
  - **Beneficio:** Registrar Propietario + Mascota + Historia en un paso

- `GET /api/facade/mascotas/alertas-medicas` - Alertas médicas
  - **Uso sugerido:** Dashboard de Veterinario y Auxiliar
  - **Beneficio:** Ver vacunas pendientes, seguimientos urgentes

#### 3. Módulo de Reportes (Facade)
- `GET /api/facade/reportes/veterinarios` - Reporte por veterinario
  - **Uso sugerido:** Página de reportes del veterinario
  - **Beneficio:** Ver estadísticas de atenciones propias

- `GET /api/facade/reportes/inventario` - Reporte de inventario
  - **Uso sugerido:** Dashboard del Auxiliar
  - **Beneficio:** Valorización del inventario

#### 4. Módulo de Citas - Funciones Avanzadas
- `PUT /api/citas/{id}/iniciar-atencion` - Iniciar atención
- `PUT /api/citas/{id}/finalizar-atencion` - Finalizar atención
  - **Uso sugerido:** Página de citas del veterinario
  - **Beneficio:** Control de estados de atención en tiempo real

#### 5. Módulo de Historias Clínicas - Memento Pattern
- `POST /api/historias-clinicas/{id}/memento` - Guardar estado
- `PUT /api/historias-clinicas/{id}/restaurar-ultimo` - Restaurar último
- `PUT /api/historias-clinicas/{id}/restaurar/{indice}` - Restaurar específico
  - **Uso sugerido:** Página de historias clínicas
  - **Beneficio:** Control de versiones de historias clínicas

---

## FUNCIONALIDADES ADICIONALES RECOMENDADAS

### 1. Componentes Compartidos

#### Barra de Búsqueda Global
- Implementar en todos los layouts
- Usar endpoint: `GET /api/facade/busquedas/global`
- Búsqueda unificada de: Mascotas, Propietarios, Citas, Historias

#### Notificaciones en Tiempo Real
- Badge de notificaciones en el header
- Usar endpoint: `GET /api/notificaciones/usuario/{id}`
- Mostrar contador de notificaciones no leídas

#### Panel de Alertas
- Componente reutilizable para mostrar:
  - Stock bajo de insumos
  - Citas próximas
  - Vacunaciones pendientes
  - Seguimientos urgentes

### 2. Funcionalidades por Rol

#### Para VETERINARIO
- **Timeline de Historia Clínica:** Línea de tiempo visual de todas las atenciones
- **Calendario de Citas:** Vista de calendario con sus citas del día/semana
- **Quick Actions:** Acciones rápidas desde el dashboard (iniciar atención, ver próxima cita)

#### Para RECEPCIONISTA
- **Vista de Calendario:** Calendario interactivo para agendar citas
- **Lista de Espera:** Gestión de pacientes en espera
- **Confirmación de Citas:** Dashboard con citas pendientes de confirmar

#### Para AUXILIAR
- **Dashboard de Inventario:** Vista con gráficos de stock
- **Alertas Críticas:** Panel de insumos agotados o por vencer
- **Log de Movimientos:** Historial de entradas/salidas de inventario

#### Para PROPIETARIO
- **Portal de Autogestión:** Permitir más autonomía
- **Historial de Pagos:** Ver pagos realizados (si se implementa módulo de pagos)
- **Descargar Documentos:** Certificados de vacunación, historias clínicas en PDF

---

## RECOMENDACIONES DE ARQUITECTURA

### 1. Servicios API Centralizados
Crear servicios centralizados en el frontend para cada módulo:
```
frontend/src/services/
  ├── auth.service.js ✅ (ya existe)
  ├── usuarios.service.js
  ├── mascotas.service.js
  ├── citas.service.js
  ├── historias.service.js
  ├── inventario.service.js
  ├── notificaciones.service.js
  └── reportes.service.js
```

### 2. Componentes Reutilizables
Crear biblioteca de componentes compartidos:
```
frontend/src/components/
  ├── forms/
  │   ├── MascotaForm.jsx
  │   ├── PropietarioForm.jsx
  │   ├── CitaForm.jsx
  │   └── InsumoForm.jsx
  ├── tables/
  │   ├── MascotasTable.jsx
  │   ├── CitasTable.jsx
  │   └── InsumosTable.jsx
  └── modals/
      ├── ConfirmModal.jsx
      ├── DetailModal.jsx
      └── FormModal.jsx
```

### 3. Hooks Personalizados
Crear hooks reutilizables:
```javascript
// useMascotas.js
export function useMascotas() {
  // Lógica compartida para obtener, crear, actualizar mascotas
}

// useCitas.js
export function useCitas() {
  // Lógica compartida para gestión de citas
}

// useInventario.js
export function useInventario() {
  // Lógica compartida para gestión de inventario
}
```

### 4. Context API para Estado Global
Además del AuthContext existente, considerar:
```
frontend/src/context/
  ├── AuthContext.jsx ✅ (ya existe)
  ├── NotificationContext.jsx (para notificaciones en tiempo real)
  ├── ThemeContext.jsx (para tema claro/oscuro)
  └── AlertContext.jsx (para alertas del sistema)
```

---

## PLAN DE IMPLEMENTACIÓN SUGERIDO

### FASE 1: Funcionalidades Críticas (2-3 semanas)
**Objetivo:** Completar funcionalidades esenciales para operación diaria

1. **Semana 1: Veterinario**
   - Mis Horarios
   - Mis Pacientes/Mascotas
   - Evoluciones Clínicas

2. **Semana 2: Recepcionista**
   - Horarios de Veterinarios
   - Servicios
   - Especies y Razas

3. **Semana 3: Auxiliar y Propietario**
   - Gestión de Insumos (Auxiliar)
   - Tipos de Insumo (Auxiliar)
   - Mi Perfil (Propietario)

### FASE 2: Funcionalidades Importantes (2-3 semanas)
**Objetivo:** Mejorar experiencia de usuario y eficiencia

4. **Semana 4: Completar Auxiliar**
   - Historias Clínicas (vista auxiliar)
   - Evoluciones Clínicas
   - Vacunaciones

5. **Semana 5: Completar Propietario**
   - Historias Clínicas (lectura)
   - Notificaciones
   - Agendar Cita

6. **Semana 6: Mejoras Veterinario**
   - Propietarios
   - Insumos/Inventario
   - Mis Reportes

### FASE 3: Optimizaciones y Mejoras (2 semanas)
**Objetivo:** Pulir la experiencia y agregar funcionalidades avanzadas

7. **Semana 7: Componentes Compartidos**
   - Barra de búsqueda global
   - Sistema de notificaciones en tiempo real
   - Panel de alertas
   - Componentes reutilizables

8. **Semana 8: Mejoras UX**
   - Recepcionista: Notificaciones y Registro Rápido
   - Auxiliar: Alertas Médicas
   - Gráficos y dashboards mejorados
   - Testing y corrección de bugs

---

## CONCLUSIÓN

El sistema tiene una base sólida con el rol ADMIN completamente implementado (100%). Sin embargo, los roles operativos necesitan atención:

### Brechas Principales:
1. **Auxiliar (75% faltante):** El rol más incompleto, necesita casi todas sus funcionalidades
2. **Veterinario (55% faltante):** Falta gestión de horarios, pacientes y evoluciones
3. **Propietario (50% faltante):** Necesita autogestión (perfil, historias)
4. **Recepcionista (56% faltante):** Falta información crítica para agendar citas

### Recomendación:
Priorizar la implementación en el siguiente orden:
1. **FASE 1 (Crítica):** Completar funcionalidades mínimas de Veterinario y Recepcionista
2. **FASE 2 (Alta):** Completar Auxiliar y Propietario
3. **FASE 3 (Media):** Mejoras de UX y funcionalidades avanzadas

### Impacto Esperado:
Al completar las 21 páginas faltantes, el sistema pasará del 60% al 100% de funcionalidad, mejorando significativamente la experiencia de usuario y la eficiencia operativa de la clínica veterinaria.

---

## ANEXO: MATRIZ DE PERMISOS POR ENDPOINT

### Veterinario - Endpoints disponibles pero sin UI
| Endpoint | Método | Permiso | Página Sugerida |
|----------|--------|---------|-----------------|
| `/api/mascotas` | GET/POST/PUT | ✅ | `/veterinario/mascotas` |
| `/api/evoluciones-clinicas` | GET/POST | ✅ | `/veterinario/evoluciones` |
| `/api/horarios/veterinario/{id}` | GET | ✅ | `/veterinario/horarios` |
| `/api/inventario/insumos` | GET | ✅ | `/veterinario/insumos` |
| `/api/propietarios` | GET | ✅ | `/veterinario/propietarios` |
| `/api/facade/reportes/veterinarios` | GET | ✅ | `/veterinario/reportes` |

### Recepcionista - Endpoints disponibles pero sin UI
| Endpoint | Método | Permiso | Página Sugerida |
|----------|--------|---------|-----------------|
| `/api/servicios/activos` | GET | ✅ | `/recepcionista/servicios` |
| `/api/horarios` | GET | ✅ | `/recepcionista/horarios` |
| `/api/especies/activas` | GET | ✅ | `/recepcionista/especies-razas` |
| `/api/razas/especie/{id}` | GET | ✅ | `/recepcionista/especies-razas` |
| `/api/notificaciones` | GET/POST | ✅ | `/recepcionista/notificaciones` |
| `/api/facade/mascotas/registro-completo` | POST | ✅ | `/recepcionista/registro-completo` |

### Auxiliar - Endpoints disponibles pero sin UI
| Endpoint | Método | Permiso | Página Sugerida |
|----------|--------|---------|-----------------|
| `/api/inventario/insumos` | POST/PUT | ✅ | `/auxiliar/insumos` |
| `/api/inventario/tipos-insumo` | POST/PUT | ✅ | `/auxiliar/tipos-insumo` |
| `/api/historias-clinicas/{id}` | PUT | ✅ | `/auxiliar/historias` |
| `/api/evoluciones-clinicas` | POST | ✅ | `/auxiliar/evoluciones` |
| `/api/vacunaciones` | POST | ✅ | `/auxiliar/vacunaciones` |
| `/api/facade/mascotas/alertas-medicas` | GET | ✅ | `/auxiliar/alertas` |

### Propietario - Endpoints disponibles pero sin UI
| Endpoint | Método | Permiso | Página Sugerida |
|----------|--------|---------|-----------------|
| `/api/historias-clinicas/mascota/{id}` | GET | ✅ | `/propietario/historias` |
| `/api/propietarios/mi-perfil` | GET | ✅ | `/propietario/perfil` |
| `/api/propietarios/{id}` | PUT | ✅ | `/propietario/perfil` |
| `/api/notificaciones/usuario/{id}` | GET | ✅ | `/propietario/notificaciones` |
| `/api/citas` | POST | ✅ | `/propietario/agendar-cita` |
| `/api/servicios/activos` | GET | ✅ | `/propietario/agendar-cita` |

---

**Documento generado el:** 24 de Noviembre de 2025
**Proyecto:** Sistema de Clínica Veterinaria
**Versión:** 1.0
