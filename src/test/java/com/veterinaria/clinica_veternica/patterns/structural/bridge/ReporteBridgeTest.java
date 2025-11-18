package com.veterinaria.clinica_veternica.patterns.structural.bridge;

import com.veterinaria.clinica_veternica.repository.CitaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests para el patrón Bridge - ReporteAbstraction
 */
@ExtendWith(MockitoExtension.class)
class ReporteBridgeTest {

    @Mock
    private CitaRepository citaRepository;

    private ReporteCitasAbstraction reporteCitas;
    private TestReporteImplementor implementorTest;

    @BeforeEach
    void setUp() {
        implementorTest = new TestReporteImplementor();
        reporteCitas = new ReporteCitasAbstraction(implementorTest, citaRepository);
    }

    @Test
    @DisplayName("Debe generar reporte usando la implementación correcta")
    void debeGenerarReporteUsandoImplementacionCorrecta() {
        reporteCitas.setRangoFechas(LocalDate.now().minusDays(7), LocalDate.now());

        byte[] resultado = reporteCitas.generar();

        assertNotNull(resultado);
        assertTrue(implementorTest.fueLlamado, "El implementor debe ser llamado");
        assertNotNull(implementorTest.datosRecibidos);
        assertNotNull(implementorTest.tituloRecibido);
    }

    @Test
    @DisplayName("Debe obtener tipo MIME del implementor")
    void debeObtenerTipoMimeDelImplementor() {
        String tipoMime = reporteCitas.getTipoMime();

        assertEquals("application/json", tipoMime);
    }

    @Test
    @DisplayName("Debe obtener extensión del implementor")
    void debeObtenerExtensionDelImplementor() {
        String extension = reporteCitas.getExtension();

        assertEquals("json", extension);
    }

    @Test
    @DisplayName("Debe cambiar de implementación sin modificar la abstracción")
    void debeCambiarImplementacionSinModificarAbstraccion() {
        // Crear con implementación JSON
        ReporteCitasAbstraction reporteJSON = new ReporteCitasAbstraction(
                new TestReporteImplementor("application/json", "json"), 
                citaRepository);

        // Cambiar a implementación PDF
        ReporteCitasAbstraction reportePDF = new ReporteCitasAbstraction(
                new TestReporteImplementor("application/pdf", "pdf"), 
                citaRepository);

        assertEquals("application/json", reporteJSON.getTipoMime());
        assertEquals("json", reporteJSON.getExtension());

        assertEquals("application/pdf", reportePDF.getTipoMime());
        assertEquals("pdf", reportePDF.getExtension());
    }

    @Test
    @DisplayName("Debe recopilar datos correctamente")
    void debeRecopilarDatosCorrectamente() {
        LocalDate fechaInicio = LocalDate.now().minusDays(7);
        LocalDate fechaFin = LocalDate.now();
        reporteCitas.setRangoFechas(fechaInicio, fechaFin);

        reporteCitas.generar();

        Map<String, Object> datos = implementorTest.datosRecibidos;
        assertNotNull(datos);
        assertTrue(datos.containsKey("totalCitas"));
        assertTrue(datos.containsKey("fechaInicio"));
        assertTrue(datos.containsKey("fechaFin"));
    }

    @Test
    @DisplayName("Debe generar título con rango de fechas")
    void debeGenerarTituloConRangoDeFechas() {
        LocalDate fechaInicio = LocalDate.of(2025, 1, 1);
        LocalDate fechaFin = LocalDate.of(2025, 1, 31);
        reporteCitas.setRangoFechas(fechaInicio, fechaFin);

        reporteCitas.generar();

        String titulo = implementorTest.tituloRecibido;
        assertNotNull(titulo);
        assertTrue(titulo.contains("Reporte de Citas"));
        assertTrue(titulo.contains("2025-01-01"));
        assertTrue(titulo.contains("2025-01-31"));
    }

    // Implementación de prueba para el Bridge
    private static class TestReporteImplementor implements ReporteImplementor {
        boolean fueLlamado = false;
        Map<String, Object> datosRecibidos;
        String tituloRecibido;
        private final String tipoMime;
        private final String extension;

        public TestReporteImplementor() {
            this("application/json", "json");
        }

        public TestReporteImplementor(String tipoMime, String extension) {
            this.tipoMime = tipoMime;
            this.extension = extension;
        }

        @Override
        public byte[] generarReporte(Map<String, Object> datos, String titulo) {
            fueLlamado = true;
            datosRecibidos = new HashMap<>(datos);
            tituloRecibido = titulo;
            return "Reporte generado".getBytes();
        }

        @Override
        public String getTipoMime() {
            return tipoMime;
        }

        @Override
        public String getExtension() {
            return extension;
        }
    }
}

