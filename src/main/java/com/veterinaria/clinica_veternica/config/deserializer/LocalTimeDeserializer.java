package com.veterinaria.clinica_veternica.config.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Deserializador personalizado para LocalTime que acepta múltiples formatos.
 * 
 * Formatos soportados:
 * - "HH:mm:ss" (ej: "09:00:00")
 * - "HH:mm" (ej: "09:00")
 * - "H:mm" (ej: "9:00")
 * - "H" (ej: "9") -> se convierte a "09:00:00"
 * - "h:mm a" (ej: "9:00 AM", "9:30 PM")
 * - "h a" (ej: "9 AM", "9PM") -> se convierte a "09:00:00" o "21:00:00"
 * - "h:mm:ss a" (ej: "9:00:00 AM", "9:30:45 PM")
 * 
 * @author Clínica Veterinaria Team
 * @version 2.1
 * @since 2025-11-13
 */
public class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {

    private static final DateTimeFormatter[] FORMATTERS = {
        DateTimeFormatter.ofPattern("HH:mm:ss"),
        DateTimeFormatter.ofPattern("HH:mm"),
        DateTimeFormatter.ofPattern("H:mm"),
        DateTimeFormatter.ofPattern("H")
    };

    private static final DateTimeFormatter[] AM_PM_FORMATTERS = {
        DateTimeFormatter.ofPattern("h:mm:ss a", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("h a", Locale.ENGLISH)
    };

    // Patrón para detectar si el string contiene AM/PM (case insensitive)
    private static final Pattern AM_PM_PATTERN = Pattern.compile("\\b(AM|PM)\\b", Pattern.CASE_INSENSITIVE);

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().trim();

        if (value == null || value.isEmpty()) {
            return null;
        }

        // Normalizar espacios alrededor de AM/PM
        value = normalizeAmPm(value);

        // Si contiene AM/PM, usar formatters específicos
        if (AM_PM_PATTERN.matcher(value).find()) {
            return parseAmPmFormat(value);
        }

        // Intentar con formatos estándar (24 horas)
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                LocalTime time = LocalTime.parse(value, formatter);
                
                // Si es solo la hora (formato "H"), asegurar minutos y segundos en 0
                if (formatter == FORMATTERS[3]) {
                    return LocalTime.of(time.getHour(), 0, 0);
                }
                
                return time;
            } catch (DateTimeParseException ignored) {
                // Continuar con el siguiente formatter
            }
        }

        throw new IOException(
            String.format(
                "No se pudo parsear '%s' como LocalTime. Formatos soportados: " +
                "HH:mm:ss, HH:mm, H:mm, H (ej: '09:00:00', '09:00', '9:00', '9') o " +
                "h:mm a, h a (ej: '9:00 AM', '9 PM', '9:30 PM')",
                value
            )
        );
    }

    /**
     * Normaliza los espacios alrededor de AM/PM y convierte a mayúsculas.
     * Ejemplos:
     * - "9AM" -> "9 AM"
     * - "9 PM" -> "9 PM"
     * - "9:30PM" -> "9:30 PM"
     * - "9am" -> "9 AM"
     * - "9pm" -> "9 PM"
     */
    private String normalizeAmPm(String value) {
        // Reemplazar "AM" o "PM" sin espacio antes con espacio
        value = value.replaceAll("(?i)(\\d)(AM|PM)", "$1 $2");
        // Normalizar a mayúsculas
        value = value.replaceAll("(?i)\\bam\\b", "AM");
        value = value.replaceAll("(?i)\\bpm\\b", "PM");
        return value;
    }

    /**
     * Parsea formatos con AM/PM.
     */
    private LocalTime parseAmPmFormat(String value) throws IOException {
        for (DateTimeFormatter formatter : AM_PM_FORMATTERS) {
            try {
                return LocalTime.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
                // Continuar con el siguiente formatter
            }
        }

        throw new IOException(
            String.format(
                "No se pudo parsear '%s' como LocalTime con formato AM/PM. " +
                "Formatos soportados: h:mm a, h a (ej: '9:00 AM', '9 PM', '9:30 PM')",
                value
            )
        );
    }
}

