# Script de prueba de endpoints para Clínica Veterinaria
# Prueba los nuevos controladores: InsumoController, ProveedorController, TipoInsumoController

$baseUrl = "http://localhost:8080/api"
$token = ""

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "PRUEBA DE ENDPOINTS - CLINICA VETERINARIA" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# Función para hacer login y obtener token
function Get-AuthToken {
    Write-Host "[1] Obteniendo token de autenticación..." -ForegroundColor Yellow
    
    $loginBody = @{
        username = "admin"
        password = "admin123"
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body $loginBody -ContentType "application/json" -ErrorAction Stop
        $script:token = $response.token
        Write-Host "[OK] Token obtenido exitosamente" -ForegroundColor Green
        Write-Host "  Token: $($token.Substring(0, [Math]::Min(50, $token.Length)))..." -ForegroundColor Gray
        return $true
    } catch {
        Write-Host "[ERROR] Error al obtener token: $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response.StatusCode -eq 401) {
            Write-Host "  Intentando registrar usuario admin..." -ForegroundColor Yellow
            # Intentar registrar usuario
            $registerBody = @{
                username = "admin"
                email = "admin@clinica.com"
                password = "admin123"
                rol = "ADMINISTRADOR"
            } | ConvertTo-Json
            
            try {
                Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method POST -Body $registerBody -ContentType "application/json" -ErrorAction Stop
                Write-Host "[OK] Usuario registrado, intentando login nuevamente..." -ForegroundColor Green
                $response = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body $loginBody -ContentType "application/json" -ErrorAction Stop
                $script:token = $response.token
                Write-Host "[OK] Token obtenido exitosamente" -ForegroundColor Green
                return $true
            } catch {
                Write-Host "[ERROR] Error al registrar usuario: $($_.Exception.Message)" -ForegroundColor Red
                return $false
            }
        }
        return $false
    }
}

# Función para hacer request con autenticación
function Invoke-AuthenticatedRequest {
    param(
        [string]$Method,
        [string]$Uri,
        [object]$Body = $null,
        [string]$Description
    )
    
    Write-Host "[$Description]" -ForegroundColor Yellow
    Write-Host "  $Method $Uri" -ForegroundColor Gray
    
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    try {
        if ($Body) {
            $bodyJson = $Body | ConvertTo-Json -Depth 10
            $response = Invoke-RestMethod -Uri $Uri -Method $Method -Headers $headers -Body $bodyJson -ErrorAction Stop
        } else {
            $response = Invoke-RestMethod -Uri $Uri -Method $Method -Headers $headers -ErrorAction Stop
        }
        Write-Host "  [OK] Exito" -ForegroundColor Green
        if ($response) {
            Write-Host "  Respuesta: $($response | ConvertTo-Json -Compress -Depth 2)" -ForegroundColor Gray
        }
        return $response
    } catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        Write-Host "  [ERROR] Error $statusCode : $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $responseBody = $reader.ReadToEnd()
            Write-Host "  Detalles: $responseBody" -ForegroundColor Red
        }
        return $null
    }
}

# Verificar si la aplicación está corriendo
Write-Host "[0] Verificando si la aplicación está corriendo..." -ForegroundColor Yellow
try {
    $healthCheck = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -Method GET -TimeoutSec 2 -ErrorAction Stop
    Write-Host "[OK] Aplicacion esta corriendo" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] La aplicacion NO esta corriendo en http://localhost:8080" -ForegroundColor Red
    Write-Host "  Por favor, inicia la aplicación primero con: mvn spring-boot:run" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# Obtener token
if (-not (Get-AuthToken)) {
    Write-Host "No se pudo obtener el token. Abortando pruebas." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "PRUEBAS DE TIPO DE INSUMO" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Crear Tipo de Insumo
$tipoInsumoBody = @{
    nombre = "Medicamento"
    descripcion = "Medicamentos veterinarios"
    categoria = "MEDICAMENTO"
    activo = $true
}
$tipoInsumo = Invoke-AuthenticatedRequest -Method "POST" -Uri "$baseUrl/inventario/tipos-insumo" -Body $tipoInsumoBody -Description "Crear Tipo de Insumo"
$tipoInsumoId = $tipoInsumo.idTipoInsumo

Write-Host ""

# 2. Listar Tipos de Insumo
Invoke-AuthenticatedRequest -Method "GET" -Uri "$baseUrl/inventario/tipos-insumo" -Description "Listar Tipos de Insumo"

Write-Host ""

# 3. Buscar Tipo de Insumo por ID
Invoke-AuthenticatedRequest -Method "GET" -Uri "$baseUrl/inventario/tipos-insumo/$tipoInsumoId" -Description "Buscar Tipo de Insumo por ID"

Write-Host ""

# 4. Actualizar Tipo de Insumo
$tipoInsumoUpdateBody = @{
    nombre = "Medicamento"
    descripcion = "Medicamentos veterinarios - Actualizado"
    categoria = "MEDICAMENTO"
    activo = $true
}
Invoke-AuthenticatedRequest -Method "PUT" -Uri "$baseUrl/inventario/tipos-insumo/$tipoInsumoId" -Body $tipoInsumoUpdateBody -Description "Actualizar Tipo de Insumo"

Write-Host ""

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "PRUEBAS DE PROVEEDOR" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# 5. Crear Proveedor
$proveedorBody = @{
    nombreEmpresa = "VetSupply S.A."
    documento = "12345678901"
    tipoDocumento = "RUC"
    nombreContacto = "Juan Pérez"
    telefono = "+573001234567"
    email = "contacto@vetsupply.com"
    direccion = "Calle 123 #45-67"
    ciudad = "Bogotá"
    pais = "Colombia"
    activo = $true
}
$proveedor = Invoke-AuthenticatedRequest -Method "POST" -Uri "$baseUrl/inventario/proveedores" -Body $proveedorBody -Description "Crear Proveedor"
$proveedorId = $proveedor.idProveedor

Write-Host ""

# 6. Listar Proveedores
Invoke-AuthenticatedRequest -Method "GET" -Uri "$baseUrl/inventario/proveedores" -Description "Listar Proveedores"

Write-Host ""

# 7. Buscar Proveedor por ID
Invoke-AuthenticatedRequest -Method "GET" -Uri "$baseUrl/inventario/proveedores/$proveedorId" -Description "Buscar Proveedor por ID"

Write-Host ""

# 8. Buscar Proveedor por Documento
Invoke-AuthenticatedRequest -Method "GET" -Uri "$baseUrl/inventario/proveedores/documento/12345678901" -Description "Buscar Proveedor por Documento"

Write-Host ""

# 9. Actualizar Proveedor
$proveedorUpdateBody = @{
    nombreEmpresa = "VetSupply S.A. - Actualizado"
    documento = "12345678901"
    tipoDocumento = "RUC"
    nombreContacto = "Juan Pérez"
    telefono = "+573001234567"
    email = "contacto@vetsupply.com"
    direccion = "Calle 123 #45-67"
    ciudad = "Bogotá"
    pais = "Colombia"
    activo = $true
}
Invoke-AuthenticatedRequest -Method "PUT" -Uri "$baseUrl/inventario/proveedores/$proveedorId" -Body $proveedorUpdateBody -Description "Actualizar Proveedor"

Write-Host ""

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "PRUEBAS DE INSUMO" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# 10. Crear Insumo
$insumoBody = @{
    codigo = "INS-001"
    nombre = "Amoxicilina 500mg"
    descripcion = "Antibiótico de amplio espectro"
    idTipoInsumo = $tipoInsumoId
    idProveedor = $proveedorId
    unidadMedida = "Tableta"
    cantidadStock = 100
    stockMinimo = 20
    stockMaximo = 500
    precioCompra = 5000.00
    precioVenta = 8000.00
    estado = "DISPONIBLE"
    activo = $true
}
$insumo = Invoke-AuthenticatedRequest -Method "POST" -Uri "$baseUrl/inventario/insumos" -Body $insumoBody -Description "Crear Insumo"
$insumoId = $insumo.idInsumo

Write-Host ""

# 11. Listar Insumos
Invoke-AuthenticatedRequest -Method "GET" -Uri "$baseUrl/inventario/insumos" -Description "Listar Insumos"

Write-Host ""

# 12. Buscar Insumo por ID
Invoke-AuthenticatedRequest -Method "GET" -Uri "$baseUrl/inventario/insumos/$insumoId" -Description "Buscar Insumo por ID"

Write-Host ""

# 13. Buscar Insumo por Código
Invoke-AuthenticatedRequest -Method "GET" -Uri "$baseUrl/inventario/insumos/codigo/INS-001" -Description "Buscar Insumo por Código"

Write-Host ""

# 14. Listar Insumos con Stock Bajo
Invoke-AuthenticatedRequest -Method "GET" -Uri "$baseUrl/inventario/insumos/stock-bajo" -Description "Listar Insumos con Stock Bajo"

Write-Host ""

# 15. Listar Insumos Agotados
Invoke-AuthenticatedRequest -Method "GET" -Uri "$baseUrl/inventario/insumos/agotados" -Description "Listar Insumos Agotados"

Write-Host ""

# 16. Listar Insumos por Tipo
Invoke-AuthenticatedRequest -Method "GET" -Uri "$baseUrl/inventario/insumos/tipo/$tipoInsumoId" -Description "Listar Insumos por Tipo"

Write-Host ""

# 17. Listar Insumos por Proveedor
Invoke-AuthenticatedRequest -Method "GET" -Uri "$baseUrl/inventario/insumos/proveedor/$proveedorId" -Description "Listar Insumos por Proveedor"

Write-Host ""

# 18. Buscar Insumos por Nombre
Invoke-AuthenticatedRequest -Method "GET" -Uri "$baseUrl/inventario/insumos/buscar?nombre=Amoxicilina" -Description "Buscar Insumos por Nombre"

Write-Host ""

# 19. Actualizar Insumo
$insumoUpdateBody = @{
    codigo = "INS-001"
    nombre = "Amoxicilina 500mg - Actualizado"
    descripcion = "Antibiótico de amplio espectro - Actualizado"
    idTipoInsumo = $tipoInsumoId
    idProveedor = $proveedorId
    unidadMedida = "Tableta"
    cantidadStock = 150
    stockMinimo = 20
    stockMaximo = 500
    precioCompra = 5000.00
    precioVenta = 8000.00
    estado = "DISPONIBLE"
    activo = $true
}
Invoke-AuthenticatedRequest -Method "PUT" -Uri "$baseUrl/inventario/insumos/$insumoId" -Body $insumoUpdateBody -Description "Actualizar Insumo"

Write-Host ""

# 20. Activar Insumo
Invoke-AuthenticatedRequest -Method "PATCH" -Uri "$baseUrl/inventario/insumos/$insumoId/activar" -Description "Activar Insumo"

Write-Host ""

# 21. Desactivar Insumo
Invoke-AuthenticatedRequest -Method "PATCH" -Uri "$baseUrl/inventario/insumos/$insumoId/desactivar" -Description "Desactivar Insumo"

Write-Host ""

# 22. Activar Insumo nuevamente
Invoke-AuthenticatedRequest -Method "PATCH" -Uri "$baseUrl/inventario/insumos/$insumoId/activar" -Description "Activar Insumo nuevamente"

Write-Host ""

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "RESUMEN DE PRUEBAS" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "[OK] Pruebas completadas" -ForegroundColor Green
Write-Host "  - Tipo de Insumo: Creado (ID: $tipoInsumoId)" -ForegroundColor Gray
Write-Host "  - Proveedor: Creado (ID: $proveedorId)" -ForegroundColor Gray
Write-Host "  - Insumo: Creado (ID: $insumoId)" -ForegroundColor Gray
Write-Host ""

