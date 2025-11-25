# PLAN DE IMPLEMENTACIÓN FRONTEND - CLÍNICA VETERINARIA

## Fecha: 24 de Noviembre de 2025

---

## RESUMEN

Este documento detalla el plan de implementación paso a paso para completar el frontend del sistema de Clínica Veterinaria. Se implementarán **21 páginas nuevas** distribuidas en **5 fases**.

**Estado actual:** 31/52 páginas (60% completado)
**Estado objetivo:** 52/52 páginas (100% completado)

---

## FASE 1: PRIORIDAD CRÍTICA (5 páginas)

### Objetivo: Completar funcionalidades esenciales para operación diaria

#### 1.1 Veterinario - Mis Horarios
- **Ruta:** `/veterinario/horarios`
- **Archivo:** `frontend/src/pages/veterinario/Horarios.jsx`
- **Endpoints:**
  - `GET /api/horarios/veterinario/{idVeterinario}`
  - `GET /api/horarios`
- **Funcionalidades:**
  - Ver horarios asignados por día de la semana
  - Vista de calendario semanal
  - Mostrar horarios de inicio y fin
  - Estado activo/inactivo
- **Componentes necesarios:**
  - Tabla de horarios
  - Vista de calendario semanal
  - Badge de estado

#### 1.2 Recepcionista - Horarios de Veterinarios
- **Ruta:** `/recepcionista/horarios`
- **Archivo:** `frontend/src/pages/recepcionista/Horarios.jsx`
- **Endpoints:**
  - `GET /api/horarios`
  - `GET /api/horarios/veterinario/{idVeterinario}`
  - `GET /api/veterinarios`
- **Funcionalidades:**
  - Ver horarios de todos los veterinarios
  - Filtrar por veterinario
  - Filtrar por día de la semana
  - Ver disponibilidad para agendar citas
- **Componentes necesarios:**
  - Selector de veterinario
  - Tabla de horarios con filtros
  - Indicador de disponibilidad

#### 1.3 Recepcionista - Servicios
- **Ruta:** `/recepcionista/servicios`
- **Archivo:** `frontend/src/pages/recepcionista/Servicios.jsx`
- **Endpoints:**
  - `GET /api/servicios/activos`
  - `GET /api/servicios/tipo/{tipoServicio}`
  - `GET /api/servicios/categoria/{categoria}`
- **Funcionalidades:**
  - Listar servicios activos
  - Buscar servicios por nombre
  - Filtrar por categoría
  - Ver precio y duración
  - Vista detallada de servicio
- **Componentes necesarios:**
  - Tabla de servicios
  - Filtros por categoría
  - Card de servicio con detalles
  - Buscador

#### 1.4 Auxiliar - Gestión de Insumos
- **Ruta:** `/auxiliar/insumos`
- **Archivo:** `frontend/src/pages/auxiliar/Insumos.jsx`
- **Endpoints:**
  - `GET /api/inventario/insumos`
  - `POST /api/inventario/insumos`
  - `PUT /api/inventario/insumos/{id}`
  - `GET /api/inventario/insumos/stock-bajo`
  - `GET /api/inventario/insumos/agotados`
  - `PATCH /api/inventario/insumos/{id}/activar`
  - `PATCH /api/inventario/insumos/{id}/desactivar`
- **Funcionalidades:**
  - CRUD completo de insumos
  - Alertas de stock bajo
  - Ver insumos agotados
  - Actualizar cantidades
  - Activar/desactivar insumos
  - Registro de entradas/salidas
- **Componentes necesarios:**
  - Formulario de insumo
  - Tabla con alertas de stock
  - Modal de entrada/salida
  - Badges de estado

#### 1.5 Propietario - Mi Perfil
- **Ruta:** `/propietario/perfil`
- **Archivo:** `frontend/src/pages/propietario/Perfil.jsx`
- **Endpoints:**
  - `GET /api/propietarios/mi-perfil`
  - `PUT /api/propietarios/{id}`
  - `PATCH /api/usuarios/{id}/cambiar-password`
- **Funcionalidades:**
  - Ver datos personales
  - Editar información de contacto
  - Cambiar contraseña
  - Actualizar preferencias
- **Componentes necesarios:**
  - Formulario de perfil
  - Formulario de cambio de contraseña
  - Sección de preferencias

---

## FASE 2: PRIORIDAD ALTA (6 páginas)

### Objetivo: Completar funcionalidades importantes de cada rol

#### 2.1 Veterinario - Mis Pacientes/Mascotas
- **Ruta:** `/veterinario/mascotas`
- **Archivo:** `frontend/src/pages/veterinario/Mascotas.jsx`
- **Endpoints:**
  - `GET /api/mascotas`
  - `POST /api/mascotas`
  - `PUT /api/mascotas/{id}`
  - `GET /api/mascotas/buscar?nombre=`
  - `GET /api/facade/mascotas/{id}/completa`
- **Funcionalidades:**
  - Buscar mascotas
  - Ver información completa
  - Ver historial de atenciones
  - Ver próximas citas
  - Crear/actualizar mascota

#### 2.2 Veterinario - Evoluciones Clínicas
- **Ruta:** `/veterinario/evoluciones`
- **Archivo:** `frontend/src/pages/veterinario/Evoluciones.jsx`
- **Endpoints:**
  - `POST /api/evoluciones-clinicas`
  - `GET /api/evoluciones-clinicas/historia-clinica/{id}`
- **Funcionalidades:**
  - Crear evoluciones clínicas
  - Ver evoluciones previas
  - Adjuntar observaciones
  - Timeline de evoluciones

#### 2.3 Auxiliar - Tipos de Insumo
- **Ruta:** `/auxiliar/tipos-insumo`
- **Archivo:** `frontend/src/pages/auxiliar/TiposInsumo.jsx`
- **Endpoints:**
  - `GET /api/inventario/tipos-insumo`
  - `POST /api/inventario/tipos-insumo`
  - `PUT /api/inventario/tipos-insumo/{id}`
- **Funcionalidades:**
  - CRUD de tipos de insumo
  - Categorización

#### 2.4 Auxiliar - Historias Clínicas
- **Ruta:** `/auxiliar/historias`
- **Archivo:** `frontend/src/pages/auxiliar/HistoriasClinicas.jsx`
- **Endpoints:**
  - `GET /api/historias-clinicas`
  - `PUT /api/historias-clinicas/{id}`
- **Funcionalidades:**
  - Ver historias clínicas
  - Actualizar información básica
  - Agregar notas

#### 2.5 Propietario - Historias Clínicas
- **Ruta:** `/propietario/historias`
- **Archivo:** `frontend/src/pages/propietario/HistoriasClinicas.jsx`
- **Endpoints:**
  - `GET /api/historias-clinicas/mascota/{idMascota}`
  - `GET /api/evoluciones-clinicas/historia-clinica/{id}`
- **Funcionalidades:**
  - Ver historias de sus mascotas (solo lectura)
  - Ver evoluciones
  - Ver tratamientos

#### 2.6 Recepcionista - Especies y Razas
- **Ruta:** `/recepcionista/especies-razas`
- **Archivo:** `frontend/src/pages/recepcionista/EspeciesRazas.jsx`
- **Endpoints:**
  - `GET /api/especies/activas`
  - `GET /api/razas/activas`
  - `GET /api/razas/especie/{idEspecie}`
- **Funcionalidades:**
  - Consultar especies
  - Consultar razas por especie
  - Buscador

---

## FASE 3: PRIORIDAD MEDIA (10 páginas)

### Objetivo: Completar funcionalidades complementarias

#### 3.1 Veterinario - Propietarios
- **Ruta:** `/veterinario/propietarios`
- **Archivo:** `frontend/src/pages/veterinario/Propietarios.jsx`

#### 3.2 Veterinario - Insumos/Inventario
- **Ruta:** `/veterinario/insumos`
- **Archivo:** `frontend/src/pages/veterinario/Insumos.jsx`

#### 3.3 Veterinario - Mis Reportes
- **Ruta:** `/veterinario/reportes`
- **Archivo:** `frontend/src/pages/veterinario/Reportes.jsx`

#### 3.4 Recepcionista - Notificaciones
- **Ruta:** `/recepcionista/notificaciones`
- **Archivo:** `frontend/src/pages/recepcionista/Notificaciones.jsx`

#### 3.5 Recepcionista - Registro Rápido
- **Ruta:** `/recepcionista/registro-completo`
- **Archivo:** `frontend/src/pages/recepcionista/RegistroCompleto.jsx`

#### 3.6 Auxiliar - Evoluciones Clínicas
- **Ruta:** `/auxiliar/evoluciones`
- **Archivo:** `frontend/src/pages/auxiliar/Evoluciones.jsx`

#### 3.7 Auxiliar - Vacunaciones
- **Ruta:** `/auxiliar/vacunaciones`
- **Archivo:** `frontend/src/pages/auxiliar/Vacunaciones.jsx`

#### 3.8 Auxiliar - Alertas Médicas
- **Ruta:** `/auxiliar/alertas`
- **Archivo:** `frontend/src/pages/auxiliar/Alertas.jsx`

#### 3.9 Propietario - Notificaciones
- **Ruta:** `/propietario/notificaciones`
- **Archivo:** `frontend/src/pages/propietario/Notificaciones.jsx`

#### 3.10 Propietario - Agendar Cita
- **Ruta:** `/propietario/agendar-cita`
- **Archivo:** `frontend/src/pages/propietario/AgendarCita.jsx`

---

## FASE 4: COMPONENTES COMPARTIDOS

### Objetivo: Crear biblioteca de componentes reutilizables

#### 4.1 Componentes de Formularios
- `MascotaForm.jsx` - Formulario de mascota reutilizable
- `PropietarioForm.jsx` - Formulario de propietario
- `CitaForm.jsx` - Formulario de cita
- `InsumoForm.jsx` - Formulario de insumo
- `HistoriaClinicaForm.jsx` - Formulario de historia clínica

#### 4.2 Componentes de Tablas
- `MascotasTable.jsx` - Tabla de mascotas con filtros
- `CitasTable.jsx` - Tabla de citas
- `InsumosTable.jsx` - Tabla de insumos con alertas
- `PropietariosTable.jsx` - Tabla de propietarios

#### 4.3 Componentes de UI
- `SearchBar.jsx` - Barra de búsqueda global
- `NotificationBadge.jsx` - Badge de notificaciones
- `AlertPanel.jsx` - Panel de alertas
- `Calendar.jsx` - Componente de calendario
- `Timeline.jsx` - Línea de tiempo para historias clínicas
- `StockBadge.jsx` - Indicador de stock
- `StatusBadge.jsx` - Badge de estados

#### 4.4 Componentes de Modal
- `ConfirmModal.jsx` - Modal de confirmación
- `DetailModal.jsx` - Modal de detalles
- `FormModal.jsx` - Modal con formulario

---

## FASE 5: SERVICIOS API CENTRALIZADOS

### Objetivo: Centralizar y estandarizar llamadas al backend

#### 5.1 Servicios por Módulo
- `usuarios.service.js` - Gestión de usuarios
- `mascotas.service.js` - Gestión de mascotas
- `citas.service.js` - Gestión de citas
- `historias.service.js` - Historias clínicas
- `inventario.service.js` - Gestión de inventario
- `notificaciones.service.js` - Sistema de notificaciones
- `reportes.service.js` - Generación de reportes
- `horarios.service.js` - Gestión de horarios
- `servicios.service.js` - Catálogo de servicios

#### 5.2 Hooks Personalizados
- `useMascotas.js` - Hook para gestión de mascotas
- `useCitas.js` - Hook para gestión de citas
- `useInventario.js` - Hook para inventario
- `useHorarios.js` - Hook para horarios
- `useNotificaciones.js` - Hook para notificaciones

#### 5.3 Context API
- `NotificationContext.jsx` - Notificaciones en tiempo real
- `AlertContext.jsx` - Sistema de alertas
- `ThemeContext.jsx` - Tema claro/oscuro (opcional)

---

## ORDEN DE IMPLEMENTACIÓN

### Semana 1: Fase 1 - Prioridad Crítica
**Días 1-2:** Veterinario - Mis Horarios
**Días 3-4:** Recepcionista - Horarios y Servicios
**Día 5:** Auxiliar - Gestión de Insumos (parte 1)

### Semana 2: Completar Fase 1 + Inicio Fase 2
**Días 1-2:** Auxiliar - Gestión de Insumos (parte 2)
**Día 3:** Propietario - Mi Perfil
**Días 4-5:** Veterinario - Mis Pacientes/Mascotas

### Semana 3: Fase 2
**Días 1-2:** Veterinario - Evoluciones Clínicas
**Día 3:** Auxiliar - Tipos de Insumo
**Día 4:** Auxiliar - Historias Clínicas
**Día 5:** Propietario - Historias Clínicas

### Semana 4: Completar Fase 2 + Inicio Fase 3
**Día 1:** Recepcionista - Especies y Razas
**Días 2-3:** Veterinario - Propietarios e Insumos
**Días 4-5:** Veterinario - Mis Reportes

### Semana 5: Fase 3
**Días 1-2:** Recepcionista - Notificaciones y Registro Rápido
**Días 3-4:** Auxiliar - Evoluciones, Vacunaciones
**Día 5:** Auxiliar - Alertas Médicas

### Semana 6: Completar Fase 3
**Días 1-2:** Propietario - Notificaciones
**Días 3-5:** Propietario - Agendar Cita

### Semana 7: Fase 4 - Componentes Compartidos
**Días 1-2:** Componentes de formularios
**Días 3-4:** Componentes de tablas y UI
**Día 5:** Componentes de modales

### Semana 8: Fase 5 - Servicios y Optimización
**Días 1-3:** Servicios API centralizados
**Días 4-5:** Hooks personalizados y Context API

---

## CHECKLIST DE IMPLEMENTACIÓN

### Por cada página a implementar:

- [ ] Crear archivo de página en la carpeta correcta
- [ ] Implementar estructura básica con estado
- [ ] Conectar con endpoints del backend (usar api.js)
- [ ] Implementar funcionalidades CRUD si aplica
- [ ] Agregar validaciones de formularios
- [ ] Implementar manejo de errores
- [ ] Agregar estados de carga
- [ ] Agregar ruta en el Layout correspondiente
- [ ] Agregar ítem en el menú de navegación
- [ ] Probar funcionalidad básica
- [ ] Revisar responsive design
- [ ] Documentar en CHANGELOG

### Por cada componente compartido:

- [ ] Crear componente en `frontend/src/components/`
- [ ] Definir PropTypes
- [ ] Implementar funcionalidad
- [ ] Hacer reutilizable y configurable
- [ ] Agregar variantes si es necesario
- [ ] Documentar uso del componente
- [ ] Probar en diferentes contextos

### Por cada servicio API:

- [ ] Crear archivo de servicio
- [ ] Implementar todas las funciones necesarias
- [ ] Agregar manejo de errores
- [ ] Agregar interceptores si es necesario
- [ ] Documentar funciones
- [ ] Probar todas las funciones

---

## MÉTRICAS DE ÉXITO

### Cobertura de Funcionalidades
- ✅ ADMIN: 100% (ya completado)
- 🎯 VETERINARIO: 100% (de 45% actual)
- 🎯 RECEPCIONISTA: 100% (de 44% actual)
- 🎯 AUXILIAR: 100% (de 25% actual)
- 🎯 PROPIETARIO: 100% (de 50% actual)

### Calidad de Código
- Componentes reutilizables creados: 15+
- Servicios API centralizados: 9+
- Hooks personalizados: 5+
- Cobertura de tests (objetivo): 70%+

### Experiencia de Usuario
- Tiempo de carga < 3 segundos
- Responsive en mobile, tablet, desktop
- Navegación intuitiva
- Mensajes de error claros

---

## DEPENDENCIAS Y CONSIDERACIONES

### Librerías a Considerar
- **react-hook-form** - Para formularios complejos
- **react-query** o **SWR** - Para caché de datos
- **date-fns** o **dayjs** - Para manejo de fechas
- **react-toastify** - Para notificaciones toast
- **recharts** o **chart.js** - Para gráficos en reportes

### Estructura de Carpetas Sugerida
```
frontend/src/
├── components/
│   ├── forms/
│   ├── tables/
│   ├── modals/
│   ├── ui/
│   └── shared/
├── services/
│   ├── api.js (ya existe)
│   ├── usuarios.service.js
│   ├── mascotas.service.js
│   └── ...
├── hooks/
│   ├── useMascotas.js
│   ├── useCitas.js
│   └── ...
├── context/
│   ├── AuthContext.jsx (ya existe)
│   ├── NotificationContext.jsx
│   └── ...
└── utils/
    ├── validators.js
    ├── formatters.js
    └── constants.js
```

---

## NOTAS IMPORTANTES

1. **Mantener consistencia:** Usar los mismos patrones de código que las páginas existentes
2. **Reutilizar componentes:** Antes de crear uno nuevo, verificar si ya existe
3. **Manejo de errores:** Siempre implementar try-catch y mostrar mensajes al usuario
4. **Estados de carga:** Mostrar spinners o skeletons durante las peticiones
5. **Validaciones:** Validar datos en frontend antes de enviar al backend
6. **Responsive:** Asegurar que todas las páginas funcionen en móvil
7. **Accesibilidad:** Usar labels, aria-labels y navegación por teclado
8. **Performance:** Optimizar re-renders innecesarios

---

**Documento creado:** 24 de Noviembre de 2025
**Proyecto:** Sistema de Clínica Veterinaria
**Versión:** 1.0
**Páginas a implementar:** 21
**Tiempo estimado:** 8 semanas
