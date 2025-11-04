package com.veterinaria.clinica_veternica.patterns.creational.singleton;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Patrón Singleton: ConfigurationManager
 *
 * Gestiona la configuración global de la aplicación de manera centralizada.
 * Garantiza una única instancia para acceder a configuraciones del sistema.
 *
 * Justificación:
 * - Evita inconsistencias en configuraciones
 * - Punto único de acceso a propiedades del sistema
 * - Thread-safe mediante ConcurrentHashMap
 *
 * Uso:
 * - Almacenar configuraciones globales de la clínica
 * - Parámetros del sistema (horarios, precios base, etc.)
 * - Configuraciones que no cambian frecuentemente
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class ConfigurationManager {

    // Thread-safe storage para configuraciones
    private final Map<String, Object> configurations = new ConcurrentHashMap<>();

    /**
     * Obtiene una configuración por su clave.
     *
     * @param key Clave de configuración
     * @return Valor de la configuración
     */
    public Object getConfiguration(String key) {
        log.debug("Obteniendo configuración: {}", key);
        return configurations.get(key);
    }

    /**
     * Obtiene una configuración como String.
     *
     * @param key Clave de configuración
     * @param defaultValue Valor por defecto si no existe
     * @return Valor de la configuración
     */
    public String getConfigurationAsString(String key, String defaultValue) {
        Object value = configurations.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value.toString();
    }

    /**
     * Obtiene una configuración como Integer.
     *
     * @param key Clave de configuración
     * @param defaultValue Valor por defecto si no existe
     * @return Valor de la configuración
     */
    public Integer getConfigurationAsInteger(String key, Integer defaultValue) {
        Object value = configurations.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            log.warn("No se pudo convertir configuración {} a Integer, usando valor por defecto", key);
            return defaultValue;
        }
    }

    /**
     * Obtiene una configuración como Boolean.
     *
     * @param key Clave de configuración
     * @param defaultValue Valor por defecto si no existe
     * @return Valor de la configuración
     */
    public Boolean getConfigurationAsBoolean(String key, Boolean defaultValue) {
        Object value = configurations.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(value.toString());
    }

    /**
     * Establece una configuración.
     *
     * @param key Clave de configuración
     * @param value Valor de la configuración
     */
    public void setConfiguration(String key, Object value) {
        log.info("Estableciendo configuración: {} = {}", key, value);
        configurations.put(key, value);
    }

    /**
     * Elimina una configuración.
     *
     * @param key Clave de configuración
     */
    public void removeConfiguration(String key) {
        log.info("Eliminando configuración: {}", key);
        configurations.remove(key);
    }

    /**
     * Verifica si existe una configuración.
     *
     * @param key Clave de configuración
     * @return true si existe
     */
    public boolean hasConfiguration(String key) {
        return configurations.containsKey(key);
    }

    /**
     * Obtiene todas las configuraciones.
     *
     * @return Mapa de configuraciones
     */
    public Map<String, Object> getAllConfigurations() {
        return new ConcurrentHashMap<>(configurations);
    }

    /**
     * Limpia todas las configuraciones.
     */
    public void clearAllConfigurations() {
        log.warn("Limpiando todas las configuraciones");
        configurations.clear();
    }

    /**
     * Obtiene el número de configuraciones almacenadas.
     *
     * @return Cantidad de configuraciones
     */
    public int getConfigurationCount() {
        return configurations.size();
    }

    // ===================================================================
    // CONFIGURACIONES PREDEFINIDAS DE LA CLÍNICA
    // ===================================================================

    /**
     * Obtiene el nombre de la clínica.
     */
    public String getClinicaName() {
        return getConfigurationAsString("clinica.nombre", "Clínica Veterinaria");
    }

    /**
     * Obtiene la hora de apertura.
     */
    public String getHoraApertura() {
        return getConfigurationAsString("clinica.hora.apertura", "08:00");
    }

    /**
     * Obtiene la hora de cierre.
     */
    public String getHoraCierre() {
        return getConfigurationAsString("clinica.hora.cierre", "18:00");
    }

    /**
     * Obtiene la duración estándar de una cita en minutos.
     */
    public Integer getDuracionCitaEstandar() {
        return getConfigurationAsInteger("clinica.duracion.cita.estandar", 30);
    }

    /**
     * Obtiene el stock mínimo general.
     */
    public Integer getStockMinimoGeneral() {
        return getConfigurationAsInteger("inventario.stock.minimo.general", 10);
    }

    /**
     * Obtiene si se envían recordatorios automáticos.
     */
    public Boolean getRecordatoriosAutomaticos() {
        return getConfigurationAsBoolean("notificaciones.recordatorios.automaticos", true);
    }

    /**
     * Obtiene las horas de anticipación para recordatorios.
     */
    public Integer getHorasAnticipacionRecordatorio() {
        return getConfigurationAsInteger("notificaciones.recordatorios.horas.anticipacion", 24);
    }
}
