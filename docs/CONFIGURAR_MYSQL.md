# Configurar MySQL para Clínica Veterinaria

## Error Actual

```
Access denied for user 'root'@'localhost' (using password: YES)
```

Este error significa que MySQL está rechazando la conexión. Hay varias posibles causas:

---

## Solución 1: Verificar si MySQL está corriendo

### Windows:

```powershell
# Abrir servicios
services.msc
```

Buscar "MySQL" en la lista y verificar que esté "Iniciado". Si no está corriendo, hacer clic derecho > Iniciar.

**O desde PowerShell (como administrador):**
```powershell
# Ver estado
net start | findstr MySQL

# Iniciar MySQL
net start MySQL80
```

### Probar conexión:
```bash
mysql -u root -p
```

---

## Solución 2: La contraseña de MySQL no es "root"

La aplicación está configurada para usar:
- Usuario: `root`
- Contraseña: `root`

### Opción A: Cambiar la contraseña de MySQL a "root"

```sql
-- Conectar a MySQL
mysql -u root -p
-- Ingresar tu contraseña actual

-- Cambiar contraseña a "root"
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
FLUSH PRIVILEGES;
```

### Opción B: Cambiar la configuración de la aplicación

Editar el archivo: `src/main/resources/application.properties`

**Línea 17:**
```properties
spring.datasource.password=root
```

Cambiar por:
```properties
spring.datasource.password=TU_CONTRASEÑA_ACTUAL
```

---

## Solución 3: El usuario root no tiene permisos

```sql
-- Conectar a MySQL
mysql -u root -p

-- Dar todos los permisos al usuario root
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION;
FLUSH PRIVILEGES;

-- Verificar permisos
SHOW GRANTS FOR 'root'@'localhost';
```

---

## Solución 4: MySQL no permite autenticación con contraseña

En MySQL 8.0+, a veces el usuario root usa `auth_socket` en lugar de contraseña.

```sql
-- Conectar a MySQL
mysql -u root -p

-- Cambiar a autenticación por contraseña
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
FLUSH PRIVILEGES;
```

---

## Solución 5: Crear un nuevo usuario para la aplicación

Si no quieres modificar el usuario root, puedes crear un usuario específico:

```sql
-- Conectar a MySQL como root
mysql -u root -p

-- Crear usuario para la aplicación
CREATE USER 'clinica_user'@'localhost' IDENTIFIED BY 'clinica_pass';

-- Dar permisos sobre la base de datos
GRANT ALL PRIVILEGES ON clinica_veterinaria.* TO 'clinica_user'@'localhost';
FLUSH PRIVILEGES;
```

**Luego actualizar `application.properties`:**
```properties
spring.datasource.username=clinica_user
spring.datasource.password=clinica_pass
```

---

## Verificar Configuración Actual

### Ver usuario y contraseña configurados:

```bash
# Linux/Mac
cat src/main/resources/application.properties | grep datasource

# Windows PowerShell
Select-String -Path "src/main/resources/application.properties" -Pattern "datasource"
```

Deberías ver:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/clinica_veterinaria?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
```

---

## Probar Conexión Manual

Antes de iniciar la aplicación, prueba conectarte manualmente a MySQL:

```bash
# Probar conexión
mysql -h localhost -P 3306 -u root -p
# Ingresar contraseña: root

# Si conecta exitosamente, verás:
# mysql>

# Probar crear la base de datos manualmente
CREATE DATABASE IF NOT EXISTS clinica_veterinaria;
USE clinica_veterinaria;
SHOW TABLES;
```

---

## Después de Configurar MySQL

Una vez que MySQL esté configurado correctamente:

1. **Limpiar el proyecto:**
```bash
mvn clean
```

2. **Compilar:**
```bash
mvn compile
```

3. **Iniciar la aplicación:**
```bash
# Con JAVA_HOME correcto
export JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-21.0.3.9-hotspot"
mvn spring-boot:run
```

4. **Verificar que inició:**
Deberías ver en la consola:
```
Started ClinicaVeternicaApplication in X seconds
```

5. **Verificar tablas creadas:**
```sql
USE clinica_veterinaria;
SHOW TABLES;
-- Deberías ver 46 tablas
```

---

## Configuración Recomendada para Desarrollo

Si quieres evitar problemas de contraseña, puedes usar una contraseña vacía (solo para desarrollo local):

```sql
ALTER USER 'root'@'localhost' IDENTIFIED BY '';
FLUSH PRIVILEGES;
```

**Actualizar application.properties:**
```properties
spring.datasource.password=
```

**⚠️ IMPORTANTE:** Esto solo es seguro para desarrollo local, NUNCA en producción.

---

## Troubleshooting Adicional

### Error: "Communications link failure"
- MySQL no está corriendo
- Puerto 3306 está bloqueado por firewall
- Verificar: `netstat -an | findstr 3306`

### Error: "Unknown database 'clinica_veterinaria'"
- La base de datos debería crearse automáticamente por el parámetro `createDatabaseIfNotExist=true`
- Si no se crea, crearla manualmente: `CREATE DATABASE clinica_veterinaria;`

### Error: "Public Key Retrieval is not allowed"
- Ya está configurado en la URL con `allowPublicKeyRetrieval=true`
- Si persiste, verificar permisos del usuario

---

## Resumen Quick Fix

**Solución rápida más común:**

```bash
# 1. Conectar a MySQL
mysql -u root -p
# (ingresar tu contraseña actual)

# 2. Ejecutar estos comandos
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
FLUSH PRIVILEGES;
CREATE DATABASE IF NOT EXISTS clinica_veterinaria;
EXIT;

# 3. Probar conexión
mysql -u root -proot
# Debería conectar sin pedir contraseña

# 4. Iniciar aplicación
export JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-21.0.3.9-hotspot"
cd C:\Users\MIGUEL\IdeaProjects\clinica_veternica
mvn spring-boot:run
```

---

Si después de seguir estos pasos sigue sin funcionar, compartir el mensaje de error específico para ayudar mejor.
