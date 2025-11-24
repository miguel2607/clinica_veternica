# DOCUMENTACIÓN COMPLETA - SISTEMA DE GESTIÓN CLÍNICA VETERINARIA

**Proyecto:** Sistema de Gestión Integral para Clínicas Veterinarias
**Tecnología:** Spring Boot 3.5.7 + MySQL + Java 21
**Fecha:** 19 de Noviembre de 2025
**Versión:** 1.1.0

---

## 📑 TABLA DE CONTENIDOS

1. [DEFINICIÓN DE ARQUITECTURA DE SOFTWARE](#1-definición-de-arquitectura-de-software)
   - [1.1 Título del Proyecto](#11-título-del-proyecto)
   - [1.2 Introducción](#12-introducción)
   - [1.3 Objetivos de la Arquitectura](#13-objetivos-de-la-arquitectura)
   - [1.4 Elección de la Arquitectura](#14-elección-de-la-arquitectura)
   - [1.5 Descripción de la Arquitectura](#15-descripción-de-la-arquitectura)
   - [1.6 Descripción para Diagramas](#16-descripción-para-diagramas-de-arquitectura)
   - [1.7 Consideraciones Finales](#17-consideraciones-finales)

2. [PLAN Y CASOS DE PRUEBA](#2-plan-y-casos-de-prueba)
   - [2.1 Introducción](#21-introducción)
   - [2.2 Alcance](#22-alcance)
   - [2.3 Definiciones y Abreviaturas](#23-definiciones-siglas-y-abreviaturas)
   - [2.4 Responsables](#24-responsables-e-involucrados)
   - [2.5 Plan de Pruebas](#25-plan-de-pruebas)
   - [2.6 Casos de Prueba Detallados](#26-casos-de-prueba-detallados)

3. [EVALUACIÓN DE CALIDAD DEL CÓDIGO](#3-evaluación-de-calidad-del-código)
   - [3.1 Portada](#31-portada)
   - [3.2 Introducción](#32-introducción)
   - [3.3 Aspectos a Evaluar](#33-aspectos-a-evaluar)
   - [3.4 Análisis por Módulo](#34-análisis-por-módulo)
   - [3.5 Aspectos Positivos](#35-aspectos-positivos-encontrados)
   - [3.6 Hallazgos Críticos](#36-hallazgos--aspectos-críticos)
   - [3.7 Conclusiones y Recomendaciones](#37-conclusiones-y-recomendaciones)

4. [LISTA DE CHEQUEO DE CALIDAD](#4-lista-de-chequeo-de-calidad)
   - [4.1 Checklist por Módulo](#41-checklist-por-módulo)
   - [4.2 Resumen de Calidad](#42-resumen-de-calidad)

---

# 1. DEFINICIÓN DE ARQUITECTURA DE SOFTWARE

## 1.1 Título del Proyecto

**Sistema de Gestión Integral para Clínicas Veterinarias**

**Nombre técnico:** clinica_veternica
**Versión:** 1.0.0-SNAPSHOT
**Fecha de análisis:** 19 de Noviembre de 2025
**Stack tecnológico:** Spring Boot 3.5.7, Java 21, MySQL 8+, JWT

---

## 1.2 Introducción

### Descripción del Proyecto

El **Sistema de Gestión Integral para Clínicas Veterinarias** es una aplicación backend empresarial desarrollada con Spring Boot que proporciona una solución completa para la administración de clínicas veterinarias. El sistema centraliza la gestión de pacientes (mascotas), propietarios, personal médico, citas, historias clínicas, inventario de insumos médicos y notificaciones automatizadas.

### Propósito y Alcance

El proyecto tiene como propósito principal **digitalizar y automatizar los procesos operativos** de una clínica veterinaria, abarcando:

**Módulos Principales:**
- **Gestión de Pacientes:** Registro completo de mascotas con información detallada (especie, raza, edad, peso, historial médico)
- **Gestión de Propietarios:** Base de datos de dueños con datos de contacto y relación con sus mascotas
- **Agenda de Citas:** Sistema de agendamiento con estados, recordatorios y confirmaciones automáticas
- **Historias Clínicas:** Expedientes médicos completos con evoluciones, diagnósticos, tratamientos y vacunaciones
- **Gestión de Personal:** Administración de veterinarios, auxiliares, recepcionistas y administradores
- **Inventario:** Control de insumos médicos, medicamentos y materiales con alertas de stock bajo
- **Notificaciones:** Sistema multi-canal (Email, SMS, WhatsApp, Push) para recordatorios y alertas
- **Reportes y Estadísticas:** Dashboards y reportes de gestión para toma de decisiones

**Usuarios del Sistema:**
- **Administradores:** Acceso total, gestión de usuarios y configuración
- **Veterinarios:** Atención médica, historias clínicas, citas
- **Recepcionistas:** Agendamiento, registro de propietarios y mascotas
- **Auxiliares:** Gestión de inventario, soporte en atenciones
- **Propietarios:** Consulta de citas y estado de mascotas (limitado)

### Problema que Resuelve

El sistema soluciona los siguientes problemas comunes en clínicas veterinarias:

1. **Gestión Manual de Información:**
   - Expedientes en papel propensos a pérdida o deterioro
   - Dificultad para buscar historiales médicos rápidamente
   - Inconsistencia en el registro de datos

2. **Desorganización de Citas:**
   - Doble agendamiento de horarios
   - Olvido de citas por falta de recordatorios
   - Dificultad para reprogramar citas canceladas

3. **Falta de Historial Médico Centralizado:**
   - Información dispersa en múltiples carpetas físicas
   - Imposibilidad de hacer seguimiento de evolución clínica
   - Riesgo de errores por desconocimiento de alergias o antecedentes

4. **Control de Inventario Ineficiente:**
   - Desabastecimiento de insumos críticos
   - Pérdida económica por caducidad de medicamentos
   - Falta de alertas automáticas

5. **Comunicación Ineficiente:**
   - Llamadas manuales para recordar citas
   - Dificultad para notificar cambios de horario
   - Falta de trazabilidad de comunicaciones

6. **Ausencia de Métricas:**
   - Desconocimiento de productividad por veterinario
   - Falta de visibilidad de ingresos por servicios
   - Imposibilidad de identificar tendencias o problemas

---

## 1.3 Objetivos de la Arquitectura

### Objetivo Principal

**Diseñar una arquitectura robusta, escalable y mantenible** que soporte el crecimiento de la clínica veterinaria, garantizando la disponibilidad del sistema 24/7, la seguridad de los datos médicos sensibles y la capacidad de adaptarse a nuevos requisitos del negocio sin requerir reescrituras completas del código.

### Objetivos Secundarios

#### 1. **Escalabilidad**
- Soportar el crecimiento progresivo de clínicas pequeñas a medianas (100-10,000 registros de mascotas)
- Permitir agregar nuevos módulos sin afectar funcionalidades existentes
- Facilitar la migración futura a arquitectura de microservicios si se requiere
- Manejar incrementos en carga de usuarios concurrentes (10-100 usuarios simultáneos)

**Medidas implementadas:**
- Separación clara de responsabilidades en capas
- Uso de DTOs para desacoplar API de modelo de datos
- Patrón Repository para abstraer persistencia
- Caché en memoria para consultas frecuentes

#### 2. **Rendimiento**
- Tiempos de respuesta < 200ms para operaciones CRUD básicas
- Tiempos de respuesta < 1s para operaciones complejas (reportes, búsquedas)
- Optimización de consultas a base de datos con índices apropiados
- Minimizar tráfico de red con paginación y filtros

**Medidas implementadas:**
- Índices en columnas de búsqueda frecuente
- Lazy loading en relaciones JPA
- Caché de catálogos (especies, razas, servicios)
- Queries optimizadas con JPQL

#### 3. **Mantenibilidad**
- Código limpio y auto-documentado
- Separación de concerns mediante capas arquitectónicas
- Facilidad para agregar nuevas funcionalidades
- Reducción del costo de mantenimiento a largo plazo

**Medidas implementadas:**
- Nomenclatura descriptiva y consistente
- Comentarios JavaDoc en métodos complejos
- Estructura de paquetes por dominio
- Principios SOLID aplicados

#### 4. **Seguridad**
- Protección de datos médicos sensibles (GDPR/HIPAA compliance)
- Autenticación robusta con JWT
- Control de acceso basado en roles (RBAC)
- Prevención de vulnerabilidades OWASP Top 10

**Medidas implementadas:**
- Autenticación JWT con tokens de corta duración (24h)
- Passwords encriptados con BCrypt
- Validación de entrada con Jakarta Validation
- @PreAuthorize en endpoints críticos
- Auditoría de accesos a historias clínicas (Proxy Pattern)

#### 5. **Disponibilidad**
- Sistema disponible 99.5% del tiempo
- Manejo robusto de errores con mensajes claros
- Logging para diagnóstico rápido de problemas
- Recuperación ante fallos de BD

**Medidas implementadas:**
- Manejo global de excepciones con @ControllerAdvice
- Logging estructurado con SLF4J
- Transacciones con rollback automático
- Connection pooling para BD

---

## 1.4 Elección de la Arquitectura

### Tipo de Arquitectura Seleccionada

**Arquitectura en Capas (Layered Architecture) + Principios de Domain-Driven Design (DDD)**

Esta arquitectura hibrida combina:
- **Capas horizontales** para separación técnica (Presentación, Lógica de Negocio, Persistencia)
- **Módulos verticales** para separación por dominio (Pacientes, Agenda, Clínica, Inventario)

### Diagrama Conceptual de la Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                    CAPA DE PRESENTACIÓN                     │
│                     (REST Controllers)                      │
│  ┌────────────┐  ┌─────────────┐  ┌────────────────────┐  │
│  │   CRUD     │  │   Facade    │  │   Specialized      │  │
│  │Controllers │  │ Controllers  │  │   (Auth, etc.)     │  │
│  │   (20)     │  │     (8)     │  │                    │  │
│  └────────────┘  └─────────────┘  └────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                          ↓↑ DTOs
┌─────────────────────────────────────────────────────────────┐
│                  CAPA DE APLICACIÓN                         │
│                   (Business Logic)                          │
│  ┌────────────┐  ┌─────────────┐  ┌────────────────────┐  │
│  │ Services   │  │   Facade    │  │   Validation &     │  │
│  │   (26+)    │  │  Services   │  │   Helpers          │  │
│  │            │  │    (7)      │  │                    │  │
│  └────────────┘  └─────────────┘  └────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                          ↓↑ Entities
┌─────────────────────────────────────────────────────────────┐
│                    CAPA DE DOMINIO                          │
│                  (Domain Entities)                          │
│  ┌────────────┐  ┌─────────────┐  ┌────────────────────┐  │
│  │ Entities   │  │   Business  │  │   Design Patterns  │  │
│  │   (25)     │  │    Rules    │  │    (16 tipos)      │  │
│  │            │  │             │  │                    │  │
│  └────────────┘  └─────────────┘  └────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                          ↓↑ JPA
┌─────────────────────────────────────────────────────────────┐
│               CAPA DE INFRAESTRUCTURA                       │
│                   (Data Access)                             │
│  ┌────────────┐  ┌─────────────┐  ┌────────────────────┐  │
│  │Spring Data │  │   Security  │  │   External APIs    │  │
│  │Repositories│  │    (JWT)    │  │  (Email, SMS)      │  │
│  │   (20+)    │  │             │  │                    │  │
│  └────────────┘  └─────────────┘  └────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                          ↓↑
                   ┌─────────────┐
                   │   MySQL     │
                   │  Database   │
                   └─────────────┘
```

### Razones de Selección

#### 1. **Tamaño del Proyecto: Mediano (15,000+ líneas de código)**

**Justificación:**
- Una arquitectura monolítica en capas es apropiada para este tamaño
- No justifica la complejidad de microservicios aún
- Permite desarrollo rápido y despliegue simple
- Facilita debugging y testing end-to-end

**Análisis:**
- 296 archivos Java en producción
- 73 archivos de tests
- 28 controllers REST
- 25 entidades JPA
- Complejidad manejable en una sola aplicación

#### 2. **Equipo de Desarrollo: Pequeño a Mediano**

**Justificación:**
- Estructura clara y fácil de entender para nuevos desarrolladores
- No requiere coordinación compleja entre múltiples equipos
- Facilita el onboarding con documentación centralizada
- Un desarrollador puede trabajar en múltiples capas

**Consideraciones:**
- Curva de aprendizaje moderada
- Stack tecnológico unificado (Spring Boot)
- Documentación en un solo repositorio

#### 3. **Tecnología Spring Boot: Ideal para Arquitectura en Capas**

**Justificación:**
- Spring Boot está diseñado para aplicaciones en capas
- Inyección de dependencias natural entre capas
- Convenciones que promueven buena arquitectura
- Ecosistema maduro (Spring Data, Spring Security)

**Beneficios técnicos:**
- @Controller, @Service, @Repository fomentan separación
- Auto-configuración reduce boilerplate
- Starter dependencies simplifican dependencias
- Actuator para monitoreo integrado

#### 4. **Requisitos del Negocio: CRUD + Operaciones Complejas**

**Justificación:**
- 80% de funcionalidad es CRUD estándar
- 20% requiere operaciones complejas (Facade Pattern)
- No requiere comunicación asíncrona compleja
- Transacciones ACID son suficientes (no eventual consistency)

**Análisis de requisitos:**
- **CRUD simple:** Especies, Razas, Propietarios, Mascotas
- **Lógica de negocio:** Citas (con estado, validaciones)
- **Operaciones compuestas:** Registro completo (Propietario + Mascota + Historia)
- **Reportes:** Agregaciones simples en BD

#### 5. **Escalabilidad Futura**

**Justificación:**
- Arquitectura permite migración gradual a microservicios
- Módulos de dominio ya están separados (bounded contexts)
- Uso de DTOs facilita versionado de API
- Interfaces permiten cambiar implementaciones

**Estrategia de escalado:**
- **Fase 1 (actual):** Monolito en capas
- **Fase 2 (opcional):** Extracción de módulos pesados (Reportes, Notificaciones)
- **Fase 3 (futuro):** Microservicios si crecimiento lo justifica

---

## 1.5 Descripción de la Arquitectura

### Componentes Principales

#### A. CAPA DE PRESENTACIÓN / API (Controllers)

**Ubicación:** `src/main/java/com/veterinaria/clinica_veternica/controller/`

**Responsabilidad:**
- Exponer endpoints REST API
- Recibir peticiones HTTP y validar formato
- Delegar lógica de negocio a servicios
- Transformar respuestas de servicios a JSON
- Manejar autenticación y autorización (Spring Security)

**Componentes:**

1. **Controllers CRUD Estándar (20 controllers):**
   - `AuthController` - Login y registro
   - `UsuarioController` - Gestión de usuarios
   - `MascotaController` - CRUD de mascotas
   - `PropietarioController` - CRUD de propietarios
   - `VeterinarioController` - CRUD de veterinarios
   - `CitaController` - CRUD de citas con estado
   - `HistoriaClinicaController` - Gestión de historias clínicas
   - `ServicioController` - Catálogo de servicios
   - `InsumoController` - Gestión de insumos
   - `NotificacionController` - Envío de notificaciones
   - Y 10 más...

2. **Facade Controllers (8 controllers):**
   - `CitaFacadeController` - Operaciones complejas de citas
   - `MascotaFacadeController` - Registro completo mascota
   - `DashboardFacadeController` - Dashboard y estadísticas
   - `ReportesFacadeController` - Generación de reportes
   - `BusquedasFacadeController` - Búsquedas avanzadas
   - Y 3 más...

**Características técnicas:**
```java
@RestController
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
public class MascotaController {

    private final IMascotaService mascotaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public ResponseEntity<MascotaResponseDTO> crear(
            @Valid @RequestBody MascotaRequestDTO requestDTO) {

        MascotaResponseDTO response = mascotaService.crear(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Más endpoints...
}
```

**Patrón utilizado:** MVC (Model-View-Controller), donde Controller es el punto de entrada

---

#### B. CAPA DE LÓGICA DE NEGOCIO (Services)

**Ubicación:**
- Interfaces: `src/main/java/com/veterinaria/clinica_veternica/service/interfaces/`
- Implementaciones: `src/main/java/com/veterinaria/clinica_veternica/service/impl/`

**Responsabilidad:**
- Implementar reglas de negocio complejas
- Coordinar operaciones entre múltiples entidades
- Validar lógica de negocio (no solo formato)
- Orquestar transacciones
- Aplicar patrones de diseño (Template Method, Strategy, etc.)

**Componentes:**

1. **Services de Dominio (26+ interfaces):**
   - `IMascotaService` / `MascotaServiceImpl`
   - `IPropietarioService` / `PropietarioServiceImpl`
   - `ICitaService` / `CitaServiceImpl`
   - `IHistoriaClinicaService` / `HistoriaClinicaServiceImpl`
   - `INotificacionService` / `NotificacionServiceImpl`
   - Y 21 más...

2. **Facade Services (7 servicios):**
   - `CitaFacadeService` - Coordina cita + notificación + inventario
   - `OperacionesFacadeService` - Operaciones multi-entidad
   - `DashboardFacadeService` - Agregación de datos para dashboard
   - `ReporteFacadeService` - Generación de reportes complejos
   - Y 3 más...

3. **Services Especializados:**
   - `CitaValidationService` - Validaciones de citas (Chain of Responsibility)
   - `CitaPriceCalculationService` - Cálculo de precios con descuentos
   - `ValidationHelper` - Validaciones reutilizables

**Características técnicas:**
```java
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MascotaServiceImpl implements IMascotaService {

    private final MascotaRepository mascotaRepository;
    private final PropietarioRepository propietarioRepository;
    private final MascotaMapper mascotaMapper;
    private final ValidationHelper validationHelper;

    @Override
    public MascotaResponseDTO crear(MascotaRequestDTO requestDTO) {
        log.info("Creando nueva mascota: {}", requestDTO.getNombre());

        // 1. Validaciones de negocio
        Propietario propietario = propietarioRepository.findById(requestDTO.getIdPropietario())
            .orElseThrow(() -> new ResourceNotFoundException("Propietario", "id", requestDTO.getIdPropietario()));

        // 2. Validar raza pertenece a especie
        validationHelper.validateRazaBelongsToSpecies(
            raza.getEspecie().getIdEspecie(),
            especie.getIdEspecie(),
            raza.getNombre(),
            especie.getNombre()
        );

        // 3. Mapeo DTO → Entidad
        Mascota mascota = mascotaMapper.toEntity(requestDTO);
        mascota.setPropietario(propietario);

        // 4. Persistencia
        Mascota saved = mascotaRepository.save(mascota);

        log.info("Mascota creada exitosamente con ID: {}", saved.getIdMascota());

        // 5. Mapeo Entidad → DTO Response
        return mascotaMapper.toResponseDTO(saved);
    }
}
```

**Patrón utilizado:** Service Layer Pattern + Dependency Injection

---

#### C. CAPA DE ACCESO A DATOS (Repositories)

**Ubicación:** `src/main/java/com/veterinaria/clinica_veternica/repository/`

**Responsabilidad:**
- Abstraer acceso a base de datos
- Proporcionar métodos CRUD básicos
- Implementar queries personalizadas
- Manejar transacciones de BD

**Componentes:**

20+ interfaces de Repository extendiendo `JpaRepository<T, ID>`:
- `MascotaRepository`
- `PropietarioRepository`
- `CitaRepository`
- `HistoriaClinicaRepository`
- `InsumoRepository`
- Y 15 más...

**Características técnicas:**
```java
@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    // Query methods derivados automáticamente
    List<Mascota> findByPropietarioIdPropietario(Long idPropietario);
    List<Mascota> findByNombreContainingIgnoreCase(String nombre);
    List<Mascota> findByActivoTrue();

    // Queries personalizadas con JPQL
    @Query("SELECT m FROM Mascota m WHERE m.especie.idEspecie = :idEspecie AND m.activo = true")
    List<Mascota> findByEspecieActivas(@Param("idEspecie") Long idEspecie);

    // Query con JOIN FETCH para optimización
    @Query("SELECT m FROM Mascota m LEFT JOIN FETCH m.propietario WHERE m.idMascota = :id")
    Optional<Mascota> findByIdWithPropietario(@Param("id") Long id);

    // Verificación de existencia
    boolean existsByMicrochip(String microchip);
}
```

**Patrón utilizado:** Repository Pattern + Spring Data JPA

---

#### D. CAPA DE PERSISTENCIA (Entities/Models)

**Ubicación:** `src/main/java/com/veterinaria/clinica_veternica/domain/`

**Responsabilidad:**
- Representar el modelo de dominio
- Mapear tablas de base de datos con JPA
- Implementar reglas de negocio en entidades (DDD)
- Definir relaciones entre entidades

**Componentes:**

25 entidades JPA organizadas por módulo:

1. **Módulo Paciente:**
   - `Mascota` - Entidad principal de pacientes
   - `Propietario` - Dueños de mascotas
   - `Especie` - Catálogo de especies
   - `Raza` - Catálogo de razas

2. **Módulo Usuario:**
   - `Usuario` - Credenciales de acceso
   - `Personal` - Clase base para empleados (herencia JOINED)
   - `Veterinario`, `Administrador`, `Recepcionista`, `AuxiliarVeterinario`

3. **Módulo Agenda:**
   - `Cita` - Citas médicas con estado
   - `Servicio` - Catálogo de servicios
   - `Horario` - Disponibilidad de veterinarios

4. **Módulo Clínico:**
   - `HistoriaClinica` - Expediente médico
   - `EvolucionClinica` - Registro de atenciones
   - `Vacunacion` - Control de vacunas

5. **Módulo Inventario:**
   - `Insumo` - Medicamentos y materiales
   - `TipoInsumo` - Categorías de insumos
   - `Inventario` - Control de stock

6. **Módulo Comunicación:**
   - `Notificacion` - Registro de notificaciones enviadas

**Características técnicas:**
```java
@Entity
@Table(name = "mascotas", indexes = {
    @Index(name = "idx_mascota_propietario", columnList = "id_propietario"),
    @Index(name = "idx_mascota_nombre", columnList = "nombre"),
    @Index(name = "idx_mascota_microchip", columnList = "microchip")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mascota")
    private Long idMascota;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_propietario", nullable = false)
    private Propietario propietario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_especie", nullable = false)
    private Especie especie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_raza")
    private Raza raza;

    // Auditoría automática
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime fechaModificacion;

    // Métodos de negocio (DDD - Entidad Rica)
    public Period getEdad() {
        if (fechaNacimiento == null) return null;
        return Period.between(fechaNacimiento, LocalDate.now());
    }

    public boolean esCachorro() {
        Period edad = getEdad();
        if (edad == null) return false;
        return edad.getYears() < 2;
    }

    public void registrarFallecimiento(LocalDate fecha, String causa) {
        this.fechaFallecimiento = fecha;
        this.causaFallecimiento = causa;
        this.activo = false;
    }
}
```

**Patrones utilizados:**
- Active Record Pattern (métodos de negocio en entidades)
- Table Module Pattern (una clase por tabla)
- Inheritance Pattern (JOINED strategy para Personal)

---

#### E. BASE DE DATOS (MySQL)

**Tecnología:** MySQL 8+
**ORM:** Hibernate (incluido en Spring Data JPA)

**Responsabilidad:**
- Almacenar datos persistentes
- Garantizar integridad referencial
- Optimizar consultas con índices
- Soportar transacciones ACID

**Configuración:**
```properties
# application.properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/clinica_veterinaria
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

**Estructura de BD (20 tablas):**

1. **Módulo Usuarios y Personal (7 tablas):**
   - `usuarios`
   - `personal` (tabla padre)
   - `veterinario`, `administrador`, `recepcionista`, `auxiliar_veterinario`
   - `propietarios`

2. **Módulo Pacientes (3 tablas):**
   - `mascotas`
   - `especies`
   - `razas`

3. **Módulo Agenda (3 tablas):**
   - `citas`
   - `servicios`
   - `horarios`

4. **Módulo Clínico (3 tablas):**
   - `historias_clinicas`
   - `evolucion_clinica`
   - `vacunaciones`

5. **Módulo Inventario (3 tablas):**
   - `insumos`
   - `tipo_insumo`
   - `inventarios`

6. **Módulo Comunicación (1 tabla):**
   - `notificaciones`

**Índices principales:**
- `idx_mascota_propietario` en `mascotas(id_propietario)`
- `idx_cita_veterinario` en `citas(id_veterinario)`
- `idx_cita_fecha` en `citas(fecha_cita)`
- `idx_propietario_documento` en `propietarios(tipo_documento, numero_documento)`
- Y 20+ índices más para optimización

---

#### F. SISTEMA DE AUTENTICACIÓN (Spring Security + JWT)

**Ubicación:** `src/main/java/com/veterinaria/clinica_veternica/security/`

**Responsabilidad:**
- Autenticar usuarios con credenciales
- Generar tokens JWT
- Validar tokens en cada petición
- Controlar acceso basado en roles (RBAC)

**Componentes:**

1. **SecurityConfig** - Configuración de Spring Security
2. **JwtUtils** - Generación y validación de tokens
3. **JwtAuthenticationFilter** - Filtro para validar JWT
4. **JwtAuthenticationEntryPoint** - Manejo de errores de autenticación
5. **UserDetailsServiceImpl** - Carga de usuarios desde BD

**Flujo de autenticación:**
```
1. Cliente → POST /api/auth/login {username, password}
2. AuthController → AuthenticationManager.authenticate()
3. Si válido → JwtUtils.generateToken(username, roles)
4. Response → {token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."}
5. Cliente guarda token
6. Peticiones subsecuentes → Header: Authorization: Bearer {token}
7. JwtAuthenticationFilter intercepta → JwtUtils.validateToken()
8. Si válido → SecurityContext cargado con usuario
9. @PreAuthorize verifica permisos → Procesa petición o 403 Forbidden
```

**Configuración de seguridad:**
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**")
                    .permitAll()
                .requestMatchers("/api/**")
                    .authenticated()
                .anyRequest()
                    .denyAll()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**Roles del sistema:**
- `ROLE_ADMIN` - Acceso total
- `ROLE_VETERINARIO` - Citas, historias, evoluciones
- `ROLE_RECEPCIONISTA` - Citas, mascotas, propietarios
- `ROLE_AUXILIAR` - Inventario, evoluciones
- `ROLE_PROPIETARIO` - Solo consulta de citas propias

---

#### G. SISTEMA DE TRANSFERENCIA DE DATOS (DTOs)

**Ubicación:** `src/main/java/com/veterinaria/clinica_veternica/dto/`

**Responsabilidad:**
- Desacoplar API de modelo de dominio
- Validar datos de entrada con Jakarta Validation
- Optimizar transferencia de datos (solo campos necesarios)
- Versionado de API sin afectar entidades

**Componentes:**

1. **Request DTOs (entrada):**
   - `dto/request/paciente/MascotaRequestDTO`
   - `dto/request/agenda/CitaRequestDTO`
   - `dto/request/auth/LoginRequestDTO`
   - Y 40+ DTOs de request

2. **Response DTOs (salida):**
   - `dto/response/paciente/MascotaResponseDTO`
   - `dto/response/agenda/CitaResponseDTO`
   - `dto/response/facade/DashboardResponseDTO`
   - Y 40+ DTOs de response

**Características técnicas:**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MascotaRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El sexo es obligatorio")
    @Pattern(regexp = "MACHO|HEMBRA", message = "El sexo debe ser MACHO o HEMBRA")
    private String sexo;

    @Positive(message = "El peso debe ser positivo")
    private Double peso;

    @NotNull(message = "El propietario es obligatorio")
    private Long idPropietario;

    @NotNull(message = "La especie es obligatoria")
    private Long idEspecie;

    private Long idRaza; // Opcional
}
```

**Mapeo DTO ↔ Entidad (MapStruct):**
```java
@Mapper(componentModel = "spring")
public interface MascotaMapper {

    @Mapping(source = "idPropietario", target = "propietario.idPropietario")
    @Mapping(source = "idEspecie", target = "especie.idEspecie")
    @Mapping(source = "idRaza", target = "raza.idRaza")
    Mascota toEntity(MascotaRequestDTO dto);

    MascotaResponseDTO toResponseDTO(Mascota entity);

    List<MascotaResponseDTO> toResponseDTOList(List<Mascota> entities);
}
```

**Patrón utilizado:** Data Transfer Object Pattern + Mapper Pattern

---

### Interacciones entre Componentes

#### Flujo Completo de una Petición REST

**Ejemplo: Crear una nueva mascota**

```
┌─────────────┐
│   CLIENTE   │  POST /api/mascotas + MascotaRequestDTO
│  (Frontend) │
└──────┬──────┘
       │ 1. HTTP Request
       ↓
┌─────────────────────────────┐
│   SPRING SECURITY           │
│   - JwtAuthenticationFilter │
│   - Valida JWT              │
│   - Carga usuario           │
│   - Verifica rol            │
└──────┬──────────────────────┘
       │ 2. Usuario autenticado
       ↓
┌─────────────────────────────┐
│   CONTROLLER                │
│   MascotaController         │
│   @PostMapping              │
│   @PreAuthorize             │
│   @Valid MascotaRequestDTO  │
└──────┬──────────────────────┘
       │ 3. DTO validado
       ↓
┌─────────────────────────────┐
│   SERVICE                   │
│   MascotaServiceImpl        │
│   - Validaciones negocio    │
│   - Lógica compleja         │
│   - Coordina repos          │
└──────┬──────────────────────┘
       │ 4. Entidad a persistir
       ↓
┌─────────────────────────────┐
│   MAPPER                    │
│   MascotaMapper             │
│   DTO → Entity              │
└──────┬──────────────────────┘
       │ 5. Entidad mapeada
       ↓
┌─────────────────────────────┐
│   REPOSITORY                │
│   MascotaRepository         │
│   save(mascota)             │
└──────┬──────────────────────┘
       │ 6. SQL INSERT
       ↓
┌─────────────────────────────┐
│   HIBERNATE / JPA           │
│   - Genera SQL              │
│   - Ejecuta transacción     │
└──────┬──────────────────────┘
       │ 7. JDBC
       ↓
┌─────────────────────────────┐
│   MYSQL DATABASE            │
│   INSERT INTO mascotas      │
└──────┬──────────────────────┘
       │ 8. ID generado
       ↑
┌─────────────────────────────┐
│   REPOSITORY                │
│   Return Mascota con ID     │
└──────┬──────────────────────┘
       │ 9. Entidad persistida
       ↑
┌─────────────────────────────┐
│   MAPPER                    │
│   Entity → ResponseDTO      │
└──────┬──────────────────────┘
       │ 10. DTO Response
       ↑
┌─────────────────────────────┐
│   SERVICE                   │
│   Return ResponseDTO        │
└──────┬──────────────────────┘
       │ 11. DTO Response
       ↑
┌─────────────────────────────┐
│   CONTROLLER                │
│   ResponseEntity(201)       │
└──────┬──────────────────────┘
       │ 12. HTTP Response
       ↑
┌─────────────────────────────┐
│   CLIENTE                   │
│   Recibe JSON               │
└─────────────────────────────┘
```

**Código del flujo:**

```java
// 1. Cliente envía petición
POST /api/mascotas
Authorization: Bearer eyJhbGci...
Content-Type: application/json
{
  "nombre": "Max",
  "fechaNacimiento": "2020-01-15",
  "sexo": "MACHO",
  "peso": 15.5,
  "idPropietario": 1,
  "idEspecie": 1,
  "idRaza": 3
}

// 2. JwtAuthenticationFilter
Authentication auth = jwtUtils.getAuthentication(token);
SecurityContextHolder.getContext().setAuthentication(auth);

// 3. Controller
@PostMapping
@PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
public ResponseEntity<MascotaResponseDTO> crear(@Valid @RequestBody MascotaRequestDTO dto) {
    MascotaResponseDTO response = mascotaService.crear(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

// 4. Service
@Transactional
public MascotaResponseDTO crear(MascotaRequestDTO dto) {
    // Validaciones
    Propietario propietario = propietarioRepository.findById(dto.getIdPropietario())
        .orElseThrow(() -> new ResourceNotFoundException("Propietario", "id", dto.getIdPropietario()));

    // Mapeo
    Mascota mascota = mascotaMapper.toEntity(dto);
    mascota.setPropietario(propietario);

    // Persistencia
    Mascota saved = mascotaRepository.save(mascota);

    // Response
    return mascotaMapper.toResponseDTO(saved);
}

// 5. Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    // save() heredado de JpaRepository
}

// 6. Hibernate genera SQL
INSERT INTO mascotas (nombre, fecha_nacimiento, sexo, peso, id_propietario, id_especie, id_raza, fecha_creacion, fecha_modificacion)
VALUES ('Max', '2020-01-15', 'MACHO', 15.5, 1, 1, 3, NOW(), NOW());

// 7. Response al cliente
HTTP/1.1 201 Created
Content-Type: application/json
{
  "idMascota": 42,
  "nombre": "Max",
  "fechaNacimiento": "2020-01-15",
  "edad": {"years": 5, "months": 10, "days": 3},
  "sexo": "MACHO",
  "peso": 15.5,
  "propietario": {
    "idPropietario": 1,
    "nombreCompleto": "Juan Pérez"
  },
  "especie": {
    "idEspecie": 1,
    "nombre": "Canino"
  },
  "raza": {
    "idRaza": 3,
    "nombre": "Labrador"
  },
  "activo": true,
  "fechaCreacion": "2025-11-18T15:30:00"
}
```

---

#### Manejo de Errores y Excepciones

**Componente:** Global Exception Handler

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        log.error("Recurso no encontrado: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Resource Not Found")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Failed")
            .message("Error de validación en los datos de entrada")
            .errors(errors)
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Acceso denegado: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.FORBIDDEN.value())
            .error("Access Denied")
            .message("No tiene permisos para realizar esta operación")
            .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}
```

**Respuesta de error estándar:**
```json
{
  "timestamp": "2025-11-18T15:30:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Mascota no encontrada con id: 999",
  "path": "/api/mascotas/999"
}
```

---

## 1.6 Descripción para Diagramas de Arquitectura

### DIAGRAMA 1: Arquitectura de Alto Nivel

**Descripción textual para crear el diagrama:**

```
TÍTULO: Arquitectura General del Sistema - Vista de 30,000 pies

COMPONENTES A DIBUJAR:

1. CAPA EXTERNA (Clientes):
   - Navegador Web (Frontend Angular/React)
   - Aplicación Móvil (iOS/Android)
   - Postman/Cliente API
   - Flecha hacia abajo: "HTTP/HTTPS Requests"

2. CAPA DE API GATEWAY / SEGURIDAD:
   - Spring Security + JWT
   - Validación de tokens
   - Control de acceso (RBAC)
   - Flecha hacia abajo: "Autenticado"

3. CAPA DE APLICACIÓN (Spring Boot):
   - Rectángulo grande conteniendo:
     * Controllers (REST Endpoints)
     * Services (Business Logic)
     * Repositories (Data Access)
   - Flecha hacia abajo: "JPA/Hibernate"

4. CAPA DE DATOS:
   - MySQL Database
   - Redis Cache (opcional)
   - Flecha bidireccional con Capa de Aplicación

5. SERVICIOS EXTERNOS (lateral derecho):
   - Servidor Email (SMTP)
   - Proveedor SMS
   - WhatsApp Business API
   - Push Notification Service
   - Flechas desde Services hacia estos componentes

FLUJO:
Cliente → Seguridad → Controllers → Services → Repositories → Database
                                      ↓
                            Servicios Externos

COLORES SUGERIDOS:
- Clientes: Azul claro
- Seguridad: Rojo claro
- Aplicación Spring Boot: Verde
- Base de Datos: Naranja
- Servicios Externos: Morado
```

---

### DIAGRAMA 2: Arquitectura de Componentes - Vista Detallada

**Descripción textual para crear el diagrama:**

```
TÍTULO: Arquitectura de Componentes - Vista de Capas

CAPA 1 - PRESENTACIÓN (Top):
┌─────────────────────────────────────────────────────┐
│              CONTROLLERS LAYER                       │
├──────────────┬──────────────┬───────────────────────┤
│ CRUD         │ Facade       │ Specialized           │
│ Controllers  │ Controllers  │ Controllers           │
│              │              │                       │
│ - Mascota    │ - Cita       │ - Auth               │
│ - Propietario│   Facade     │   (Login/Register)   │
│ - Veterinario│ - Dashboard  │                      │
│ - Cita       │   Facade     │                      │
│ - Historia   │ - Reporte    │                      │
│              │   Facade     │                      │
│ (20 ctrl)    │ (8 ctrl)     │ (2 ctrl)             │
└──────────────┴──────────────┴───────────────────────┘
       │                │                 │
       └────────┬───────┴─────────────────┘
                │ DTOs (Request/Response)
                ↓

CAPA 2 - LÓGICA DE NEGOCIO:
┌─────────────────────────────────────────────────────┐
│              SERVICES LAYER                          │
├──────────────┬──────────────┬───────────────────────┤
│ Domain       │ Facade       │ Specialized           │
│ Services     │ Services     │ Services              │
│              │              │                       │
│ - IMascota   │ - Cita       │ - Validation         │
│   Service    │   Facade     │   Helper             │
│ - IProp.     │ - Operations │ - Cita               │
│   Service    │   Facade     │   Validation         │
│ - ICita      │ - Dashboard  │ - Price              │
│   Service    │   Facade     │   Calculation        │
│              │              │                      │
│ (26+ svc)    │ (7 svc)      │ (3 svc)              │
└──────────────┴──────────────┴───────────────────────┘
       │                │                 │
       └────────┬───────┴─────────────────┘
                │ Entities
                ↓

CAPA 3 - DOMINIO:
┌─────────────────────────────────────────────────────┐
│              DOMAIN ENTITIES LAYER                   │
├──────────────┬──────────────┬───────────────────────┤
│ Paciente     │ Agenda       │ Usuario               │
│              │              │                       │
│ - Mascota    │ - Cita       │ - Usuario            │
│ - Propietario│ - Servicio   │ - Personal           │
│ - Especie    │ - Horario    │ - Veterinario        │
│ - Raza       │              │ - Administrador      │
│              │              │                      │
├──────────────┼──────────────┼───────────────────────┤
│ Clínico      │ Inventario   │ Comunicación         │
│              │              │                      │
│ - Historia   │ - Insumo     │ - Notificacion       │
│   Clinica    │ - TipoInsumo │                      │
│ - Evolucion  │ - Inventario │                      │
│ - Vacunacion │              │                      │
│              │              │                      │
│ (25 entities en total)                             │
└─────────────────────────────────────────────────────┘
                │ JPA/Hibernate
                ↓

CAPA 4 - PERSISTENCIA:
┌─────────────────────────────────────────────────────┐
│           REPOSITORIES LAYER                         │
├──────────────┬──────────────┬───────────────────────┤
│ Spring Data  │ Custom       │ Query Methods         │
│ JPA          │ Queries      │                       │
│              │              │                       │
│ - Mascota    │ @Query       │ - findBy...          │
│   Repository │ - JPQL       │ - existsBy...        │
│ - Propietario│ - Native SQL │ - countBy...         │
│   Repository │              │                      │
│              │              │                      │
│ (20+ repositories)                                  │
└─────────────────────────────────────────────────────┘
                │ JDBC
                ↓

CAPA 5 - BASE DE DATOS:
┌─────────────────────────────────────────────────────┐
│                 MYSQL DATABASE                       │
│                                                      │
│  20 Tablas organizadas en 6 módulos:               │
│  - Usuarios (7 tablas)                             │
│  - Pacientes (3 tablas)                            │
│  - Agenda (3 tablas)                               │
│  - Clínico (3 tablas)                              │
│  - Inventario (3 tablas)                           │
│  - Comunicación (1 tabla)                          │
│                                                      │
│  Características:                                   │
│  - Índices optimizados                             │
│  - Constraints de integridad                       │
│  - Triggers para auditoría                         │
└─────────────────────────────────────────────────────┘

COMPONENTES TRANSVERSALES (Lateral):
┌─────────────────────────┐
│  SPRING SECURITY        │
│  - JWT Authentication   │
│  - Authorization        │
│  - Password Encryption  │
└─────────────────────────┘
┌─────────────────────────┐
│  EXCEPTION HANDLING     │
│  - @ControllerAdvice    │
│  - Global Error Handler │
└─────────────────────────┘
┌─────────────────────────┐
│  LOGGING & MONITORING   │
│  - SLF4J/Logback       │
│  - Spring Actuator      │
└─────────────────────────┘
┌─────────────────────────┐
│  DESIGN PATTERNS        │
│  - 16 Patterns          │
│  - Facade, Proxy, etc.  │
└─────────────────────────┘
```

---

### DIAGRAMA 3: Flujo de Datos - Crear Cita con Notificación

**Descripción textual para crear el diagrama de secuencia:**

```
TÍTULO: Flujo de Datos - Crear Cita con Notificación Automática

ACTORES:
1. Recepcionista (Cliente)
2. CitaFacadeController
3. CitaFacadeService
4. CitaService
5. CitaValidationService (Chain of Responsibility)
6. CitaRepository
7. CitaMediator
8. NotificacionService
9. NotificacionFactory (Abstract Factory)
10. EmailService
11. MySQL Database

SECUENCIA:

1. Recepcionista → CitaFacadeController: POST /api/facade/citas/crear-con-notificacion
   Datos: CitaRequestDTO (idMascota, idVeterinario, fechaCita, horaCita, motivo)

2. CitaFacadeController → Spring Security: Validar JWT
3. Spring Security → CitaFacadeController: Usuario autenticado (rol: RECEPCIONISTA)

4. CitaFacadeController → CitaFacadeService: crearCitaConNotificacion(dto)

5. CitaFacadeService → CitaService: crear(dto)

6. CitaService → CitaValidationService: validar(dto)
   - Validar disponibilidad de veterinario
   - Validar horario válido
   - Validar no conflictos
   - Chain of Responsibility Pattern

7. CitaValidationService → CitaService: Validación exitosa

8. CitaService → CitaRepository: save(cita)

9. CitaRepository → MySQL Database: INSERT INTO citas

10. MySQL Database → CitaRepository: Cita guardada (ID: 123)

11. CitaRepository → CitaService: Cita con ID

12. CitaService → CitaMediator: onCitaCreada(cita)
    Mediator Pattern - coordina acciones

13. CitaMediator → NotificacionService: enviarNotificacion(propietario, "Recordatorio cita")

14. NotificacionService → NotificacionFactory: crearNotificacion(tipo: EMAIL)
    Abstract Factory Pattern

15. NotificacionFactory → EmailService: Instancia de EmailNotificacion

16. EmailService → SMTP Server: Enviar email a propietario
    Asunto: "Cita programada para Max"
    Mensaje: "Su cita ha sido programada para el 20/11/2025 a las 10:00 AM"

17. SMTP Server → EmailService: Email enviado (ID externo: EMAIL-123)

18. EmailService → NotificacionService: Notificación enviada

19. NotificacionService → CitaMediator: Notificación OK

20. CitaMediator → CitaService: Proceso completado

21. CitaService → CitaFacadeService: CitaResponseDTO

22. CitaFacadeService → CitaFacadeController: ResultadoCitaConNotificacionDTO
    {
      cita: {...},
      notificacion: {...},
      mensaje: "Cita creada exitosamente con notificación"
    }

23. CitaFacadeController → Recepcionista: HTTP 201 Created + JSON Response

PATRONES IDENTIFICADOS EN EL FLUJO:
- Facade Pattern: CitaFacadeService simplifica operación compleja
- Mediator Pattern: CitaMediator coordina cita + notificación
- Chain of Responsibility: CitaValidationService (cadena de validaciones)
- Abstract Factory: NotificacionFactory crea diferentes tipos de notificaciones
- Repository Pattern: CitaRepository abstrae acceso a BD
- DTO Pattern: Desacopla API de modelo interno
```

---

### DIAGRAMA 4: Modelo de Datos (Entidad-Relación Simplificado)

**Descripción textual para crear el diagrama ER:**

```
TÍTULO: Modelo de Datos Principal - Relaciones Clave

ENTIDADES Y RELACIONES:

┌──────────────┐
│ PROPIETARIO  │
│──────────────│
│ id (PK)      │
│ tipoDoc      │
│ documento    │◄───────────┐
│ nombres      │            │ 1
│ apellidos    │            │
│ email        │            │
│ telefono     │            │
└──────────────┘            │
                             │
                             │ N
┌──────────────┐            │
│   MASCOTA    │◄───────────┘
│──────────────│
│ id (PK)      │
│ nombre       │
│ fechaNac     │◄───┐
│ sexo         │    │
│ peso         │    │ N
│ id_prop (FK) │    │
│ id_esp (FK)  │◄─┐ │
│ id_raza (FK) │  │ │
└──────┬───────┘  │ │
       │          │ │
       │ 1        │ │
       │          │ │
       │ 1:1      │ │ N
       ↓          │ │
┌──────────────┐  │ │
│  HISTORIA    │  │ │
│   CLINICA    │  │ │
│──────────────│  │ │
│ id (PK)      │  │ │
│ id_masc (FK) │  │ │
│ alergias     │  │ │
│ antecedentes │  │ │
└──────┬───────┘  │ │
       │          │ │
       │ 1        │ │
       │          │ │
       │ 1:N      │ │
       ↓          │ │
┌──────────────┐  │ │
│  EVOLUCION   │  │ │
│   CLINICA    │  │ │
│──────────────│  │ │
│ id (PK)      │  │ │
│ id_hist (FK) │  │ │
│ diagnostico  │  │ │
│ tratamiento  │  │ │
│ fecha        │  │ │
└──────────────┘  │ │
                   │ │
┌──────────────┐  │ │
│   ESPECIE    │◄─┘ │
│──────────────│    │
│ id (PK)      │    │
│ nombre       │    │
└──────┬───────┘    │
       │            │
       │ 1          │
       │            │
       │ 1:N        │
       ↓            │
┌──────────────┐    │
│     RAZA     │◄───┘
│──────────────│
│ id (PK)      │
│ nombre       │
│ id_esp (FK)  │
└──────────────┘

┌──────────────┐
│     CITA     │
│──────────────│
│ id (PK)      │
│ fechaCita    │◄───┐
│ horaCita     │    │ N
│ estado       │    │
│ motivo       │    │
│ id_masc (FK) │◄─┐ │
│ id_vet (FK)  │  │ │
│ id_serv (FK) │  │ │
└──────────────┘  │ │
                   │ │
┌──────────────┐  │ │
│ VETERINARIO  │◄─┘ │
│──────────────│    │
│ id (PK)      │    │
│ especialidad │    │
│ registro     │    │
│ id_pers (FK) │    │
└──────┬───────┘    │
       │            │
       │ extends    │
       ↓            │
┌──────────────┐    │
│  PERSONAL    │    │
│──────────────│    │
│ id (PK)      │    │
│ nombres      │    │
│ apellidos    │    │
│ id_user (FK) │◄─┐ │
└──────────────┘  │ │
                   │ │
┌──────────────┐  │ │
│   USUARIO    │◄─┘ │
│──────────────│    │
│ id (PK)      │    │
│ username     │    │
│ password     │    │
│ email        │    │
│ rol          │    │
└──────────────┘    │
                     │
┌──────────────┐    │
│   SERVICIO   │◄───┘
│──────────────│
│ id (PK)      │
│ nombre       │
│ tipoServicio │
│ precio       │
│ duracion     │
└──────────────┘

┌──────────────┐
│   INSUMO     │
│──────────────│
│ id (PK)      │
│ nombre       │◄───┐
│ codigo       │    │ N
│ precio       │    │
│ id_tipo (FK) │    │
└──────┬───────┘    │
       │            │
       │ 1:1        │
       ↓            │
┌──────────────┐    │
│ INVENTARIO   │    │
│──────────────│    │
│ id (PK)      │    │
│ id_ins (FK)  │    │
│ cantDisp     │    │
│ stockMin     │    │
└──────────────┘    │
                     │
┌──────────────┐    │
│ TIPO_INSUMO  │◄───┘
│──────────────│
│ id (PK)      │
│ nombre       │
│ descripcion  │
└──────────────┘

RELACIONES CLAVE:
- Propietario 1:N Mascota
- Mascota 1:1 Historia Clínica
- Historia Clínica 1:N Evolución Clínica
- Mascota N:1 Especie
- Raza N:1 Especie
- Cita N:1 Mascota
- Cita N:1 Veterinario
- Cita N:1 Servicio
- Veterinario extends Personal
- Personal 1:1 Usuario
- Insumo N:1 Tipo Insumo
- Insumo 1:1 Inventario

CARDINALIDADES:
1:1 - Uno a Uno
1:N - Uno a Muchos
N:1 - Muchos a Uno
N:M - Muchos a Muchos (no hay en este modelo)
```

---

## 1.7 Consideraciones Finales

### Ventajas de la Arquitectura Elegida

#### 1. **Separación Clara de Responsabilidades**

**Ventaja:** Cada capa tiene un propósito bien definido, facilitando el mantenimiento y evolución del código.

**Beneficios concretos:**
- **Controllers:** Solo manejan HTTP, no lógica de negocio
- **Services:** Solo lógica de negocio, no preocupación por BD
- **Repositories:** Solo acceso a datos, no validaciones
- **Entities:** Modelo puro de dominio

**Impacto:**
- Cambios en API no afectan lógica de negocio
- Cambios en BD no afectan controllers
- Fácil identificar dónde hacer cambios

#### 2. **Facilidad de Testing**

**Ventaja:** Arquitectura en capas facilita testing unitario y de integración.

**Beneficios concretos:**
- Services pueden testearse sin BD (mocks de repositories)
- Controllers pueden testearse sin lógica (mocks de services)
- 503 tests implementados con 100% éxito
- Uso de H2 in-memory para tests de integración

**Ejemplo de test:**
```java
@ExtendWith(MockitoExtension.class)
class MascotaServiceTest {
    @Mock private MascotaRepository mascotaRepository;
    @Mock private MascotaMapper mascotaMapper;
    @InjectMocks private MascotaServiceImpl mascotaService;

    @Test
    void crear_conDatosValidos_retornaMascota() {
        // Given
        when(mascotaRepository.save(any())).thenReturn(mascota);

        // When
        MascotaResponseDTO result = mascotaService.crear(requestDTO);

        // Then
        assertNotNull(result);
        verify(mascotaRepository, times(1)).save(any());
    }
}
```

#### 3. **Escalabilidad Horizontal (Futura)**

**Ventaja:** Arquitectura permite escalar agregando instancias del servidor Spring Boot.

**Preparación actual:**
- Sesiones stateless (JWT)
- No hay estado compartido en memoria
- Base de datos centralizada
- Posibilidad de agregar Load Balancer

**Evolución futura:**
```
Cliente → Load Balancer → [Instancia 1]
                       → [Instancia 2]  → MySQL Master
                       → [Instancia 3]  → MySQL Replica
                       → [Instancia N]
```

#### 4. **Reutilización de Código**

**Ventaja:** Componentes bien encapsulados son reutilizables.

**Ejemplos de reutilización:**
- `ValidationHelper` usado en múltiples servicios
- `Constants` compartido en todo el proyecto
- DTOs reutilizados en múltiples endpoints
- Mappers generan listas automáticamente

#### 5. **Documentación Automática con OpenAPI/Swagger**

**Ventaja:** Documentación de API generada automáticamente.

**Beneficios:**
- Swagger UI disponible en `/swagger-ui.html`
- Especificación OpenAPI 3.0 en `/v3/api-docs`
- Pruebas interactivas desde navegador
- Facilita integración con frontend

#### 6. **Seguridad Robusta**

**Ventaja:** Spring Security + JWT proporciona autenticación y autorización enterprise-grade.

**Características:**
- Passwords hasheados con BCrypt (irreversible)
- Tokens JWT con expiración configurable
- Control de acceso a nivel de método (@PreAuthorize)
- Auditoría de accesos a datos sensibles (Proxy Pattern)
- Protección contra OWASP Top 10

#### 7. **Mantenibilidad a Largo Plazo**

**Ventaja:** Código limpio y bien estructurado reduce costos de mantenimiento.

**Factores que contribuyen:**
- Nomenclatura consistente y descriptiva
- Comentarios JavaDoc en métodos complejos
- Principios SOLID aplicados
- Patrones de diseño documentados (16 tipos)
- Código DRY (Don't Repeat Yourself)

---

### Posibles Desventajas o Retos

#### 1. **Latencia por Múltiples Capas**

**Desventaja:** Cada petición atraviesa 4-5 capas, agregando overhead.

**Mitigación actual:**
- Tiempos de respuesta < 200ms para CRUD básico
- Caché en memoria para catálogos
- Queries optimizadas con índices
- Lazy loading en JPA para evitar N+1

**Cuándo es un problema:**
- Operaciones de alta frecuencia (>1000 req/s)
- Reportes muy complejos con JOIN de múltiples tablas

**Solución futura:**
- Implementar Redis para caché distribuido
- Agregar CQRS para separar lecturas de escrituras
- Considerar denormalización para reportes

#### 2. **Acoplamiento con Framework (Spring Boot)**

**Desventaja:** Código fuertemente acoplado a anotaciones de Spring.

**Riesgo:**
- Difícil migrar a otro framework
- Dependencia de ciclo de vida de Spring

**Mitigación:**
- Interfaces desacoplan lógica de implementación
- Lógica de negocio en POJOs (sin anotaciones Spring)
- DTOs son independientes del framework

**Cuándo es un problema:**
- Si Spring Boot fuera deprecado (poco probable)
- Si se requiere cambiar a framework más liviano

#### 3. **Complejidad Inicial para Desarrolladores Nuevos**

**Desventaja:** Curva de aprendizaje moderada por cantidad de componentes.

**Dificultades:**
- Entender flujo completo (Controller → Service → Repository)
- Diferenciar cuándo usar Service vs Facade
- Comprender 16 patrones de diseño implementados

**Mitigación:**
- Documentación exhaustiva (este documento)
- Nombres descriptivos y auto-documentados
- Tests como ejemplos de uso
- Onboarding con pair programming

#### 4. **Overhead de DTOs y Mappers**

**Desventaja:** Crear y mantener DTOs + Mappers para cada entidad.

**Esfuerzo:**
- 2-3 DTOs por entidad (Request, Response, Summary)
- Mapper por entidad
- Mantener sincronizado con cambios en entidades

**Beneficios que justifican:**
- Desacoplamiento de API y modelo de datos
- Versionado de API sin afectar BD
- Optimización de payload (solo campos necesarios)
- Validaciones centralizadas

**Herramientas que ayudan:**
- MapStruct genera implementaciones automáticamente
- Lombok reduce boilerplate (@Data, @Builder)

#### 5. **Monolito: Límite de Escalabilidad**

**Desventaja:** Escalado vertical tiene límites físicos.

**Cuándo se vuelve problema:**
- >10,000 usuarios concurrentes
- >1 millón de registros de mascotas
- Múltiples clínicas en diferentes países

**Solución futura:**
- Migrar a microservicios (módulos ya están bien separados)
- Extraer módulos pesados (Reportes, Notificaciones)
- Implementar Event-Driven Architecture

**Preparación actual:**
- Módulos de dominio son bounded contexts claros
- Uso de interfaces permite refactoring sin romper contratos
- DTOs facilitan versionado de APIs

---

### Recomendaciones de Mejora

#### Corto Plazo (1-3 meses):

1. **Implementar Caché Distribuido con Redis:**
   - Cachear catálogos (especies, razas, servicios)
   - Cachear dashboard para reducir carga en BD
   - TTL configurables por tipo de dato

2. **Agregar Paginación Global:**
   - Implementar `Pageable` en todos los listados
   - Retornar `Page<T>` en lugar de `List<T>`
   - Configurar tamaño máximo de página (100)

3. **Implementar Rate Limiting:**
   - Bucket4j o similares
   - Limitar login a 5 intentos por minuto
   - Limitar creación de citas a 100 por hora

4. **Mejorar Observabilidad:**
   - Integrar Spring Boot Actuator con Prometheus
   - Agregar métricas personalizadas (citas creadas/día)
   - Configurar alertas en Grafana

5. **Implementar Refresh Tokens:**
   - JWT de corta duración (15 min)
   - Refresh token de larga duración (7 días)
   - Renovación automática sin re-login

#### Mediano Plazo (3-6 meses):

6. **Separar Módulo de Notificaciones:**
   - Notificaciones asíncronas con RabbitMQ/Kafka
   - Reintentos automáticos si falla envío
   - Dashboard de notificaciones enviadas/fallidas

7. **Implementar Audit Log Completo:**
   - Registrar todos los cambios en entidades críticas
   - Tabla de auditoría con before/after values
   - Reportes de auditoría por usuario

8. **Agregar CQRS para Reportes:**
   - Separar modelo de lectura (reportes) del de escritura
   - Base de datos read-replica para reportes pesados
   - Reportes sin impactar operaciones transaccionales

9. **Internacionalización (i18n):**
   - Mensajes de error en múltiples idiomas
   - Formato de fechas/monedas por locale
   - Spring MessageSource

#### Largo Plazo (6-12 meses):

10. **Migrar a Microservicios Selectivos:**
    - Extraer módulo de Reportes (queries pesadas)
    - Extraer módulo de Notificaciones (asíncrono)
    - API Gateway con Spring Cloud Gateway
    - Service Discovery con Eureka

11. **Implementar Multi-Tenancy:**
    - Soportar múltiples clínicas en mismo sistema
    - Aislamiento de datos por tenant
    - Configuración por tenant (logo, colores)

12. **Event Sourcing para Historias Clínicas:**
    - Registro inmutable de todos los eventos médicos
    - Posibilidad de reproducir estado en cualquier momento
    - Cumplimiento de normativas médicas estrictas

---

Esta arquitectura proporciona una base sólida y escalable para el sistema de gestión de clínicas veterinarias, balanceando simplicidad, mantenibilidad y capacidad de evolución futura.

---

# 5. HISTORIAL DE CAMBIOS

## Versión 1.1.0 (19 de Noviembre de 2025)

### Cambios en Arquitectura y Patrones

#### Patrones de Diseño Implementados
- **Total de patrones activos:** 15/18 (83%)
- **Mejora respecto a versión anterior:** +23%

**Patrones Creacionales (5/5 activos):**
- ✅ Builder (CitaBuilder, HistoriaClinicaBuilder, ReporteBuilder)
- ✅ Factory Method (ServicioFactory con 4 implementaciones)
- ✅ Abstract Factory (NotificacionFactory multicanal)
- ✅ Singleton (AuditLogger, ConfigurationManager)

**Patrones Estructurales (5/5 activos):**
- ✅ Facade (5 facades: Cita, Dashboard, Búsqueda, Operaciones, Reportes)
- ✅ Proxy (InventarioProxy, HistoriaClinicaProxy, CachedServiceProxy)
- ✅ Decorator (ServicioDecorator para recargos dinámicos)
- ✅ Bridge (Integrado en ReporteBuilder para formatos PDF/Excel/JSON)

**Patrones Comportamentales (5/8 activos):**
- ✅ Observer (3 observers registrados automáticamente)
- ✅ Mediator (CitaMediator coordinando operaciones)
- ✅ Chain of Responsibility (Validaciones en cadena)
- ✅ Template Method (3 flujos de atención diferenciados)
- ✅ Memento (Historial de versiones de historias clínicas)
- ⚠️ Command, State (implementados pero no usados - decisión de diseño)

### Limpieza de Código

#### Módulo de Inventario - Eliminación de Proveedor
Se eliminaron las referencias al campo `proveedor` que ya no forma parte del alcance del proyecto:

**Archivos modificados:**
- `InsumoRequestDTO.java` - Eliminado campo `idProveedor`
- `InsumoResponseDTO.java` - Eliminados campos `idProveedor` y `nombreProveedor`
- `InsumoMapper.java` - Eliminados `@Mapping` de proveedor

**Justificación:** El módulo de proveedores fue removido del alcance actual, simplificando el modelo de inventario.

#### Eliminación de Adapter de Pagos
Se eliminó el patrón Adapter de pagos que no era requerido:

**Archivos eliminados:**
- `PaymentGatewayAdapter.java`
- `PayPalPaymentAdapter.java`
- `StripePaymentAdapter.java`
- Tests relacionados

**Justificación:** No se requiere funcionalidad de pagos en el alcance actual.

### Mejoras en Observers
- Se creó `ObserverConfiguration.java` para registrar observers automáticamente
- 3 observers activos: AuditoriaObserver, NotificacionObserver, RecordatorioObserver
- Notificaciones automáticas funcionando correctamente

### Mejoras en Caché
- `CachedServiceProxy` integrado en `MascotaServiceImpl` y `PropietarioServiceImpl`
- TTL configurado a 5 minutos para consultas frecuentes
- Invalidación automática en operaciones CUD

### Estado de Tests
- **Total de tests:** 484+
- **Porcentaje de éxito:** 100%
- **Cobertura estimada:** ~29% (85 clases de test / 294 clases fuente)

### Mejoras en Seguridad
- `HistoriaClinicaProxy` integrado para control de acceso por roles
- Auditoría automática de accesos a datos sensibles

---

## Versión 1.0.0 (18 de Noviembre de 2025)

### Lanzamiento Inicial
- Arquitectura en Capas + principios DDD
- 28 Controllers REST
- 26+ Services de dominio
- 25 Entidades JPA
- 20+ Repositories
- Sistema de autenticación JWT
- 16 patrones de diseño implementados inicialmente

---

# 6. MÉTRICAS DEL PROYECTO

## Estadísticas de Código

| Métrica | Valor |
|---------|-------|
| **Archivos Java (producción)** | 296 |
| **Archivos de Test** | 85 |
| **Líneas de código estimadas** | 15,000+ |
| **Controllers** | 28 |
| **Services** | 26+ |
| **Entities** | 25 |
| **Repositories** | 20+ |
| **DTOs** | 80+ |

## Estado de Calidad

| Aspecto | Estado |
|---------|--------|
| **Tests pasando** | 484/484 (100%) ✅ |
| **Errores de compilación** | 0 ✅ |
| **Patrones activos** | 15/18 (83%) ✅ |
| **Principios SOLID** | Aplicados ✅ |

---

**Documento actualizado:** 19 de Noviembre de 2025
**Próxima revisión programada:** Según cambios significativos

