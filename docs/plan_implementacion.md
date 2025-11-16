# 📋 Plan de Implementación - Sistema de Gestión Veterinaria

## 🎯 Objetivo
Desarrollar un sistema completo de gestión veterinaria implementando 19 patrones de diseño de forma coherente y justificada, siguiendo las mejores prácticas de Spring Boot y arquitectura por capas.

---

## 🏗️ Estructura de Carpetas del Proyecto

```
clinica_veternica/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── veterinaria/
│   │   │           └── clinica_veternica/
│   │   │               ├── ClinicaVeternicaApplication.java
│   │   │               │
│   │   │               ├── config/                    # Configuraciones
│   │   │               │   ├── SecurityConfig.java    # Spring Security + JWT
│   │   │               │   ├── SwaggerConfig.java     # OpenAPI/Swagger
│   │   │               │   ├── CacheConfig.java       # Spring Cache
│   │   │               │   ├── DatabaseConfig.java    # DataSource
│   │   │               │   └── AppConfig.java         # Beans generales
│   │   │               │
│   │   │               ├── domain/                    # Capa de Dominio (Entidades JPA)
│   │   │               │   ├── usuario/
│   │   │               │   │   ├── Usuario.java
│   │   │               │   │   ├── Personal.java (abstract)
│   │   │               │   │   ├── Veterinario.java
│   │   │               │   │   ├── Administrador.java
│   │   │               │   │   ├── Recepcionista.java
│   │   │               │   │   ├── AuxiliarVeterinario.java
│   │   │               │   │   └── RolUsuario.java (enum)
│   │   │               │   │
│   │   │               │   ├── paciente/
│   │   │               │   │   ├── Mascota.java
│   │   │               │   │   ├── Propietario.java
│   │   │               │   │   ├── Especie.java
│   │   │               │   │   └── Raza.java
│   │   │               │   │
│   │   │               │   ├── practica/
│   │   │               │   │   ├── Estudiante.java
│   │   │               │   │   ├── SupervisorPractica.java
│   │   │               │   │   ├── EvaluacionEstudiante.java
│   │   │               │   │   └── Bitacora.java
│   │   │               │   │
│   │   │               │   ├── agenda/
│   │   │               │   │   ├── Cita.java
│   │   │               │   │   ├── Horario.java
│   │   │               │   │   ├── Servicio.java
│   │   │               │   │   ├── EstadoCita.java (enum)
│   │   │               │   │   ├── CategoriaServicio.java (enum)
│   │   │               │   │   └── TipoServicio.java (enum)
│   │   │               │   │
│   │   │               │   ├── clinico/
│   │   │               │   │   ├── HistoriaClinica.java
│   │   │               │   │   ├── EvolucionClinica.java
│   │   │               │   │   ├── Tratamiento.java
│   │   │               │   │   ├── RecetaMedica.java
│   │   │               │   │   └── Vacunacion.java
│   │   │               │   │
│   │   │               │   ├── inventario/
│   │   │               │   │   ├── Insumo.java
│   │   │               │   │   ├── Proveedor.java
│   │   │               │   │   ├── TipoInsumo.java
│   │   │               │   │   ├── Inventario.java
│   │   │               │   │   ├── MovimientoInventario.java
│   │   │               │   │   └── EstadoInsumo.java (enum)
│   │   │               │   │
│   │   │               │   ├── facturacion/
│   │   │               │   │   ├── Factura.java
│   │   │               │   │   ├── DetalleFactura.java
│   │   │               │   │   ├── Pago.java
│   │   │               │   │   └── MetodoPago.java (enum)
│   │   │               │   │
│   │   │               │   └── comunicacion/
│   │   │               │       ├── Notificacion.java
│   │   │               │       ├── RecordatorioCita.java
│   │   │               │       └── Correo.java
│   │   │               │
│   │   │               ├── dto/                       # Data Transfer Objects
│   │   │               │   ├── request/
│   │   │               │   │   ├── LoginRequest.java
│   │   │               │   │   ├── MascotaRequest.java
│   │   │               │   │   ├── CitaRequest.java
│   │   │               │   │   ├── ServicioRequest.java
│   │   │               │   │   ├── FacturaRequest.java
│   │   │               │   │   └── ...
│   │   │               │   │
│   │   │               │   └── response/
│   │   │               │       ├── AuthResponse.java
│   │   │               │       ├── MascotaResponse.java
│   │   │               │       ├── CitaResponse.java
│   │   │               │       ├── ErrorResponse.java
│   │   │               │       └── ...
│   │   │               │
│   │   │               ├── repository/                # Spring Data JPA Repositories
│   │   │               │   ├── UsuarioRepository.java
│   │   │               │   ├── MascotaRepository.java
│   │   │               │   ├── PropietarioRepository.java
│   │   │               │   ├── CitaRepository.java
│   │   │               │   ├── ServicioRepository.java
│   │   │               │   ├── HistoriaClinicaRepository.java
│   │   │               │   ├── InsumoRepository.java
│   │   │               │   ├── FacturaRepository.java
│   │   │               │   └── ...
│   │   │               │
│   │   │               ├── service/                   # Capa de Servicios (Lógica de Negocio)
│   │   │               │   ├── interfaces/
│   │   │               │   │   ├── IUsuarioService.java
│   │   │               │   │   ├── IMascotaService.java
│   │   │               │   │   ├── ICitaService.java
│   │   │               │   │   ├── IServicioService.java
│   │   │               │   │   ├── IInventarioService.java
│   │   │               │   │   ├── IFacturacionService.java
│   │   │               │   │   └── ...
│   │   │               │   │
│   │   │               │   └── impl/
│   │   │               │       ├── UsuarioServiceImpl.java
│   │   │               │       ├── MascotaServiceImpl.java
│   │   │               │       ├── CitaServiceImpl.java
│   │   │               │       ├── ServicioServiceImpl.java
│   │   │               │       ├── InventarioServiceImpl.java
│   │   │               │       ├── FacturacionServiceImpl.java
│   │   │               │       └── ...
│   │   │               │
│   │   │               ├── controller/                # Controllers REST
│   │   │               │   ├── AuthController.java
│   │   │               │   ├── MascotaController.java
│   │   │               │   ├── PropietarioController.java
│   │   │               │   ├── CitaController.java
│   │   │               │   ├── ServicioController.java
│   │   │               │   ├── HistoriaClinicaController.java
│   │   │               │   ├── InventarioController.java
│   │   │               │   ├── FacturacionController.java
│   │   │               │   └── ...
│   │   │               │
│   │   │               ├── patterns/                  # 🎨 PATRONES DE DISEÑO
│   │   │               │   │
│   │   │               │   ├── creational/            # Patrones Creacionales
│   │   │               │   │   │
│   │   │               │   │   ├── singleton/
│   │   │               │   │   │   ├── ConfigurationManager.java
│   │   │               │   │   │   └── AuditLogger.java
│   │   │               │   │   │
│   │   │               │   │   ├── factory/
│   │   │               │   │   │   ├── ServicioFactory.java (abstract)
│   │   │               │   │   │   ├── ServicioClinicoFactory.java
│   │   │               │   │   │   ├── ServicioQuirurgicoFactory.java
│   │   │               │   │   │   ├── ServicioEsteticoFactory.java
│   │   │               │   │   │   └── ServicioEmergenciaFactory.java
│   │   │               │   │   │
│   │   │               │   │   ├── abstractfactory/
│   │   │               │   │   │   ├── NotificacionFactory.java (interface)
│   │   │               │   │   │   ├── EmailNotificacionFactory.java
│   │   │               │   │   │   ├── SMSNotificacionFactory.java
│   │   │               │   │   │   ├── WhatsAppNotificacionFactory.java
│   │   │               │   │   │   └── PushNotificacionFactory.java
│   │   │               │   │   │
│   │   │               │   │   ├── builder/
│   │   │               │   │   │   ├── FacturaBuilder.java
│   │   │               │   │   │   ├── HistoriaClinicaBuilder.java
│   │   │               │   │   │   ├── CitaBuilder.java
│   │   │               │   │   │   └── ReporteBuilder.java
│   │   │               │   │   │
│   │   │               │   ├── structural/            # Patrones Estructurales
│   │   │               │   │   │
│   │   │               │   │   ├── adapter/
│   │   │               │   │   │   ├── PaymentGatewayAdapter.java (interface)
│   │   │               │   │   │   ├── StripePaymentAdapter.java
│   │   │               │   │   │   ├── PayPalPaymentAdapter.java
│   │   │               │   │   │   └── MercadoPagoAdapter.java
│   │   │               │   │   │
│   │   │               │   │   ├── bridge/
│   │   │               │   │   │   ├── ReporteAbstraction.java (abstract)
│   │   │               │   │   │   ├── ReporteImplementor.java (interface)
│   │   │               │   │   │   ├── ReportePDFImpl.java
│   │   │               │   │   │   ├── ReporteExcelImpl.java
│   │   │               │   │   │   └── ReporteJSONImpl.java
│   │   │               │   │   │
│   │   │               │   │   ├── decorator/
│   │   │               │   │   │   ├── ServicioDecorator.java (abstract)
│   │   │               │   │   │   ├── ServicioConDescuentoDecorator.java
│   │   │               │   │   │   ├── ServicioConSeguroDecorator.java
│   │   │               │   │   │   ├── ServicioUrgenciaDecorator.java
│   │   │               │   │   │   └── ServicioDomicilioDecorator.java
│   │   │               │   │   │
│   │   │               │   │   ├── facade/            # ⭐ IMPORTANTE
│   │   │               │   │   │   ├── ClinicaFacade.java
│   │   │               │   │   │   ├── CitaFacade.java
│   │   │               │   │   │   ├── FacturacionFacade.java
│   │   │               │   │   │   └── InventarioFacade.java
│   │   │               │   │   │
│   │   │               │   │   └── proxy/
│   │   │               │   │       ├── InventarioProxy.java
│   │   │               │   │       ├── HistoriaClinicaProxy.java
│   │   │               │   │       └── CachedServiceProxy.java
│   │   │               │   │
│   │   │               │   └── behavioral/            # Patrones de Comportamiento
│   │   │               │       │
│   │   │               │       ├── observer/
│   │   │               │       │   ├── CitaObserver.java (interface)
│   │   │               │       │   ├── CitaSubject.java
│   │   │               │       │   ├── NotificacionObserver.java
│   │   │               │       │   ├── RecordatorioObserver.java
│   │   │               │       │   ├── InventarioObserver.java
│   │   │               │       │   └── AuditoriaObserver.java
│   │   │               │       │
│   │   │               │       ├── strategy/
│   │   │               │       │   ├── PagoStrategy.java (interface)
│   │   │               │       │   ├── PagoEfectivoStrategy.java
│   │   │               │       │   ├── PagoTarjetaStrategy.java
│   │   │               │       │   └── PagoTransferenciaStrategy.java
│   │   │               │       │
│   │   │               │       ├── template/
│   │   │               │       │   ├── AtencionTemplate.java (abstract)
│   │   │               │       │   ├── AtencionConsultaGeneral.java
│   │   │               │       │   ├── AtencionCirugia.java
│   │   │               │       │   └── AtencionEmergencia.java
│   │   │               │       │
│   │   │               │       ├── chain/
│   │   │               │       │   ├── ValidacionHandler.java (abstract)
│   │   │               │       │   ├── ValidacionDatosHandler.java
│   │   │               │       │   ├── ValidacionDisponibilidadHandler.java
│   │   │               │       │   ├── ValidacionPermisoHandler.java
│   │   │               │       │   └── ValidacionStockHandler.java
│   │   │               │       │
│   │   │               │       ├── command/
│   │   │               │       │   ├── Command.java (interface)
│   │   │               │       │   ├── CrearCitaCommand.java
│   │   │               │       │   ├── CancelarCitaCommand.java
│   │   │               │       │   ├── ActualizarStockCommand.java
│   │   │               │       │   └── CommandInvoker.java
│   │   │               │       │
│   │   │               │       ├── state/
│   │   │               │       │   ├── CitaState.java (interface)
│   │   │               │       │   ├── CitaProgramadaState.java
│   │   │               │       │   ├── CitaConfirmadaState.java
│   │   │               │       │   ├── CitaAtendidaState.java
│   │   │               │       │   └── CitaCanceladaState.java
│   │   │               │       │
│   │   │               │       ├── mediator/
│   │   │               │       │   ├── CitaMediator.java (interface)
│   │   │               │       │   ├── CitaMediatorImpl.java
│   │   │               │       │   └── Component.java (abstract)
│   │   │               │       │
│   │   │               │       └── memento/
│   │   │               │           ├── HistoriaClinicaMemento.java
│   │   │               │           ├── HistoriaClinicaCaretaker.java
│   │   │               │           └── HistoriaClinicaOriginator.java
│   │   │               │
│   │   │               ├── security/                  # Seguridad
│   │   │               │   ├── jwt/
│   │   │               │   │   ├── JwtTokenProvider.java
│   │   │               │   │   ├── JwtAuthenticationFilter.java
│   │   │               │   │   └── JwtAuthenticationEntryPoint.java
│   │   │               │   │
│   │   │               │   └── CustomUserDetailsService.java
│   │   │               │
│   │   │               ├── exception/                 # Manejo de Excepciones
│   │   │               │   ├── GlobalExceptionHandler.java (@ControllerAdvice)
│   │   │               │   ├── ResourceNotFoundException.java
│   │   │               │   ├── UnauthorizedException.java
│   │   │               │   ├── ValidationException.java
│   │   │               │   ├── BusinessException.java
│   │   │               │   └── ...
│   │   │               │
│   │   │               ├── validation/                # Validaciones personalizadas
│   │   │               │   ├── validators/
│   │   │               │   └── annotations/
│   │   │               │
│   │   │               └── util/                      # Utilidades
│   │   │                   ├── Constants.java
│   │   │                   ├── DateUtils.java
│   │   │                   └── ResponseUtils.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       ├── data.sql (datos iniciales)
│   │       └── schema.sql (opcional)
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── veterinaria/
│                   └── clinica_veternica/
│                       ├── service/
│                       ├── controller/
│                       └── integration/
│
├── docs/
│   ├── contexto_proyecto.md
│   ├── diagrama_clases.md
│   ├── historias_por_epica.md
│   ├── plan_implementacion.md (este archivo)
│   └── postman_collection.json (para testing)
│
├── pom.xml
├── .gitignore
└── README.md
```

---

## 🎨 Mapeo de Patrones de Diseño a Funcionalidades

### **1. SINGLETON** 🔐
**Uso**: Gestión de configuración global y logging
- `ConfigurationManager`: Configuración centralizada de la aplicación
- `AuditLogger`: Sistema único de auditoría

**Justificación**: Garantiza una única instancia para recursos compartidos críticos.

---

### **2. FACTORY METHOD** 🏭
**Uso**: Creación de diferentes tipos de servicios veterinarios
- `ServicioFactory` (abstract)
- `ServicioClinicoFactory`
- `ServicioQuirurgicoFactory`
- `ServicioEsteticoFactory`
- `ServicioEmergenciaFactory`

**Justificación**: Cada categoría de servicio tiene características específicas (precio, duración, insumos). El Factory encapsula la lógica de creación.

**Épicas relacionadas**: Épica 2 (Gestión de Servicios)

---

### **3. ABSTRACT FACTORY** 📱
**Uso**: Sistema de notificaciones multi-canal
- `NotificacionFactory` (interface)
- `EmailNotificacionFactory`
- `SMSNotificacionFactory`
- `WhatsAppNotificacionFactory`
- `PushNotificacionFactory`

**Justificación**: Permite crear familias de objetos relacionados (notificaciones por diferentes canales) sin especificar sus clases concretas.

**Épicas relacionadas**: Épica 3 (Gestión de Citas - Recordatorios)

---

### **4. BUILDER** 🏗️
**Uso**: Construcción de objetos complejos paso a paso
- `FacturaBuilder`: Factura con múltiples detalles
- `HistoriaClinicaBuilder`: Historia con evoluciones, tratamientos, recetas
- `CitaBuilder`: Cita con validaciones y datos opcionales
- `ReporteBuilder`: Reportes con múltiples filtros

**Justificación**: Objetos con múltiples atributos opcionales y validaciones complejas.

**Épicas relacionadas**: Épicas 4, 6 (Historia Clínica, Facturación)

---

### **5. ADAPTER** 🔌
**Uso**: Integración con pasarelas de pago externas
- `PaymentGatewayAdapter` (interface)
- `StripePaymentAdapter`
- `PayPalPaymentAdapter`
- `MercadoPagoAdapter`

**Justificación**: Diferentes APIs de pago tienen interfaces incompatibles. El Adapter las unifica.

**Épicas relacionadas**: Épica 6 (Gestión de Pagos)

---

### **6. BRIDGE** 🌉
**Uso**: Generación de reportes en múltiples formatos
- `ReporteAbstraction` (abstract): Define qué reportar
- `ReporteImplementor` (interface): Define cómo exportar
- Implementaciones: PDF, Excel, JSON

**Justificación**: Separa la abstracción (tipo de reporte) de su implementación (formato de salida).

**Épicas relacionadas**: Todas las épicas que requieran reportes

---

### **7. DECORATOR** 🎁
**Uso**: Agregar funcionalidades adicionales a servicios dinámicamente
- `ServicioDecorator` (abstract)
- `ServicioConDescuentoDecorator`: Aplica descuentos
- `ServicioConSeguroDecorator`: Incluye seguro
- `ServicioUrgenciaDecorator`: Cargo adicional por urgencia
- `ServicioDomicilioDecorator`: Servicio a domicilio

**Justificación**: Permite agregar responsabilidades sin modificar la clase base.

**Épicas relacionadas**: Épica 2 (Gestión de Servicios), Épica 6 (Facturación)

---

### **8. FACADE** ⭐ **(IMPORTANTE)** 🏛️
**Uso**: Simplificar subsistemas complejos
- `ClinicaFacade`: Operaciones comunes de la clínica
- `CitaFacade`: Coordina la creación de citas (validaciones, notificaciones, disponibilidad)
- `FacturacionFacade`: Proceso completo de facturación
- `InventarioFacade`: Gestión simplificada de inventario

**Justificación**: Oculta la complejidad de múltiples servicios detrás de una interfaz simple.

**Épicas relacionadas**: Todas las épicas principales

---

### **9. PROXY** 🛡️
**Uso**: Control de acceso y caché
- `InventarioProxy`: Verifica permisos antes de modificar inventario
- `HistoriaClinicaProxy`: Control de acceso a historias clínicas sensibles
- `CachedServiceProxy`: Implementa caché para consultas frecuentes

**Justificación**: Añade control de acceso y optimización sin modificar las clases reales.

**Épicas relacionadas**: Épica 5 (Inventario), Épica 4 (Historia Clínica)

---

### **10. CHAIN OF RESPONSIBILITY** ⛓️
**Uso**: Cadena de validaciones
- `ValidacionHandler` (abstract)
- `ValidacionDatosHandler`: Valida datos de entrada
- `ValidacionDisponibilidadHandler`: Valida disponibilidad de horario
- `ValidacionPermisoHandler`: Valida permisos del usuario
- `ValidacionStockHandler`: Valida stock de insumos

**Justificación**: Permite que múltiples objetos tengan la oportunidad de manejar la solicitud.

**Épicas relacionadas**: Épicas 3, 5 (Citas, Inventario)

---

### **11. COMMAND** 📝
**Uso**: Encapsular operaciones como objetos
- `Command` (interface)
- `CrearCitaCommand`
- `CancelarCitaCommand`
- `ActualizarStockCommand`
- `CommandInvoker`: Ejecutor de comandos

**Justificación**: Permite deshacer operaciones, auditoría y colas de comandos.

**Épicas relacionadas**: Épicas 3, 5 (Citas, Inventario)

---

### **12. OBSERVER** 👁️
**Uso**: Sistema de notificaciones automáticas
- `CitaObserver` (interface)
- `NotificacionObserver`: Envía notificaciones al cambiar estado de cita
- `RecordatorioObserver`: Programa recordatorios
- `InventarioObserver`: Alerta de stock bajo
- `AuditoriaObserver`: Registra cambios para auditoría

**Justificación**: Notifica automáticamente a múltiples observadores cuando cambia el estado.

**Épicas relacionadas**: Épica 3 (Citas), Épica 5 (Inventario)

---

### **13. STRATEGY** 💡
**Uso**: Diferentes estrategias de pago
- `PagoStrategy` (interface)
- `PagoEfectivoStrategy`
- `PagoTarjetaStrategy`
- `PagoTransferenciaStrategy`

**Justificación**: Permite cambiar el algoritmo de procesamiento de pago en tiempo de ejecución.

**Épicas relacionadas**: Épica 6 (Gestión de Pagos)

---

### **14. TEMPLATE METHOD** 📋
**Uso**: Flujo estándar de atención con pasos variables
- `AtencionTemplate` (abstract): Define el flujo general
- `AtencionConsultaGeneral`
- `AtencionCirugia`
- `AtencionEmergencia`

**Justificación**: Define el esqueleto de un algoritmo, delegando pasos específicos a subclases.

**Épicas relacionadas**: Épica 3, 4 (Citas, Historia Clínica)

---

### **15. MEDIATOR** 🤝
**Uso**: Coordinar comunicación entre componentes de una cita
- `CitaMediator`: Coordina Veterinario, Mascota, Horario, Servicio, Notificaciones
- `Component`: Componentes que se comunican vía mediador

**Justificación**: Reduce el acoplamiento entre componentes que necesitan interactuar.

**Épicas relacionadas**: Épica 3 (Gestión de Citas)

---

### **16. MEMENTO** 💾
**Uso**: Guardar y restaurar estados de historias clínicas
- `HistoriaClinicaMemento`: Captura estado
- `HistoriaClinicaCaretaker`: Gestiona mementos
- `HistoriaClinicaOriginator`: Crea y restaura desde mementos

**Justificación**: Permite deshacer cambios en historias clínicas sin violar encapsulación.

**Épicas relacionadas**: Épica 4 (Historia Clínica)

---

### **17. STATE** 🔄
**Uso**: Gestión de estados de citas
- `CitaState` (interface)
- `CitaProgramadaState`
- `CitaConfirmadaState`
- `CitaAtendidaState`
- `CitaCanceladaState`

**Justificación**: El comportamiento de una cita cambia según su estado.

**Épicas relacionadas**: Épica 3 (Gestión de Citas)

---

## 📦 Dependencias Maven (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-cache</artifactId>
    </dependency>

    <!-- MySQL Driver -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>

    <!-- OpenAPI/Swagger -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.3.0</version>
    </dependency>

    <!-- Lombok (opcional, reduce boilerplate) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- MapStruct (para mapeo DTO <-> Entity) -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>1.5.5.Final</version>
        <scope>provided</scope>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- H2 Database (para testing) -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## 🗓️ Plan de Implementación por Fases

### **FASE 0: Configuración Inicial** (Prioridad: CRÍTICA)
- ✅ Estructura de carpetas
- ✅ Configuración de `pom.xml` con todas las dependencias
- ✅ Configuración de `application.properties`
- ✅ Configuración de base de datos MySQL
- ✅ Configuración de Swagger
- ✅ Configuración de Spring Security (básica)

---

### **FASE 1: Capa de Dominio (Entidades JPA)** (Prioridad: CRÍTICA)
**Orden de implementación**:
1. Entidades base sin relaciones
   - `Usuario`, `RolUsuario`, `Personal` y sus subclases
   - `Propietario`, `Especie`, `Raza`, `Mascota`
   - Enums básicos

2. Entidades con relaciones simples
   - `Horario`, `Servicio`
   - `Insumo`, `Proveedor`, `TipoInsumo`

3. Entidades con relaciones complejas
   - `Cita` (relaciona Mascota, Veterinario, Servicio, Horario)
   - `HistoriaClinica`, `EvolucionClinica`, `Tratamiento`, `RecetaMedica`
   - `Factura`, `DetalleFactura`, `Pago`
   - `Notificacion`, `RecordatorioCita`

**Tiempo estimado**: 3-4 días

---

### **FASE 2: Capa de Repositorios** (Prioridad: ALTA)
- Crear todos los repositorios que extienden `JpaRepository`
- Implementar queries personalizados con `@Query`
- Implementar queries derivados por nombre de método
- Queries para búsquedas complejas (filtros, paginación)

**Tiempo estimado**: 1-2 días

---

### **FASE 3: DTOs y Mappers** (Prioridad: ALTA)
- Crear DTOs de Request y Response
- Implementar Mappers con MapStruct
- Validaciones con Bean Validation (`@Valid`, `@NotNull`, etc.)

**Tiempo estimado**: 2-3 días

---

### **FASE 4: Manejo de Excepciones** (Prioridad: ALTA)
- Crear excepciones personalizadas
- Implementar `@ControllerAdvice` global
- Estructura de respuesta de error estándar

**Tiempo estimado**: 1 día

---

### **FASE 5: Patrones Creacionales** (Prioridad: MEDIA-ALTA)
**Orden de implementación**:
1. **Singleton** → ConfigurationManager, AuditLogger
2. **Factory Method** → ServicioFactory y sus implementaciones
3. **Abstract Factory** → NotificacionFactory
4. **Builder** → FacturaBuilder, HistoriaClinicaBuilder, CitaBuilder
5. **Prototype** → PlantillaServicio

**Tiempo estimado**: 2-3 días

---

### **FASE 6: Capa de Servicios (Lógica de Negocio)** (Prioridad: CRÍTICA)
**Orden de implementación**:
1. Servicios básicos
   - `UsuarioService` (con BCrypt para passwords)
   - `MascotaService`, `PropietarioService`
   - `EspecieService`, `RazaService`

2. Servicios intermedios
   - `ServicioService` (usa Factory pattern)
   - `HorarioService`
   - `InventarioService` (usa Proxy pattern)

3. Servicios avanzados
   - `CitaService` (usa múltiples patrones)
   - `HistoriaClinicaService` (usa Memento, Builder)
   - `FacturacionService` (usa Strategy, Builder)

**Tiempo estimado**: 5-6 días

---

### **FASE 7: Patrones Estructurales** (Prioridad: ALTA)
**Orden de implementación**:
1. **Facade** ⭐ → ClinicaFacade, CitaFacade, FacturacionFacade
2. **Proxy** → InventarioProxy, HistoriaClinicaProxy
3. **Adapter** → PaymentGatewayAdapters
4. **Decorator** → ServicioDecorators
5. **Bridge** → Sistema de reportes

**Tiempo estimado**: 3-4 días

---

### **FASE 8: Patrones de Comportamiento** (Prioridad: ALTA)
**Orden de implementación**:
1. **Strategy** → Estrategias de pago
2. **Observer** → Sistema de notificaciones
3. **State** → Estados de cita
4. **Template Method** → Flujos de atención
5. **Chain of Responsibility** → Validaciones
6. **Command** → Comandos de operaciones
7. **Mediator** → Mediador de citas
8. **Memento** → Respaldo de historia clínica

**Tiempo estimado**: 4-5 días

---

### **FASE 9: Seguridad (Spring Security + JWT)** (Prioridad: CRÍTICA)
- Configurar `SecurityConfig`
- Implementar `JwtTokenProvider`
- Implementar `JwtAuthenticationFilter`
- Implementar `CustomUserDetailsService`
- Control de acceso basado en roles (`@PreAuthorize`)

**Tiempo estimado**: 2-3 días

---

### **FASE 10: Capa de Controllers** (Prioridad: CRÍTICA)
**Orden de implementación**:
1. `AuthController` (login, registro)
2. `MascotaController`, `PropietarioController`
3. `CitaController`
4. `ServicioController`
5. `HistoriaClinicaController`
6. `InventarioController`
7. `FacturacionController`

**Documentación Swagger en cada endpoint**

**Tiempo estimado**: 4-5 días

---

### **FASE 11: Testing** (Prioridad: ALTA)
- Unit tests para servicios críticos
- Integration tests para endpoints principales
- Tests de seguridad
- Tests de patrones de diseño

**Tiempo estimado**: 3-4 días

---

### **FASE 12: Documentación y Refinamiento** (Prioridad: MEDIA)
- README.md completo
- JavaDoc en clases críticas
- Diagramas actualizados
- Colección de Postman
- Scripts de base de datos

**Tiempo estimado**: 2 días

---

## 🎯 Prioridades de Implementación

### 🔴 **Prioridad CRÍTICA** (Debe implementarse primero)
1. Fase 0: Configuración
2. Fase 1: Entidades
3. Fase 2: Repositorios
4. Fase 6: Servicios básicos
5. Fase 9: Seguridad
6. Fase 10: Controllers

### 🟠 **Prioridad ALTA** (Implementar después de críticas)
1. Fase 3: DTOs
2. Fase 4: Excepciones
3. Fase 7: Patrones Estructurales (especialmente Facade)
4. Fase 8: Patrones de Comportamiento
5. Fase 11: Testing

### 🟡 **Prioridad MEDIA** (Implementar al final)
1. Fase 5: Patrones Creacionales (algunos)
2. Fase 12: Documentación

---

## 📊 Estimación Total

| Fase | Días Estimados |
|------|----------------|
| Fase 0 | 1 día |
| Fase 1 | 4 días |
| Fase 2 | 2 días |
| Fase 3 | 3 días |
| Fase 4 | 1 día |
| Fase 5 | 3 días |
| Fase 6 | 6 días |
| Fase 7 | 4 días |
| Fase 8 | 5 días |
| Fase 9 | 3 días |
| Fase 10 | 5 días |
| Fase 11 | 4 días |
| Fase 12 | 2 días |
| **TOTAL** | **43 días** ≈ **8-9 semanas** |

---

## ✅ Checklist de Calidad

Antes de considerar completa cada fase:

- [ ] Código sigue principios SOLID
- [ ] No hay antipatrones implementados
- [ ] Todas las clases tienen JavaDoc
- [ ] Tests unitarios pasan al 100%
- [ ] No hay magic numbers
- [ ] Validaciones implementadas
- [ ] Excepciones manejadas correctamente
- [ ] Logging implementado
- [ ] Documentación Swagger completa
- [ ] Seguridad verificada (sin vulnerabilidades OWASP Top 10)

---

## 🚀 Próximos Pasos

1. **Revisar y aprobar este plan**
2. **Comenzar con Fase 0**: Configuración inicial
3. **Implementar fase por fase** siguiendo el orden establecido
4. **Testing continuo** después de cada fase
5. **Refactorización** cuando sea necesario

---

## 📝 Notas Finales

- Cada patrón está **justificado** por una necesidad real del sistema
- La arquitectura es **escalable** y **mantenible**
- Se evitan todos los **antipatrones** mencionados
- El código será **production-ready**
- Los 19 patrones están **integrados orgánicamente** en el flujo del sistema

