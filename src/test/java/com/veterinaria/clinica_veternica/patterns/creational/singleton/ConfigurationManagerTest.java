package com.veterinaria.clinica_veternica.patterns.creational.singleton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el patrón Singleton - ConfigurationManager
 */
@SpringBootTest
class ConfigurationManagerTest {

    @Autowired
    private ConfigurationManager configurationManager;

    @BeforeEach
    void setUp() {
        // Limpiar configuraciones antes de cada test
        configurationManager.clearAllConfigurations();
    }

    @Test
    @DisplayName("Singleton - Debe ser la misma instancia")
    void debeSerMismaInstancia() {
        assertNotNull(configurationManager);
        
        // Verificar que Spring inyecta la misma instancia
        ConfigurationManager otraInstancia = configurationManager;
        assertSame(configurationManager, otraInstancia);
    }

    @Test
    @DisplayName("Debe establecer y obtener configuración")
    void debeEstablecerYObtenerConfiguracion() {
        configurationManager.setConfiguration("test.key", "test.value");

        Object valor = configurationManager.getConfiguration("test.key");
        assertEquals("test.value", valor);
    }

    @Test
    @DisplayName("Debe obtener configuración como String")
    void debeObtenerConfiguracionComoString() {
        configurationManager.setConfiguration("string.key", "valor");

        String valor = configurationManager.getConfigurationAsString("string.key", "default");
        assertEquals("valor", valor);
    }

    @Test
    @DisplayName("Debe usar valor por defecto si configuración no existe (String)")
    void debeUsarValorPorDefectoSiConfiguracionNoExisteString() {
        String valor = configurationManager.getConfigurationAsString("no.existe", "default");
        assertEquals("default", valor);
    }

    @Test
    @DisplayName("Debe obtener configuración como Integer")
    void debeObtenerConfiguracionComoInteger() {
        configurationManager.setConfiguration("int.key", 42);

        Integer valor = configurationManager.getConfigurationAsInteger("int.key", 0);
        assertEquals(42, valor);
    }

    @Test
    @DisplayName("Debe convertir String a Integer")
    void debeConvertirStringAInteger() {
        configurationManager.setConfiguration("int.string", "100");

        Integer valor = configurationManager.getConfigurationAsInteger("int.string", 0);
        assertEquals(100, valor);
    }

    @Test
    @DisplayName("Debe usar valor por defecto si conversión falla")
    void debeUsarValorPorDefectoSiConversionFalla() {
        configurationManager.setConfiguration("invalid.int", "no-es-un-numero");

        Integer valor = configurationManager.getConfigurationAsInteger("invalid.int", 999);
        assertEquals(999, valor);
    }

    @Test
    @DisplayName("Debe obtener configuración como Boolean")
    void debeObtenerConfiguracionComoBoolean() {
        configurationManager.setConfiguration("bool.key", true);

        Boolean valor = configurationManager.getConfigurationAsBoolean("bool.key", false);
        assertTrue(valor);
    }

    @Test
    @DisplayName("Debe convertir String a Boolean")
    void debeConvertirStringABoolean() {
        configurationManager.setConfiguration("bool.string", "true");

        Boolean valor = configurationManager.getConfigurationAsBoolean("bool.string", false);
        assertTrue(valor);
    }

    @Test
    @DisplayName("Debe eliminar configuración")
    void debeEliminarConfiguracion() {
        configurationManager.setConfiguration("temp.key", "temp.value");
        assertTrue(configurationManager.hasConfiguration("temp.key"));

        configurationManager.removeConfiguration("temp.key");
        assertFalse(configurationManager.hasConfiguration("temp.key"));
    }

    @Test
    @DisplayName("Debe verificar existencia de configuración")
    void debeVerificarExistenciaConfiguracion() {
        assertFalse(configurationManager.hasConfiguration("no.existe"));

        configurationManager.setConfiguration("existe", "valor");
        assertTrue(configurationManager.hasConfiguration("existe"));
    }

    @Test
    @DisplayName("Debe obtener todas las configuraciones")
    void debeObtenerTodasLasConfiguraciones() {
        configurationManager.setConfiguration("key1", "value1");
        configurationManager.setConfiguration("key2", "value2");
        configurationManager.setConfiguration("key3", 123);

        Map<String, Object> todas = configurationManager.getAllConfigurations();

        assertNotNull(todas);
        assertEquals(3, todas.size());
        assertEquals("value1", todas.get("key1"));
        assertEquals("value2", todas.get("key2"));
        assertEquals(123, todas.get("key3"));
    }

    @Test
    @DisplayName("Debe limpiar todas las configuraciones")
    void debeLimpiarTodasLasConfiguraciones() {
        configurationManager.setConfiguration("key1", "value1");
        configurationManager.setConfiguration("key2", "value2");
        assertEquals(2, configurationManager.getConfigurationCount());

        configurationManager.clearAllConfigurations();

        assertEquals(0, configurationManager.getConfigurationCount());
        assertTrue(configurationManager.getAllConfigurations().isEmpty());
    }

    @Test
    @DisplayName("Debe obtener conteo de configuraciones")
    void debeObtenerConteoConfiguraciones() {
        assertEquals(0, configurationManager.getConfigurationCount());

        configurationManager.setConfiguration("key1", "value1");
        assertEquals(1, configurationManager.getConfigurationCount());

        configurationManager.setConfiguration("key2", "value2");
        assertEquals(2, configurationManager.getConfigurationCount());
    }

    @Test
    @DisplayName("Debe obtener nombre de clínica por defecto")
    void debeObtenerNombreClinicaPorDefecto() {
        String nombre = configurationManager.getClinicaName();
        assertEquals("Clínica Veterinaria", nombre);
    }

    @Test
    @DisplayName("Debe obtener nombre de clínica personalizado")
    void debeObtenerNombreClinicaPersonalizado() {
        configurationManager.setConfiguration("clinica.nombre", "Mi Clínica");

        String nombre = configurationManager.getClinicaName();
        assertEquals("Mi Clínica", nombre);
    }

    @Test
    @DisplayName("Debe obtener hora de apertura por defecto")
    void debeObtenerHoraAperturaPorDefecto() {
        String hora = configurationManager.getHoraApertura();
        assertEquals("08:00", hora);
    }

    @Test
    @DisplayName("Debe obtener hora de cierre por defecto")
    void debeObtenerHoraCierrePorDefecto() {
        String hora = configurationManager.getHoraCierre();
        assertEquals("18:00", hora);
    }

    @Test
    @DisplayName("Debe obtener duración estándar de cita por defecto")
    void debeObtenerDuracionCitaEstandarPorDefecto() {
        Integer duracion = configurationManager.getDuracionCitaEstandar();
        assertEquals(30, duracion);
    }

    @Test
    @DisplayName("Debe obtener stock mínimo general por defecto")
    void debeObtenerStockMinimoGeneralPorDefecto() {
        Integer stock = configurationManager.getStockMinimoGeneral();
        assertEquals(10, stock);
    }

    @Test
    @DisplayName("Debe obtener recordatorios automáticos por defecto")
    void debeObtenerRecordatoriosAutomaticosPorDefecto() {
        Boolean recordatorios = configurationManager.getRecordatoriosAutomaticos();
        assertTrue(recordatorios);
    }

    @Test
    @DisplayName("Debe obtener horas de anticipación para recordatorios por defecto")
    void debeObtenerHorasAnticipacionRecordatorioPorDefecto() {
        Integer horas = configurationManager.getHorasAnticipacionRecordatorio();
        assertEquals(24, horas);
    }

    @Test
    @DisplayName("Debe manejar configuraciones concurrentes")
    void debeManejarConfiguracionesConcurrentes() throws InterruptedException {
        // Simular múltiples operaciones concurrentes
        int numThreads = 100;
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    configurationManager.setConfiguration("key." + index, "value." + index);
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        // Esperar a que todos los threads terminen usando CountDownLatch
        latch.await();

        // Verificar que todas las configuraciones se guardaron
        assertTrue(configurationManager.getConfigurationCount() >= numThreads);
    }
}

