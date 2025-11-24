# PATRONES DE DISENO - IMPLEMENTACION Y USO

**Proyecto:** Sistema de Gestion de Clinica Veterinaria
**Fecha:** 2025-11-20
**Version:** 4.0

---

## RESUMEN EJECUTIVO

Este documento detalla **todos los patrones de diseno activamente usados** en el proyecto.

### Estadisticas

| Categoria | Implementados | En Uso |
|-----------|:-------------:|:------:|
| **Creacionales** | 4 | 4 |
| **Estructurales** | 4 | 4 |
| **Comportamentales** | 6 | 6 |
| **TOTAL** | **14** | **14 (100%)** |

---

## PATRONES CREACIONALES

### 1. BUILDER Pattern

#### 1.1 CitaBuilder

**Ubicacion:** `patterns/creational/builder/CitaBuilder.java`

**Usado en:** `CitaServiceImpl.java:82-97`

```java
CitaBuilder builder = new CitaBuilder()
    .conMascota(mascota)
    .conVeterinario(veterinario)
    .conServicio(servicio)
    .conFecha(requestDTO.getFechaCita())
    .conHora(requestDTO.getHoraCita())
    .conMotivoConsulta(requestDTO.getMotivo())
    .conObservaciones(requestDTO.getObservaciones());

if (Constants.isTrue(requestDTO.getEsEmergencia())) {
    builder.comoEmergencia();
}

Cita cita = builder
    .conPrecioFinal(citaPriceCalculationService.calcularPrecioFinal(servicio, requestDTO))
    .build();
```

#### 1.2 HistoriaClinicaBuilder

**Ubicacion:** `patterns/creational/builder/HistoriaClinicaBuilder.java`

**Usado en:** `HistoriaClinicaServiceImpl.java:89-95`

```java
HistoriaClinica historiaClinica = new HistoriaClinicaBuilder()
    .conMascota(mascota)
    .conAlergias(requestDTO.getAlergias())
    .conEnfermedadesCronicas(requestDTO.getEnfermedadesCronicas())
    .conMedicamentosActuales(requestDTO.getMedicamentosActuales())
    .conObservacionesGenerales(requestDTO.getObservaciones())
    .build();
```

#### 1.3 ReporteBuilder

**Ubicacion:** `patterns/creational/builder/ReporteBuilder.java`

**Usado en:** `ReporteFacadeService.java:218-232, 251-262, 284-295`

```java
ReporteBuilder.Reporte reporte = new ReporteBuilder()
    .tipoReporte(ReporteBuilder.TipoReporte.CITAS)
    .conRangoFechas(fechaInicio, fechaFin)
    .conFormato(formato)
    .conTitulo(String.format("Reporte de Citas (%s - %s)", fechaInicio, fechaFin))
    .incluirGraficos(incluirGraficos)
    .incluirResumen(true)
    .agregarColumna("Fecha")
    .agregarColumna("Mascota")
    .conOrdenamiento("fecha", true)
    .build();
```

---

### 2. FACTORY METHOD Pattern

**Ubicacion:**
- `patterns/creational/factory/ServicioFactory.java` (abstracta)
- `ServicioClinicoFactory.java`
- `ServicioQuirurgicoFactory.java`
- `ServicioEsteticoFactory.java`
- `ServicioEmergenciaFactory.java`

**Usado en:** `ServicioServiceImpl.java:71-72`

```java
ServicioFactory factory = factoryRegistry.obtenerFactory(categoriaEnum);
Servicio servicio = factory.crearServicioCompleto(nombre, descripcion, precio);
```

---

### 3. ABSTRACT FACTORY Pattern

**Ubicacion:**
- `patterns/creational/abstractfactory/NotificacionFactory.java` (interface)
- `EmailNotificacionFactory.java`
- `SMSNotificacionFactory.java`
- `WhatsAppNotificacionFactory.java`
- `PushNotificacionFactory.java`

**Usado en:** `NotificacionServiceImpl.java:76-92`

```java
NotificacionFactory factory = factories.get(requestDTO.getCanal().toUpperCase());

ValidadorDestinatario validador = factory.crearValidador();
if (!validador.esValido(destinatario)) {
    throw new ValidationException(...);
}

MensajeNotificacion mensajeNotificacion = factory.crearMensaje(destinatario, asunto, mensaje);
EnviadorNotificacion enviador = factory.crearEnviador();
boolean enviado = enviador.enviar(mensajeNotificacion);
```

---

### 4. SINGLETON Pattern

#### 4.1 AuditLogger

**Ubicacion:** `patterns/creational/singleton/AuditLogger.java`

**Usado en:**
- `InventarioProxy.java:33`
- `HistoriaClinicaProxy.java:50`
- `AuditoriaObserver.java:47`

```java
auditLogger.logAccess(
    Constants.ENTIDAD_HISTORIA_CLINICA_SIN_ESPACIO,
    historiaClinica.getIdHistoriaClinica(),
    usuario
);

auditLogger.logStateChange(
    "Cita",
    cita.getIdCita(),
    usuario,
    estadoAnterior,
    estadoNuevo
);
```

#### 4.2 ConfigurationManager

**Ubicacion:** `patterns/creational/singleton/ConfigurationManager.java`

**Usado en:**
- `RecordatorioObserver.java:53, 126, 138`
- `CachedServiceProxy.java:51, 61`

```java
// RecordatorioObserver.java:126
if (!configurationManager.getRecordatoriosAutomaticos()) {
    log.info("Recordatorios automaticos deshabilitados en configuracion");
    return;
}

// CachedServiceProxy.java:61
private long getDefaultTTL() {
    return configurationManager.getConfigurationAsInteger("cache.ttl.default.seconds", 300) * 1000L;
}
```

---

## PATRONES ESTRUCTURALES

### 5. FACADE Pattern

**Ubicacion:** `patterns/structural/facade/`

| Facade | Controlador | Proposito |
|--------|-------------|-----------|
| `CitaFacadeService` | `CitaFacadeController` | Operaciones de citas con notificacion |
| `DashboardFacadeService` | `DashboardFacadeController` | Estadisticas y KPIs |
| `BusquedaFacadeService` | `BusquedasFacadeController` | Busquedas unificadas |
| `OperacionesFacadeService` | Multiples controllers | Operaciones transaccionales |
| `NotificacionesFacadeService` | `NotificacionesFacadeController` | Gestion masiva de notificaciones |
| `ReporteFacadeService` | `ReportesFacadeController` | Generacion de reportes |

**Ejemplo de uso:** `CitaFacadeController.java`

```java
@PostMapping("/crear-con-notificacion")
public ResponseEntity<CitaResponseDTO> crearCitaConNotificacion(@RequestBody CitaRequestDTO requestDTO) {
    return ResponseEntity.ok(citaFacadeService.crearCitaConNotificacion(requestDTO));
}
```

---

### 6. PROXY Pattern

#### 6.1 InventarioProxy

**Ubicacion:** `patterns/structural/proxy/InventarioProxy.java`

**Usado en:** `InventarioServiceImpl.java:38`

**Funcionalidades:**
- Verificacion de permisos antes de modificar inventario
- Registro automatico en auditoria

#### 6.2 HistoriaClinicaProxy

**Ubicacion:** `patterns/structural/proxy/HistoriaClinicaProxy.java`

**Usado en:** `HistoriaClinicaServiceImpl.java`

```java
if (!historiaClinicaProxy.tienePermisoLectura(historiaClinica)) {
    throw new UnauthorizedException("No tiene permisos...");
}
```

#### 6.3 CachedServiceProxy

**Ubicacion:** `patterns/structural/proxy/CachedServiceProxy.java`

**Usado en:** `MascotaServiceImpl.java`, `PropietarioServiceImpl.java`

```java
return cachedServiceProxy.executeWithCache(
    "mascotas:activas",
    () -> {
        List<Mascota> mascotas = repository.findByActivoTrue();
        return mapper.toResponseDTOList(mascotas);
    },
    300000L // 5 minutos
);
```

---

### 7. BRIDGE Pattern

**Ubicacion:**
- `patterns/structural/bridge/ReporteAbstraction.java`
- `ReporteCitasAbstraction.java`
- `ReporteService.java`
- **Implementadores:**
  - `ReportePDFImpl.java`
  - `ReporteExcelImpl.java`
  - `ReporteJSONImpl.java`

**Usado en:** `ReporteBuilder.java:272-278`

```java
private ReporteImplementor obtenerImplementor() {
    return switch (formato) {
        case PDF -> new ReportePDFImpl();
        case EXCEL -> new ReporteExcelImpl();
        case JSON -> new ReporteJSONImpl();
    };
}
```

---

### 8. DECORATOR Pattern

**Ubicacion:**
- `patterns/structural/decorator/ServicioDecorator.java` (abstracta)
- `ServicioUrgenciaDecorator.java`

**Usado en:** `CitaPriceCalculationService.java:27-29`

```java
if (Constants.isTrue(requestDTO.getEsEmergencia())) {
    ServicioUrgenciaDecorator decorator = new ServicioUrgenciaDecorator(servicio);
    precioBase = decorator.getPrecio();  // Aplica recargo de urgencia
}
```

---

## PATRONES COMPORTAMENTALES

### 9. OBSERVER Pattern

**Ubicacion:**
- `patterns/behavioral/observer/CitaSubject.java`
- `CitaObserver.java` (interface)
- **Observers:**
  - `AuditoriaObserver.java`
  - `NotificacionObserver.java`
  - `RecordatorioObserver.java`
  - `InventarioObserver.java`

**Configuracion:** `config/ObserverConfiguration.java`

```java
@PostConstruct
public void registrarObservers() {
    citaSubject.addObserver(auditoriaObserver);
    citaSubject.addObserver(notificacionObserver);
    citaSubject.addObserver(recordatorioObserver);
    log.info("Observer Pattern activado: 3 observers registrados");
}
```

**Usado en:** `CitaMediatorImpl.java:54, 73, 95`

```java
citaSubject.notifyCitaCreated(citaGuardada);
citaSubject.notifyStateChanged(cita, "PROGRAMADA", "CONFIRMADA");
citaSubject.notifyCitaCancelled(cita, motivo);
```

---

### 10. STATE Pattern

**Ubicacion:**
- `patterns/behavioral/state/CitaState.java` (interface)
- **Estados:**
  - `CitaProgramadaState.java`
  - `CitaConfirmadaState.java`
  - `CitaAtendidaState.java`
  - `CitaCanceladaState.java`

**Usado en:** Entidad `Cita` y `ICitaStateService`

**Nota:** El proyecto usa este patron en conjunto con enum `EstadoCita` para las transiciones de estado.

---

### 11. MEDIATOR Pattern

**Ubicacion:**
- `patterns/behavioral/mediator/CitaMediator.java` (interface)
- `CitaMediatorImpl.java`

**Usado en:** `CitaServiceImpl.java:58, 107, 202, 211`

```java
// Inyeccion
private final CitaMediator citaMediator;

// Uso
Cita citaCreada = citaMediator.crearCita(cita);
citaMediator.confirmarCita(id);
citaMediator.cancelarCita(id, motivo);
```

---

### 12. CHAIN OF RESPONSIBILITY Pattern

**Ubicacion:**
- `patterns/behavioral/chain/ValidacionHandler.java` (abstracta)
- **Handlers:**
  - `ValidacionDatosHandler.java`
  - `ValidacionDisponibilidadHandler.java`
  - `ValidacionPermisoHandler.java`
  - `ValidacionStockHandler.java`

**Usado en:** `CitaValidationService.java:23-26, 40-44`

```java
@PostConstruct
private void construirCadenaValidaciones() {
    validacionDatosHandler
        .setSiguiente(validacionDisponibilidadHandler)
        .setSiguiente(validacionPermisoHandler)
        .setSiguiente(validacionStockHandler);
}

// Ejecucion
validacionDatosHandler.validar(cita);
```

---

### 13. TEMPLATE METHOD Pattern

**Ubicacion:**
- `patterns/behavioral/template/AtencionTemplate.java` (abstracta)
- **Implementaciones:**
  - `AtencionConsultaGeneral.java`
  - `AtencionCirugia.java`
  - `AtencionEmergencia.java`

**Usado en:** `CitaServiceImpl.java:225-226, 266-280`

```java
AtencionTemplate template = obtenerTemplateAtencion(cita);
template.procesarAtencion(cita);

private AtencionTemplate obtenerTemplateAtencion(Cita cita) {
    if (Boolean.TRUE.equals(cita.getEsEmergencia()) ||
        cita.getServicio().esEmergencia()) {
        return atencionEmergencia;
    }
    if (cita.getServicio().esCirugia()) {
        return atencionCirugia;
    }
    return atencionConsultaGeneral;
}
```

**Flujo del template:**
1. `validarPrecondiciones()`
2. `registrarInicio()`
3. `prepararRecursos()`
4. `realizarAtencion()` - abstracto
5. `registrarResultados()` - abstracto
6. `finalizarAtencion()`

---

### 14. MEMENTO Pattern

**Ubicacion:**
- `patterns/behavioral/memento/HistoriaClinicaMemento.java`
- `HistoriaClinicaOriginator.java`
- `HistoriaClinicaCaretaker.java`

**Usado en:** `HistoriaClinicaServiceImpl.java:42, 67, 114, 173, 187`

```java
// Guardar estado
historiaClinicaCaretaker.guardarMemento(historiaGuardada);

// Restaurar ultimo estado (UNDO)
boolean restaurado = historiaClinicaCaretaker.restaurarUltimoMemento(historiaClinica);

// Restaurar version especifica
boolean restaurado = historiaClinicaCaretaker.restaurarMemento(historiaClinica, indice);
```

---

## PATRONES ELIMINADOS

Los siguientes patrones fueron removidos del proyecto por no estar en uso:

### Command Pattern (eliminado 2025-11-20)
- `Command.java`
- `CommandInvoker.java`
- `CrearCitaCommand.java`
- `CancelarCitaCommand.java`
- `ActualizarStockCommand.java`
- Tests relacionados

**Razon:** No se usaba en ningun servicio. La funcionalidad de undo/redo se implementa con Memento Pattern.

### Decoradores no usados (eliminado 2025-11-20)
- `ServicioConDescuentoDecorator.java`
- `ServicioConSeguroDecorator.java`
- `ServicioDomicilioDecorator.java`
- Tests relacionados

**Razon:** Solo `ServicioUrgenciaDecorator` se usaba en produccion.

### Adapter Pattern (eliminado previamente)
- `PaymentGatewayAdapter.java`
- `PayPalPaymentAdapter.java`
- `StripePaymentAdapter.java`
- Tests relacionados

**Razon:** No se requiere funcionalidad de pagos en el alcance actual.

---

## DISTRIBUCION POR TIPO

### Por Categoria

| Tipo | Patrones |
|------|----------|
| **Creacionales** | Builder, Factory Method, Abstract Factory, Singleton |
| **Estructurales** | Facade, Proxy, Bridge, Decorator |
| **Comportamentales** | Observer, State, Mediator, Chain of Responsibility, Template Method, Memento |

### Por Capa de Aplicacion

| Capa | Patrones |
|------|----------|
| **Controladores** | Facade |
| **Servicios** | Builder, Factory, Mediator, Template Method, Chain of Responsibility |
| **Dominio** | State, Memento |
| **Infraestructura** | Singleton (AuditLogger), Proxy, Observer |
| **Cross-cutting** | Abstract Factory (Notificaciones), Bridge (Reportes), Decorator |

---

## CONCLUSION

El proyecto implementa **14 patrones de diseno** completamente integrados y funcionales:

- **100% de uso efectivo** - Todos los patrones implementados estan en produccion
- **Cobertura completa** - Patrones creacionales, estructurales y comportamentales
- **Codigo limpio** - Se eliminaron 12 archivos de dead code
- **SOLID principles** - Los patrones refuerzan los principios de diseno

---

**Documento actualizado:** 2025-11-20
**Version:** 4.0
**Estado:** IMPLEMENTACION OPTIMIZADA - SOLO PATRONES EN USO
