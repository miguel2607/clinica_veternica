package com.veterinaria.clinica_veternica.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuración de Spring Cache para mejorar el rendimiento del sistema.
 *
 * Implementa caché en memoria para consultas frecuentes, reduciendo la carga
 * en la base de datos y mejorando los tiempos de respuesta.
 *
 * Esta configuración utiliza SimpleCacheManager con ConcurrentMapCache,
 * apropiado para desarrollo y aplicaciones de tamaño medio.
 *
 * NOTA: Para aplicaciones de alto tráfico, considerar Redis o Hazelcast.
 *
 * Patrones de diseño relacionados:
 * - Proxy Pattern: El caché actúa como proxy del servicio real
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Nombres de los cachés utilizados en la aplicación.
     * Definidos como constantes para evitar magic strings.
     */
    public static final String CACHE_SERVICIOS = "servicios";
    public static final String CACHE_MASCOTAS = "mascotas";
    public static final String CACHE_PROPIETARIOS = "propietarios";
    public static final String CACHE_HORARIOS = "horarios";
    public static final String CACHE_ESPECIES = "especies";
    public static final String CACHE_RAZAS = "razas";
    public static final String CACHE_INSUMOS = "insumos";
    public static final String CACHE_VETERINARIOS = "veterinarios";

    /**
     * Configuración del CacheManager.
     *
     * Define los cachés disponibles en la aplicación y sus características.
     * Cada caché está diseñado para almacenar datos específicos que se
     * consultan frecuentemente pero cambian poco.
     *
     * Uso en servicios:
     * - @Cacheable: Para consultas (ej: findById, findAll)
     * - @CachePut: Para actualizar el caché al modificar datos
     * - @CacheEvict: Para limpiar el caché al eliminar datos
     *
     * Ejemplo de uso:
     * <pre>
     * {@code
     * @Cacheable(value = CacheConfig.CACHE_SERVICIOS, key = "#id")
     * public Servicio findById(Long id) {
     *     return servicioRepository.findById(id)
     *         .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));
     * }
     * }
     * </pre>
     *
     * @return CacheManager configurado con los cachés definidos
     */
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        cacheManager.setCaches(Arrays.asList(
                // Caché para servicios veterinarios
                // Datos: Tipos de servicios (consultas, cirugías, vacunaciones, etc.)
                // Frecuencia de cambio: Baja
                new ConcurrentMapCache(CACHE_SERVICIOS),

                // Caché para mascotas
                // Datos: Información básica de mascotas activas
                // Frecuencia de cambio: Media
                new ConcurrentMapCache(CACHE_MASCOTAS),

                // Caché para propietarios
                // Datos: Información de contacto de propietarios
                // Frecuencia de cambio: Media
                new ConcurrentMapCache(CACHE_PROPIETARIOS),

                // Caché para horarios de veterinarios
                // Datos: Disponibilidad y horarios de atención
                // Frecuencia de cambio: Baja
                new ConcurrentMapCache(CACHE_HORARIOS),

                // Caché para especies
                // Datos: Catálogo de especies (perro, gato, ave, etc.)
                // Frecuencia de cambio: Muy baja
                new ConcurrentMapCache(CACHE_ESPECIES),

                // Caché para razas
                // Datos: Catálogo de razas por especie
                // Frecuencia de cambio: Muy baja
                new ConcurrentMapCache(CACHE_RAZAS),

                // Caché para insumos
                // Datos: Catálogo de insumos médicos y medicamentos
                // Frecuencia de cambio: Baja
                new ConcurrentMapCache(CACHE_INSUMOS),

                // Caché para veterinarios
                // Datos: Información de veterinarios activos
                // Frecuencia de cambio: Baja
                new ConcurrentMapCache(CACHE_VETERINARIOS)
        ));

        return cacheManager;
    }

    /**
     * NOTAS DE IMPLEMENTACIÓN:
     *
     * 1. CUÁNDO USAR CACHÉ:
     *    - Datos que se leen frecuentemente
     *    - Datos que cambian poco
     *    - Consultas costosas en términos de tiempo
     *
     * 2. CUÁNDO NO USAR CACHÉ:
     *    - Datos que cambian constantemente (ej: estado de citas)
     *    - Datos sensibles que requieren información en tiempo real
     *    - Transacciones financieras
     *
     * 3. ESTRATEGIAS DE INVALIDACIÓN:
     *    - Time-based: Evictar después de X tiempo (no implementado aquí)
     *    - Event-based: Evictar cuando se modifica la entidad (recomendado)
     *
     * 4. MEJORAS FUTURAS:
     *    - Implementar TTL (Time To Live) para cada caché
     *    - Migrar a Redis para aplicaciones distribuidas
     *    - Implementar estadísticas de hit/miss del caché
     *    - Configurar tamaño máximo de cada caché
     *

     * - Redis con Spring Data Redis
     * - Hazelcast para caché distribuido
     * - Caffeine como implementación más eficiente
     */
}
