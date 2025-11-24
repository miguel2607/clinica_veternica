package com.veterinaria.clinica_veternica.patterns.structural.facade;

import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.comunicacion.NotificacionResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.DashboardResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.EstadisticasGeneralesDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.InventarioResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.PropietarioResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.VeterinarioResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.ICitaService;
import com.veterinaria.clinica_veternica.service.interfaces.IInventarioService;
import com.veterinaria.clinica_veternica.service.interfaces.IMascotaService;
import com.veterinaria.clinica_veternica.service.interfaces.INotificacionService;
import com.veterinaria.clinica_veternica.service.interfaces.IPropietarioService;
import com.veterinaria.clinica_veternica.service.interfaces.IVeterinarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test suite for DashboardFacadeService.
 * Verifies the functionality of dashboard and statistics retrieval operations.
 *
 * @author Test Team
 * @version 1.0
 * @since 2025-11-19
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardFacadeService Tests")
class DashboardFacadeServiceTest {

    @Mock
    private ICitaService citaService;

    @Mock
    private IInventarioService inventarioService;

    @Mock
    private INotificacionService notificacionService;

    @Mock
    private IMascotaService mascotaService;

    @Mock
    private IPropietarioService propietarioService;

    @Mock
    private IVeterinarioService veterinarioService;

    @InjectMocks
    private DashboardFacadeService dashboardFacadeService;

    // Test data builders
    private CitaResponseDTO citaResponseDTO;
    private InventarioResponseDTO inventarioResponseDTO;
    private NotificacionResponseDTO notificacionResponseDTO;
    private MascotaResponseDTO mascotaResponseDTO;
    private PropietarioResponseDTO propietarioResponseDTO;
    private VeterinarioResponseDTO veterinarioResponseDTO;

    @BeforeEach
    void setUp() {
        // Initialize test data objects
        citaResponseDTO = CitaResponseDTO.builder()
                .idCita(1L)
                .build();

        inventarioResponseDTO = InventarioResponseDTO.builder()
                .idInventario(1L)
                .nombreInsumo("Medicamento Test")
                .build();

        notificacionResponseDTO = NotificacionResponseDTO.builder()
                .idComunicacion(1L)
                .build();

        mascotaResponseDTO = MascotaResponseDTO.builder()
                .idMascota(1L)
                .nombre("Mascota Test")
                .build();

        propietarioResponseDTO = PropietarioResponseDTO.builder()
                .idPropietario(1L)
                .build();

        veterinarioResponseDTO = VeterinarioResponseDTO.builder()
                .idPersonal(1L)
                .nombreCompleto("Veterinario Test")
                .build();
    }

    // ==================== Tests for obtenerDashboard() ====================

    @Test
    @DisplayName("Debe obtener dashboard con todos los datos disponibles")
    void debeObtenerDashboardConDatosCompletos() {
        // Arrange
        List<CitaResponseDTO> citasHoy = List.of(citaResponseDTO);
        List<CitaResponseDTO> citasProgramadas = List.of(citaResponseDTO);
        List<InventarioResponseDTO> stockBajo = List.of(inventarioResponseDTO);
        List<NotificacionResponseDTO> notificacionesRecientes = List.of(notificacionResponseDTO);

        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citasHoy);
        when(citaService.listarProgramadas()).thenReturn(citasProgramadas);
        when(inventarioService.listarConStockBajo()).thenReturn(stockBajo);
        when(notificacionService.listarTodas()).thenReturn(notificacionesRecientes);

        // Act
        DashboardResponseDTO result = dashboardFacadeService.obtenerDashboard();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalCitasHoy());
        assertEquals(1, result.getTotalCitasProgramadas());
        assertEquals(1, result.getTotalStockBajo());
        assertEquals(1, result.getTotalNotificacionesRecientes());
        assertEquals(1, result.getCitasHoy().size());
        assertEquals(1, result.getCitasProgramadas().size());
        assertEquals(1, result.getStockBajo().size());
        assertEquals(1, result.getNotificacionesRecientes().size());

        verify(citaService).listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class));
        verify(citaService).listarProgramadas();
        verify(inventarioService).listarConStockBajo();
        verify(notificacionService).listarTodas();
    }

    @Test
    @DisplayName("Debe obtener dashboard vacío cuando no hay datos")
    void debeObtenerDashboardVacio() {
        // Arrange
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(citaService.listarProgramadas()).thenReturn(Collections.emptyList());
        when(inventarioService.listarConStockBajo()).thenReturn(Collections.emptyList());
        when(notificacionService.listarTodas()).thenReturn(Collections.emptyList());

        // Act
        DashboardResponseDTO result = dashboardFacadeService.obtenerDashboard();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalCitasHoy());
        assertEquals(0, result.getTotalCitasProgramadas());
        assertEquals(0, result.getTotalStockBajo());
        assertEquals(0, result.getTotalNotificacionesRecientes());
        assertTrue(result.getCitasHoy().isEmpty());
        assertTrue(result.getCitasProgramadas().isEmpty());
        assertTrue(result.getStockBajo().isEmpty());
        assertTrue(result.getNotificacionesRecientes().isEmpty());
    }

    @Test
    @DisplayName("Debe obtener dashboard con múltiples citas hoy")
    void debeObtenerDashboardConMultiplesCitasHoy() {
        // Arrange
        CitaResponseDTO cita2 = CitaResponseDTO.builder().idCita(2L).build();
        CitaResponseDTO cita3 = CitaResponseDTO.builder().idCita(3L).build();
        List<CitaResponseDTO> citasHoy = List.of(citaResponseDTO, cita2, cita3);

        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citasHoy);
        when(citaService.listarProgramadas()).thenReturn(Collections.emptyList());
        when(inventarioService.listarConStockBajo()).thenReturn(Collections.emptyList());
        when(notificacionService.listarTodas()).thenReturn(Collections.emptyList());

        // Act
        DashboardResponseDTO result = dashboardFacadeService.obtenerDashboard();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalCitasHoy());
        assertEquals(3, result.getCitasHoy().size());
    }

    @Test
    @DisplayName("Debe obtener dashboard con múltiples items de stock bajo")
    void debeObtenerDashboardConMultiplesStockBajo() {
        // Arrange
        InventarioResponseDTO inventario2 = InventarioResponseDTO.builder()
                .idInventario(2L)
                .nombreInsumo("Medicamento Test 2")
                .build();
        InventarioResponseDTO inventario3 = InventarioResponseDTO.builder()
                .idInventario(3L)
                .nombreInsumo("Medicamento Test 3")
                .build();
        List<InventarioResponseDTO> stockBajo = List.of(inventarioResponseDTO, inventario2, inventario3);

        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(citaService.listarProgramadas()).thenReturn(Collections.emptyList());
        when(inventarioService.listarConStockBajo()).thenReturn(stockBajo);
        when(notificacionService.listarTodas()).thenReturn(Collections.emptyList());

        // Act
        DashboardResponseDTO result = dashboardFacadeService.obtenerDashboard();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalStockBajo());
        assertEquals(3, result.getStockBajo().size());
    }

    @Test
    @DisplayName("Debe limitar notificaciones a 10 máximo")
    void debeObtenerDashboardLimitandoNotificaciones() {
        // Arrange
        List<NotificacionResponseDTO> notificacionesExcesivas = List.of(
                NotificacionResponseDTO.builder().idComunicacion(1L).build(),
                NotificacionResponseDTO.builder().idComunicacion(2L).build(),
                NotificacionResponseDTO.builder().idComunicacion(3L).build(),
                NotificacionResponseDTO.builder().idComunicacion(4L).build(),
                NotificacionResponseDTO.builder().idComunicacion(5L).build(),
                NotificacionResponseDTO.builder().idComunicacion(6L).build(),
                NotificacionResponseDTO.builder().idComunicacion(7L).build(),
                NotificacionResponseDTO.builder().idComunicacion(8L).build(),
                NotificacionResponseDTO.builder().idComunicacion(9L).build(),
                NotificacionResponseDTO.builder().idComunicacion(10L).build(),
                NotificacionResponseDTO.builder().idComunicacion(11L).build(),
                NotificacionResponseDTO.builder().idComunicacion(12L).build(),
                NotificacionResponseDTO.builder().idComunicacion(13L).build(),
                NotificacionResponseDTO.builder().idComunicacion(14L).build(),
                NotificacionResponseDTO.builder().idComunicacion(15L).build()
        );

        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(citaService.listarProgramadas()).thenReturn(Collections.emptyList());
        when(inventarioService.listarConStockBajo()).thenReturn(Collections.emptyList());
        when(notificacionService.listarTodas()).thenReturn(notificacionesExcesivas);

        // Act
        DashboardResponseDTO result = dashboardFacadeService.obtenerDashboard();

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getTotalNotificacionesRecientes());
        assertEquals(10, result.getNotificacionesRecientes().size());
    }

    @Test
    @DisplayName("Debe obtener dashboard con citas programadas")
    void debeObtenerDashboardConCitasProgramadas() {
        // Arrange
        CitaResponseDTO citaProgramada1 = CitaResponseDTO.builder().idCita(10L).build();
        CitaResponseDTO citaProgramada2 = CitaResponseDTO.builder().idCita(11L).build();
        List<CitaResponseDTO> citasProgramadas = List.of(citaProgramada1, citaProgramada2);

        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(citaService.listarProgramadas()).thenReturn(citasProgramadas);
        when(inventarioService.listarConStockBajo()).thenReturn(Collections.emptyList());
        when(notificacionService.listarTodas()).thenReturn(Collections.emptyList());

        // Act
        DashboardResponseDTO result = dashboardFacadeService.obtenerDashboard();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalCitasProgramadas());
        assertEquals(2, result.getCitasProgramadas().size());
        assertEquals(citaProgramada1.getIdCita(), result.getCitasProgramadas().get(0).getIdCita());
        assertEquals(citaProgramada2.getIdCita(), result.getCitasProgramadas().get(1).getIdCita());
    }

    @Test
    @DisplayName("Debe llamar a todos los servicios en obtenerDashboard")
    void debeVerificarLlamadaATodosLosServicios() {
        // Arrange
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(citaService.listarProgramadas()).thenReturn(Collections.emptyList());
        when(inventarioService.listarConStockBajo()).thenReturn(Collections.emptyList());
        when(notificacionService.listarTodas()).thenReturn(Collections.emptyList());

        // Act
        dashboardFacadeService.obtenerDashboard();

        // Assert
        verify(citaService, times(1)).listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class));
        verify(citaService).listarProgramadas();
        verify(inventarioService).listarConStockBajo();
        verify(notificacionService).listarTodas();
    }

    // ==================== Tests for obtenerEstadisticasGenerales() ====================

    @Test
    @DisplayName("Debe obtener estadísticas generales con datos completos")
    void debeObtenerEstadisticasGeneralesConDatosCompletos() {
        // Arrange
        List<MascotaResponseDTO> mascotas = List.of(mascotaResponseDTO);
        List<PropietarioResponseDTO> propietarios = List.of(propietarioResponseDTO);
        List<VeterinarioResponseDTO> veterinarios = List.of(veterinarioResponseDTO);
        List<CitaResponseDTO> citasProgramadas = List.of(citaResponseDTO);
        List<CitaResponseDTO> citasHoy = List.of(citaResponseDTO);
        List<InventarioResponseDTO> stockBajo = List.of(inventarioResponseDTO);

        when(mascotaService.listarActivas()).thenReturn(mascotas);
        when(propietarioService.listarActivos()).thenReturn(propietarios);
        when(veterinarioService.listarActivos()).thenReturn(veterinarios);
        when(citaService.listarProgramadas()).thenReturn(citasProgramadas);
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citasHoy);
        when(inventarioService.listarConStockBajo()).thenReturn(stockBajo);

        // Act
        EstadisticasGeneralesDTO result = dashboardFacadeService.obtenerEstadisticasGenerales();

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotalMascotas());
        assertEquals(1L, result.getTotalPropietarios());
        assertEquals(1L, result.getTotalVeterinarios());
        assertEquals(1L, result.getTotalCitasProgramadas());
        assertEquals(1L, result.getTotalCitasHoy());
        assertEquals(1, result.getInsumosStockBajo());

        verify(mascotaService).listarActivas();
        verify(propietarioService).listarActivos();
        verify(veterinarioService).listarActivos();
        verify(citaService).listarProgramadas();
        verify(citaService).listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class));
        verify(inventarioService).listarConStockBajo();
    }

    @Test
    @DisplayName("Debe obtener estadísticas generales vacías cuando no hay datos")
    void debeObtenerEstadisticasGeneralesVacias() {
        // Arrange
        when(mascotaService.listarActivas()).thenReturn(Collections.emptyList());
        when(propietarioService.listarActivos()).thenReturn(Collections.emptyList());
        when(veterinarioService.listarActivos()).thenReturn(Collections.emptyList());
        when(citaService.listarProgramadas()).thenReturn(Collections.emptyList());
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(inventarioService.listarConStockBajo()).thenReturn(Collections.emptyList());

        // Act
        EstadisticasGeneralesDTO result = dashboardFacadeService.obtenerEstadisticasGenerales();

        // Assert
        assertNotNull(result);
        assertEquals(0L, result.getTotalMascotas());
        assertEquals(0L, result.getTotalPropietarios());
        assertEquals(0L, result.getTotalVeterinarios());
        assertEquals(0L, result.getTotalCitasProgramadas());
        assertEquals(0L, result.getTotalCitasHoy());
        assertEquals(0, result.getInsumosStockBajo());
    }

    @Test
    @DisplayName("Debe contar múltiples mascotas activas")
    void debeContarMultiplesMascotas() {
        // Arrange
        MascotaResponseDTO mascota2 = MascotaResponseDTO.builder()
                .idMascota(2L)
                .nombre("Mascota Test 2")
                .build();
        MascotaResponseDTO mascota3 = MascotaResponseDTO.builder()
                .idMascota(3L)
                .nombre("Mascota Test 3")
                .build();
        List<MascotaResponseDTO> mascotas = List.of(mascotaResponseDTO, mascota2, mascota3);

        when(mascotaService.listarActivas()).thenReturn(mascotas);
        when(propietarioService.listarActivos()).thenReturn(Collections.emptyList());
        when(veterinarioService.listarActivos()).thenReturn(Collections.emptyList());
        when(citaService.listarProgramadas()).thenReturn(Collections.emptyList());
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(inventarioService.listarConStockBajo()).thenReturn(Collections.emptyList());

        // Act
        EstadisticasGeneralesDTO result = dashboardFacadeService.obtenerEstadisticasGenerales();

        // Assert
        assertEquals(3L, result.getTotalMascotas());
    }

    @Test
    @DisplayName("Debe contar múltiples propietarios activos")
    void debeContarMultiplesPropietarios() {
        // Arrange
        PropietarioResponseDTO propietario2 = PropietarioResponseDTO.builder()
                .idPropietario(2L)
                .build();
        PropietarioResponseDTO propietario3 = PropietarioResponseDTO.builder()
                .idPropietario(3L)
                .build();
        List<PropietarioResponseDTO> propietarios = List.of(propietarioResponseDTO, propietario2, propietario3);

        when(mascotaService.listarActivas()).thenReturn(Collections.emptyList());
        when(propietarioService.listarActivos()).thenReturn(propietarios);
        when(veterinarioService.listarActivos()).thenReturn(Collections.emptyList());
        when(citaService.listarProgramadas()).thenReturn(Collections.emptyList());
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(inventarioService.listarConStockBajo()).thenReturn(Collections.emptyList());

        // Act
        EstadisticasGeneralesDTO result = dashboardFacadeService.obtenerEstadisticasGenerales();

        // Assert
        assertEquals(3L, result.getTotalPropietarios());
    }

    @Test
    @DisplayName("Debe contar múltiples veterinarios activos")
    void debeContarMultiplesVeterinarios() {
        // Arrange
        VeterinarioResponseDTO veterinario2 = VeterinarioResponseDTO.builder()
                .idPersonal(2L)
                .nombreCompleto("Veterinario Test 2")
                .build();
        VeterinarioResponseDTO veterinario3 = VeterinarioResponseDTO.builder()
                .idPersonal(3L)
                .nombreCompleto("Veterinario Test 3")
                .build();
        List<VeterinarioResponseDTO> veterinarios = List.of(veterinarioResponseDTO, veterinario2, veterinario3);

        when(mascotaService.listarActivas()).thenReturn(Collections.emptyList());
        when(propietarioService.listarActivos()).thenReturn(Collections.emptyList());
        when(veterinarioService.listarActivos()).thenReturn(veterinarios);
        when(citaService.listarProgramadas()).thenReturn(Collections.emptyList());
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(inventarioService.listarConStockBajo()).thenReturn(Collections.emptyList());

        // Act
        EstadisticasGeneralesDTO result = dashboardFacadeService.obtenerEstadisticasGenerales();

        // Assert
        assertEquals(3L, result.getTotalVeterinarios());
    }

    @Test
    @DisplayName("Debe contar múltiples citas programadas en estadísticas")
    void debeContarMultiplesCitasProgramadas() {
        // Arrange
        CitaResponseDTO cita2 = CitaResponseDTO.builder().idCita(2L).build();
        CitaResponseDTO cita3 = CitaResponseDTO.builder().idCita(3L).build();
        List<CitaResponseDTO> citasProgramadas = List.of(citaResponseDTO, cita2, cita3);

        when(mascotaService.listarActivas()).thenReturn(Collections.emptyList());
        when(propietarioService.listarActivos()).thenReturn(Collections.emptyList());
        when(veterinarioService.listarActivos()).thenReturn(Collections.emptyList());
        when(citaService.listarProgramadas()).thenReturn(citasProgramadas);
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(inventarioService.listarConStockBajo()).thenReturn(Collections.emptyList());

        // Act
        EstadisticasGeneralesDTO result = dashboardFacadeService.obtenerEstadisticasGenerales();

        // Assert
        assertEquals(3L, result.getTotalCitasProgramadas());
    }

    @Test
    @DisplayName("Debe contar múltiples citas de hoy en estadísticas")
    void debeContarMultiplesCitasHoyEnEstadisticas() {
        // Arrange
        CitaResponseDTO cita2 = CitaResponseDTO.builder().idCita(2L).build();
        CitaResponseDTO cita3 = CitaResponseDTO.builder().idCita(3L).build();
        CitaResponseDTO cita4 = CitaResponseDTO.builder().idCita(4L).build();
        List<CitaResponseDTO> citasHoy = List.of(citaResponseDTO, cita2, cita3, cita4);

        when(mascotaService.listarActivas()).thenReturn(Collections.emptyList());
        when(propietarioService.listarActivos()).thenReturn(Collections.emptyList());
        when(veterinarioService.listarActivos()).thenReturn(Collections.emptyList());
        when(citaService.listarProgramadas()).thenReturn(Collections.emptyList());
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citasHoy);
        when(inventarioService.listarConStockBajo()).thenReturn(Collections.emptyList());

        // Act
        EstadisticasGeneralesDTO result = dashboardFacadeService.obtenerEstadisticasGenerales();

        // Assert
        assertEquals(4L, result.getTotalCitasHoy());
    }

    @Test
    @DisplayName("Debe contar múltiples insumos con stock bajo en estadísticas")
    void debeContarMultiplesInsumosStockBajo() {
        // Arrange
        InventarioResponseDTO inventario2 = InventarioResponseDTO.builder()
                .idInventario(2L)
                .nombreInsumo("Medicamento Test 2")
                .build();
        InventarioResponseDTO inventario3 = InventarioResponseDTO.builder()
                .idInventario(3L)
                .nombreInsumo("Medicamento Test 3")
                .build();
        InventarioResponseDTO inventario4 = InventarioResponseDTO.builder()
                .idInventario(4L)
                .nombreInsumo("Medicamento Test 4")
                .build();
        List<InventarioResponseDTO> stockBajo = List.of(inventarioResponseDTO, inventario2, inventario3, inventario4);

        when(mascotaService.listarActivas()).thenReturn(Collections.emptyList());
        when(propietarioService.listarActivos()).thenReturn(Collections.emptyList());
        when(veterinarioService.listarActivos()).thenReturn(Collections.emptyList());
        when(citaService.listarProgramadas()).thenReturn(Collections.emptyList());
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(inventarioService.listarConStockBajo()).thenReturn(stockBajo);

        // Act
        EstadisticasGeneralesDTO result = dashboardFacadeService.obtenerEstadisticasGenerales();

        // Assert
        assertEquals(4, result.getInsumosStockBajo());
    }

    @Test
    @DisplayName("Debe llamar a todos los servicios en obtenerEstadisticasGenerales")
    void debeVerificarLlamadaATodosLosServiciosEnEstadisticas() {
        // Arrange
        when(mascotaService.listarActivas()).thenReturn(Collections.emptyList());
        when(propietarioService.listarActivos()).thenReturn(Collections.emptyList());
        when(veterinarioService.listarActivos()).thenReturn(Collections.emptyList());
        when(citaService.listarProgramadas()).thenReturn(Collections.emptyList());
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(inventarioService.listarConStockBajo()).thenReturn(Collections.emptyList());

        // Act
        dashboardFacadeService.obtenerEstadisticasGenerales();

        // Assert
        verify(mascotaService).listarActivas();
        verify(propietarioService).listarActivos();
        verify(veterinarioService).listarActivos();
        verify(citaService).listarProgramadas();
        verify(citaService).listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class));
        verify(inventarioService).listarConStockBajo();
    }

    @Test
    @DisplayName("Debe obtener estadísticas con todos los conteos correctos")
    void debeObtenerEstadisticasConTodosLosConteos() {
        // Arrange
        List<MascotaResponseDTO> mascotas = List.of(
                MascotaResponseDTO.builder().idMascota(1L).nombre("Mascota 1").build(),
                MascotaResponseDTO.builder().idMascota(2L).nombre("Mascota 2").build()
        );
        List<PropietarioResponseDTO> propietarios = List.of(
                PropietarioResponseDTO.builder().idPropietario(1L).build(),
                PropietarioResponseDTO.builder().idPropietario(2L).build(),
                PropietarioResponseDTO.builder().idPropietario(3L).build()
        );
        List<VeterinarioResponseDTO> veterinarios = List.of(
                VeterinarioResponseDTO.builder().idPersonal(1L).nombreCompleto("Vet 1").build()
        );
        List<CitaResponseDTO> citasProgramadas = List.of(
                CitaResponseDTO.builder().idCita(1L).build(),
                CitaResponseDTO.builder().idCita(2L).build()
        );
        List<CitaResponseDTO> citasHoy = List.of(
                CitaResponseDTO.builder().idCita(10L).build()
        );
        List<InventarioResponseDTO> stockBajo = List.of(
                InventarioResponseDTO.builder().idInventario(1L).nombreInsumo("Insumo 1").build(),
                InventarioResponseDTO.builder().idInventario(2L).nombreInsumo("Insumo 2").build()
        );

        when(mascotaService.listarActivas()).thenReturn(mascotas);
        when(propietarioService.listarActivos()).thenReturn(propietarios);
        when(veterinarioService.listarActivos()).thenReturn(veterinarios);
        when(citaService.listarProgramadas()).thenReturn(citasProgramadas);
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citasHoy);
        when(inventarioService.listarConStockBajo()).thenReturn(stockBajo);

        // Act
        EstadisticasGeneralesDTO result = dashboardFacadeService.obtenerEstadisticasGenerales();

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotalMascotas());
        assertEquals(3L, result.getTotalPropietarios());
        assertEquals(1L, result.getTotalVeterinarios());
        assertEquals(2L, result.getTotalCitasProgramadas());
        assertEquals(1L, result.getTotalCitasHoy());
        assertEquals(2, result.getInsumosStockBajo());
    }

    @Test
    @DisplayName("Debe verificar que el servicio no retorna null en obtenerDashboard")
    void debeVerificarNoNullEnDashboard() {
        // Arrange
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(citaService.listarProgramadas()).thenReturn(Collections.emptyList());
        when(inventarioService.listarConStockBajo()).thenReturn(Collections.emptyList());
        when(notificacionService.listarTodas()).thenReturn(Collections.emptyList());

        // Act
        DashboardResponseDTO result = dashboardFacadeService.obtenerDashboard();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getCitasHoy());
        assertNotNull(result.getCitasProgramadas());
        assertNotNull(result.getStockBajo());
        assertNotNull(result.getNotificacionesRecientes());
    }

    @Test
    @DisplayName("Debe verificar que el servicio no retorna null en obtenerEstadisticasGenerales")
    void debeVerificarNoNullEnEstadisticas() {
        // Arrange
        when(mascotaService.listarActivas()).thenReturn(Collections.emptyList());
        when(propietarioService.listarActivos()).thenReturn(Collections.emptyList());
        when(veterinarioService.listarActivos()).thenReturn(Collections.emptyList());
        when(citaService.listarProgramadas()).thenReturn(Collections.emptyList());
        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(inventarioService.listarConStockBajo()).thenReturn(Collections.emptyList());

        // Act
        EstadisticasGeneralesDTO result = dashboardFacadeService.obtenerEstadisticasGenerales();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getTotalMascotas());
        assertNotNull(result.getTotalPropietarios());
        assertNotNull(result.getTotalVeterinarios());
        assertNotNull(result.getTotalCitasProgramadas());
        assertNotNull(result.getTotalCitasHoy());
        assertNotNull(result.getInsumosStockBajo());
    }
}
