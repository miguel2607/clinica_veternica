## Especificaciones Técnicas Obligatorias

### Stack Tecnológico
- *Framework*: Spring Boot 3.5
- *Lenguaje*: Java 21
- *Base de datos*: mysql
- *ORM*: Spring Data JPA / Hibernate
- *Seguridad*: Spring Security con JWT
- *Documentación API*: OpenAPI/Swagger
- *Build*: Maven
- *Testing*: JUnit 5


### Arquitectura
Implementar una *arquitectura por capas bien definida*:


## Antipatrones a EVITAR

❌ *God Object*: No crear clases que hagan todo
❌ *Spaghetti Code*: Mantener código organizado y limpio
❌ *Magic Numbers*: Usar constantes con nombres descriptivos
❌ *Copy-Paste Programming*: Reutilizar código mediante herencia/composición
❌ *Hard Coding*: Usar archivos de configuración (application.properties)
❌ *No manejo de excepciones*: Implementar @ControllerAdvice y excepciones personalizadas
❌ *Dependencias circulares*: Mantener arquitectura en capas clara
❌ *Exponer entidades en endpoints*: Siempre usar DTOs
❌ *No validar datos de entrada*: Usar Bean Validation
❌ *Contraseñas en texto plano*: Usar BCrypt para passwords



### Performance
- Paginación en listados grandes
- Queries optimizadas con índices
- Lazy loading donde sea apropiado
- Caché para consultas frecuentes (Spring Cache)


### Mantenibilidad
- Código limpio y bien documentado
- Nombres descriptivos
- Principios SOLID
- Separación de responsabilidades
- Testing unitario y de integración



## Instrucciones para Claude Code

Por favor, desarrolla el backend completo siguiendo estas especificaciones:

1. *Inicializa el proyecto* con Spring Initializr incluyendo las dependencias necesarias

2. *Crea la estructura de carpetas* que sea mas conveniente segun la complejidad del proyecto para que este cumpla con
Escalabilidad
Mantenibilidad
Seguridad
Rendimiento
Reutilización de código
Documentación
Pruebas automatizadas: unitarias y de integracion
Diseño modular

3. *Implementa las entidades JPA* con todas las relaciones apropiadas

4. *Desarrolla los repositorios* usando Spring Data JPA

5. *Implementa los patrones de diseño* mencionados de forma explícita y bien documentada

6. *Crea los servicios* con toda la lógica de negocio, incluyendo:


7. *Desarrolla los controllers* con validaciones y documentación Swagger

8. *Configura Spring Security* con JWT y roles

9. *Implementa el sistema de notificaciones* que:
   - Detecte stock bajo automáticamente
   - Notifique a propietarios sobre citas
   - Use diferentes canales (Email, SMS, WhatsApp)

10. *Crea excepciones personalizadas* y un @ControllerAdvice global

11. *Configura la base de datos* con mysql

12. *Añade tests unitarios* para servicios críticos

13. *Documenta el código* con JavaDoc donde sea apropiado

14. *Crea un README.md* con instrucciones de instalación y uso

15. *Asegura que NO se usen antipatrones* mencionados

## Notas Importantes

- Prioriza la *claridad y mantenibilidad* del código
- Cada patrón de diseño debe tener *un propósito claro* y estar bien implementado
- La *seguridad y control de acceso* son fundamentales
- Usa *Spring Boot best practices*
- Implementa *logging apropiado* para auditoría
- El código debe estar *listo para producción*

## Entregables Esperados

1. Proyecto Spring Boot completo y funcional
2. Base de datos con schema DDL
3. Collection de Postman/Insomnia para probar endpoints
4. README con documentación
5. Tests unitarios básicos
6. Comentarios explicando la implementación de cada patrón