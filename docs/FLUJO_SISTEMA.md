# 📋 FLUJO DEL SISTEMA - CLÍNICA VETERINARIA

**Sistema de Gestión Integral para Clínicas Veterinarias**

> Versión: 4.0
> Fecha: 2025-11-18
> Autor: Clínica Veterinaria Team

---

## 📑 ÍNDICE

1. [Arquitectura General](#arquitectura-general)
2. [Capas del Sistema](#capas-del-sistema)
3. [Flujo de Datos](#flujo-de-datos)
4. [Patrones de Diseño](#patrones-de-diseño)
5. [Casos de Uso Principales](#casos-de-uso-principales)
6. [Módulos del Sistema](#módulos-del-sistema)
7. [Seguridad y Autenticación](#seguridad-y-autenticación)

---

## 🏗️ ARQUITECTURA GENERAL

El sistema sigue una **arquitectura en capas** basada en los principios de **Domain-Driven Design (DDD)** y **Clean Architecture**.

```
┌─────────────────────────────────────────────────────────┐
│                    CAPA DE PRESENTACIÓN                 │
│                   (REST Controllers)                    │
│  ┌──────────┐  ┌──────────┐  ┌─────────────────────┐  │
│  │  CRUD    │  │  Facade  │  │  Specialized APIs   │  │
│  │Controllers│  │Controllers│  │  (Auth, Reports)    │  │
│  └──────────┘  └──────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                          ↓↑
┌─────────────────────────────────────────────────────────┐
│                    CAPA DE APLICACIÓN                   │
│                      (Services)                         │
│  ┌──────────┐  ┌──────────┐  ┌─────────────────────┐  │
│  │ Business │  │  Facade  │  │  Domain Services    │  │
│  │ Services │  │ Services │  │  (Validations)      │  │
│  └──────────┘  └──────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                          ↓↑
┌─────────────────────────────────────────────────────────┐
│                     CAPA DE DOMINIO                     │
│                  (Entities & Logic)                     │
│  ┌──────────┐  ┌──────────┐  ┌─────────────────────┐  │
│  │ Entities │  │  Events  │  │  Business Rules     │  │
│  │          │  │ (Mediator)│  │  (Domain Logic)     │  │
│  └──────────┘  └──────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                          ↓↑
┌─────────────────────────────────────────────────────────┐
│                  CAPA DE INFRAESTRUCTURA                │
│                   (Repositories)                        │
│  ┌──────────┐  ┌──────────┐  ┌─────────────────────┐  │
│  │   JPA    │  │  Spring  │  │  External Services  │  │
│  │Repositories│ │ Security │  │  (Email, SMS)       │  │
│  └──────────┘  └──────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                          ↓↑
                    ┌──────────┐
                    │ DATABASE │
                    │  (MySQL) │
                    └──────────┘
```

---

## 🔄 CAPAS DEL SISTEMA

### 1️⃣ **CAPA DE PRESENTACIÓN** (Controllers)

**Responsabilidad:** Exponer APIs REST y manejar peticiones HTTP.

#### Tipos de Controladores:

**A. Controladores CRUD Estándar**
```
📂 controller/
├── MascotaController.java
├── PropietarioController.java
├── VeterinarioController.java
├── CitaController.java
├── HistoriaClinicaController.java
└── ... (más controladores CRUD)
```

**Patrón de endpoints:**
- `POST /api/mascotas` - Crear
- `GET /api/mascotas/{id}` - Obtener por ID
- `GET /api/mascotas` - Listar todos
- `PUT /api/mascotas/{id}` - Actualizar
- `DELETE /api/mascotas/{id}` - Eliminar

**B. Controladores Facade (Operaciones Complejas)**
```
📂 controller/
├── CitaFacadeController.java
├── MascotaFacadeController.java
├── PropietarioFacadeController.java
├── VeterinarioFacadeController.java
├── BusquedasFacadeController.java
├── ReportesFacadeController.java
├── DashboardFacadeController.java
└── NotificacionesFacadeController.java
```

**Endpoints especiales:**
- `/api/facade/citas/crear-con-notificacion`
- `/api/facade/mascotas/registro-completo`
- `/api/facade/dashboard`
- `/api/facade/reportes/citas`

---

### 2️⃣ **CAPA DE APLICACIÓN** (Services)

**Responsabilidad:** Lógica de negocio y coordinación de operaciones.

#### Estructura de Servicios:

**A. Servicios de Negocio (Impl)**
```java
// Patrón de implementación
@Service
@RequiredArgsConstructor
@Transactional
public class MascotaServiceImpl implements IMascotaService {

    private final MascotaRepository mascotaRepository;
    private final MascotaMapper mascotaMapper;
    private final ValidationHelper validationHelper;

    @Override
    public MascotaResponseDTO crear(MascotaRequestDTO requestDTO) {
        // 1. Validaciones
        validationHelper.validateNotEmpty(requestDTO.getNombre(), "nombre");

        // 2. Conversión DTO → Entidad
        Mascota mascota = mascotaMapper.toEntity(requestDTO);

        // 3. Lógica de negocio
        mascota.calcularEdad();

        // 4. Persistencia
        Mascota saved = mascotaRepository.save(mascota);

        // 5. Conversión Entidad → DTO
        return mascotaMapper.toResponseDTO(saved);
    }
}
```

**B. Servicios Facade (Coordinación)**
```java
// Ejemplo: CitaFacadeService
@Service
@RequiredArgsConstructor
public class CitaFacadeService {

    private final ICitaService citaService;
    private final INotificacionService notificacionService;

    public ResultadoCitaConNotificacionDTO crearCitaConNotificacion(
            CitaRequestDTO requestDTO) {

        // 1. Crear cita (incluye validaciones)
        CitaResponseDTO cita = citaService.crear(requestDTO);

        // 2. Enviar notificación automática (Patrón Mediator)
        // La notificación se envía automáticamente vía eventos

        // 3. Retornar resultado consolidado
        return ResultadoCitaConNotificacionDTO.builder()
                .cita(cita)
                .mensaje("Cita creada y notificación enviada")
                .build();
    }
}
```

**Servicios Facade Especializados:**
- `BusquedaFacadeService` - Búsquedas complejas
- `CitaFacadeService` - Operaciones de citas con notificaciones
- `DashboardFacadeService` - Dashboard y estadísticas
- `NotificacionesFacadeService` - Notificaciones masivas
- `OperacionesFacadeService` - Operaciones multiservicio
- `ReporteFacadeService` - Generación de reportes

---

### 3️⃣ **CAPA DE DOMINIO** (Entities)

**Responsabilidad:** Modelo de dominio y reglas de negocio.

#### Módulos de Dominio:

**A. Módulo Paciente**
```
📂 domain/paciente/
├── Mascota.java         (Entidad principal)
├── Propietario.java     (Dueño de mascotas)
├── Especie.java         (Catálogo de especies)
└── Raza.java            (Catálogo de razas)
```

**B. Módulo Usuario**
```
📂 domain/usuario/
├── Usuario.java         (Usuario base)
├── Personal.java        (Personal clínica)
├── Veterinario.java     (Hereda de Personal)
├── AuxiliarVeterinario.java
├── Administrador.java
└── Recepcionista.java
```

**C. Módulo Agenda**
```
📂 domain/agenda/
├── Cita.java           (Citas médicas)
├── Horario.java        (Horarios de veterinarios)
├── Servicio.java       (Catálogo de servicios)
└── EstadoCita.java     (Enum: PROGRAMADA, ATENDIDA, etc.)
```

**D. Módulo Clínico**
```
📂 domain/clinico/
├── HistoriaClinica.java
├── EvolucionClinica.java
├── Vacunacion.java
└── ExamenLaboratorio.java
```

**E. Módulo Inventario**
```
📂 domain/inventario/
├── Insumo.java         (Productos/medicamentos)
├── TipoInsumo.java     (Catálogo)
└── Inventario.java     (Control de stock)
```

**F. Módulo Comunicación**
```
📂 domain/comunicacion/
├── Notificacion.java
├── Comunicacion.java
└── TipoNotificacion.java
```

#### Ejemplo de Entidad Rica (DDD):

```java
@Entity
@Table(name = "citas")
public class Cita {

    // Campos...

    // ========================================
    // MÉTODOS DE NEGOCIO (Business Logic)
    // ========================================

    /**
     * Confirma la cita.
     */
    public void confirmar() {
        validarEstadoParaConfirmar();
        this.estado = EstadoCita.CONFIRMADA;
        this.fechaConfirmacion = LocalDateTime.now();
    }

    /**
     * Marca la cita como atendida.
     */
    public void marcarComoAtendida() {
        validarEstadoParaAtender();
        this.estado = EstadoCita.ATENDIDA;
        this.fechaHoraInicioAtencion = LocalDateTime.now();
    }

    /**
     * Cancela la cita.
     */
    public void cancelar(String motivo, String usuario) {
        validarPuedeCancelarse();
        this.estado = EstadoCita.CANCELADA;
        this.motivoCancelacion = motivo;
        this.canceladaPor = usuario;
        this.fechaCancelacion = LocalDateTime.now();
    }

    /**
     * Verifica si la cita puede cancelarse.
     */
    public boolean puedeCancelarse() {
        return estado == EstadoCita.PROGRAMADA ||
               estado == EstadoCita.CONFIRMADA;
    }

    // Validaciones privadas...
}
```

---

### 4️⃣ **CAPA DE INFRAESTRUCTURA** (Repositories)

**Responsabilidad:** Acceso a datos y servicios externos.

```java
@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    // Queries derivadas (Spring Data JPA)
    List<Mascota> findByPropietarioIdPropietario(Long idPropietario);
    List<Mascota> findByNombreContainingIgnoreCase(String nombre);

    // Queries personalizadas con @Query
    @Query("SELECT m FROM Mascota m WHERE m.activo = true")
    List<Mascota> findAllActivas();

    @Query("SELECT m FROM Mascota m " +
           "WHERE m.especie.nombre LIKE %:especie% " +
           "AND m.activo = true")
    List<Mascota> findByEspecieNombre(@Param("especie") String especie);
}
```

---

## 🔄 FLUJO DE DATOS

### Flujo Típico de una Petición:

```
1. CLIENTE (Frontend/Postman)
        ↓ HTTP Request
2. SPRING SECURITY
        ↓ Autenticación JWT
3. CONTROLLER
        ↓ Validación @Valid
4. SERVICE
        ↓ Lógica de negocio
5. MAPPER (DTO → Entity)
        ↓
6. REPOSITORY
        ↓ JPA/Hibernate
7. DATABASE
        ↓ Resultado
8. REPOSITORY
        ↓
9. MAPPER (Entity → DTO)
        ↓
10. SERVICE
        ↓
11. CONTROLLER
        ↓ HTTP Response
12. CLIENTE
```

---

## 🎨 PATRONES DE DISEÑO

### 1. **Patrón Facade** ⭐

**Problema:** Operaciones complejas que requieren múltiples servicios.

**Solución:** Servicios Facade que coordinan operaciones complejas.

**Ejemplo:**
```java
@Service
public class OperacionesFacadeService {

    private final IMascotaService mascotaService;
    private final IPropietarioService propietarioService;
    private final IHistoriaClinicaService historiaClinicaService;

    /**
     * Registra mascota completa:
     * 1. Crea propietario
     * 2. Crea mascota
     * 3. Crea historia clínica
     */
    @Transactional
    public ResultadoRegistroCompletoDTO registrarMascotaCompleta(
            PropietarioRequestDTO propietarioDTO,
            MascotaRequestDTO mascotaDTO,
            HistoriaClinicaRequestDTO historiaDTO) {

        // Paso 1: Crear propietario
        var propietario = propietarioService.crear(propietarioDTO);

        // Paso 2: Crear mascota asociada
        mascotaDTO.setIdPropietario(propietario.getIdPropietario());
        var mascota = mascotaService.crear(mascotaDTO);

        // Paso 3: Crear historia clínica
        historiaDTO.setIdMascota(mascota.getIdMascota());
        var historia = historiaClinicaService.crear(historiaDTO);

        return ResultadoRegistroCompletoDTO.builder()
                .propietario(propietario)
                .mascota(mascota)
                .historiaClinica(historia)
                .mensaje("Registro completo exitoso")
                .build();
    }
}
```

**Ubicación:** `patterns/structural/facade/`

---

### 2. **Patrón Builder** 🏗️

**Problema:** DTOs y entidades con muchos campos.

**Solución:** Uso de Lombok @Builder.

**Ejemplo:**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MascotaResponseDTO {
    private Long idMascota;
    private String nombre;
    private LocalDate fechaNacimiento;
    private String sexo;
    // ... más campos
}

// Uso:
MascotaResponseDTO dto = MascotaResponseDTO.builder()
        .idMascota(1L)
        .nombre("Max")
        .fechaNacimiento(LocalDate.of(2020, 1, 15))
        .sexo("MACHO")
        .build();
```

---

### 3. **Patrón Strategy** 🎯

**Problema:** Diferentes estrategias de cálculo de precios.

**Solución:** Interface Strategy con múltiples implementaciones.

**Ejemplo:**
```java
// Interface Strategy
public interface PriceCalculationStrategy {
    BigDecimal calcularPrecio(Cita cita);
}

// Estrategia Regular
@Component
public class RegularPriceStrategy implements PriceCalculationStrategy {
    @Override
    public BigDecimal calcularPrecio(Cita cita) {
        return cita.getServicio().getPrecio();
    }
}

// Estrategia con Descuento
@Component
public class DiscountPriceStrategy implements PriceCalculationStrategy {
    @Override
    public BigDecimal calcularPrecio(Cita cita) {
        BigDecimal precioBase = cita.getServicio().getPrecio();
        return precioBase.multiply(BigDecimal.valueOf(0.85)); // 15% descuento
    }
}
```

**Ubicación:** `patterns/behavioral/strategy/`

---

### 4. **Patrón Mediator** 📡

**Problema:** Comunicación compleja entre servicios.

**Solución:** Mediator para eventos de dominio.

**Ejemplo:**
```java
@Component
public class CitaEventMediator {

    private final INotificacionService notificacionService;

    /**
     * Maneja el evento de cita creada.
     */
    public void onCitaCreada(Cita cita) {
        // Enviar notificación al propietario
        enviarNotificacionPropietario(cita);

        // Actualizar estadísticas
        actualizarEstadisticas(cita);

        // Verificar disponibilidad de veterinario
        verificarDisponibilidad(cita);
    }

    private void enviarNotificacionPropietario(Cita cita) {
        NotificacionRequestDTO notif = NotificacionRequestDTO.builder()
                .destinatario(cita.getMascota().getPropietario())
                .asunto("Cita programada")
                .mensaje("Su cita ha sido programada para " + cita.getFechaCita())
                .tipo("RECORDATORIO")
                .build();

        notificacionService.enviar(notif);
    }
}
```

**Ubicación:** `patterns/behavioral/mediator/`

---

### 5. **Patrón Factory** 🏭

**Problema:** Creación compleja de servicios u objetos.

**Solución:** Factory para crear instancias.

**Ejemplo:**
```java
@Component
public class ServicioFactory {

    public Servicio crearServicio(String tipo) {
        return switch (tipo) {
            case "CONSULTA" -> crearConsultaGeneral();
            case "VACUNACION" -> crearServicioVacunacion();
            case "CIRUGIA" -> crearServicioCirugia();
            case "LABORATORIO" -> crearServicioLaboratorio();
            default -> throw new IllegalArgumentException("Tipo no soportado: " + tipo);
        };
    }

    private Servicio crearConsultaGeneral() {
        return Servicio.builder()
                .nombre("Consulta General")
                .tipoServicio("CONSULTA")
                .duracionMinutos(30)
                .precio(BigDecimal.valueOf(50.00))
                .build();
    }
}
```

**Ubicación:** `patterns/creational/factory/`

---

### 6. **Patrón Repository** 💾

**Problema:** Abstracción del acceso a datos.

**Solución:** Spring Data JPA Repositories.

**Ejemplo:**
```java
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    // Query Methods
    List<Cita> findByMascotaIdMascota(Long idMascota);
    List<Cita> findByVeterinarioIdPersonal(Long idVeterinario);
    List<Cita> findByEstado(EstadoCita estado);

    // Custom Queries
    @Query("SELECT c FROM Cita c " +
           "WHERE c.fechaHora BETWEEN :inicio AND :fin " +
           "ORDER BY c.fechaHora ASC")
    List<Cita> findByRangoFechas(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
}
```

---

### 7. **Patrón DTO (Data Transfer Object)** 📦

**Problema:** Exponer entidades JPA directamente en APIs.

**Solución:** DTOs de Request y Response.

**Estructura:**
```
dto/
├── request/
│   ├── agenda/
│   │   └── CitaRequestDTO.java
│   ├── paciente/
│   │   ├── MascotaRequestDTO.java
│   │   └── PropietarioRequestDTO.java
│   └── ... (más request DTOs)
│
└── response/
    ├── agenda/
    │   └── CitaResponseDTO.java
    ├── paciente/
    │   ├── MascotaResponseDTO.java
    │   └── PropietarioResponseDTO.java
    ├── facade/
    │   ├── DashboardResponseDTO.java
    │   ├── ReporteCitasDTO.java
    │   └── ... (más facade DTOs)
    └── ... (más response DTOs)
```

**Mapeo con MapStruct:**
```java
@Mapper(componentModel = "spring")
public interface MascotaMapper {

    // Entity → Response DTO
    MascotaResponseDTO toResponseDTO(Mascota mascota);
    List<MascotaResponseDTO> toResponseDTOList(List<Mascota> mascotas);

    // Request DTO → Entity
    Mascota toEntity(MascotaRequestDTO requestDTO);

    // Update Entity from Request DTO
    @MappingTarget
    void updateEntityFromRequest(MascotaRequestDTO dto, @MappingTarget Mascota entity);
}
```

---

## 📌 CASOS DE USO PRINCIPALES

### **CASO DE USO 1: Crear Cita con Notificación**

**Actor:** Recepcionista

**Flujo:**
```
1. Recepcionista → POST /api/facade/citas/crear-con-notificacion
        ↓
2. CitaFacadeController recibe CitaRequestDTO
        ↓
3. Spring Security valida JWT y permisos
        ↓
4. CitaFacadeService.crearCitaConNotificacion()
        ↓
5. ICitaService.crear() - Validaciones y persistencia
        ↓
6. CitaEventMediator.onCitaCreada() - Evento de dominio
        ↓
7. INotificacionService.enviar() - Notificación automática
        ↓
8. ResultadoCitaConNotificacionDTO retornado
        ↓
9. HTTP 201 Created con resultado
```

**Request:**
```json
POST /api/facade/citas/crear-con-notificacion
{
  "idMascota": 1,
  "idVeterinario": 2,
  "idServicio": 5,
  "fechaCita": "2025-11-20",
  "horaCita": "10:00",
  "motivo": "Consulta general",
  "esEmergencia": false
}
```

**Response:**
```json
{
  "cita": {
    "idCita": 123,
    "mascota": { "idMascota": 1, "nombre": "Max" },
    "veterinario": { "idPersonal": 2, "nombreCompleto": "Dr. García" },
    "fechaCita": "2025-11-20",
    "horaCita": "10:00",
    "estado": "PROGRAMADA"
  },
  "notificacion": null,
  "mensaje": "Cita creada exitosamente con notificación"
}
```

---

### **CASO DE USO 2: Registro Completo de Mascota**

**Actor:** Veterinario/Recepcionista

**Flujo:**
```
1. Usuario → POST /api/facade/mascotas/registro-completo
        ↓
2. MascotaFacadeController recibe Request Compuesto
        ↓
3. OperacionesFacadeService.registrarMascotaCompleta()
        ↓
4. IPropietarioService.crear() → Crea propietario
        ↓
5. IMascotaService.crear() → Crea mascota (asociada a propietario)
        ↓
6. IHistoriaClinicaService.crear() → Crea historia clínica
        ↓
7. @Transactional garantiza atomicidad (todo o nada)
        ↓
8. ResultadoRegistroCompletoDTO retornado
        ↓
9. HTTP 201 Created con resultado completo
```

**Request:**
```json
POST /api/facade/mascotas/registro-completo
{
  "propietario": {
    "nombre": "Juan",
    "apellido": "Pérez",
    "tipoDocumento": "DNI",
    "numeroDocumento": "12345678",
    "telefono": "999888777",
    "email": "juan@email.com"
  },
  "mascota": {
    "nombre": "Max",
    "fechaNacimiento": "2020-05-15",
    "sexo": "MACHO",
    "idEspecie": 1,
    "idRaza": 3
  },
  "historiaClinica": {
    "alergias": "Ninguna",
    "enfermedadesPreexistentes": "Ninguna"
  }
}
```

**Response:**
```json
{
  "propietario": { "idPropietario": 45, "nombre": "Juan", ... },
  "mascota": { "idMascota": 89, "nombre": "Max", ... },
  "historiaClinica": { "idHistoriaClinica": 67, ... },
  "mensaje": "Registro completo exitoso"
}
```

---

### **CASO DE USO 3: Procesar Atención Completa**

**Actor:** Veterinario

**Flujo:**
```
1. Veterinario → POST /api/facade/citas/{idCita}/atencion-completa
        ↓
2. CitaFacadeController recibe EvolucionClinicaRequestDTO
        ↓
3. OperacionesFacadeService.procesarAtencionCompleta()
        ↓
4. ICitaService.marcarComoAtendida() → Actualiza estado de cita
        ↓
5. IHistoriaClinicaService.buscarPorMascota() → Obtiene historia
        ↓
6. IEvolucionClinicaService.crear() → Registra evolución clínica
        ↓
7. ResultadoAtencionCompletaDTO consolidado
        ↓
8. HTTP 200 OK con resultado
```

**Request:**
```json
POST /api/facade/citas/123/atencion-completa
{
  "tipoEvolucion": "CONSULTA",
  "motivoConsulta": "Control de rutina",
  "hallazgosExamen": "Mascota en buen estado general",
  "diagnostico": "Saludable",
  "planTratamiento": "Continuar con alimentación actual",
  "peso": 15.5,
  "temperatura": 38.5,
  "frecuenciaCardiaca": 120
}
```

**Response:**
```json
{
  "cita": { "idCita": 123, "estado": "ATENDIDA", ... },
  "historiaClinica": { "idHistoriaClinica": 67, ... },
  "evolucionClinica": { "idEvolucion": 234, ... },
  "mensaje": "Atención completa procesada exitosamente"
}
```

---

### **CASO DE USO 4: Dashboard Administrativo**

**Actor:** Administrador

**Flujo:**
```
1. Admin → GET /api/facade/dashboard
        ↓
2. DashboardFacadeController
        ↓
3. DashboardFacadeService.obtenerDashboard()
        ↓
4. Consultas en paralelo:
   - ICitaService.listarHoy()
   - ICitaService.listarProgramadas()
   - IInventarioService.listarConStockBajo()
   - INotificacionService.listarRecientes()
        ↓
5. DashboardResponseDTO consolidado
        ↓
6. HTTP 200 OK con dashboard completo
```

**Response:**
```json
{
  "citasHoy": [...],
  "totalCitasHoy": 8,
  "citasProgramadas": [...],
  "totalCitasProgramadas": 15,
  "stockBajo": [...],
  "totalStockBajo": 3,
  "notificacionesRecientes": [...],
  "totalNotificacionesRecientes": 5
}
```

---

### **CASO DE USO 5: Generar Reporte de Citas**

**Actor:** Administrador/Veterinario

**Flujo:**
```
1. Usuario → GET /api/facade/reportes/citas?fechaInicio=2025-11-01&fechaFin=2025-11-30
        ↓
2. ReportesFacadeController
        ↓
3. ReporteFacadeService.generarReporteCitas()
        ↓
4. ICitaService.listarPorRangoFechas()
        ↓
5. Procesamiento y estadísticas:
   - Contar citas por estado
   - Agrupar por veterinario
   - Calcular métricas
        ↓
6. ReporteCitasDTO con estadísticas
        ↓
7. HTTP 200 OK con reporte completo
```

**Response:**
```json
{
  "fechaInicio": "2025-11-01",
  "fechaFin": "2025-11-30",
  "citas": [...],
  "totalCitas": 120,
  "citasAtendidas": 95,
  "citasProgramadas": 20,
  "citasCanceladas": 5
}
```

---

## 🔐 SEGURIDAD Y AUTENTICACIÓN

### **Flujo de Autenticación JWT**

```
1. Login → POST /api/auth/login
        ↓
2. AuthController.login(username, password)
        ↓
3. AuthenticationManager verifica credenciales
        ↓
4. Si válido:
   - JwtUtils.generarToken(usuario)
   - Token JWT generado
        ↓
5. Response: { "token": "eyJhbGc...", "usuario": {...} }
        ↓
6. Cliente guarda token (localStorage/sessionStorage)
        ↓
7. Peticiones subsecuentes:
   - Header: Authorization: Bearer eyJhbGc...
        ↓
8. JwtAuthenticationFilter intercepta
        ↓
9. JwtUtils.validarToken() y extraer usuario
        ↓
10. SecurityContext cargado con usuario
        ↓
11. @PreAuthorize verifica permisos
        ↓
12. Si autorizado → Procesa petición
    Si no → HTTP 403 Forbidden
```

### **Roles y Permisos**

```
ADMIN
  ├── Gestión completa de usuarios
  ├── Acceso a reportes administrativos
  ├── Configuración del sistema
  └── Todas las operaciones CRUD

VETERINARIO
  ├── Gestión de citas
  ├── Historias clínicas
  ├── Evoluciones clínicas
  ├── Vacunaciones
  └── Reportes clínicos

RECEPCIONISTA
  ├── Crear citas
  ├── Registrar propietarios y mascotas
  ├── Consultar información
  └── Enviar notificaciones

AUXILIAR
  ├── Gestión de inventario
  ├── Control de stock
  └── Notificaciones de inventario

PROPIETARIO
  ├── Ver sus mascotas
  ├── Ver citas programadas
  ├── Actualizar su perfil
  └── Ver historias clínicas (solo lectura)
```

---

## 📊 MÓDULOS DEL SISTEMA

### **1. Módulo de Gestión de Pacientes**
- Registro de propietarios
- Registro de mascotas
- Especies y razas (catálogos)
- Búsqueda de pacientes

### **2. Módulo de Agenda**
- Programación de citas
- Gestión de horarios de veterinarios
- Catálogo de servicios
- Calendario de citas

### **3. Módulo Clínico**
- Historias clínicas
- Evoluciones clínicas
- Vacunaciones
- Exámenes de laboratorio

### **4. Módulo de Inventario**
- Gestión de insumos
- Control de stock
- Alertas de stock bajo
- Tipos de insumos

### **5. Módulo de Comunicaciones**
- Notificaciones automáticas
- Recordatorios de citas
- Comunicaciones internas

### **6. Módulo de Usuarios**
- Gestión de usuarios
- Roles y permisos
- Autenticación JWT
- Personal de la clínica

### **7. Módulo de Reportes**
- Reportes de citas
- Reportes de inventario
- Reportes por veterinario
- Dashboard administrativo

---

## 🔄 FLUJOS ESPECIALES

### **Flujo de Notificaciones Automáticas**

```
Evento → Cita Creada
   ↓
CitaEventMediator.onCitaCreada()
   ↓
┌─────────────────────────────────┐
│  Acciones en Paralelo:          │
│  1. Enviar email al propietario │
│  2. Enviar SMS (si configurado) │
│  3. Registrar en sistema        │
│  4. Actualizar estadísticas     │
└─────────────────────────────────┘
   ↓
NotificacionService.enviar()
   ↓
Notificación registrada en BD
```

### **Flujo de Validaciones en Cascada**

```
CitaService.crear()
   ↓
┌──────────────────────────────────┐
│ Validaciones:                    │
│ 1. Veterinario disponible        │
│ 2. Horario válido                │
│ 3. Mascota activa                │
│ 4. Servicio disponible           │
│ 5. No hay conflictos de horario  │
└──────────────────────────────────┘
   ↓
Si alguna falla → ValidationException
Si todas pasan → Persistir cita
```

---

## 📈 MÉTRICAS Y OBSERVABILIDAD

### **Logging**

```java
@Slf4j
@Service
public class CitaServiceImpl implements ICitaService {

    public CitaResponseDTO crear(CitaRequestDTO requestDTO) {
        log.info("Creando cita para mascota ID: {}", requestDTO.getIdMascota());

        try {
            // Lógica...
            log.info("Cita {} creada exitosamente", cita.getIdCita());
            return response;
        } catch (Exception e) {
            log.error("Error creando cita: {}", e.getMessage(), e);
            throw e;
        }
    }
}
```

### **Auditoría Automática**

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Cita {

    @CreatedDate
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    private LocalDateTime fechaModificacion;

    @CreatedBy
    private String creadoPor;

    @LastModifiedBy
    private String modificadoPor;
}
```

---

## 🎯 CONCLUSIÓN

Este sistema implementa una **arquitectura limpia y escalable** con:

- ✅ **Separación clara de responsabilidades**
- ✅ **Patrones de diseño probados**
- ✅ **Código mantenible y testeable**
- ✅ **Seguridad robusta con JWT**
- ✅ **DTOs para type-safety**
- ✅ **Transacciones atómicas**
- ✅ **Eventos de dominio**
- ✅ **Auditoría automática**

El flujo del sistema garantiza:
- 🔒 Seguridad en todas las operaciones
- ⚡ Performance optimizado
- 📊 Trazabilidad completa
- 🛡️ Validaciones exhaustivas
- 🔄 Operaciones transaccionales

---

**Última actualización:** 2025-11-18
**Versión del sistema:** 4.0
**Estado:** ✅ Producción Ready
