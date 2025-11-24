package com.veterinaria.clinica_veternica.patterns.structural.proxy;

import com.veterinaria.clinica_veternica.patterns.creational.singleton.ConfigurationManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Patrón Proxy: CachedServiceProxy
 *
 * Proxy genérico que implementa caché para servicios, mejorando el rendimiento
 * de consultas frecuentes y reduciendo la carga en la base de datos.
 *
 * PROPÓSITO:
 * - Mejora el rendimiento de consultas frecuentes mediante caché
 * - Reduce la carga en la base de datos
 * - Proporciona una interfaz uniforme para operaciones con caché
 * - Permite invalidar caché cuando sea necesario
 *
 * CASOS DE USO:
 * 1. Cachear consultas de servicios frecuentes (listados, búsquedas)
 * 2. Cachear datos de referencia (especies, razas, tipos de insumos)
 * 3. Cachear resultados de cálculos complejos
 * 4. Invalidar caché cuando se actualizan datos
 *
 * ESTRATEGIAS DE CACHÉ:
 * - Cacheable: Lee del caché si existe, sino ejecuta y guarda
 * - CachePut: Siempre ejecuta y actualiza el caché
 * - CacheEvict: Elimina entradas del caché
 *
 * Justificación:
 * - Mejora significativa del rendimiento para datos frecuentemente consultados
 * - Reduce la carga en la base de datos
 * - Proporciona una capa de abstracción para el manejo de caché
 * - Facilita la implementación de diferentes estrategias de caché
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-01-27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CachedServiceProxy {

    private final ConfigurationManager configurationManager;

    // Caché en memoria para operaciones simples
    private final ConcurrentHashMap<String, CacheEntry> memoryCache = new ConcurrentHashMap<>();

    /**
     * Obtiene el TTL por defecto desde la configuración.
     * Si no está configurado, usa 5 minutos por defecto.
     */
    private long getDefaultTTL() {
        return configurationManager.getConfigurationAsInteger("cache.ttl.default.seconds", 300) * 1000L;
    }

    /**
     * Ejecuta una operación con caché. Si el resultado está en caché y no ha expirado,
     * lo retorna. Si no, ejecuta la operación y guarda el resultado en caché.
     *
     * PROPÓSITO: Proporciona caché transparente para cualquier operación.
     *
     * @param key Clave única para el caché
     * @param operation Operación a ejecutar si no hay caché
     * @param <T> Tipo del resultado
     * @return Resultado de la operación (desde caché o ejecutado)
     */
    public <T> T executeWithCache(String key, Supplier<T> operation) {
        return executeWithCache(key, operation, getDefaultTTL());
    }

    /**
     * Ejecuta una operación con caché con TTL personalizado.
     *
     * PROPÓSITO: Permite configurar el tiempo de vida del caché según la necesidad.
     *
     * @param key Clave única para el caché
     * @param operation Operación a ejecutar si no hay caché
     * @param ttl Tiempo de vida en milisegundos
     * @param <T> Tipo del resultado
     * @return Resultado de la operación
     * @apiNote Se usa @SuppressWarnings("unchecked") porque el tipo genérico T
     *          se pierde en tiempo de ejecución al almacenar en CacheEntry como Object.
     *          La conversión es segura porque el valor fue almacenado con el mismo tipo T.
     */
    @SuppressWarnings("unchecked")
    public <T> T executeWithCache(String key, Supplier<T> operation, long ttl) {
        CacheEntry entry = memoryCache.get(key);

        // Verificar si existe y no ha expirado
        if (entry != null && !entry.isExpired()) {
            log.debug("Cache hit para clave: {}", key);
            return (T) entry.getValue();
        }

        // Cache miss - ejecutar operación
        log.debug("Cache miss para clave: {}. Ejecutando operación...", key);
        T result = operation.get();

        // Guardar en caché
        memoryCache.put(key, new CacheEntry(result, System.currentTimeMillis() + ttl));
        log.debug("Resultado guardado en caché para clave: {}", key);

        return result;
    }

    /**
     * Fuerza la ejecución de una operación y actualiza el caché.
     *
     * PROPÓSITO: Útil cuando se necesita actualizar el caché con datos frescos.
     *
     * @param key Clave única para el caché
     * @param operation Operación a ejecutar
     * @param <T> Tipo del resultado
     * @return Resultado de la operación
     * @apiNote Se usa @SuppressWarnings("unchecked") porque el tipo genérico T
     *          se pierde en tiempo de ejecución al almacenar en CacheEntry como Object.
     *          La conversión es segura porque el valor fue almacenado con el mismo tipo T.
     */
    @SuppressWarnings("unchecked")
    public <T> T executeAndCache(String key, Supplier<T> operation) {
        return executeAndCache(key, operation, getDefaultTTL());
    }

    /**
     * Fuerza la ejecución y actualiza el caché con TTL personalizado.
     *
     * @param key Clave única para el caché
     * @param operation Operación a ejecutar
     * @param ttl Tiempo de vida en milisegundos
     * @param <T> Tipo del resultado
     * @return Resultado de la operación
     * @apiNote Se usa @SuppressWarnings("unchecked") porque el tipo genérico T
     *          se pierde en tiempo de ejecución al almacenar en CacheEntry como Object.
     *          La conversión es segura porque el valor fue almacenado con el mismo tipo T.
     */
    @SuppressWarnings("unchecked")
    public <T> T executeAndCache(String key, Supplier<T> operation, long ttl) {
        log.debug("Ejecutando y actualizando caché para clave: {}", key);
        T result = operation.get();
        memoryCache.put(key, new CacheEntry(result, System.currentTimeMillis() + ttl));
        return result;
    }

    /**
     * Elimina una entrada del caché.
     *
     * PROPÓSITO: Permite invalidar caché cuando se actualizan datos.
     *
     * @param key Clave a eliminar
     */
    public void evict(String key) {
        memoryCache.remove(key);
        log.debug("Caché eliminado para clave: {}", key);
    }

    /**
     * Elimina todas las entradas del caché que coincidan con un patrón.
     *
     * PROPÓSITO: Útil para invalidar múltiples entradas relacionadas.
     *
     * @param pattern Patrón de clave (ej: "mascota:*")
     */
    public void evictPattern(String pattern) {
        if (pattern.endsWith("*")) {
            String prefix = pattern.substring(0, pattern.length() - 1);
            memoryCache.keySet().removeIf(key -> key.startsWith(prefix));
            log.debug("Caché eliminado para patrón: {}", pattern);
        } else {
            evict(pattern);
        }
    }

    /**
     * Limpia  el caché.
     *
     * PROPÓSITO: Elimina todas las entradas del caché.
     */
    public void clear() {
        int size = memoryCache.size();
        memoryCache.clear();
        log.info("Caché limpiado. {} entradas eliminadas", size);
    }

    /**
     * Obtiene estadísticas del caché.
     *
     * PROPÓSITO: Útil para monitoreo y debugging.
     *
     * @return Estadísticas del caché
     */
    public CacheStats getStats() {
        long totalEntries = memoryCache.size();
        long expiredEntries = memoryCache.values().stream()
                .mapToLong(entry -> entry.isExpired() ? 1 : 0)
                .sum();
        long validEntries = totalEntries - expiredEntries;

        return new CacheStats(totalEntries, validEntries, expiredEntries);
    }

    /**
     * Limpia entradas expiradas del caché.
     *
     * PROPÓSITO: Mantiene el caché limpio eliminando entradas obsoletas.
     */
    public void cleanExpired() {
        int removed = 0;
        for (Map.Entry<String, CacheEntry> entry : memoryCache.entrySet()) {
            if (entry.getValue() != null && entry.getValue().isExpired()) {
                memoryCache.remove(entry.getKey());
                removed++;
            }
        }
        if (removed > 0) {
            log.debug("{} entradas expiradas eliminadas del caché", removed);
        }
    }

    /**
     * Clase interna para representar una entrada del caché.
     */
    private static class CacheEntry {
        private final Object value;
        private final long expirationTime;

        public CacheEntry(Object value, long expirationTime) {
            this.value = value;
            this.expirationTime = expirationTime;
        }

        public Object getValue() {
            return value;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }

    /**
     * Clase para estadísticas del caché.
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class CacheStats {
        private long totalEntries;
        private long validEntries;
        private long expiredEntries;
    }
}

