# Configuración de Gmail para Notificaciones

## Problema: Las notificaciones no están llegando al Gmail

Gmail requiere una **"App Password" (Contraseña de aplicación)** en lugar de tu contraseña normal cuando se accede desde aplicaciones de terceros.

## Solución: Obtener una App Password

### Paso 1: Activar Verificación en 2 Pasos
1. Ve a tu cuenta de Google: https://myaccount.google.com/
2. Navega a **Seguridad**
3. Busca **Verificación en 2 pasos** y actívala si no está activada
   - Gmail requiere que la verificación en 2 pasos esté activada para generar App Passwords

### Paso 2: Generar App Password
1. En la misma página de **Seguridad**, busca **Contraseñas de aplicaciones**
2. Si no ves esta opción, busca "App passwords" o "Contraseñas de aplicaciones"
3. Selecciona:
   - **App**: "Correo" o "Mail"
   - **Dispositivo**: "Otro (nombre personalizado)" y escribe "Clínica Veterinaria"
4. Haz clic en **Generar**
5. **Copia la contraseña generada** (16 caracteres sin espacios)
   - Ejemplo: `abcd efgh ijkl mnop` → usar `abcdefghijklmnop`

### Paso 3: Actualizar la Configuración
1. Abre el archivo: `src/main/resources/application-dev.properties`
2. Busca la línea:
   ```properties
   spring.mail.password=1094898817
   ```
3. Reemplázala con tu App Password:
   ```properties
   spring.mail.password=TU_APP_PASSWORD_AQUI
   ```
4. **NO uses tu contraseña normal de Gmail**

### Paso 4: Reiniciar la Aplicación
1. Detén la aplicación Spring Boot
2. Iníciala nuevamente
3. Prueba enviando una notificación

## Verificación

Para verificar que funciona:
1. Crea un nuevo usuario o propietario
2. Verifica que llegue el email de bienvenida
3. Revisa los logs de la aplicación para ver si hay errores

## Errores Comunes

### Error: "Authentication failed"
- **Causa**: Estás usando tu contraseña normal en lugar de App Password
- **Solución**: Genera una App Password y úsala

### Error: "Could not connect to SMTP host"
- **Causa**: Problema de conexión a internet o firewall
- **Solución**: Verifica tu conexión y que el puerto 587 no esté bloqueado

### Error: "Username and Password not accepted"
- **Causa**: App Password incorrecta o expirada
- **Solución**: Genera una nueva App Password

## Notas Importantes

- ⚠️ **NUNCA** compartas tu App Password
- ⚠️ **NUNCA** subas el archivo `application-dev.properties` a un repositorio público
- ✅ Las App Passwords son específicas por aplicación
- ✅ Puedes generar múltiples App Passwords para diferentes aplicaciones
- ✅ Puedes revocar App Passwords en cualquier momento desde tu cuenta de Google

## Configuración Actual

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=marodriguez_141@cue.edu.co
spring.mail.password=TU_APP_PASSWORD_AQUI  # ← Reemplazar con App Password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

## Enlaces Útiles

- [Gestionar tu cuenta de Google](https://myaccount.google.com/)
- [Contraseñas de aplicaciones](https://myaccount.google.com/apppasswords)
- [Verificación en 2 pasos](https://myaccount.google.com/signinoptions/two-step-verification)

