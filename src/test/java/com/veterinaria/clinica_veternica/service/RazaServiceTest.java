package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.paciente.Especie;
import com.veterinaria.clinica_veternica.domain.paciente.Raza;
import com.veterinaria.clinica_veternica.dto.request.paciente.RazaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.RazaResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.paciente.RazaMapper;
import com.veterinaria.clinica_veternica.repository.EspecieRepository;
import com.veterinaria.clinica_veternica.repository.RazaRepository;
import com.veterinaria.clinica_veternica.service.impl.RazaServiceImpl;
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
class RazaServiceTest {

    @Mock
    private RazaRepository razaRepository;

    @Mock
    private EspecieRepository especieRepository;

    @Mock
    private RazaMapper razaMapper;

    @InjectMocks
    private RazaServiceImpl razaService;

    private RazaRequestDTO requestDTO;
    private Raza raza;
    private RazaResponseDTO responseDTO;
    private Especie especie;

    @BeforeEach
    void setUp() {
        especie = Especie.builder()
                .idEspecie(1L)
                .nombre("Canino")
                .activo(true)
                .build();

        requestDTO = new RazaRequestDTO();
        requestDTO.setNombre("Labrador");
        requestDTO.setIdEspecie(1L);
        requestDTO.setDescripcion("Raza labrador");

        raza = Raza.builder()
                .idRaza(1L)
                .nombre("Labrador")
                .descripcion("Raza labrador")
                .especie(especie)
                .activo(true)
                .build();

        responseDTO = new RazaResponseDTO();
        responseDTO.setIdRaza(1L);
        responseDTO.setNombre("Labrador");
        responseDTO.setDescripcion("Raza labrador");
        responseDTO.setActivo(true);
    }

    @Test
    @DisplayName("CREATE - Debe crear una raza exitosamente")
    void testCrearRazaExitoso() {
        when(especieRepository.findById(1L)).thenReturn(Optional.of(especie));
        when(razaRepository.existsByNombreAndEspecieId("Labrador", 1L)).thenReturn(false);
        when(razaMapper.toEntity(requestDTO)).thenReturn(raza);
        when(razaRepository.save(any(Raza.class))).thenReturn(raza);
        when(razaMapper.toResponseDTO(raza)).thenReturn(responseDTO);

        RazaResponseDTO resultado = razaService.crear(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdRaza());
        assertEquals("Labrador", resultado.getNombre());
        verify(razaRepository, times(1)).save(any(Raza.class));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción cuando especie no existe")
    void testCrearRazaEspecieNoExiste() {
        when(especieRepository.findById(999L)).thenReturn(Optional.empty());

        requestDTO.setIdEspecie(999L);
        assertThrows(ResourceNotFoundException.class, () -> razaService.crear(requestDTO));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción cuando raza ya existe")
    void testCrearRazaDuplicada() {
        when(especieRepository.findById(1L)).thenReturn(Optional.of(especie));
        when(razaRepository.existsByNombreAndEspecieId("Labrador", 1L)).thenReturn(true);

        assertThrows(ValidationException.class, () -> razaService.crear(requestDTO));
    }

    @Test
    @DisplayName("READ - Debe buscar raza por ID exitosamente")
    void testBuscarRazaPorIdExitoso() {
        when(razaRepository.findById(1L)).thenReturn(Optional.of(raza));
        when(razaMapper.toResponseDTO(raza)).thenReturn(responseDTO);

        RazaResponseDTO resultado = razaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdRaza());
    }

    @Test
    @DisplayName("READ - Debe lanzar excepción cuando raza no existe")
    void testBuscarRazaPorIdNoEncontrado() {
        when(razaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> razaService.buscarPorId(999L));
    }

    @Test
    @DisplayName("READ - Debe listar todas las razas")
    void testListarTodasLasRazas() {
        List<Raza> razas = Arrays.asList(raza);
        List<RazaResponseDTO> responseDTOs = Arrays.asList(responseDTO);
        when(razaRepository.findAll()).thenReturn(razas);
        when(razaMapper.toResponseDTOList(razas)).thenReturn(responseDTOs);

        List<RazaResponseDTO> resultado = razaService.listarTodas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }
}

