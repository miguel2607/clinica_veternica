package com.veterinaria.clinica_veternica.patterns.structural.facade;

import com.veterinaria.clinica_veternica.dto.request.clinico.EvolucionClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.clinico.HistoriaClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.paciente.MascotaRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.paciente.PropietarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.EvolucionClinicaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.HistoriaClinicaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.InformacionCompletaMascotaDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.InformacionCompletaPropietarioDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.InformacionCompletaVeterinarioDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ResultadoAtencionCompletaDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ResultadoRegistroCompletoDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.PropietarioResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.VeterinarioResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.service.interfaces.ICitaService;
import com.veterinaria.clinica_veternica.service.interfaces.IEvolucionClinicaService;
import com.veterinaria.clinica_veternica.service.interfaces.IHistoriaClinicaService;
import com.veterinaria.clinica_veternica.service.interfaces.IMascotaService;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test suite for OperacionesFacadeService.
 * Tests all public methods with proper mocking of service dependencies.
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-19
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OperacionesFacadeService Tests")
class OperacionesFacadeServiceTest {

    @Mock
    private ICitaService citaService;

    @Mock
    private IHistoriaClinicaService historiaClinicaService;

    @Mock
    private IEvolucionClinicaService evolucionClinicaService;

    @Mock
    private IMascotaService mascotaService;

    @Mock
    private IPropietarioService propietarioService;

    @Mock
    private IVeterinarioService veterinarioService;

    @InjectMocks
    private OperacionesFacadeService operacionesFacadeService;

    private PropietarioResponseDTO propietarioResponse;
    private MascotaResponseDTO mascotaResponse;
    private HistoriaClinicaResponseDTO historiaClinicaResponse;
    private CitaResponseDTO citaResponse;
    private VeterinarioResponseDTO veterinarioResponse;
    private EvolucionClinicaResponseDTO evolucionResponse;

    @BeforeEach
    void setUp() {
        // Inicializar datos de prueba
        propietarioResponse = PropietarioResponseDTO.builder()
                .idPropietario(1L)
                .nombres("Juan")
                .apellidos("Pérez García")
                .email("juan@example.com")
                .telefono("123456789")
                .build();

        mascotaResponse = MascotaResponseDTO.builder()
                .idMascota(1L)
                .nombre("Buddy")
                .especie(MascotaResponseDTO.EspecieSimpleDTO.builder()
                        .idEspecie(1L)
                        .nombre("Perro")
                        .build())
                .edad(5)
                .build();

        historiaClinicaResponse = HistoriaClinicaResponseDTO.builder()
                .idHistoriaClinica(1L)
                .mascota(HistoriaClinicaResponseDTO.MascotaSimpleDTO.builder()
                        .idMascota(1L)
                        .nombre("Buddy")
                        .especie("Perro")
                        .build())
                .observaciones("Sin problemas")
                .build();

        citaResponse = CitaResponseDTO.builder()
                .idCita(1L)
                .mascota(CitaResponseDTO.MascotaSimpleDTO.builder()
                        .idMascota(1L)
                        .nombre("Buddy")
                        .especie("Perro")
                        .build())
                .veterinario(CitaResponseDTO.VeterinarioSimpleDTO.builder()
                        .idPersonal(1L)
                        .nombreCompleto("Dr. López")
                        .build())
                .fechaHora(LocalDateTime.now())
                .motivo("Consulta general")
                .build();

        veterinarioResponse = VeterinarioResponseDTO.builder()
                .idPersonal(1L)
                .nombreCompleto("Dr. López Martínez")
                .correo("lopez@clinic.com")
                .especialidad("Medicina General")
                .build();

        evolucionResponse = EvolucionClinicaResponseDTO.builder()
                .idEvolucion(1L)
                .tipoEvolucion("Consulta")
                .motivoConsulta("Consulta completada")
                .diagnostico("Consulta completada")
                .planTratamiento("Reposo")
                .observaciones("Antiinflamatorio")
                .build();
    }

    @Test
    @DisplayName("Debe procesar la atención completa exitosamente")
    void testProcesarAtencionCompleta_Success() {
        // Arrange
        Long idCita = 1L;
        EvolucionClinicaRequestDTO evolucionRequest = EvolucionClinicaRequestDTO.builder()
                .idHistoriaClinica(1L)
                .idVeterinario(1L)
                .tipoEvolucion("Consulta")
                .motivoConsulta("Consulta completada")
                .diagnostico("Consulta completada")
                .planTratamiento("Reposo")
                .observaciones("Antiinflamatorio")
                .build();

        when(citaService.marcarComoAtendida(idCita)).thenReturn(citaResponse);
        when(historiaClinicaService.buscarPorMascota(1L)).thenReturn(historiaClinicaResponse);
        when(evolucionClinicaService.crear(eq(1L), any(EvolucionClinicaRequestDTO.class)))
                .thenReturn(evolucionResponse);

        // Act
        ResultadoAtencionCompletaDTO resultado = operacionesFacadeService.procesarAtencionCompleta(
                idCita,
                evolucionRequest
        );

        // Assert
        assertNotNull(resultado);
        assertEquals(citaResponse, resultado.getCita());
        assertEquals(historiaClinicaResponse, resultado.getHistoriaClinica());
        assertEquals("Atención completa procesada exitosamente", resultado.getMensaje());
        verify(citaService, times(1)).marcarComoAtendida(idCita);
        verify(historiaClinicaService, times(1)).buscarPorMascota(1L);
        verify(evolucionClinicaService, times(1)).crear(eq(1L), any(EvolucionClinicaRequestDTO.class));
    }

    @Test
    @DisplayName("Debe procesar atención completa cuando la cita no existe")
    void testProcesarAtencionCompleta_CitaNoEncontrada() {
        // Arrange
        Long idCita = 999L;
        EvolucionClinicaRequestDTO evolucionRequest = EvolucionClinicaRequestDTO.builder()
                .diagnostico("Consulta completada")
                .build();

        when(citaService.marcarComoAtendida(idCita))
                .thenThrow(new ResourceNotFoundException("Cita no encontrada"));

        // Act & Assert
        try {
            operacionesFacadeService.procesarAtencionCompleta(idCita, evolucionRequest);
        } catch (ResourceNotFoundException e) {
            assertEquals("Cita no encontrada", e.getMessage());
        }

        verify(citaService, times(1)).marcarComoAtendida(idCita);
        verify(historiaClinicaService, never()).buscarPorMascota(any());
    }

    @Test
    @DisplayName("Debe registrar mascota completa con propietario e historia clínica")
    void testRegistrarMascotaCompleta_Success() {
        // Arrange
        PropietarioRequestDTO propietarioRequest = PropietarioRequestDTO.builder()
                .nombres("Juan")
                .apellidos("Pérez García")
                .documento("12345678")
                .tipoDocumento("CC")
                .email("juan@example.com")
                .telefono("123456789")
                .build();

        MascotaRequestDTO mascotaRequest = MascotaRequestDTO.builder()
                .nombre("Buddy")
                .idEspecie(1L)
                .idRaza(1L)
                .sexo("Macho")
                .idPropietario(1L)
                .build();

        HistoriaClinicaRequestDTO historiaRequest = HistoriaClinicaRequestDTO.builder()
                .idMascota(1L)
                .numeroHistoria("HC-001")
                .observaciones("Sin problemas")
                .build();

        when(propietarioService.crear(propietarioRequest)).thenReturn(propietarioResponse);
        when(mascotaService.crear(any(MascotaRequestDTO.class))).thenReturn(mascotaResponse);
        when(historiaClinicaService.buscarPorMascota(mascotaResponse.getIdMascota()))
                .thenReturn(historiaClinicaResponse);
        when(historiaClinicaService.actualizar(
                eq(historiaClinicaResponse.getIdHistoriaClinica()),
                any(HistoriaClinicaRequestDTO.class)))
                .thenReturn(historiaClinicaResponse);

        // Act
        ResultadoRegistroCompletoDTO resultado = operacionesFacadeService.registrarMascotaCompleta(
                propietarioRequest,
                mascotaRequest,
                historiaRequest
        );

        // Assert
        assertNotNull(resultado);
        assertEquals(propietarioResponse, resultado.getPropietario());
        assertEquals(mascotaResponse, resultado.getMascota());
        assertEquals(historiaClinicaResponse, resultado.getHistoriaClinica());
        assertEquals("Registro completo exitoso", resultado.getMensaje());
        verify(propietarioService, times(1)).crear(propietarioRequest);
        verify(mascotaService, times(1)).crear(any(MascotaRequestDTO.class));
        verify(historiaClinicaService, times(1)).buscarPorMascota(mascotaResponse.getIdMascota());
        verify(historiaClinicaService, times(1)).actualizar(
                eq(historiaClinicaResponse.getIdHistoriaClinica()),
                any(HistoriaClinicaRequestDTO.class));
        verify(historiaClinicaService, never()).crear(any());
    }

    @Test
    @DisplayName("Debe registrar mascota sin historia clínica cuando no se proporciona")
    void testRegistrarMascotaCompleta_SinHistoriaClinica() {
        // Arrange
        PropietarioRequestDTO propietarioRequest = PropietarioRequestDTO.builder()
                .nombres("Juan")
                .apellidos("Pérez García")
                .documento("12345678")
                .tipoDocumento("CC")
                .email("juan@example.com")
                .telefono("123456789")
                .build();

        MascotaRequestDTO mascotaRequest = MascotaRequestDTO.builder()
                .nombre("Buddy")
                .idEspecie(1L)
                .idRaza(1L)
                .sexo("Macho")
                .idPropietario(1L)
                .build();

        when(propietarioService.crear(propietarioRequest)).thenReturn(propietarioResponse);
        when(mascotaService.crear(any(MascotaRequestDTO.class))).thenReturn(mascotaResponse);
        when(historiaClinicaService.buscarPorMascota(mascotaResponse.getIdMascota()))
                .thenThrow(new ResourceNotFoundException("Historia clínica no encontrada"));

        // Act
        ResultadoRegistroCompletoDTO resultado = operacionesFacadeService.registrarMascotaCompleta(
                propietarioRequest,
                mascotaRequest,
                null
        );

        // Assert
        assertNotNull(resultado);
        assertEquals(propietarioResponse, resultado.getPropietario());
        assertEquals(mascotaResponse, resultado.getMascota());
        assertNull(resultado.getHistoriaClinica());
        verify(propietarioService, times(1)).crear(propietarioRequest);
        verify(mascotaService, times(1)).crear(any(MascotaRequestDTO.class));
        verify(historiaClinicaService, times(1)).buscarPorMascota(mascotaResponse.getIdMascota());
        verify(historiaClinicaService, never()).actualizar(anyLong(), any(HistoriaClinicaRequestDTO.class));
        verify(historiaClinicaService, never()).crear(any());
    }

    @Test
    @DisplayName("Debe obtener información completa de mascota con historia y citas")
    void testObtenerInformacionCompletaMascota_Success() {
        // Arrange
        Long idMascota = 1L;
        List<CitaResponseDTO> citas = List.of(citaResponse);

        when(mascotaService.buscarPorId(idMascota)).thenReturn(mascotaResponse);
        when(historiaClinicaService.buscarPorMascota(idMascota)).thenReturn(historiaClinicaResponse);
        when(citaService.listarPorMascota(idMascota)).thenReturn(citas);

        // Act
        InformacionCompletaMascotaDTO resultado = operacionesFacadeService.obtenerInformacionCompletaMascota(
                idMascota
        );

        // Assert
        assertNotNull(resultado);
        assertEquals(mascotaResponse, resultado.getMascota());
        assertEquals(historiaClinicaResponse, resultado.getHistoriaClinica());
        assertEquals(1, resultado.getTotalCitas());
        assertEquals(citas, resultado.getCitas());
        verify(mascotaService, times(1)).buscarPorId(idMascota);
        verify(historiaClinicaService, times(1)).buscarPorMascota(idMascota);
        verify(citaService, times(1)).listarPorMascota(idMascota);
    }

    @Test
    @DisplayName("Debe obtener información de mascota sin historia clínica si no existe")
    void testObtenerInformacionCompletaMascota_SinHistoriaClinica() {
        // Arrange
        Long idMascota = 1L;
        List<CitaResponseDTO> citas = List.of(citaResponse);

        when(mascotaService.buscarPorId(idMascota)).thenReturn(mascotaResponse);
        when(historiaClinicaService.buscarPorMascota(idMascota))
                .thenThrow(new ResourceNotFoundException("Historia clínica no encontrada"));
        when(citaService.listarPorMascota(idMascota)).thenReturn(citas);

        // Act
        InformacionCompletaMascotaDTO resultado = operacionesFacadeService.obtenerInformacionCompletaMascota(
                idMascota
        );

        // Assert
        assertNotNull(resultado);
        assertEquals(mascotaResponse, resultado.getMascota());
        assertNull(resultado.getHistoriaClinica());
        assertEquals(1, resultado.getTotalCitas());
        verify(mascotaService, times(1)).buscarPorId(idMascota);
        verify(historiaClinicaService, times(1)).buscarPorMascota(idMascota);
        verify(citaService, times(1)).listarPorMascota(idMascota);
    }

    @Test
    @DisplayName("Debe obtener información completa de mascota sin citas")
    void testObtenerInformacionCompletaMascota_SinCitas() {
        // Arrange
        Long idMascota = 1L;
        List<CitaResponseDTO> citasVacias = new ArrayList<>();

        when(mascotaService.buscarPorId(idMascota)).thenReturn(mascotaResponse);
        when(historiaClinicaService.buscarPorMascota(idMascota)).thenReturn(historiaClinicaResponse);
        when(citaService.listarPorMascota(idMascota)).thenReturn(citasVacias);

        // Act
        InformacionCompletaMascotaDTO resultado = operacionesFacadeService.obtenerInformacionCompletaMascota(
                idMascota
        );

        // Assert
        assertNotNull(resultado);
        assertEquals(0, resultado.getTotalCitas());
        assertTrue(resultado.getCitas().isEmpty());
    }

    @Test
    @DisplayName("Debe obtener información completa de propietario con mascotas e historias")
    void testObtenerInformacionCompletaPropietario_Success() {
        // Arrange
        Long idPropietario = 1L;
        List<MascotaResponseDTO> mascotas = List.of(mascotaResponse);

        when(propietarioService.buscarPorId(idPropietario)).thenReturn(propietarioResponse);
        when(mascotaService.listarPorPropietario(idPropietario)).thenReturn(mascotas);
        when(historiaClinicaService.buscarPorMascota(1L)).thenReturn(historiaClinicaResponse);

        // Act
        InformacionCompletaPropietarioDTO resultado = operacionesFacadeService
                .obtenerInformacionCompletaPropietario(idPropietario);

        // Assert
        assertNotNull(resultado);
        assertEquals(propietarioResponse, resultado.getPropietario());
        assertEquals(1, resultado.getTotalMascotas());
        assertEquals(mascotas, resultado.getMascotas());
        assertEquals(1, resultado.getHistoriasClinicas().size());
        verify(propietarioService, times(1)).buscarPorId(idPropietario);
        verify(mascotaService, times(1)).listarPorPropietario(idPropietario);
        verify(historiaClinicaService, times(1)).buscarPorMascota(1L);
    }

    @Test
    @DisplayName("Debe obtener información de propietario cuando algunas mascotas no tienen historia")
    void testObtenerInformacionCompletaPropietario_AlgunasSinHistoria() {
        // Arrange
        Long idPropietario = 1L;
        List<MascotaResponseDTO> mascotas = List.of(mascotaResponse);

        when(propietarioService.buscarPorId(idPropietario)).thenReturn(propietarioResponse);
        when(mascotaService.listarPorPropietario(idPropietario)).thenReturn(mascotas);
        when(historiaClinicaService.buscarPorMascota(1L))
                .thenThrow(new ResourceNotFoundException("Historia no encontrada"));

        // Act
        InformacionCompletaPropietarioDTO resultado = operacionesFacadeService
                .obtenerInformacionCompletaPropietario(idPropietario);

        // Assert
        assertNotNull(resultado);
        assertEquals(propietarioResponse, resultado.getPropietario());
        assertEquals(1, resultado.getTotalMascotas());
        assertEquals(0, resultado.getHistoriasClinicas().size());
        verify(mascotaService, times(1)).listarPorPropietario(idPropietario);
    }

    @Test
    @DisplayName("Debe obtener información de propietario sin mascotas")
    void testObtenerInformacionCompletaPropietario_SinMascotas() {
        // Arrange
        Long idPropietario = 1L;
        List<MascotaResponseDTO> mascotasVacias = new ArrayList<>();

        when(propietarioService.buscarPorId(idPropietario)).thenReturn(propietarioResponse);
        when(mascotaService.listarPorPropietario(idPropietario)).thenReturn(mascotasVacias);

        // Act
        InformacionCompletaPropietarioDTO resultado = operacionesFacadeService
                .obtenerInformacionCompletaPropietario(idPropietario);

        // Assert
        assertNotNull(resultado);
        assertEquals(0, resultado.getTotalMascotas());
        assertTrue(resultado.getMascotas().isEmpty());
        assertTrue(resultado.getHistoriasClinicas().isEmpty());
    }

    @Test
    @DisplayName("Debe obtener información completa de veterinario con citas programadas")
    void testObtenerInformacionCompletaVeterinario_Success() {
        // Arrange
        Long idVeterinario = 1L;
        List<CitaResponseDTO> citas = List.of(citaResponse);

        when(veterinarioService.buscarPorId(idVeterinario)).thenReturn(veterinarioResponse);
        when(citaService.listarPorVeterinario(idVeterinario)).thenReturn(citas);

        // Act
        InformacionCompletaVeterinarioDTO resultado = operacionesFacadeService
                .obtenerInformacionCompletaVeterinario(idVeterinario);

        // Assert
        assertNotNull(resultado);
        assertEquals(veterinarioResponse, resultado.getVeterinario());
        assertEquals(1, resultado.getTotalCitasProgramadas());
        assertEquals(citas, resultado.getCitasProgramadas());
        assertEquals(0, resultado.getTotalHorarios());
        verify(veterinarioService, times(1)).buscarPorId(idVeterinario);
        verify(citaService, times(1)).listarPorVeterinario(idVeterinario);
    }

    @Test
    @DisplayName("Debe obtener información de veterinario sin citas programadas")
    void testObtenerInformacionCompletaVeterinario_SinCitas() {
        // Arrange
        Long idVeterinario = 1L;
        List<CitaResponseDTO> citasVacias = new ArrayList<>();

        when(veterinarioService.buscarPorId(idVeterinario)).thenReturn(veterinarioResponse);
        when(citaService.listarPorVeterinario(idVeterinario)).thenReturn(citasVacias);

        // Act
        InformacionCompletaVeterinarioDTO resultado = operacionesFacadeService
                .obtenerInformacionCompletaVeterinario(idVeterinario);

        // Assert
        assertNotNull(resultado);
        assertEquals(veterinarioResponse, resultado.getVeterinario());
        assertEquals(0, resultado.getTotalCitasProgramadas());
        assertTrue(resultado.getCitasProgramadas().isEmpty());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando mascota no existe al obtener información")
    void testObtenerInformacionCompletaMascota_MascotaNoEncontrada() {
        // Arrange
        Long idMascota = 999L;

        when(mascotaService.buscarPorId(idMascota))
                .thenThrow(new ResourceNotFoundException("Mascota no encontrada"));

        // Act & Assert
        try {
            operacionesFacadeService.obtenerInformacionCompletaMascota(idMascota);
        } catch (ResourceNotFoundException e) {
            assertEquals("Mascota no encontrada", e.getMessage());
        }

        verify(mascotaService, times(1)).buscarPorId(idMascota);
        verify(historiaClinicaService, never()).buscarPorMascota(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando propietario no existe")
    void testObtenerInformacionCompletaPropietario_PropietarioNoEncontrado() {
        // Arrange
        Long idPropietario = 999L;

        when(propietarioService.buscarPorId(idPropietario))
                .thenThrow(new ResourceNotFoundException("Propietario no encontrado"));

        // Act & Assert
        try {
            operacionesFacadeService.obtenerInformacionCompletaPropietario(idPropietario);
        } catch (ResourceNotFoundException e) {
            assertEquals("Propietario no encontrado", e.getMessage());
        }

        verify(propietarioService, times(1)).buscarPorId(idPropietario);
        verify(mascotaService, never()).listarPorPropietario(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando veterinario no existe")
    void testObtenerInformacionCompletaVeterinario_VeterinarioNoEncontrado() {
        // Arrange
        Long idVeterinario = 999L;

        when(veterinarioService.buscarPorId(idVeterinario))
                .thenThrow(new ResourceNotFoundException("Veterinario no encontrado"));

        // Act & Assert
        try {
            operacionesFacadeService.obtenerInformacionCompletaVeterinario(idVeterinario);
        } catch (ResourceNotFoundException e) {
            assertEquals("Veterinario no encontrado", e.getMessage());
        }

        verify(veterinarioService, times(1)).buscarPorId(idVeterinario);
        verify(citaService, never()).listarPorVeterinario(any());
    }
}
