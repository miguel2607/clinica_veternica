package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.paciente.Especie;
import com.veterinaria.clinica_veternica.dto.request.paciente.EspecieRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.EspecieResponseDTO;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.paciente.EspecieMapper;
import com.veterinaria.clinica_veternica.repository.EspecieRepository;
import com.veterinaria.clinica_veternica.service.impl.EspecieServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EspecieServiceTest {

    @Mock
    private EspecieRepository especieRepository;

    @Mock
    private EspecieMapper especieMapper;

    @InjectMocks
    private EspecieServiceImpl especieService;

    private EspecieRequestDTO requestDTO;
    private Especie especie;
    private EspecieResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new EspecieRequestDTO();
        requestDTO.setNombre("Canino");
        requestDTO.setDescripcion("Especie canina");

        especie = Especie.builder()
                .idEspecie(1L)
                .nombre("Canino")
                .descripcion("Especie canina")
                .activo(true)
                .build();

        responseDTO = new EspecieResponseDTO();
        responseDTO.setIdEspecie(1L);
        responseDTO.setNombre("Canino");
        responseDTO.setDescripcion("Especie canina");
        responseDTO.setActivo(true);
    }

    @Test
    @DisplayName("CREATE - Debe crear una especie exitosamente")
    void testCrearEspecieExitoso() {
        when(especieRepository.existsByNombreIgnoreCase("Canino")).thenReturn(false);
        when(especieMapper.toEntity(requestDTO)).thenReturn(especie);
        when(especieRepository.save(any(Especie.class))).thenReturn(especie);
        when(especieMapper.toResponseDTO(especie)).thenReturn(responseDTO);

        EspecieResponseDTO resultado = especieService.crear(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdEspecie());
        assertEquals("Canino", resultado.getNombre());
        verify(especieRepository, times(1)).save(any(Especie.class));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción cuando especie ya existe")
    void testCrearEspecieDuplicada() {
        when(especieRepository.existsByNombreIgnoreCase("Canino")).thenReturn(true);

        assertThrows(ValidationException.class, () -> especieService.crear(requestDTO));
        verify(especieRepository, never()).save(any(Especie.class));
    }

    @Test
    @DisplayName("READ - Debe buscar especie por ID exitosamente")
    void testBuscarEspeciePorIdExitoso() {
        when(especieRepository.findById(1L)).thenReturn(Optional.of(especie));
        when(especieMapper.toResponseDTO(especie)).thenReturn(responseDTO);

        EspecieResponseDTO resultado = especieService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdEspecie());
    }

    @Test
    @DisplayName("READ - Debe lanzar excepción cuando especie no existe")
    void testBuscarEspeciePorIdNoEncontrado() {
        when(especieRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> especieService.buscarPorId(999L));
    }

    @Test
    @DisplayName("READ - Debe listar todas las especies")
    void testListarTodasLasEspecies() {
        List<Especie> especies = Arrays.asList(especie);
        List<EspecieResponseDTO> responseDTOs = Arrays.asList(responseDTO);
        when(especieRepository.findAll()).thenReturn(especies);
        when(especieMapper.toResponseDTOList(especies)).thenReturn(responseDTOs);

        List<EspecieResponseDTO> resultado = especieService.listarTodas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("READ - Debe listar especies activas")
    void testListarEspeciesActivas() {
        List<Especie> especies = Arrays.asList(especie);
        List<EspecieResponseDTO> responseDTOs = Arrays.asList(responseDTO);
        when(especieRepository.findByActivoTrue()).thenReturn(especies);
        when(especieMapper.toResponseDTOList(especies)).thenReturn(responseDTOs);

        List<EspecieResponseDTO> resultado = especieService.listarActivas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("READ - Debe buscar especies por nombre")
    void testBuscarEspeciesPorNombre() {
        List<Especie> especies = Arrays.asList(especie);
        List<EspecieResponseDTO> responseDTOs = Arrays.asList(responseDTO);
        when(especieRepository.findByNombreContainingIgnoreCase("Canino")).thenReturn(especies);
        when(especieMapper.toResponseDTOList(especies)).thenReturn(responseDTOs);

        List<EspecieResponseDTO> resultado = especieService.buscarPorNombre("Canino");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("UPDATE - Debe actualizar una especie exitosamente")
    void testActualizarEspecieExitoso() {
        when(especieRepository.findById(1L)).thenReturn(Optional.of(especie));
        when(especieRepository.existsByNombreIgnoreCase("Canino Actualizado")).thenReturn(false);
        when(especieRepository.save(any(Especie.class))).thenReturn(especie);
        when(especieMapper.toResponseDTO(especie)).thenReturn(responseDTO);

        EspecieRequestDTO updateDTO = new EspecieRequestDTO();
        updateDTO.setNombre("Canino Actualizado");
        EspecieResponseDTO resultado = especieService.actualizar(1L, updateDTO);

        assertNotNull(resultado);
        verify(especieRepository, times(1)).save(any(Especie.class));
    }

    @Test
    @DisplayName("DELETE - Debe eliminar una especie exitosamente (soft delete)")
    void testEliminarEspecieExitoso() {
        especie.setRazas(null);
        when(especieRepository.findById(1L)).thenReturn(Optional.of(especie));
        when(especieRepository.save(any(Especie.class))).thenReturn(especie);

        assertDoesNotThrow(() -> especieService.eliminar(1L));
        verify(especieRepository, times(1)).save(any(Especie.class));
        assertFalse(especie.getActivo());
    }

    @Test
    @DisplayName("DELETE - Debe lanzar excepción cuando especie tiene razas asociadas")
    void testEliminarEspecieConRazas() {
        // Crear una lista con al menos una raza para que falle la validación
        com.veterinaria.clinica_veternica.domain.paciente.Raza raza = 
            com.veterinaria.clinica_veternica.domain.paciente.Raza.builder()
                .idRaza(1L)
                .nombre("Labrador")
                .build();
        especie.setRazas(Arrays.asList(raza));
        when(especieRepository.findById(1L)).thenReturn(Optional.of(especie));

        assertThrows(BusinessException.class, () -> especieService.eliminar(1L));
    }

    @Test
    @DisplayName("PATCH - Debe activar una especie exitosamente")
    void testActivarEspecieExitoso() {
        especie.setActivo(false);
        when(especieRepository.findById(1L)).thenReturn(Optional.of(especie));
        when(especieRepository.save(any(Especie.class))).thenReturn(especie);
        when(especieMapper.toResponseDTO(especie)).thenReturn(responseDTO);

        EspecieResponseDTO resultado = especieService.activar(1L);

        assertNotNull(resultado);
        assertTrue(especie.getActivo());
    }

    @Test
    @DisplayName("PATCH - Debe lanzar excepción cuando especie ya está activa")
    void testActivarEspecieYaActiva() {
        when(especieRepository.findById(1L)).thenReturn(Optional.of(especie));

        assertThrows(BusinessException.class, () -> especieService.activar(1L));
    }

    @Test
    @DisplayName("GET - Debe verificar existencia por nombre")
    void testExistePorNombre() {
        when(especieRepository.existsByNombreIgnoreCase("Canino")).thenReturn(true);

        boolean resultado = especieService.existePorNombre("Canino");

        assertTrue(resultado);
    }
}

