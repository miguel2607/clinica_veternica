package com.veterinaria.clinica_veternica.patterns.creational.builder;

import com.veterinaria.clinica_veternica.patterns.creational.builder.ReporteBuilder.FormatoReporte;
import com.veterinaria.clinica_veternica.patterns.creational.builder.ReporteBuilder.Reporte;
import com.veterinaria.clinica_veternica.patterns.creational.builder.ReporteBuilder.TipoReporte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el patrón Builder - ReporteBuilder
 */
class ReporteBuilderTest {

    private ReporteBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new ReporteBuilder();
    }

    @Test
    @DisplayName("Builder - Debe construir reporte básico exitosamente")
    void debeConstruirReporteBasico() {
        Reporte reporte = builder
                .tipoReporte(TipoReporte.CITAS)
                .build();

        assertNotNull(reporte);
        assertEquals(TipoReporte.CITAS, reporte.getTipoReporte());
        assertEquals(FormatoReporte.PDF, reporte.getFormato()); // Formato por defecto
        assertNotNull(reporte.getTitulo());
        assertTrue(reporte.getTitulo().contains("CITAS"));
    }

    @Test
    @DisplayName("Builder - Debe establecer tipo de reporte")
    void debeEstablecerTipoReporte() {
        Reporte reporte = builder
                .tipoReporte(TipoReporte.FACTURACION)
                .build();

        assertEquals(TipoReporte.FACTURACION, reporte.getTipoReporte());
    }

    @Test
    @DisplayName("Builder - Debe establecer rango de fechas")
    void debeEstablecerRangoFechas() {
        LocalDate fechaInicio = LocalDate.now().minusDays(30);
        LocalDate fechaFin = LocalDate.now();

        Reporte reporte = builder
                .tipoReporte(TipoReporte.CITAS)
                .conRangoFechas(fechaInicio, fechaFin)
                .build();

        assertEquals(fechaInicio, reporte.getFechaInicio());
        assertEquals(fechaFin, reporte.getFechaFin());
    }

    @Test
    @DisplayName("Builder - Debe establecer rango de fecha y hora")
    void debeEstablecerRangoFechaHora() {
        LocalDateTime fechaHoraInicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fechaHoraFin = LocalDateTime.now();

        Reporte reporte = builder
                .tipoReporte(TipoReporte.CITAS)
                .conRangoFechaHora(fechaHoraInicio, fechaHoraFin)
                .build();

        assertEquals(fechaHoraInicio, reporte.getFechaHoraInicio());
        assertEquals(fechaHoraFin, reporte.getFechaHoraFin());
    }

    @Test
    @DisplayName("Builder - Debe lanzar excepción si fecha inicio es posterior a fecha fin")
    void debeLanzarExcepcionSiFechaInicioPosteriorAFechaFin() {
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = LocalDate.now().minusDays(1);

        // La excepción se lanza inmediatamente al llamar conRangoFechas
        builder.tipoReporte(TipoReporte.CITAS);
        assertThrows(IllegalArgumentException.class, 
                () -> builder.conRangoFechas(fechaInicio, fechaFin));
    }

    @Test
    @DisplayName("Builder - Debe agregar filtros personalizados")
    void debeAgregarFiltrosPersonalizados() {
        Reporte reporte = builder
                .tipoReporte(TipoReporte.CITAS)
                .conFiltro("veterinario", 1L)
                .conFiltro("estado", "ATENDIDA")
                .build();

        assertNotNull(reporte.getFiltros());
        assertEquals(1L, reporte.getFiltros().get("veterinario"));
        assertEquals("ATENDIDA", reporte.getFiltros().get("estado"));
    }

    @Test
    @DisplayName("Builder - Debe lanzar excepción con nombre de filtro vacío")
    void debeLanzarExcepcionConNombreFiltroVacio() {
        assertThrows(IllegalArgumentException.class, 
                () -> builder.conFiltro("", "valor"));
    }

    @Test
    @DisplayName("Builder - Debe establecer formato del reporte")
    void debeEstablecerFormatoReporte() {
        Reporte reporte = builder
                .tipoReporte(TipoReporte.CITAS)
                .conFormato(FormatoReporte.EXCEL)
                .build();

        assertEquals(FormatoReporte.EXCEL, reporte.getFormato());
        assertNotNull(reporte.getImplementor());
    }

    @Test
    @DisplayName("Builder - Debe establecer título personalizado")
    void debeEstablecerTituloPersonalizado() {
        String titulo = "Reporte Personalizado de Citas";
        Reporte reporte = builder
                .tipoReporte(TipoReporte.CITAS)
                .conTitulo(titulo)
                .build();

        assertEquals(titulo, reporte.getTitulo());
    }

    @Test
    @DisplayName("Builder - Debe generar título por defecto")
    void debeGenerarTituloPorDefecto() {
        LocalDate fechaInicio = LocalDate.now().minusDays(7);
        LocalDate fechaFin = LocalDate.now();

        Reporte reporte = builder
                .tipoReporte(TipoReporte.CITAS)
                .conRangoFechas(fechaInicio, fechaFin)
                .build();

        assertNotNull(reporte.getTitulo());
        assertTrue(reporte.getTitulo().contains("CITAS"));
        assertTrue(reporte.getTitulo().contains(fechaInicio.toString()));
        assertTrue(reporte.getTitulo().contains(fechaFin.toString()));
    }

    @Test
    @DisplayName("Builder - Debe configurar opciones de inclusión")
    void debeConfigurarOpcionesInclusion() {
        Reporte reporte = builder
                .tipoReporte(TipoReporte.CITAS)
                .incluirGraficos(true)
                .incluirDetalles(false)
                .incluirResumen(true)
                .build();

        assertTrue(reporte.isIncluirGraficos());
        assertFalse(reporte.isIncluirDetalles());
        assertTrue(reporte.isIncluirResumen());
    }

    @Test
    @DisplayName("Builder - Debe establecer columnas")
    void debeEstablecerColumnas() {
        List<String> columnas = Arrays.asList("fecha", "veterinario", "mascota", "estado");
        Reporte reporte = builder
                .tipoReporte(TipoReporte.CITAS)
                .conColumnas(columnas)
                .build();

        assertNotNull(reporte.getColumnas());
        assertEquals(4, reporte.getColumnas().size());
        assertTrue(reporte.getColumnas().contains("fecha"));
    }

    @Test
    @DisplayName("Builder - Debe agregar columnas individualmente")
    void debeAgregarColumnasIndividualmente() {
        Reporte reporte = builder
                .tipoReporte(TipoReporte.CITAS)
                .agregarColumna("fecha")
                .agregarColumna("veterinario")
                .agregarColumna("mascota")
                .build();

        assertEquals(3, reporte.getColumnas().size());
        assertTrue(reporte.getColumnas().contains("fecha"));
    }

    @Test
    @DisplayName("Builder - Debe establecer ordenamiento")
    void debeEstablecerOrdenamiento() {
        Reporte reporte = builder
                .tipoReporte(TipoReporte.CITAS)
                .conOrdenamiento("fecha", false)
                .build();

        assertEquals("fecha", reporte.getOrdenamiento());
        assertFalse(reporte.isOrdenAscendente());
    }

    @Test
    @DisplayName("Builder - Debe lanzar excepción sin tipo de reporte")
    void debeLanzarExcepcionSinTipoReporte() {
        LocalDate fechaInicio = LocalDate.now().minusDays(7);
        LocalDate fechaFin = LocalDate.now();
        ReporteBuilder builderIncompleto = builder
                .conRangoFechas(fechaInicio, fechaFin);
        assertThrows(IllegalStateException.class, builderIncompleto::build);
    }

    @Test
    @DisplayName("Builder - Debe generar reporte con implementador")
    void debeGenerarReporteConImplementador() {
        Reporte reporte = builder
                .tipoReporte(TipoReporte.CITAS)
                .conFormato(FormatoReporte.JSON)
                .build();

        assertNotNull(reporte.getImplementor());
        byte[] resultado = reporte.generar();
        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Builder - Debe usar diferentes implementadores según formato")
    void debeUsarDiferentesImplementadoresSegunFormato() {
        Reporte reportePDF = builder
                .tipoReporte(TipoReporte.CITAS)
                .conFormato(FormatoReporte.PDF)
                .build();

        Reporte reporteExcel = new ReporteBuilder()
                .tipoReporte(TipoReporte.CITAS)
                .conFormato(FormatoReporte.EXCEL)
                .build();

        Reporte reporteJSON = new ReporteBuilder()
                .tipoReporte(TipoReporte.CITAS)
                .conFormato(FormatoReporte.JSON)
                .build();

        assertNotNull(reportePDF.getImplementor());
        assertNotNull(reporteExcel.getImplementor());
        assertNotNull(reporteJSON.getImplementor());

        // Verificar que cada uno genera un reporte
        assertNotNull(reportePDF.generar());
        assertNotNull(reporteExcel.generar());
        assertNotNull(reporteJSON.generar());
    }

    @Test
    @DisplayName("Builder - Debe construir reporte completo con todas las opciones")
    void debeConstruirReporteCompleto() {
        LocalDate fechaInicio = LocalDate.now().minusDays(30);
        LocalDate fechaFin = LocalDate.now();
        List<String> columnas = Arrays.asList("fecha", "veterinario", "mascota");

        Reporte reporte = builder
                .tipoReporte(TipoReporte.CITAS)
                .conRangoFechas(fechaInicio, fechaFin)
                .conFiltro("veterinario", 1L)
                .conFiltro("estado", "ATENDIDA")
                .conFormato(FormatoReporte.EXCEL)
                .conTitulo("Reporte Completo de Citas")
                .incluirGraficos(true)
                .incluirDetalles(true)
                .incluirResumen(true)
                .conColumnas(columnas)
                .conOrdenamiento("fecha", true)
                .build();

        assertNotNull(reporte);
        assertEquals(TipoReporte.CITAS, reporte.getTipoReporte());
        assertEquals(fechaInicio, reporte.getFechaInicio());
        assertEquals(fechaFin, reporte.getFechaFin());
        assertEquals(2, reporte.getFiltros().size());
        assertEquals(FormatoReporte.EXCEL, reporte.getFormato());
        assertEquals("Reporte Completo de Citas", reporte.getTitulo());
        assertTrue(reporte.isIncluirGraficos());
        assertEquals(3, reporte.getColumnas().size());
        assertEquals("fecha", reporte.getOrdenamiento());
        assertTrue(reporte.isOrdenAscendente());
    }
}

