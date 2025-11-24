# ✅ Configuración de Gmail - PASOS RÁPIDOS

## 🔧 Estado Actual

Tu configuración ya está lista:
- ✅ Email: `miguelitorodriguezaranzazu@gmail.com`
- ✅ App Password: `asjkgbztsxkosydo` (sin espacios)
- ✅ Servidor SMTP: `smtp.gmail.com:587`

## 📋 Pasos para Verificar que Funciona

### 1. Reiniciar la Aplicación Spring Boot
```bash
# Detén la aplicación (Ctrl+C)
# Luego iníciala nuevamente
mvn spring-boot:run
```

### 2. Probar el Envío de Emails

#### Opción A: Crear un Nuevo Usuario
1. Ve al panel de administración
2. Click en "Usuarios" → "Nuevo Usuario"
3. Crea un usuario con un email válido
4. **Debería llegar un email de bienvenida**

#### Opción B: Crear un Nuevo Propietario
1. Ve al panel de administración
2. Click en "Propietarios" → "Nuevo Propietario"
3. Crea un propietario con un email válido
4. **Debería llegar un email de bienvenida**

#### Opción C: Crear una Nueva Cita
1. Ve al panel de administración
2. Click en "Citas" → "Nueva Cita"
3. Crea una cita para una mascota
4. **Debería llegar un email al propietario**

#### Opción D: Enviar Notificación Manual
1. Ve al panel de administración
2. Click en "Notificaciones" → "Enviar Notificación"
3. Selecciona un usuario y envía una notificación
4. **Debería llegar el email**

### 3. Verificar los Logs

Revisa la consola de Spring Boot. Deberías ver mensajes como:
```
✅ CORRECTO:
INFO - Correo enviado exitosamente a: usuario@email.com - Asunto: ...

❌ ERROR (si hay problemas):
ERROR - Error de autenticación al enviar correo...
ERROR - Error al enviar correo...
```

## 🐛 Si NO Funciona

### Error: "Authentication failed"
**Solución:**
1. Ve a: https://myaccount.google.com/apppasswords
2. Verifica que la App Password esté activa
3. Si no está, genera una nueva:
   - App: "Correo"
   - Dispositivo: "Clínica Veterinaria"
   - Copia la contraseña SIN ESPACIOS
4. Actualiza `application-dev.properties` con la nueva contraseña
5. Reinicia la aplicación

### Error: "Could not connect to SMTP host"
**Solución:**
- Verifica tu conexión a internet
- Verifica que el puerto 587 no esté bloqueado por firewall
- Prueba desde otra red si es posible

### Error: "Username and Password not accepted"
**Solución:**
- Verifica que el email sea correcto: `miguelitorodriguezaranzazu@gmail.com`
- Verifica que la App Password no tenga espacios
- Genera una nueva App Password si es necesario

## 📧 Notificaciones Automáticas Implementadas

El sistema envía emails automáticamente cuando:

1. ✅ **Se crea un nuevo Usuario** → Email de bienvenida
2. ✅ **Se crea un nuevo Propietario** → Email de bienvenida
3. ✅ **Se registra una nueva Mascota** → Email al propietario
4. ✅ **Se crea una nueva Cita** → Email al propietario con detalles
5. ✅ **Stock bajo de insumos** → Email a administradores y auxiliares
6. ✅ **Notificación manual** → Email según se configure

## 🔍 Verificar en Gmail

1. Abre tu Gmail: https://mail.google.com
2. Revisa la bandeja de entrada
3. Si no ves los emails, revisa:
   - Carpeta de "Spam" o "Correo no deseado"
   - Carpeta de "Promociones" (si usas pestañas)
   - Busca por "Clínica Veterinaria" en el buscador

## ⚙️ Configuración Actual

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=miguelitorodriguezaranzazu@gmail.com
spring.mail.password=asjkgbztsxkosydo
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

## 🎯 Próximos Pasos

1. **Reinicia la aplicación Spring Boot**
2. **Prueba creando un usuario o propietario**
3. **Revisa los logs para ver si hay errores**
4. **Revisa tu Gmail (incluyendo spam)**

Si después de esto no funciona, revisa los logs de la aplicación para ver el error específico.

