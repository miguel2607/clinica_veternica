# Configuración de Notificaciones por Email

## Resumen

Se ha implementado un sistema completo de notificaciones por correo electrónico que envía emails reales en los siguientes casos:

1. **Alertas de Stock Bajo**: Cuando el stock de insumos está bajo, crítico o agotado, se envían correos a todos los administradores y auxiliares activos.
2. **Notificaciones de Citas**: Cuando se crea una nueva cita, se envía un correo al propietario de la mascota con los detalles de la cita.

## Componentes Implementados

### 1. EmailService
Servicio que maneja el envío real de correos usando Spring Mail.

**Ubicación**: `src/main/java/com/veterinaria/clinica_veternica/service/EmailService.java`

**Métodos principales**:
- `enviarEmailSimple()`: Envía correos de texto plano
- `enviarEmailHtml()`: Envía correos HTML
- `enviarEmailMultiple()`: Envía correos a múltiples destinatarios

### 2. EmailNotificacionFactory (Modificado)
Ahora usa el servicio real de email en lugar de simular el envío.

**Ubicación**: `src/main/java/com/veterinaria/clinica_veternica/patterns/creational/abstractfactory/EmailNotificacionFactory.java`

### 3. InventarioObserver (Modificado)
Envía correos a administradores y auxiliares cuando detecta problemas de stock.

**Ubicación**: `src/main/java/com/veterinaria/clinica_veternica/patterns/behavioral/observer/InventarioObserver.java`

**Alertas enviadas**:
- Stock bajo (cuando está por debajo del mínimo)
- Stock crítico (cuando está muy bajo)
- Stock agotado (cuando llega a cero)

### 4. NotificacionObserver (Modificado)
Envía correos al propietario cuando se crea una nueva cita.

**Ubicación**: `src/main/java/com/veterinaria/clinica_veternica/patterns/behavioral/observer/NotificacionObserver.java`

## Configuración

### Paso 1: Configurar Credenciales de Email

Edita el archivo `src/main/resources/application-dev.properties` y configura las siguientes propiedades:

#### Para Gmail:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu-email@gmail.com
spring.mail.password=tu-contraseña-de-aplicacion
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

**IMPORTANTE para Gmail**: 
- No uses tu contraseña normal de Gmail
- Debes crear una "Contraseña de aplicación" en tu cuenta de Google
- Ve a: Google Account → Seguridad → Verificación en 2 pasos → Contraseñas de aplicaciones

#### Para Outlook:

```properties
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
spring.mail.username=tu-email@outlook.com
spring.mail.password=tu-contraseña
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

#### Para otros proveedores SMTP:

Consulta la documentación de tu proveedor para obtener:
- Host SMTP
- Puerto (generalmente 587 para TLS o 465 para SSL)
- Si requiere autenticación
- Si requiere STARTTLS

### Paso 2: Configurar Nombre del Remitente (Opcional)

```properties
app.email.from-name=Clínica Veterinaria
```

### Paso 3: Verificar que los Usuarios Tengan Email

Asegúrate de que:
- Los administradores y auxiliares tengan un email válido en su perfil
- Los propietarios tengan un email válido en su perfil
- Los usuarios estén activos (`estado = true`)

## Funcionamiento

### Alertas de Stock

El sistema monitorea el inventario cada hora (configurable) y envía correos automáticamente cuando:

1. **Stock Bajo**: El stock está por debajo del mínimo pero mayor a la mitad del mínimo
2. **Stock Crítico**: El stock está por debajo de la mitad del mínimo pero mayor a cero
3. **Stock Agotado**: El stock llega a cero

Los correos se envían a:
- Todos los usuarios con rol `ADMIN` que estén activos
- Todos los usuarios con rol `AUXILIAR` que estén activos

### Notificaciones de Citas

Cuando se crea una nueva cita, automáticamente se envía un correo al propietario de la mascota con:

- Nombre de la mascota
- Nombre del veterinario
- Servicio a realizar
- Fecha y hora de la cita
- Motivo de la cita

El correo se envía al email registrado del propietario en el sistema.

## Pruebas

### Probar Alertas de Stock

1. Crea un insumo con stock mínimo (ej: stock mínimo = 10)
2. Reduce el stock a un valor por debajo del mínimo
3. Espera a que se ejecute el monitoreo (cada hora) o ejecuta manualmente el método `monitorearStock()`
4. Verifica que los administradores y auxiliares reciban el correo

### Probar Notificaciones de Citas

1. Crea una nueva cita para una mascota cuyo propietario tenga email
2. Verifica que el propietario reciba el correo con los detalles de la cita

## Solución de Problemas

### El correo no se envía

1. **Verifica las credenciales**: Asegúrate de que el usuario y contraseña sean correctos
2. **Verifica el host y puerto**: Confirma que sean correctos para tu proveedor
3. **Revisa los logs**: Busca errores en la consola de la aplicación
4. **Para Gmail**: Asegúrate de usar una "Contraseña de aplicación", no tu contraseña normal
5. **Firewall/Antivirus**: Algunos firewalls pueden bloquear conexiones SMTP

### El correo llega a spam

- Configura SPF, DKIM y DMARC en tu dominio (para producción)
- Usa un servicio de email profesional (SendGrid, AWS SES, etc.) en producción
- Evita palabras que activan filtros de spam en el asunto

### Error: "Authentication failed"

- Verifica que el usuario y contraseña sean correctos
- Para Gmail, asegúrate de usar una "Contraseña de aplicación"
- Verifica que la verificación en 2 pasos esté habilitada (para Gmail)

## Producción

Para producción, se recomienda:

1. **Usar un servicio de email profesional**:
   - SendGrid
   - AWS SES
   - Mailgun
   - Postmark

2. **Configurar variables de entorno** en lugar de propiedades en archivos:
   ```properties
   spring.mail.username=${MAIL_USERNAME}
   spring.mail.password=${MAIL_PASSWORD}
   ```

3. **Configurar SPF, DKIM y DMARC** para mejorar la entregabilidad

4. **Usar templates HTML** para correos más profesionales

5. **Implementar cola de correos** para manejar grandes volúmenes

## Dependencias Agregadas

- `spring-boot-starter-mail`: Dependencia de Spring Mail agregada al `pom.xml`

## Archivos Modificados

1. `pom.xml` - Agregada dependencia de Spring Mail
2. `EmailService.java` - Nuevo servicio de email
3. `EmailNotificacionFactory.java` - Modificado para usar EmailService
4. `InventarioObserver.java` - Modificado para enviar correos a administradores
5. `NotificacionObserver.java` - Modificado para enviar correos al crear citas
6. `application.properties` - Agregada configuración de email
7. `application-dev.properties` - Agregada configuración de email para desarrollo

