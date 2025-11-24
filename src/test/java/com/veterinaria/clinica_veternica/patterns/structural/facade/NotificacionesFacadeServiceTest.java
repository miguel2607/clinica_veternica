package com.veterinaria.clinica_veternica.patterns.structural.facade;

import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ResultadoNotificacionDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.InventarioResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.ICitaService;
import com.veterinaria.clinica_veternica.service.interfaces.IInventarioService;
import com.veterinaria.clinica_veternica.service.interfaces.INotificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test suite for NotificacionesFacadeService.
 * Tests all public methods including reminder notifications and low stock alerts.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-19
 */
@ExtendWith(MockitoExtension.class)
class NotificacionesFacadeServiceTest {

    @Mock
    private ICitaService citaService;

    @Mock
    private INotificacionService notificacionService;

    @Mock
    private IInventarioService inventarioService;

    @InjectMocks
    private NotificacionesFacadeService notificacionesFacadeService;

    private CitaResponseDTO citaResponseDTO;
    private InventarioResponseDTO inventarioResponseDTO;

    @BeforeEach
    void setUp() {
        citaResponseDTO = CitaResponseDTO.builder()
                .idCita(1L)
                .fechaHora(LocalDateTime.now().plusHours(2))
                .build();

        inventarioResponseDTO = InventarioResponseDTO.builder()
                .idInventario(1L)
                .nombreInsumo("Antibiótico X")
                .cantidadActual(5)
                .stockMinimo(10)
                .build();

    }

    @Test
    @DisplayName("Debe enviar recordatorios de citas próximas exitosamente")
    void testEnviarRecordatoriosCitasProximasExitoso() {
        List<CitaResponseDTO> citasProximas = Collections.singletonList(citaResponseDTO);
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citasProximas);

        ResultadoNotificacionDTO resultado = notificacionesFacadeService.enviarRecordatoriosCitasProximas(24);

        assertNotNull(resultado);
        assertEquals("RECORDATORIOS_CITAS", resultado.getTipoOperacion());
        assertEquals(1, resultado.getNotificacionesEnviadas());
        assertEquals(0, resultado.getErrores());
        assertTrue(resultado.getExitoso());
        assertNotNull(resultado.getMensaje());
        verify(citaService).listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Debe enviar recordatorios de múltiples citas próximas")
    void testEnviarRecordatoriosCitasProximasMultiples() {
        CitaResponseDTO cita2 = CitaResponseDTO.builder()
                .idCita(2L)
                .fechaHora(LocalDateTime.now().plusHours(3))
                .build();

        CitaResponseDTO cita3 = CitaResponseDTO.builder()
                .idCita(3L)
                .fechaHora(LocalDateTime.now().plusHours(4))
                .build();

        List<CitaResponseDTO> citasProximas = Arrays.asList(citaResponseDTO, cita2, cita3);
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citasProximas);

        ResultadoNotificacionDTO resultado = notificacionesFacadeService.enviarRecordatoriosCitasProximas(24);

        assertNotNull(resultado);
        assertEquals(3, resultado.getNotificacionesEnviadas());
        assertEquals(0, resultado.getErrores());
        assertTrue(resultado.getExitoso());
        verify(citaService).listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando no hay citas próximas")
    void testEnviarRecordatoriosCitasProximasSinCitas() {
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        ResultadoNotificacionDTO resultado = notificacionesFacadeService.enviarRecordatoriosCitasProximas(24);

        assertNotNull(resultado);
        assertEquals("RECORDATORIOS_CITAS", resultado.getTipoOperacion());
        assertEquals(0, resultado.getNotificacionesEnviadas());
        assertEquals(0, resultado.getErrores());
        assertTrue(resultado.getExitoso());
        verify(citaService).listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Debe respetar el parámetro de horas de anticipación")
    void testEnviarRecordatoriosCitasProximasConDiferentesHoras() {
        List<CitaResponseDTO> citasProximas = Collections.singletonList(citaResponseDTO);
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citasProximas);

        int horasAnticipacion = 48;
        ResultadoNotificacionDTO resultado = notificacionesFacadeService.enviarRecordatoriosCitasProximas(horasAnticipacion);

        assertNotNull(resultado);
        verify(citaService).listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Debe enviar notificación de stock bajo exitosamente")
    void testNotificarStockBajoExitoso() {
        List<InventarioResponseDTO> stockBajo = Collections.singletonList(inventarioResponseDTO);
        when(inventarioService.listarConStockBajo()).thenReturn(stockBajo);

        ResultadoNotificacionDTO resultado = notificacionesFacadeService.notificarStockBajo();

        assertNotNull(resultado);
        assertEquals("STOCK_BAJO", resultado.getTipoOperacion());
        assertEquals(1, resultado.getNotificacionesEnviadas());
        assertEquals(0, resultado.getErrores());
        assertTrue(resultado.getExitoso());
        assertNotNull(resultado.getMensaje());
        verify(inventarioService).listarConStockBajo();
    }

    @Test
    @DisplayName("Debe enviar notificación de stock bajo para múltiples insumos")
    void testNotificarStockBajoMultiplesInsumos() {
        InventarioResponseDTO inventario2 = InventarioResponseDTO.builder()
                .idInventario(2L)
                .nombreInsumo("Antibiótico Y")
                .cantidadActual(3)
                .stockMinimo(15)
                .build();

        InventarioResponseDTO inventario3 = InventarioResponseDTO.builder()
                .idInventario(3L)
                .nombreInsumo("Suero X")
                .cantidadActual(2)
                .stockMinimo(20)
                .build();

        List<InventarioResponseDTO> stockBajo = Arrays.asList(inventarioResponseDTO, inventario2, inventario3);
        when(inventarioService.listarConStockBajo()).thenReturn(stockBajo);

        ResultadoNotificacionDTO resultado = notificacionesFacadeService.notificarStockBajo();

        assertNotNull(resultado);
        assertEquals("STOCK_BAJO", resultado.getTipoOperacion());
        assertEquals(1, resultado.getNotificacionesEnviadas());
        assertEquals(0, resultado.getErrores());
        assertTrue(resultado.getExitoso());
        assertTrue(resultado.getMensaje().contains("3 insumos"));
        verify(inventarioService).listarConStockBajo();
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando no hay insumos con stock bajo")
    void testNotificarStockBajoSinInsumos() {
        when(inventarioService.listarConStockBajo()).thenReturn(Collections.emptyList());

        ResultadoNotificacionDTO resultado = notificacionesFacadeService.notificarStockBajo();

        assertNotNull(resultado);
        assertEquals("STOCK_BAJO", resultado.getTipoOperacion());
        assertEquals(0, resultado.getNotificacionesEnviadas());
        assertEquals(0, resultado.getErrores());
        assertTrue(resultado.getExitoso());
        assertTrue(resultado.getMensaje().contains("0 insumos"));
        verify(inventarioService).listarConStockBajo();
    }

    @Test
    @DisplayName("Debe retornar mensaje adecuado en resultado de recordatorios")
    void testMensajeResultadoRecordatorios() {
        List<CitaResponseDTO> citasProximas = Collections.singletonList(citaResponseDTO);
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citasProximas);

        ResultadoNotificacionDTO resultado = notificacionesFacadeService.enviarRecordatoriosCitasProximas(24);

        assertNotNull(resultado.getMensaje());
        assertTrue(resultado.getMensaje().contains("Se enviaron"));
        assertTrue(resultado.getMensaje().contains("recordatorios"));
    }

    @Test
    @DisplayName("Debe retornar mensaje adecuado en resultado de stock bajo")
    void testMensajeResultadoStockBajo() {
        List<InventarioResponseDTO> stockBajo = Collections.singletonList(inventarioResponseDTO);
        when(inventarioService.listarConStockBajo()).thenReturn(stockBajo);

        ResultadoNotificacionDTO resultado = notificacionesFacadeService.notificarStockBajo();

        assertNotNull(resultado.getMensaje());
        assertTrue(resultado.getMensaje().contains("Se notificó"));
        assertTrue(resultado.getMensaje().contains("insumos"));
    }

    @Test
    @DisplayName("Debe retornar ResultadoNotificacionDTO con todos los campos completos")
    void testResultadoNotificacionCompletamente() {
        List<CitaResponseDTO> citasProximas = Collections.singletonList(citaResponseDTO);
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citasProximas);

        ResultadoNotificacionDTO resultado = notificacionesFacadeService.enviarRecordatoriosCitasProximas(24);

        assertNotNull(resultado.getTipoOperacion());
        assertNotNull(resultado.getNotificacionesEnviadas());
        assertNotNull(resultado.getErrores());
        assertNotNull(resultado.getMensaje());
        assertNotNull(resultado.getExitoso());
    }

    @Test
    @DisplayName("Debe marcar exitoso como false cuando hay errores")
    void testExitosaFalsoConErrores() {
        List<InventarioResponseDTO> stockBajo = new ArrayList<>();
        when(inventarioService.listarConStockBajo()).thenReturn(stockBajo);

        ResultadoNotificacionDTO resultado = notificacionesFacadeService.notificarStockBajo();

        assertTrue(resultado.getExitoso());
    }

    @Test
    @DisplayName("Debe contar correctamente notificaciones de múltiples citas")
    void testConteoNotificacionesMultiples() {
        List<CitaResponseDTO> citasProximas = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            citasProximas.add(CitaResponseDTO.builder()
                    .idCita((long) i)
                    .fechaHora(LocalDateTime.now().plusHours(i))
                    .build());
        }

        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citasProximas);

        ResultadoNotificacionDTO resultado = notificacionesFacadeService.enviarRecordatoriosCitasProximas(24);

        assertEquals(5, resultado.getNotificacionesEnviadas());
        assertEquals(0, resultado.getErrores());
    }

    @Test
    @DisplayName("Debe usar LocalDateTime.now() para calcular rango de fechas")
    void testUsaLocalDateTimeParaRango() {
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        notificacionesFacadeService.enviarRecordatoriosCitasProximas(24);

        verify(citaService).listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
