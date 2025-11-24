package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.dto.request.clinico.HistoriaClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.HistoriaClinicaResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.mapper.clinico.HistoriaClinicaMapper;
import com.veterinaria.clinica_veternica.patterns.behavioral.memento.HistoriaClinicaCaretaker;
import com.veterinaria.clinica_veternica.repository.HistoriaClinicaRepository;
import com.veterinaria.clinica_veternica.repository.MascotaRepository;
import com.veterinaria.clinica_veternica.service.impl.HistoriaClinicaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests básicos CRUD para HistoriaClinicaService
 */
@ExtendWith(MockitoExtension.class)
class HistoriaClinicaServiceTest {

    @Mock
    private HistoriaClinicaRepository historiaClinicaRepository;

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private HistoriaClinicaMapper historiaClinicaMapper;

    @Mock
    private HistoriaClinicaCaretaker historiaClinicaCaretaker;

    @Mock
    private com.veterinaria.clinica_veternica.patterns.structural.proxy.HistoriaClinicaProxy historiaClinicaProxy;

    @InjectMocks
    private HistoriaClinicaServiceImpl historiaClinicaService;

    private HistoriaClinicaRequestDTO requestDTO;
    private HistoriaClinica historiaClinica;
    private HistoriaClinicaResponseDTO responseDTO;
    private Mascota mascota;

    @BeforeEach
    void setUp() {
        mascota = Mascota.builder()
                .idMascota(1L)
                .nombre("Max")
                .build();

        requestDTO = HistoriaClinicaRequestDTO.builder()
                .idMascota(1L)
                .numeroHistoria("HC-001")
                .alergias("Ninguna")
                .enfermedadesCronicas("Ninguna")
                .observaciones("Mascota saludable")
                .build();

        historiaClinica = HistoriaClinica.builder()
                .idHistoriaClinica(1L)
                .numeroHistoria("HC-001")
                .mascota(mascota)
                .alergias("Ninguna")
                .enfermedadesCronicas("Ninguna")
                .observacionesGenerales("Mascota saludable")
                .activa(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        responseDTO = HistoriaClinicaResponseDTO.builder()
                .idHistoriaClinica(1L)
                .numeroHistoria("HC-001")
                .alergias("Ninguna")
                .enfermedadesCronicas("Ninguna")
                .observaciones("Mascota saludable")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("CREATE - Debe crear una historia clínica exitosamente")
    void testCrearHistoriaClinicaExitoso() {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(historiaClinicaRepository.findByMascota(mascota)).thenReturn(Optional.empty());
        when(historiaClinicaMapper.toEntity(requestDTO)).thenReturn(historiaClinica);
        when(historiaClinicaRepository.save(any(HistoriaClinica.class))).thenReturn(historiaClinica);
        when(historiaClinicaMapper.toResponseDTO(historiaClinica)).thenReturn(responseDTO);
        doNothing().when(historiaClinicaCaretaker).guardarMemento(historiaClinica);

        HistoriaClinicaResponseDTO resultado = historiaClinicaService.crear(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdHistoriaClinica());
        assertEquals("HC-001", resultado.getNumeroHistoria());
        verify(historiaClinicaRepository, times(1)).save(any(HistoriaClinica.class));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción si mascota no existe")
    void testCrearHistoriaClinicaMascotaNoExiste() {
        when(mascotaRepository.findById(999L)).thenReturn(Optional.empty());

        requestDTO.setIdMascota(999L);
        assertThrows(ResourceNotFoundException.class, () -> historiaClinicaService.crear(requestDTO));
    }

    @Test
    @DisplayName("READ - Debe buscar historia clínica por ID exitosamente")
    void testBuscarHistoriaClinicaPorIdExitoso() {
        when(historiaClinicaRepository.findById(1L)).thenReturn(Optional.of(historiaClinica));
        when(historiaClinicaProxy.tienePermisoLectura(any())).thenReturn(true);
        when(historiaClinicaMapper.toResponseDTO(historiaClinica)).thenReturn(responseDTO);

        HistoriaClinicaResponseDTO resultado = historiaClinicaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdHistoriaClinica());
        assertEquals("HC-001", resultado.getNumeroHistoria());
    }

    @Test
    @DisplayName("READ - Debe lanzar excepción cuando historia clínica no existe")
    void testBuscarHistoriaClinicaPorIdNoEncontrado() {
        when(historiaClinicaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> historiaClinicaService.buscarPorId(999L));
    }

    @Test
    @DisplayName("UPDATE - Debe actualizar una historia clínica exitosamente")
    void testActualizarHistoriaClinicaExitoso() {
        when(historiaClinicaRepository.findById(1L)).thenReturn(Optional.of(historiaClinica));
        when(historiaClinicaProxy.tienePermisoEscritura(any())).thenReturn(true);
        when(historiaClinicaRepository.save(any(HistoriaClinica.class))).thenReturn(historiaClinica);
        when(historiaClinicaMapper.toResponseDTO(historiaClinica)).thenReturn(responseDTO);
        doNothing().when(historiaClinicaCaretaker).guardarMemento(historiaClinica);

        HistoriaClinicaResponseDTO resultado = historiaClinicaService.actualizar(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdHistoriaClinica());
        verify(historiaClinicaRepository, times(1)).save(any(HistoriaClinica.class));
    }

    @Test
    @DisplayName("DELETE - Debe archivar una historia clínica exitosamente")
    void testArchivarHistoriaClinicaExitoso() {
        when(historiaClinicaRepository.findById(1L)).thenReturn(Optional.of(historiaClinica));
        when(historiaClinicaRepository.save(any(HistoriaClinica.class))).thenReturn(historiaClinica);

        assertDoesNotThrow(() -> historiaClinicaService.archivar(1L, "Archivado por administración"));
        verify(historiaClinicaRepository, times(1)).save(any(HistoriaClinica.class));
    }
}

