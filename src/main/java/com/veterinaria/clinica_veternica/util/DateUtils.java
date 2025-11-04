package com.veterinaria.clinica_veternica.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Clase de utilidades para manejo de fechas y horas.
 *
 * Proporciona métodos helper para operaciones comunes con fechas,
 * conversiones, formateo y cálculos de diferencias.
 *
 * Utiliza la API de Java Time (java.time) introducida en Java 8,
 * que es más robusta y thread-safe que la antigua API Date/Calendar.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
public final class DateUtils {

    // Constructor privado para evitar instanciación
    private DateUtils() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad y no puede ser instanciada");
    }

    /**
     * Zona horaria por defecto del sistema (Colombia).
     */
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of(Constants.DEFAULT_TIMEZONE);

    /**
     * Formateador para fecha estándar.
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);

    /**
     * Formateador para fecha y hora estándar.
     */
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT);

    /**
     * Formateador para hora estándar.
     */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(Constants.TIME_FORMAT);

    // ===================================================================
    // MÉTODOS DE OBTENCIÓN DE FECHA/HORA ACTUAL
    // ===================================================================

    /**
     * Obtiene la fecha actual del sistema.
     *
     * @return LocalDate con la fecha actual
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now(DEFAULT_ZONE_ID);
    }

    /**
     * Obtiene la fecha y hora actual del sistema.
     *
     * @return LocalDateTime con la fecha y hora actual
     */
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now(DEFAULT_ZONE_ID);
    }

    /**
     * Obtiene la hora actual del sistema.
     *
     * @return LocalTime con la hora actual
     */
    public static LocalTime getCurrentTime() {
        return LocalTime.now(DEFAULT_ZONE_ID);
    }

    // ===================================================================
    // MÉTODOS DE CONVERSIÓN
    // ===================================================================

    /**
     * Convierte LocalDateTime a Date (java.util.Date).
     *
     * @param localDateTime LocalDateTime a convertir
     * @return Date equivalente
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * Convierte LocalDate a Date (java.util.Date).
     *
     * @param localDate LocalDate a convertir
     * @return Date equivalente (con hora 00:00:00)
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * Convierte Date a LocalDateTime.
     *
     * @param date Date a convertir
     * @return LocalDateTime equivalente
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE_ID);
    }

    /**
     * Convierte Date a LocalDate.
     *
     * @param date Date a convertir
     * @return LocalDate equivalente
     */
    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDate();
    }

    // ===================================================================
    // MÉTODOS DE FORMATEO
    // ===================================================================

    /**
     * Formatea LocalDate como String.
     *
     * @param date Fecha a formatear
     * @return String con formato yyyy-MM-dd
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * Formatea LocalDateTime como String.
     *
     * @param dateTime Fecha y hora a formatear
     * @return String con formato yyyy-MM-dd HH:mm:ss
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATETIME_FORMATTER);
    }

    /**
     * Formatea LocalTime como String.
     *
     * @param time Hora a formatear
     * @return String con formato HH:mm:ss
     */
    public static String formatTime(LocalTime time) {
        if (time == null) {
            return null;
        }
        return time.format(TIME_FORMATTER);
    }

    // ===================================================================
    // MÉTODOS DE PARSING
    // ===================================================================

    /**
     * Parsea un String a LocalDate.
     *
     * @param dateString String con formato yyyy-MM-dd
     * @return LocalDate parseado
     * @throws java.time.format.DateTimeParseException Si el formato es inválido
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }

    /**
     * Parsea un String a LocalDateTime.
     *
     * @param dateTimeString String con formato yyyy-MM-dd HH:mm:ss
     * @return LocalDateTime parseado
     * @throws java.time.format.DateTimeParseException Si el formato es inválido
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeString, DATETIME_FORMATTER);
    }

    // ===================================================================
    // MÉTODOS DE CÁLCULO
    // ===================================================================

    /**
     * Calcula la diferencia en días entre dos fechas.
     *
     * @param startDate Fecha inicial
     * @param endDate Fecha final
     * @return Número de días de diferencia (puede ser negativo si endDate es anterior)
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * Calcula la diferencia en horas entre dos fechas y horas.
     *
     * @param startDateTime Fecha y hora inicial
     * @param endDateTime Fecha y hora final
     * @return Número de horas de diferencia (puede ser negativo si endDateTime es anterior)
     */
    public static long hoursBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(startDateTime, endDateTime);
    }

    /**
     * Calcula la edad en años basándose en la fecha de nacimiento.
     *
     * @param birthDate Fecha de nacimiento
     * @return Edad en años
     */
    public static int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, getCurrentDate()).getYears();
    }

    /**
     * Agrega días a una fecha.
     *
     * @param date Fecha base
     * @param days Número de días a agregar (puede ser negativo)
     * @return Nueva fecha con los días agregados
     */
    public static LocalDate addDays(LocalDate date, long days) {
        if (date == null) {
            return null;
        }
        return date.plusDays(days);
    }

    /**
     * Agrega horas a una fecha y hora.
     *
     * @param dateTime Fecha y hora base
     * @param hours Número de horas a agregar (puede ser negativo)
     * @return Nueva fecha y hora con las horas agregadas
     */
    public static LocalDateTime addHours(LocalDateTime dateTime, long hours) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusHours(hours);
    }

    // ===================================================================
    // MÉTODOS DE VALIDACIÓN
    // ===================================================================

    /**
     * Verifica si una fecha está en el pasado.
     *
     * @param date Fecha a verificar
     * @return true si la fecha es anterior a hoy
     */
    public static boolean isInThePast(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isBefore(getCurrentDate());
    }

    /**
     * Verifica si una fecha está en el futuro.
     *
     * @param date Fecha a verificar
     * @return true si la fecha es posterior a hoy
     */
    public static boolean isInTheFuture(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isAfter(getCurrentDate());
    }

    /**
     * Verifica si una fecha y hora está en el pasado.
     *
     * @param dateTime Fecha y hora a verificar
     * @return true si la fecha y hora es anterior a ahora
     */
    public static boolean isInThePast(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        return dateTime.isBefore(getCurrentDateTime());
    }

    /**
     * Verifica si una fecha y hora está en el futuro.
     *
     * @param dateTime Fecha y hora a verificar
     * @return true si la fecha y hora es posterior a ahora
     */
    public static boolean isInTheFuture(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        return dateTime.isAfter(getCurrentDateTime());
    }

    /**
     * Verifica si una fecha está entre dos fechas (inclusive).
     *
     * @param date Fecha a verificar
     * @param startDate Fecha de inicio del rango
     * @param endDate Fecha de fin del rango
     * @return true si la fecha está dentro del rango
     */
    public static boolean isBetween(LocalDate date, LocalDate startDate, LocalDate endDate) {
        if (date == null || startDate == null || endDate == null) {
            return false;
        }
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    // ===================================================================
    // MÉTODOS ESPECÍFICOS DEL NEGOCIO
    // ===================================================================

    /**
     * Verifica si un insumo está próximo a vencer.
     * Se considera próximo a vencer si faltan menos de 30 días.
     *
     * @param expirationDate Fecha de vencimiento del insumo
     * @return true si está próximo a vencer
     */
    public static boolean isCloseToExpiration(LocalDate expirationDate) {
        if (expirationDate == null) {
            return false;
        }
        long daysUntilExpiration = daysBetween(getCurrentDate(), expirationDate);
        return daysUntilExpiration >= 0 && daysUntilExpiration <= Constants.DIAS_ALERTA_VENCIMIENTO;
    }

    /**
     * Verifica si un producto ya está vencido.
     *
     * @param expirationDate Fecha de vencimiento
     * @return true si ya venció
     */
    public static boolean isExpired(LocalDate expirationDate) {
        if (expirationDate == null) {
            return false;
        }
        return expirationDate.isBefore(getCurrentDate());
    }

    /**
     * Obtiene el inicio del día para una fecha dada.
     *
     * @param date Fecha
     * @return LocalDateTime con la hora 00:00:00
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay();
    }

    /**
     * Obtiene el fin del día para una fecha dada.
     *
     * @param date Fecha
     * @return LocalDateTime con la hora 23:59:59
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atTime(23, 59, 59);
    }
}
