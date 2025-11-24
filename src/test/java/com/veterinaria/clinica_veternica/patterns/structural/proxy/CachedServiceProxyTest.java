package com.veterinaria.clinica_veternica.patterns.structural.proxy;

import com.veterinaria.clinica_veternica.patterns.creational.singleton.ConfigurationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests para el patrón Proxy - CachedServiceProxy
 */
@ExtendWith(MockitoExtension.class)
class CachedServiceProxyTest {

    @Mock
    private ConfigurationManager configurationManager;

    @InjectMocks
    private CachedServiceProxy cachedServiceProxy;

    @BeforeEach
    void setUp() {
        // Configurar TTL por defecto de 5 minutos (300 segundos)
        lenient().when(configurationManager.getConfigurationAsInteger(anyString(), anyInt()))
                .thenReturn(300);
    }

    @Test
    @DisplayName("CACHE HIT - Debe retornar valor del caché sin ejecutar operación")
    void debeBuscarEnCacheSiExiste() {
        // Arrange
        String key = "test-key";
        String expectedValue = "cached-value";
        Supplier<String> operation = mock(Supplier.class);
        lenient().when(operation.get()).thenReturn("new-value");

        // Primera ejecución - guarda en caché
        cachedServiceProxy.executeWithCache(key, () -> expectedValue);

        // Act - Segunda ejecución - debe usar caché
        String result = cachedServiceProxy.executeWithCache(key, operation);

        // Assert
        assertEquals(expectedValue, result);
        verify(operation, never()).get(); // No debe ejecutar la operación
    }

    @Test
    @DisplayName("CACHE MISS - Debe ejecutar operación y guardar en caché")
    void debeEjecutarOperacionSiNoExisteEnCache() {
        // Arrange
        String key = "test-key";
        String expectedValue = "computed-value";
        Supplier<String> operation = () -> expectedValue;

        // Act
        String result = cachedServiceProxy.executeWithCache(key, operation);

        // Assert
        assertEquals(expectedValue, result);
    }

    @Test
    @DisplayName("Debe ejecutar operación cuando entrada expiró")
    void debeEjecutarOperacionCuandoEntradaExpiro() throws InterruptedException {
        // Arrange
        String key = "test-key";
        long ttl = 100L; // 100ms
        Supplier<String> operation1 = () -> "value-1";
        Supplier<String> operation2 = () -> "value-2";

        // Act - Primera ejecución
        String result1 = cachedServiceProxy.executeWithCache(key, operation1, ttl);
        assertEquals("value-1", result1);

        // Esperar que expire
        // SonarQube: Thread.sleep necesario para probar expiración de caché
        Thread.sleep(150); // NOSONAR

        // Segunda ejecución - debe ejecutar operación de nuevo
        String result2 = cachedServiceProxy.executeWithCache(key, operation2, ttl);

        // Assert
        assertEquals("value-2", result2);
    }

    @Test
    @DisplayName("Debe usar TTL personalizado cuando se especifica")
    void debeUsarTTLPersonalizado() {
        // Arrange
        String key = "test-key";
        String expectedValue = "value";
        long customTTL = 60000L; // 1 minuto
        Supplier<String> operation = () -> expectedValue;

        // Act
        String result = cachedServiceProxy.executeWithCache(key, operation, customTTL);

        // Assert
        assertEquals(expectedValue, result);
        // Verificar que está en caché
        String cachedResult = cachedServiceProxy.executeWithCache(key, () -> "other-value", customTTL);
        assertEquals(expectedValue, cachedResult);
    }

    @Test
    @DisplayName("Debe forzar actualización del caché con executeAndCache")
    void debeForzarActualizacionDelCache() {
        // Arrange
        String key = "test-key";
        cachedServiceProxy.executeWithCache(key, () -> "old-value");

        // Act - Forzar actualización
        String result = cachedServiceProxy.executeAndCache(key, () -> "new-value");

        // Assert
        assertEquals("new-value", result);
        // Verificar que el caché se actualizó
        String cachedResult = cachedServiceProxy.executeWithCache(key, () -> "another-value");
        assertEquals("new-value", cachedResult);
    }

    @Test
    @DisplayName("Debe eliminar entrada del caché con evict")
    void debeEliminarEntradaDelCache() {
        // Arrange
        String key = "test-key";
        String initialValue = "initial";
        cachedServiceProxy.executeWithCache(key, () -> initialValue);

        // Act
        cachedServiceProxy.evict(key);

        // Assert - Debe ejecutar operación de nuevo
        String newValue = "new-value";
        Supplier<String> operation = mock(Supplier.class);
        when(operation.get()).thenReturn(newValue);

        String result = cachedServiceProxy.executeWithCache(key, operation);

        assertEquals(newValue, result);
        verify(operation, times(1)).get();
    }

    @Test
    @DisplayName("Debe eliminar múltiples entradas con evictPattern")
    void debeEliminarMultiplesEntradasConPattern() {
        // Arrange
        cachedServiceProxy.executeWithCache("mascota:1", () -> "mascota-1");
        cachedServiceProxy.executeWithCache("mascota:2", () -> "mascota-2");
        cachedServiceProxy.executeWithCache("propietario:1", () -> "propietario-1");

        // Act
        cachedServiceProxy.evictPattern("mascota:*");

        // Assert - Mascotas deben ejecutar operación, propietario no
        Supplier<String> operationMascota = mock(Supplier.class);
        when(operationMascota.get()).thenReturn("new-mascota");
        cachedServiceProxy.executeWithCache("mascota:1", operationMascota);
        verify(operationMascota, times(1)).get();

        Supplier<String> operationPropietario = mock(Supplier.class);
        String result = cachedServiceProxy.executeWithCache("propietario:1", operationPropietario);
        assertEquals("propietario-1", result);
        verify(operationPropietario, never()).get();
    }

    @Test
    @DisplayName("Debe limpiar todo el caché con clear")
    void debeLimpiarTodoElCache() {
        // Arrange
        cachedServiceProxy.executeWithCache("key1", () -> "value1");
        cachedServiceProxy.executeWithCache("key2", () -> "value2");
        cachedServiceProxy.executeWithCache("key3", () -> "value3");

        // Act
        cachedServiceProxy.clear();

        // Assert - Todas las operaciones deben ejecutarse de nuevo
        CachedServiceProxy.CacheStats stats = cachedServiceProxy.getStats();
        assertEquals(0, stats.getTotalEntries());
    }

    @Test
    @DisplayName("Debe retornar estadísticas correctas del caché")
    void debeRetornarEstadisticasCorrectasDelCache() {
        // Arrange
        cachedServiceProxy.executeWithCache("key1", () -> "value1");
        cachedServiceProxy.executeWithCache("key2", () -> "value2");

        // Act
        CachedServiceProxy.CacheStats stats = cachedServiceProxy.getStats();

        // Assert
        assertEquals(2, stats.getTotalEntries());
        assertEquals(2, stats.getValidEntries());
        assertEquals(0, stats.getExpiredEntries());
    }

    @Test
    @DisplayName("Debe contar entradas expiradas en estadísticas")
    void debeContarEntradasExpiradasEnEstadisticas() throws InterruptedException {
        // Arrange
        long shortTTL = 50L; // 50ms
        cachedServiceProxy.executeWithCache("key1", () -> "value1", shortTTL);
        cachedServiceProxy.executeWithCache("key2", () -> "value2", 60000L); // No expira

        // Esperar que expire key1
        // SonarQube: Thread.sleep necesario para probar expiración de caché
        Thread.sleep(100); // NOSONAR

        // Act
        CachedServiceProxy.CacheStats stats = cachedServiceProxy.getStats();

        // Assert
        assertEquals(2, stats.getTotalEntries());
        assertEquals(1, stats.getValidEntries());
        assertEquals(1, stats.getExpiredEntries());
    }

    @Test
    @DisplayName("Debe limpiar solo entradas expiradas con cleanExpired")
    void debeEliminarSoloEntradasExpiradas() throws InterruptedException {
        // Arrange
        long shortTTL = 50L;
        cachedServiceProxy.executeWithCache("expired1", () -> "value1", shortTTL);
        cachedServiceProxy.executeWithCache("expired2", () -> "value2", shortTTL);
        cachedServiceProxy.executeWithCache("valid", () -> "value3", 60000L);

        // Esperar que expiren
        // SonarQube: Thread.sleep necesario para probar expiración de caché
        Thread.sleep(100); // NOSONAR

        // Act
        cachedServiceProxy.cleanExpired();

        // Assert
        CachedServiceProxy.CacheStats stats = cachedServiceProxy.getStats();
        assertEquals(1, stats.getTotalEntries());
        assertEquals(1, stats.getValidEntries());
    }

    @Test
    @DisplayName("Debe soportar diferentes tipos de objetos en caché")
    void debeSoportarDiferentesTiposDeObjetos() {
        // Arrange & Act
        String stringValue = cachedServiceProxy.executeWithCache("string-key", () -> "texto");
        Integer intValue = cachedServiceProxy.executeWithCache("int-key", () -> 42);
        Boolean boolValue = cachedServiceProxy.executeWithCache("bool-key", () -> true);

        // Assert
        assertEquals("texto", stringValue);
        assertEquals(42, intValue);
        assertTrue(boolValue);
    }

    @Test
    @DisplayName("Debe manejar operaciones que retornan null")
    void debeManejarOperacionesQueRetornanNull() {
        // Arrange
        String key = "null-key";
        Supplier<String> operation = () -> null;

        // Act
        String result = cachedServiceProxy.executeWithCache(key, operation);

        // Assert
        assertNull(result);
        // Verificar que se guardó en caché
        String cachedResult = cachedServiceProxy.executeWithCache(key, () -> "not-null");
        assertNull(cachedResult);
    }

    @Test
    @DisplayName("executeAndCache debe usar TTL por defecto cuando no se especifica")
    void executeAndCacheDebeUsarTTLPorDefecto() {
        // Arrange
        String key = "test-key";
        String value = "test-value";

        // Act
        String result = cachedServiceProxy.executeAndCache(key, () -> value);

        // Assert
        assertEquals(value, result);
        verify(configurationManager, atLeastOnce())
                .getConfigurationAsInteger("cache.ttl.default.seconds", 300);
    }

    @Test
    @DisplayName("evictPattern sin wildcard debe comportarse como evict simple")
    void evictPatternSinWildcardDebeComportarseComoEvictSimple() {
        // Arrange
        cachedServiceProxy.executeWithCache("exact-key", () -> "value");

        // Act
        cachedServiceProxy.evictPattern("exact-key");

        // Assert - Debe ejecutar operación de nuevo
        Supplier<String> operation = mock(Supplier.class);
        when(operation.get()).thenReturn("new-value");
        cachedServiceProxy.executeWithCache("exact-key", operation);
        verify(operation, times(1)).get();
    }
}
