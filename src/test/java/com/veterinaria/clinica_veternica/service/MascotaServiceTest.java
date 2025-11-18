package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.paciente.*;
import com.veterinaria.clinica_veternica.dto.request.paciente.MascotaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.mapper.paciente.MascotaMapper;
import com.veterinaria.clinica_veternica.repository.*;
import com.veterinaria.clinica_veternica.service.impl.MascotaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests básicos CRUD para MascotaService
 */
@ExtendWith(MockitoExtension.class)
class MascotaServiceTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private PropietarioRepository propietarioRepository;

    @Mock
    private EspecieRepository especieRepository;

    @Mock
    private RazaRepository razaRepository;

    @Mock
    private MascotaMapper mascotaMapper;

    @InjectMocks
    private MascotaServiceImpl mascotaService;

    private MascotaRequestDTO requestDTO;
    private Mascota mascota;
    private MascotaResponseDTO responseDTO;
    private Propietario propietario;
    private Especie especie;
    private Raza raza;

    @BeforeEach
    void setUp() {
        propietario = Propietario.builder()
                .idPropietario(1L)
                .nombres("Juan")
                .apellidos("Pérez")
                .build();

        especie = Especie.builder()
                .idEspecie(1L)
                .nombre("Canino")
                .build();

        raza = Raza.builder()
                .idRaza(1L)
                .nombre("Labrador")
                .especie(especie)
                .build();

        requestDTO = MascotaRequestDTO.builder()
                .nombre("Max")
                .sexo("Macho")
                .fechaNacimiento(LocalDate.of(2020, 1, 15))
                .color("Café")
                .peso(15.5)
                .idPropietario(1L)
                .idEspecie(1L)
                .idRaza(1L)
                .build();

        mascota = Mascota.builder()
                .idMascota(1L)
                .nombre("Max")
                .sexo("Macho")
                .fechaNacimiento(LocalDate.of(2020, 1, 15))
                .color("Café")
                .peso(15.5)
                .propietario(propietario)
                .especie(especie)
                .raza(raza)
                .activo(true)
                .build();

        responseDTO = MascotaResponseDTO.builder()
                .idMascota(1L)
                .nombre("Max")
                .sexo("Macho")
                .color("Café")
                .peso(15.5)
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("CREATE - Debe crear una mascota exitosamente")
    void testCrearMascotaExitoso() {
        when(propietarioRepository.findById(1L)).thenReturn(Optional.of(propietario));
        when(especieRepository.findById(1L)).thenReturn(Optional.of(especie));
        when(razaRepository.findById(1L)).thenReturn(Optional.of(raza));
        when(mascotaMapper.toEntity(requestDTO)).thenReturn(mascota);
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(mascota);
        when(mascotaMapper.toResponseDTO(mascota)).thenReturn(responseDTO);

        MascotaResponseDTO resultado = mascotaService.crear(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdMascota());
        assertEquals("Max", resultado.getNombre());
        verify(mascotaRepository, times(1)).save(any(Mascota.class));
    }

    @Test
    @DisplayName("READ - Debe buscar mascota por ID exitosamente")
    void testBuscarMascotaPorIdExitoso() {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(mascotaMapper.toResponseDTO(mascota)).thenReturn(responseDTO);

        MascotaResponseDTO resultado = mascotaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdMascota());
        assertEquals("Max", resultado.getNombre());
    }

    @Test
    @DisplayName("READ - Debe lanzar excepción cuando mascota no existe")
    void testBuscarMascotaPorIdNoEncontrado() {
        when(mascotaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mascotaService.buscarPorId(999L));
    }

    @Test
    @DisplayName("UPDATE - Debe actualizar una mascota exitosamente")
    void testActualizarMascotaExitoso() {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(propietarioRepository.findById(1L)).thenReturn(Optional.of(propietario));
        when(especieRepository.findById(1L)).thenReturn(Optional.of(especie));
        when(razaRepository.findById(1L)).thenReturn(Optional.of(raza));
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(mascota);
        when(mascotaMapper.toResponseDTO(mascota)).thenReturn(responseDTO);

        MascotaResponseDTO resultado = mascotaService.actualizar(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdMascota());
        verify(mascotaRepository, times(1)).save(any(Mascota.class));
    }

    @Test
    @DisplayName("DELETE - Debe eliminar una mascota exitosamente (soft delete)")
    void testEliminarMascotaExitoso() {
        mascota.setHistoriaClinica(null);
        mascota.setCitas(null);
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(mascota);

        assertDoesNotThrow(() -> mascotaService.eliminar(1L));
        verify(mascotaRepository, times(1)).save(any(Mascota.class));
        assertFalse(mascota.getActivo());
    }
}

