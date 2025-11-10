package com.veterinaria.clinica_veternica.util;

/**
 * Clase de constantes globales del sistema.
 *
 * Centraliza todos los valores constantes utilizados en la aplicación
 * para evitar magic numbers y magic strings, siguiendo las mejores prácticas.
 *
 * ANTIPATRÓN EVITADO: Magic Numbers y Hard Coding
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
public final class Constants {

    // Constructor privado para evitar instanciación
    private Constants() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad y no puede ser instanciada");
    }

    // ===================================================================
    // CONSTANTES DE SEGURIDAD Y JWT
    // ===================================================================

    /**
     * Prefijo del token Bearer en el header Authorization.
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * Nombre del header HTTP donde se envía el token.
     */
    public static final String HEADER_STRING = "Authorization";

    /**
     * Tiempo de expiración del token en milisegundos (por defecto 24 horas).
     */
    public static final long JWT_EXPIRATION_TIME = 86400000L; // 24 horas

    // ===================================================================
    // CONSTANTES DE PAGINACIÓN
    // ===================================================================

    /**
     * Número de página inicial (primera página).
     */
    public static final int DEFAULT_PAGE_NUMBER = 0;

    /**
     * Tamaño de página por defecto.
     */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * Tamaño máximo de página permitido.
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * Campo de ordenamiento por defecto.
     */
    public static final String DEFAULT_SORT_BY = "id";

    /**
     * Dirección de ordenamiento por defecto.
     */
    public static final String DEFAULT_SORT_DIRECTION = "ASC";

    // ===================================================================
    // CONSTANTES DE ROLES
    // ===================================================================

    /**
     * Rol de Administrador.
     */
    public static final String ROLE_ADMIN = "ADMIN";

    /**
     * Rol de Veterinario.
     */
    public static final String ROLE_VETERINARIO = "VETERINARIO";

    /**
     * Rol de Auxiliar Veterinario.
     */
    public static final String ROLE_AUXILIAR = "AUXILIAR";

    /**
     * Rol de Recepcionista.
     */
    public static final String ROLE_RECEPCIONISTA = "RECEPCIONISTA";

    /**
     * Rol de Propietario (dueño de mascota).
     */
    public static final String ROLE_PROPIETARIO = "PROPIETARIO";

    /**
     * Rol de Estudiante en práctica.
     */
    public static final String ROLE_ESTUDIANTE = "ESTUDIANTE";

    // ===================================================================
    // CONSTANTES DE ESTADOS DE CITA
    // ===================================================================

    /**
     * Estado: Cita programada.
     */
    public static final String ESTADO_CITA_PROGRAMADA = "PROGRAMADA";

    /**
     * Estado: Cita confirmada por el propietario.
     */
    public static final String ESTADO_CITA_CONFIRMADA = "CONFIRMADA";

    /**
     * Estado: Cita atendida exitosamente.
     */
    public static final String ESTADO_CITA_ATENDIDA = "ATENDIDA";

    /**
     * Estado: Cita cancelada.
     */
    public static final String ESTADO_CITA_CANCELADA = "CANCELADA";

    // ===================================================================
    // CONSTANTES DE INVENTARIO
    // ===================================================================

    /**
     * Stock mínimo por defecto para alertas.
     */
    public static final int STOCK_MINIMO_DEFAULT = 10;

    /**
     * Días antes del vencimiento para generar alerta.
     */
    public static final int DIAS_ALERTA_VENCIMIENTO = 30;

    /**
     * Estado: Insumo disponible.
     */
    public static final String ESTADO_INSUMO_DISPONIBLE = "DISPONIBLE";

    /**
     * Estado: Insumo agotado.
     */
    public static final String ESTADO_INSUMO_AGOTADO = "AGOTADO";

    /**
     * Estado: Insumo en pedido.
     */
    public static final String ESTADO_INSUMO_EN_PEDIDO = "EN_PEDIDO";

    // ===================================================================
    // CONSTANTES DE VALIDACIÓN
    // ===================================================================

    /**
     * Longitud mínima de contraseña.
     */
    public static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * Longitud máxima de contraseña.
     */
    public static final int MAX_PASSWORD_LENGTH = 50;

    /**
     * Longitud mínima de nombre.
     */
    public static final int MIN_NAME_LENGTH = 2;

    /**
     * Longitud máxima de nombre.
     */
    public static final int MAX_NAME_LENGTH = 100;

    /**
     * Patrón de validación para email.
     */
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    /**
     * Patrón de validación para teléfono (Colombia).
     */
    public static final String PHONE_PATTERN = "^\\+?[0-9]{10,13}$";

    // ===================================================================
    // CONSTANTES DE MENSAJES
    // ===================================================================

    /**
     * Mensaje de recurso no encontrado.
     */
    public static final String MSG_RESOURCE_NOT_FOUND = "Recurso no encontrado";

    /**
     * Mensaje de operación exitosa.
     */
    public static final String MSG_OPERATION_SUCCESS = "Operación realizada exitosamente";

    /**
     * Mensaje de error de validación.
     */
    public static final String MSG_VALIDATION_ERROR = "Error de validación";

    /**
     * Mensaje de error de autenticación.
     */
    public static final String MSG_AUTHENTICATION_ERROR = "Error de autenticación";

    /**
     * Mensaje de acceso denegado.
     */
    public static final String MSG_ACCESS_DENIED = "Acceso denegado";

    // ===================================================================
    // CONSTANTES DE FORMATOS DE FECHA
    // ===================================================================

    /**
     * Formato de fecha estándar.
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Formato de fecha y hora estándar.
     */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Formato de hora estándar.
     */
    public static final String TIME_FORMAT = "HH:mm:ss";

    // ===================================================================
    // CONSTANTES DE ARCHIVOS
    // ===================================================================

    /**
     * Tamaño máximo de archivo en bytes (10 MB).
     */
    public static final long MAX_FILE_SIZE = 10485760L; // 10 MB

    /**
     * Extensiones de archivo permitidas para documentos.
     */
    public static final String[] ALLOWED_FILE_EXTENSIONS = {"pdf", "jpg", "jpeg", "png", "doc", "docx"};

    // ===================================================================
    // CONSTANTES DE DURACIÓN DE SERVICIOS (en minutos)
    // ===================================================================

    /**
     * Duración estándar de consulta general (minutos).
     */
    public static final int DURACION_CONSULTA_GENERAL = 30;

    /**
     * Duración estándar de vacunación (minutos).
     */
    public static final int DURACION_VACUNACION = 15;

    /**
     * Duración estándar de cirugía (minutos).
     */
    public static final int DURACION_CIRUGIA = 120;

    /**
     * Duración estándar de baño y peluquería (minutos).
     */
    public static final int DURACION_ESTETICO = 60;

    // ===================================================================
    // CONSTANTES DE FACTURACIÓN
    // ===================================================================

    /**
     * Porcentaje de IVA aplicable.
     */
    public static final double IVA_PORCENTAJE = 0.19; // 19%

    /**
     * Método de pago: Efectivo.
     */
    public static final String METODO_PAGO_EFECTIVO = "EFECTIVO";

    /**
     * Método de pago: Tarjeta.
     */
    public static final String METODO_PAGO_TARJETA = "TARJETA";

    /**
     * Método de pago: Transferencia.
     */
    public static final String METODO_PAGO_TRANSFERENCIA = "TRANSFERENCIA";

    // ===================================================================
    // CONSTANTES DE ZONA HORARIA
    // ===================================================================

    /**
     * Zona horaria por defecto (Colombia).
     */
    public static final String DEFAULT_TIMEZONE = "America/Bogota";

    // ===================================================================
    // CONSTANTES DE TIEMPO (en milisegundos)
    // ===================================================================

    /**
     * Una hora en milisegundos.
     */
    public static final long UNA_HORA_MS = 60 * 60 * 1000L; // 3600000

    /**
     * Un día en milisegundos.
     */
    public static final long UN_DIA_MS = 24 * 60 * 60 * 1000L; // 86400000

    /**
     * Un mes en días (promedio).
     */
    public static final int DIAS_POR_MES = 30;

    // ===================================================================
    // CONSTANTES DE PORCENTAJES Y DIVISORES
    // ===================================================================

    /**
     * Divisor para convertir porcentaje a decimal (100).
     */
    public static final BigDecimal PORCENTAJE_DIVISOR = new BigDecimal("100");

    /**
     * Días de vigencia para recetas controladas.
     */
    public static final int DIAS_VIGENCIA_RECETA_CONTROLADA = 15;

    /**
     * Días de vigencia para recetas simples.
     */
    public static final int DIAS_VIGENCIA_RECETA_SIMPLE = 30;

    /**
     * Consumo diario promedio estimado para cálculo de reposición (unidades por día).
     */
    public static final int CONSUMO_DIARIO_PROMEDIO_ESTIMADO = 10;
}
