package com.veterinaria.clinica_veternica.patterns.structural.facade;

import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ReporteCitasDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ReporteInventarioDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ReporteVeterinariosDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.InventarioResponseDTO;
import com.veterinaria.clinica_veternica.patterns.creational.builder.ReporteBuilder;
import com.veterinaria.clinica_veternica.service.interfaces.ICitaService;
import com.veterinaria.clinica_veternica.service.interfaces.IInventarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para el servicio ReporteFacadeService.
 *
 * Verifica que el servicio facade de reportes:
 * - Genere reportes de citas por rango de fechas
 * - Genere reportes de inventario con estadísticas
 * - Genere reportes de atenciones por veterinario
 * - Genere reportes usando el patrón Builder
 * - Calcule correctamente valores totales y estadísticas
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-19
 */
@ExtendWith(MockitoExtension.class)
class ReporteFacadeServiceTest {

    @Mock
    private ICitaService citaService;

    @Mock
    private IInventarioService inventarioService;

    @InjectMocks
    private ReporteFacadeService reporteFacadeService;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<CitaResponseDTO> citasMock;
    private List<InventarioResponseDTO> inventariosMock;

    @BeforeEach
    void setUp() {
        fechaInicio = LocalDate.of(2025, 1, 1);
        fechaFin = LocalDate.of(2025, 1, 31);

        // Preparar datos de prueba para citas
        citasMock = crearCitasMock();
        inventariosMock = crearInventariosMock();
    }

    // ===================================================================
    // PRUEBAS PARA: generarReporteCitas
    // ===================================================================

    @Test
    @DisplayName("Debe generar reporte de citas con fechas válidas")
    void debeGenerarReporteCitasConFechasValidas() {
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citasMock);

        ReporteCitasDTO resultado = reporteFacadeService.generarReporteCitas(fechaInicio, fechaFin);

        assertNotNull(resultado, "El reporte no debe ser nulo");
        assertEquals(fechaInicio, resultado.getFechaInicio(), "La fecha de inicio debe coincidir");
        assertEquals(fechaFin, resultado.getFechaFin(), "La fecha de fin debe coincidir");
        assertEquals(3, resultado.getTotalCitas(), "Debe haber 3 citas totales");
        assertNotNull(resultado.getCitas(), "La lista de citas no debe ser nula");
        assertEquals(3, resultado.getCitas().size(), "La lista debe tener 3 citas");

        verify(citaService, times(1)).listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class));
        verifyNoMoreInteractions(citaService);
    }

    @Test
    @DisplayName("Debe contar correctamente citas por estado")
    void debeContarCorrectamenteCitasPorEstado() {
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citasMock);

        ReporteCitasDTO resultado = reporteFacadeService.generarReporteCitas(fechaInicio, fechaFin);

        assertNotNull(resultado, "El reporte no debe ser nulo");
        assertEquals(1L, resultado.getCitasAtendidas(), "Debe haber 1 cita atendida");
        assertEquals(1L, resultado.getCitasProgramadas(), "Debe haber 1 cita programada");
        assertEquals(1L, resultado.getCitasCanceladas(), "Debe haber 1 cita cancelada");
    }

    @Test
    @DisplayName("Debe retornar reporte vacío cuando no hay citas")
    void debeRetornarReporteVacioSinCitas() {
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        ReporteCitasDTO resultado = reporteFacadeService.generarReporteCitas(fechaInicio, fechaFin);

        assertNotNull(resultado, "El reporte no debe ser nulo");
        assertEquals(0, resultado.getTotalCitas(), "No debe haber citas");
        assertEquals(0L, resultado.getCitasAtendidas(), "No debe haber citas atendidas");
        assertEquals(0L, resultado.getCitasProgramadas(), "No debe haber citas programadas");
        assertEquals(0L, resultado.getCitasCanceladas(), "No debe haber citas canceladas");
        assertTrue(resultado.getCitas().isEmpty(), "La lista de citas debe estar vacía");

        verify(citaService, times(1)).listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Debe convertir correctamente las fechas a LocalDateTime")
    void debeConvertirFechasALocalDateTime() {
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citasMock);

        reporteFacadeService.generarReporteCitas(fechaInicio, fechaFin);

        LocalDateTime inicioEsperado = fechaInicio.atStartOfDay();
        LocalDateTime finEsperado = fechaFin.atTime(23, 59, 59);

        verify(citaService).listarPorRangoFechas(inicioEsperado, finEsperado);
    }

    @Test
    @DisplayName("Debe manejar múltiples citas con diferentes estados")
    void debeManejartMultiplesCitasConDiferentesEstados() {
        List<CitaResponseDTO> citasVariadas = List.of(
                CitaResponseDTO.builder().idCita(1L).estado("ATENDIDA").build(),
                CitaResponseDTO.builder().idCita(2L).estado("ATENDIDA").build(),
                CitaResponseDTO.builder().idCita(3L).estado("ATENDIDA").build(),
                CitaResponseDTO.builder().idCita(4L).estado("PROGRAMADA").build(),
                CitaResponseDTO.builder().idCita(5L).estado("PROGRAMADA").build(),
                CitaResponseDTO.builder().idCita(6L).estado("CANCELADA").build()
        );

        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citasVariadas);

        ReporteCitasDTO resultado = reporteFacadeService.generarReporteCitas(fechaInicio, fechaFin);

        assertEquals(6, resultado.getTotalCitas(), "Debe haber 6 citas totales");
        assertEquals(3L, resultado.getCitasAtendidas(), "Debe haber 3 citas atendidas");
        assertEquals(2L, resultado.getCitasProgramadas(), "Debe haber 2 citas programadas");
        assertEquals(1L, resultado.getCitasCanceladas(), "Debe haber 1 cita cancelada");
    }

    // ===================================================================
    // PRUEBAS PARA: generarReporteInventario
    // ===================================================================

    @Test
    @DisplayName("Debe generar reporte de inventario completo")
    void debeGenerarReporteInventarioCompleto() {
        when(inventarioService.listarTodos()).thenReturn(inventariosMock);
        when(inventarioService.listarConStockBajo()).thenReturn(inventariosMock.subList(0, 1));

        ReporteInventarioDTO resultado = reporteFacadeService.generarReporteInventario();

        assertNotNull(resultado, "El reporte no debe ser nulo");
        assertNotNull(resultado.getInventarios(), "La lista de inventarios no debe ser nula");
        assertEquals(3, resultado.getTotalItems(), "Debe haber 3 items totales");
        assertEquals(1, resultado.getTotalStockBajo(), "Debe haber 1 item con stock bajo");
        assertNotNull(resultado.getValorTotalInventario(), "El valor total no debe ser nulo");

        verify(inventarioService, times(1)).listarTodos();
        verify(inventarioService, times(1)).listarConStockBajo();
        verifyNoMoreInteractions(inventarioService);
    }

    @Test
    @DisplayName("Debe calcular correctamente el valor total del inventario")
    void debeCalcularCorrectamenteValorTotalInventario() {
        when(inventarioService.listarTodos()).thenReturn(inventariosMock);
        when(inventarioService.listarConStockBajo()).thenReturn(Collections.emptyList());

        ReporteInventarioDTO resultado = reporteFacadeService.generarReporteInventario();

        BigDecimal valorEsperado = new BigDecimal("350.00");
        assertEquals(0, valorEsperado.compareTo(resultado.getValorTotalInventario()),
                "El valor total debe ser 350.00");
    }

    @Test
    @DisplayName("Debe retornar reporte vacío cuando no hay inventario")
    void debeRetornarReporteVacioSinInventario() {
        when(inventarioService.listarTodos()).thenReturn(Collections.emptyList());
        when(inventarioService.listarConStockBajo()).thenReturn(Collections.emptyList());

        ReporteInventarioDTO resultado = reporteFacadeService.generarReporteInventario();

        assertNotNull(resultado, "El reporte no debe ser nulo");
        assertEquals(0, resultado.getTotalItems(), "No debe haber items");
        assertEquals(0, resultado.getTotalStockBajo(), "No debe haber items con stock bajo");
        assertEquals(0, resultado.getValorTotalInventario().compareTo(BigDecimal.ZERO),
                "El valor total debe ser cero");
        assertTrue(resultado.getInventarios().isEmpty(), "La lista de inventarios debe estar vacía");

        verify(inventarioService, times(1)).listarTodos();
        verify(inventarioService, times(1)).listarConStockBajo();
    }

    @Test
    @DisplayName("Debe manejar valores null en inventario")
    void debeManejartValoresNullEnInventario() {
        List<InventarioResponseDTO> inventariosConNull = List.of(
                InventarioResponseDTO.builder().idInventario(1L).nombreInsumo("Insumo 1").valorTotal(new BigDecimal("100.00")).build(),
                InventarioResponseDTO.builder().idInventario(2L).nombreInsumo("Insumo 2").valorTotal(null).build(),
                InventarioResponseDTO.builder().idInventario(3L).nombreInsumo("Insumo 3").valorTotal(new BigDecimal("50.00")).build()
        );

        when(inventarioService.listarTodos()).thenReturn(inventariosConNull);
        when(inventarioService.listarConStockBajo()).thenReturn(Collections.emptyList());

        ReporteInventarioDTO resultado = reporteFacadeService.generarReporteInventario();

        assertEquals(3, resultado.getTotalItems(), "Debe haber 3 items");
        BigDecimal valorEsperado = new BigDecimal("150.00");
        assertEquals(0, valorEsperado.compareTo(resultado.getValorTotalInventario()),
                "El valor total debe ser 150.00 (ignorando null)");
    }

    @Test
    @DisplayName("Debe incluir items con stock bajo en el reporte")
    void debeIncluirItemsConStockBajo() {
        List<InventarioResponseDTO> conStockBajo = List.of(
                inventariosMock.get(0),
                inventariosMock.get(1)
        );

        when(inventarioService.listarTodos()).thenReturn(inventariosMock);
        when(inventarioService.listarConStockBajo()).thenReturn(conStockBajo);

        ReporteInventarioDTO resultado = reporteFacadeService.generarReporteInventario();

        assertEquals(3, resultado.getTotalItems(), "Debe haber 3 items totales");
        assertEquals(2, resultado.getTotalStockBajo(), "Debe haber 2 items con stock bajo");
        assertEquals(2, resultado.getStockBajo().size(), "La lista de stock bajo debe tener 2 items");

        verify(inventarioService).listarConStockBajo();
    }

    // ===================================================================
    // PRUEBAS PARA: generarReporteVeterinarios
    // ===================================================================

    @Test
    @DisplayName("Debe generar reporte de veterinarios con estadísticas")
    void debeGenerarReporteVeterinarios() {
        List<CitaResponseDTO> citasConVeterinario = crearCitasConVeterinarioMock();

        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citasConVeterinario);

        ReporteVeterinariosDTO resultado = reporteFacadeService.generarReporteVeterinarios(fechaInicio, fechaFin);

        assertNotNull(resultado, "El reporte no debe ser nulo");
        assertEquals(fechaInicio, resultado.getFechaInicio(), "La fecha de inicio debe coincidir");
        assertEquals(fechaFin, resultado.getFechaFin(), "La fecha de fin debe coincidir");
        assertNotNull(resultado.getEstadisticasPorVeterinario(), "Las estadísticas no deben ser nulas");
        assertTrue(resultado.getEstadisticasPorVeterinario().size() > 0, "Debe haber al menos un veterinario");

        verify(citaService, times(1)).listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Debe calcular correctamente atenciones por veterinario")
    void debeCalcularAtencionesPorVeterinario() {
        List<CitaResponseDTO> citasConVeterinario = crearCitasConVeterinarioMock();

        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citasConVeterinario);

        ReporteVeterinariosDTO resultado = reporteFacadeService.generarReporteVeterinarios(fechaInicio, fechaFin);

        long totalAtenciones = resultado.getEstadisticasPorVeterinario().stream()
                .mapToLong(ReporteVeterinariosDTO.EstadisticaVeterinarioDTO::getTotalCitasAtendidas)
                .sum();

        assertEquals(resultado.getTotalAtenciones(), totalAtenciones, "El total de atenciones debe ser consistente");
    }

    @Test
    @DisplayName("Debe retornar reporte vacío sin veterinarios")
    void debeRetornarReporteVacioSinVeterinarios() {
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        ReporteVeterinariosDTO resultado = reporteFacadeService.generarReporteVeterinarios(fechaInicio, fechaFin);

        assertNotNull(resultado, "El reporte no debe ser nulo");
        assertEquals(fechaInicio, resultado.getFechaInicio(), "La fecha de inicio debe coincidir");
        assertEquals(fechaFin, resultado.getFechaFin(), "La fecha de fin debe coincidir");
        assertEquals(0, resultado.getEstadisticasPorVeterinario().size(), "No debe haber veterinarios");
        assertEquals(0L, resultado.getTotalAtenciones(), "El total de atenciones debe ser cero");

        verify(citaService, times(1)).listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Debe ignorar citas sin veterinario")
    void debeIgnorarCitasSinVeterinario() {
        List<CitaResponseDTO> citasConYSinVeterinario = List.of(
                CitaResponseDTO.builder()
                        .idCita(1L)
                        .estado("ATENDIDA")
                        .veterinario(null)
                        .build(),
                CitaResponseDTO.builder()
                        .idCita(2L)
                        .estado("ATENDIDA")
                        .veterinario(crearVeterinarioMock(1L, "Dr. Juan", "Cirugía"))
                        .build()
        );

        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citasConYSinVeterinario);

        ReporteVeterinariosDTO resultado = reporteFacadeService.generarReporteVeterinarios(fechaInicio, fechaFin);

        assertEquals(1, resultado.getEstadisticasPorVeterinario().size(),
                "Debe haber solo 1 veterinario (ignorando citas sin veterinario)");
    }

    // ===================================================================
    // PRUEBAS PARA: generarReporteCitasConBuilder
    // ===================================================================

    @Test
    @DisplayName("Debe generar reporte de citas con Builder en formato PDF")
    void debeGenerarReporteCitasConBuilderPDF() {
        ReporteBuilder.Reporte reporte = reporteFacadeService.generarReporteCitasConBuilder(
                fechaInicio,
                fechaFin,
                ReporteBuilder.FormatoReporte.PDF,
                true
        );

        assertNotNull(reporte, "El reporte no debe ser nulo");
        assertEquals(ReporteBuilder.TipoReporte.CITAS, reporte.getTipoReporte(), "El tipo debe ser CITAS");
        assertEquals(ReporteBuilder.FormatoReporte.PDF, reporte.getFormato(), "El formato debe ser PDF");
        assertTrue(reporte.isIncluirGraficos(), "Debe incluir gráficos");
        assertTrue(reporte.isIncluirResumen(), "Debe incluir resumen");
        assertTrue(reporte.isIncluirDetalles(), "Debe incluir detalles");
        assertNotNull(reporte.getTitulo(), "El título no debe ser nulo");
    }

    @Test
    @DisplayName("Debe generar reporte de citas con Builder en formato EXCEL")
    void debeGenerarReporteCitasConBuilderEXCEL() {
        ReporteBuilder.Reporte reporte = reporteFacadeService.generarReporteCitasConBuilder(
                fechaInicio,
                fechaFin,
                ReporteBuilder.FormatoReporte.EXCEL,
                false
        );

        assertNotNull(reporte, "El reporte no debe ser nulo");
        assertEquals(ReporteBuilder.FormatoReporte.EXCEL, reporte.getFormato(), "El formato debe ser EXCEL");
        assertFalse(reporte.isIncluirGraficos(), "No debe incluir gráficos");
    }

    @Test
    @DisplayName("Debe generar reporte de citas con Builder en formato JSON")
    void debeGenerarReporteCitasConBuilderJSON() {
        ReporteBuilder.Reporte reporte = reporteFacadeService.generarReporteCitasConBuilder(
                fechaInicio,
                fechaFin,
                ReporteBuilder.FormatoReporte.JSON,
                true
        );

        assertNotNull(reporte, "El reporte no debe ser nulo");
        assertEquals(ReporteBuilder.FormatoReporte.JSON, reporte.getFormato(), "El formato debe ser JSON");
    }

    @Test
    @DisplayName("Debe incluir columnas correctas en reporte de citas")
    void debeIncluirColumnasCorrectasEnReporteCitas() {
        ReporteBuilder.Reporte reporte = reporteFacadeService.generarReporteCitasConBuilder(
                fechaInicio,
                fechaFin,
                ReporteBuilder.FormatoReporte.PDF,
                true
        );

        assertNotNull(reporte.getColumnas(), "Las columnas no deben ser nulas");
        assertTrue(reporte.getColumnas().contains("Fecha"), "Debe incluir columna Fecha");
        assertTrue(reporte.getColumnas().contains("Hora"), "Debe incluir columna Hora");
        assertTrue(reporte.getColumnas().contains("Mascota"), "Debe incluir columna Mascota");
        assertTrue(reporte.getColumnas().contains("Veterinario"), "Debe incluir columna Veterinario");
        assertTrue(reporte.getColumnas().contains("Estado"), "Debe incluir columna Estado");
    }

    @Test
    @DisplayName("Debe establecer rango de fechas en reporte de citas")
    void debeEstablecerRangoFechasEnReporteCitas() {
        ReporteBuilder.Reporte reporte = reporteFacadeService.generarReporteCitasConBuilder(
                fechaInicio,
                fechaFin,
                ReporteBuilder.FormatoReporte.PDF,
                true
        );

        assertEquals(fechaInicio, reporte.getFechaInicio(), "La fecha de inicio debe coincidir");
        assertEquals(fechaFin, reporte.getFechaFin(), "La fecha de fin debe coincidir");
    }

    @Test
    @DisplayName("Debe generar título descriptivo para reporte de citas")
    void debeGenerarTituloDescriptivo() {
        ReporteBuilder.Reporte reporte = reporteFacadeService.generarReporteCitasConBuilder(
                fechaInicio,
                fechaFin,
                ReporteBuilder.FormatoReporte.PDF,
                true
        );

        assertNotNull(reporte.getTitulo(), "El título no debe ser nulo");
        assertTrue(reporte.getTitulo().contains("Reporte de Citas"), "El título debe contener 'Reporte de Citas'");
        assertTrue(reporte.getTitulo().contains(fechaInicio.toString()), "El título debe contener la fecha de inicio");
        assertTrue(reporte.getTitulo().contains(fechaFin.toString()), "El título debe contener la fecha de fin");
    }

    // ===================================================================
    // PRUEBAS PARA: generarReporteInventarioConBuilder
    // ===================================================================

    @Test
    @DisplayName("Debe generar reporte de inventario con Builder")
    void debeGenerarReporteInventarioConBuilder() {
        ReporteBuilder.Reporte reporte = reporteFacadeService.generarReporteInventarioConBuilder(
                ReporteBuilder.FormatoReporte.PDF,
                true
        );

        assertNotNull(reporte, "El reporte no debe ser nulo");
        assertEquals(ReporteBuilder.TipoReporte.INVENTARIO, reporte.getTipoReporte(), "El tipo debe ser INVENTARIO");
        assertEquals(ReporteBuilder.FormatoReporte.PDF, reporte.getFormato(), "El formato debe ser PDF");
        assertTrue(reporte.isIncluirGraficos(), "Debe incluir gráficos");
        assertTrue(reporte.isIncluirResumen(), "Debe incluir resumen");
    }

    @Test
    @DisplayName("Debe generar reporte de inventario sin gráficos")
    void debeGenerarReporteInventarioSinGraficos() {
        ReporteBuilder.Reporte reporte = reporteFacadeService.generarReporteInventarioConBuilder(
                ReporteBuilder.FormatoReporte.EXCEL,
                false
        );

        assertNotNull(reporte, "El reporte no debe ser nulo");
        assertFalse(reporte.isIncluirGraficos(), "No debe incluir gráficos");
        assertEquals(ReporteBuilder.FormatoReporte.EXCEL, reporte.getFormato(), "El formato debe ser EXCEL");
    }

    @Test
    @DisplayName("Debe incluir columnas correctas en reporte de inventario")
    void debeIncluirColumnasCorrectasEnReporteInventario() {
        ReporteBuilder.Reporte reporte = reporteFacadeService.generarReporteInventarioConBuilder(
                ReporteBuilder.FormatoReporte.PDF,
                true
        );

        assertNotNull(reporte.getColumnas(), "Las columnas no deben ser nulas");
        assertTrue(reporte.getColumnas().contains("Insumo"), "Debe incluir columna Insumo");
        assertTrue(reporte.getColumnas().contains("Cantidad"), "Debe incluir columna Cantidad");
        assertTrue(reporte.getColumnas().contains("Stock Mínimo"), "Debe incluir columna Stock Mínimo");
        assertTrue(reporte.getColumnas().contains("Valor Total"), "Debe incluir columna Valor Total");
    }

    @Test
    @DisplayName("Debe establecer ordenamiento en reporte de inventario")
    void debeEstablecerOrdenamientoEnReporteInventario() {
        ReporteBuilder.Reporte reporte = reporteFacadeService.generarReporteInventarioConBuilder(
                ReporteBuilder.FormatoReporte.PDF,
                true
        );

        assertEquals("cantidad", reporte.getOrdenamiento(), "El ordenamiento debe ser por cantidad");
        assertFalse(reporte.isOrdenAscendente(), "El ordenamiento debe ser descendente");
    }

    @Test
    @DisplayName("Debe generar título 'Reporte de Inventario'")
    void debeGenerarTituloReporteInventario() {
        ReporteBuilder.Reporte reporte = reporteFacadeService.generarReporteInventarioConBuilder(
                ReporteBuilder.FormatoReporte.PDF,
                true
        );

        assertEquals("Reporte de Inventario", reporte.getTitulo(), "El título debe ser 'Reporte de Inventario'");
    }

    // ===================================================================
    // PRUEBAS PARA: generarReporteConsolidadoConBuilder
    // ===================================================================

    @Test
    @DisplayName("Debe generar reporte consolidado con Builder")
    void debeGenerarReporteConsolidado() {
        ReporteBuilder.Reporte reporte = reporteFacadeService.generarReporteConsolidadoConBuilder(
                fechaInicio,
                fechaFin,
                ReporteBuilder.FormatoReporte.PDF
        );

        assertNotNull(reporte, "El reporte no debe ser nulo");
        assertEquals(ReporteBuilder.TipoReporte.CONSOLIDADO, reporte.getTipoReporte(), "El tipo debe ser CONSOLIDADO");
        assertEquals(ReporteBuilder.FormatoReporte.PDF, reporte.getFormato(), "El formato debe ser PDF");
        assertTrue(reporte.isIncluirGraficos(), "Debe incluir gráficos");
        assertTrue(reporte.isIncluirResumen(), "Debe incluir resumen");
        assertTrue(reporte.isIncluirDetalles(), "Debe incluir detalles");
    }

    @Test
    @DisplayName("Debe generar reporte consolidado en formato JSON")
    void debeGenerarReporteConsolidadoJSON() {
        ReporteBuilder.Reporte reporte = reporteFacadeService.generarReporteConsolidadoConBuilder(
                fechaInicio,
                fechaFin,
                ReporteBuilder.FormatoReporte.JSON
        );

        assertNotNull(reporte, "El reporte no debe ser nulo");
        assertEquals(ReporteBuilder.FormatoReporte.JSON, reporte.getFormato(), "El formato debe ser JSON");
    }

    @Test
    @DisplayName("Debe establecer rango de fechas en reporte consolidado")
    void debeEstablecerRangoFechasEnReporteConsolidado() {
        ReporteBuilder.Reporte reporte = reporteFacadeService.generarReporteConsolidadoConBuilder(
                fechaInicio,
                fechaFin,
                ReporteBuilder.FormatoReporte.PDF
        );

        assertEquals(fechaInicio, reporte.getFechaInicio(), "La fecha de inicio debe coincidir");
        assertEquals(fechaFin, reporte.getFechaFin(), "La fecha de fin debe coincidir");
    }

    @Test
    @DisplayName("Debe incluir columnas correctas en reporte consolidado")
    void debeIncluirColumnasCorrectasEnReporteConsolidado() {
        ReporteBuilder.Reporte reporte = reporteFacadeService.generarReporteConsolidadoConBuilder(
                fechaInicio,
                fechaFin,
                ReporteBuilder.FormatoReporte.PDF
        );

        assertNotNull(reporte.getColumnas(), "Las columnas no deben ser nulas");
        assertTrue(reporte.getColumnas().contains("Sección"), "Debe incluir columna Sección");
        assertTrue(reporte.getColumnas().contains("Métrica"), "Debe incluir columna Métrica");
        assertTrue(reporte.getColumnas().contains("Valor"), "Debe incluir columna Valor");
    }

    @Test
    @DisplayName("Debe generar título descriptivo para reporte consolidado")
    void debeGenerarTituloDescriptivosConsolidado() {
        ReporteBuilder.Reporte reporte = reporteFacadeService.generarReporteConsolidadoConBuilder(
                fechaInicio,
                fechaFin,
                ReporteBuilder.FormatoReporte.PDF
        );

        assertNotNull(reporte.getTitulo(), "El título no debe ser nulo");
        assertTrue(reporte.getTitulo().contains("Reporte Consolidado"), "El título debe contener 'Reporte Consolidado'");
        assertTrue(reporte.getTitulo().contains(fechaInicio.toString()), "El título debe contener la fecha de inicio");
        assertTrue(reporte.getTitulo().contains(fechaFin.toString()), "El título debe contener la fecha de fin");
    }

    // ===================================================================
    // MÉTODOS AUXILIARES
    // ===================================================================

    /**
     * Crea una lista de citas mock para pruebas.
     */
    private List<CitaResponseDTO> crearCitasMock() {
        return List.of(
                CitaResponseDTO.builder()
                        .idCita(1L)
                        .fechaCita(fechaInicio)
                        .horaCita(LocalTime.of(9, 0))
                        .estado("ATENDIDA")
                        .motivo("Consulta general")
                        .build(),
                CitaResponseDTO.builder()
                        .idCita(2L)
                        .fechaCita(fechaInicio.plusDays(1))
                        .horaCita(LocalTime.of(10, 0))
                        .estado("PROGRAMADA")
                        .motivo("Vacunación")
                        .build(),
                CitaResponseDTO.builder()
                        .idCita(3L)
                        .fechaCita(fechaInicio.plusDays(2))
                        .horaCita(LocalTime.of(14, 0))
                        .estado("CANCELADA")
                        .motivo("Cirugía")
                        .build()
        );
    }

    /**
     * Crea una lista de inventarios mock para pruebas.
     */
    private List<InventarioResponseDTO> crearInventariosMock() {
        return List.of(
                InventarioResponseDTO.builder()
                        .idInventario(1L)
                        .nombreInsumo("Insumo A")
                        .cantidadActual(50)
                        .valorTotal(new BigDecimal("100.00"))
                        .build(),
                InventarioResponseDTO.builder()
                        .idInventario(2L)
                        .nombreInsumo("Insumo B")
                        .cantidadActual(30)
                        .valorTotal(new BigDecimal("150.00"))
                        .build(),
                InventarioResponseDTO.builder()
                        .idInventario(3L)
                        .nombreInsumo("Insumo C")
                        .cantidadActual(100)
                        .valorTotal(new BigDecimal("100.00"))
                        .build()
        );
    }

    /**
     * Crea citas con información de veterinario para pruebas.
     */
    private List<CitaResponseDTO> crearCitasConVeterinarioMock() {
        return List.of(
                CitaResponseDTO.builder()
                        .idCita(1L)
                        .estado("ATENDIDA")
                        .veterinario(crearVeterinarioMock(1L, "Dr. Juan", "Cirugía"))
                        .build(),
                CitaResponseDTO.builder()
                        .idCita(2L)
                        .estado("ATENDIDA")
                        .veterinario(crearVeterinarioMock(1L, "Dr. Juan", "Cirugía"))
                        .build(),
                CitaResponseDTO.builder()
                        .idCita(3L)
                        .estado("PROGRAMADA")
                        .veterinario(crearVeterinarioMock(2L, "Dra. María", "Medicina Interna"))
                        .build(),
                CitaResponseDTO.builder()
                        .idCita(4L)
                        .estado("ATENDIDA")
                        .veterinario(crearVeterinarioMock(2L, "Dra. María", "Medicina Interna"))
                        .build()
        );
    }

    /**
     * Crea un DTO de veterinario mock.
     */
    private CitaResponseDTO.VeterinarioSimpleDTO crearVeterinarioMock(Long id, String nombre, String especialidad) {
        return CitaResponseDTO.VeterinarioSimpleDTO.builder()
                .idPersonal(id)
                .nombreCompleto(nombre)
                .especialidad(especialidad)
                .build();
    }
}
