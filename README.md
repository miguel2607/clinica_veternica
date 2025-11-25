# 🏥 Sistema de Gestión Veterinaria - Clínica Veterinaria

Sistema completo de gestión para clínicas veterinarias desarrollado con **Spring Boot 3.5**, **Java 21** y **MySQL**, implementando **17 patrones de diseño** de forma coherente y justificada.

## 📋 Características Principales

- ✅ **Gestión completa de pacientes** (mascotas y propietarios)
- ✅ **Sistema de citas** con validaciones y notificaciones
- ✅ **Historias clínicas** con respaldo y restauración (Memento)
- ✅ **Control de inventario** con auditoría (Proxy)
- ✅ **Sistema de usuarios** con roles y permisos
- ✅ **Seguridad JWT** implementada
- ✅ **API REST** documentada con Swagger/OpenAPI
- ✅ **17 Patrones de Diseño** implementados

## 🎨 Patrones de Diseño Implementados

### Creacionales (4)
1. **Singleton** - AuditLogger, ConfigurationManager
2. **Factory Method** - ServicioFactory y sus implementaciones
3. **Abstract Factory** - NotificacionFactory
4. **Builder** - HistoriaClinicaBuilder, CitaBuilder, ReporteBuilder

### Estructurales (5)
5. **Adapter** - PaymentGatewayAdapters (PayPal, Stripe) - No usado activamente
6. **Bridge** - Sistema de reportes (PDF, Excel, JSON)
7. **Decorator** - ServicioDecorators (Urgencia, Domicilio, Seguro)
8. **Facade** - ClinicaFacade (punto de acceso unificado para frontend)
9. **Proxy** - InventarioProxy, HistoriaClinicaProxy, CachedServiceProxy

### Comportamiento (8)
10. **Chain of Responsibility** - Validaciones de citas
11. **Command** - Comandos de operaciones (CrearCita, CancelarCita, ActualizarStock)
12. **Mediator** - CitaMediator
13. **Memento** - Respaldo de historias clínicas
14. **Observer** - Sistema de notificaciones de citas e inventario
15. **State** - Estados de citas
16. **Template Method** - Flujos de atención veterinaria

## 🛠️ Tecnologías

- **Java 21**
- **Spring Boot 3.5.7**
- **Spring Data JPA / Hibernate**
- **MySQL 8.0**
- **Spring Security + JWT**
- **MapStruct** (mapeo DTOs)
- **Lombok**
- **OpenAPI/Swagger**
- **Maven**
- **JUnit 5**

## 📦 Estructura del Proyecto

```
clinica_veternica/
├── src/main/java/com/veterinaria/clinica_veternica/
│   ├── domain/              # Entidades JPA
│   ├── dto/                  # DTOs (Request/Response)
│   ├── repository/           # Repositorios Spring Data
│   ├── service/              # Lógica de negocio
│   ├── controller/           # Controllers REST
│   ├── mapper/               # MapStruct Mappers
│   ├── exception/            # Excepciones personalizadas
│   ├── security/             # Configuración de seguridad
│   ├── config/               # Configuraciones
│   └── patterns/             # Patrones de diseño
│       ├── creational/
│       ├── structural/
│       └── behavioral/
└── src/test/                 # Tests
```

## 🚀 Inicio Rápido

### Prerrequisitos

- Java 21
- Maven 3.8+
- MySQL 8.0+

### Configuración

1. **Clonar el repositorio**
```bash
git clone <repository-url>
cd clinica_veternica
```

2. **Configurar base de datos**

Editar `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/clinica_veterinaria
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
```

3. **Compilar el proyecto**
```bash
mvn clean install
```

4. **Ejecutar la aplicación**
```bash
mvn spring-boot:run
```

5. **Acceder a Swagger UI**
```
http://localhost:8080/swagger-ui.html
```

   Para más información sobre cómo usar Swagger UI, consulta la [Guía de Swagger](docs/GUIA_SWAGGER.md)

## 📚 API Endpoints Principales

### Autenticación
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/register` - Registro de usuario

### Propietarios
- `GET /api/propietarios` - Listar todos
- `POST /api/propietarios` - Crear propietario
- `GET /api/propietarios/{id}` - Buscar por ID

### Mascotas
- `GET /api/mascotas` - Listar todas
- `POST /api/mascotas` - Crear mascota
- `GET /api/mascotas/{id}` - Buscar por ID

### Citas
- `GET /api/citas` - Listar todas
- `POST /api/citas` - Crear cita (usa Builder, Chain, Mediator)
- `PUT /api/citas/{id}/confirmar` - Confirmar cita
- `PUT /api/citas/{id}/cancelar` - Cancelar cita (Command pattern)

### Servicios
- `GET /api/servicios` - Listar todos
- `POST /api/servicios` - Crear servicio
- `POST /api/servicios/factory` - Crear con Factory pattern

### Inventario
- `GET /api/inventario` - Listar inventario
- `GET /api/inventario/stock-bajo` - Listar con stock bajo
- `GET /api/inventario/agotados` - Listar agotados
- `GET /api/inventario/insumos` - Gestión de insumos

### Facade (Punto de acceso unificado para frontend)
- `GET /api/facade/dashboard` - Dashboard completo (citas, stock, notificaciones)
- `GET /api/facade/inventario/resumen` - Resumen de inventario con alertas
- `GET /api/facade/mascotas/{id}/completa` - Información completa de mascota
- `GET /api/facade/citas/calendario` - Calendario de citas por fecha
- `POST /api/facade/citas/crear-con-notificacion` - Crear cita con notificación
- `POST /api/facade/citas/{id}/atencion-completa` - Proceso completo de atención
- `POST /api/facade/mascotas/registro-completo` - Registro completo (propietario + mascota + historia clínica)

## 🧪 Testing

Ejecutar todos los tests:
```bash
mvn test
```

Ejecutar tests específicos:
```bash
mvn test -Dtest=PropietarioServiceTest
```

## 📖 Documentación

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

## 🔒 Seguridad

- Autenticación basada en JWT
- Control de acceso basado en roles
- Endpoints protegidos con Spring Security
- Auditoría de operaciones críticas (Singleton)

## 🎯 Principios Aplicados

- ✅ **SOLID** - Principios aplicados en toda la arquitectura
- ✅ **DRY** - Sin duplicación de código
- ✅ **Clean Code** - Código limpio y mantenible
- ✅ **Best Practices** - Mejores prácticas de Spring Boot
- ✅ **Design Patterns** - 17 patrones implementados justificadamente

## 📝 Notas de Implementación

- Todos los patrones están **justificados** por necesidades reales del sistema
- La arquitectura es **escalable** y **mantenible**
- Se evitan **antipatrones** comunes
- El código está listo para uso

## 👥 Autores

Clínica Veterinaria Team

## 📄 Licencia

Este proyecto es de uso interno.

---

**Versión**: 1.0  
**Última actualización**: 2025-11-06

