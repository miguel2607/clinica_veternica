package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.agenda.CategoriaServicio;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import com.veterinaria.clinica_veternica.dto.request.agenda.ServicioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.ServicioResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.agenda.ServicioMapper;
import com.veterinaria.clinica_veternica.patterns.creational.factory.ServicioFactory;
import com.veterinaria.clinica_veternica.repository.ServicioRepository;
import com.veterinaria.clinica_veternica.service.impl.ServicioServiceImpl;
import com.veterinaria.clinica_veternica.service.registry.ServicioFactoryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioServiceTest {

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private ServicioMapper servicioMapper;

    @Mock
    private ServicioFactoryRegistry factoryRegistry;

    @Mock
    private ServicioFactory servicioFactory;

    @InjectMocks
    private ServicioServiceImpl servicioService;

    private ServicioRequestDTO requestDTO;
    private Servicio servicio;
    private ServicioResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new ServicioRequestDTO();
        requestDTO.setNombre("Consulta General");
        requestDTO.setDescripcion("Consulta veterinaria general");
        requestDTO.setPrecio(BigDecimal.valueOf(50.00));
        requestDTO.setCategoria("CLINICO");

        servicio = Servicio.builder()
                .idServicio(1L)
                .nombre("Consulta General")
                .descripcion("Consulta veterinaria general")
                .precio(BigDecimal.valueOf(50.00))
                .categoria(CategoriaServicio.CLINICO)
                .activo(true)
                .build();

        responseDTO = new ServicioResponseDTO();
        responseDTO.setIdServicio(1L);
        responseDTO.setNombre("Consulta General");
        responseDTO.setPrecio(BigDecimal.valueOf(50.00));
        responseDTO.setActivo(true);
    }

    @Test
    @DisplayName("CREATE - Debe crear un servicio exitosamente")
    void testCrearServicioExitoso() {
        when(servicioRepository.existsByNombre("Consulta General")).thenReturn(false);
        when(servicioMapper.toEntity(requestDTO)).thenReturn(servicio);
        when(servicioRepository.save(any(Servicio.class))).thenReturn(servicio);
        when(servicioMapper.toResponseDTO(servicio)).thenReturn(responseDTO);

        ServicioResponseDTO resultado = servicioService.crear(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdServicio());
        assertEquals("Consulta General", resultado.getNombre());
        verify(servicioRepository, times(1)).save(any(Servicio.class));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción cuando servicio ya existe")
    void testCrearServicioDuplicado() {
        when(servicioRepository.existsByNombre("Consulta General")).thenReturn(true);

        assertThrows(ValidationException.class, () -> servicioService.crear(requestDTO));
    }

    @Test
    @DisplayName("CREATE - Debe crear servicio con Factory exitosamente")
    void testCrearServicioConFactory() {
        when(factoryRegistry.obtenerFactory(CategoriaServicio.CLINICO)).thenReturn(servicioFactory);
        when(servicioFactory.crearServicioCompleto(anyString(), anyString(), any(BigDecimal.class))).thenReturn(servicio);
        when(servicioRepository.save(any(Servicio.class))).thenReturn(servicio);
        when(servicioMapper.toResponseDTO(servicio)).thenReturn(responseDTO);

        ServicioResponseDTO resultado = servicioService.crearConFactory(
                "Consulta General", "Descripción", BigDecimal.valueOf(50.00), "CLINICO");

        assertNotNull(resultado);
        verify(factoryRegistry, times(1)).obtenerFactory(CategoriaServicio.CLINICO);
    }

    @Test
    @DisplayName("READ - Debe buscar servicio por ID exitosamente")
    void testBuscarServicioPorIdExitoso() {
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio));
        when(servicioMapper.toResponseDTO(servicio)).thenReturn(responseDTO);

        ServicioResponseDTO resultado = servicioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdServicio());
    }

    @Test
    @DisplayName("READ - Debe lanzar excepción cuando servicio no existe")
    void testBuscarServicioPorIdNoEncontrado() {
        when(servicioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> servicioService.buscarPorId(999L));
    }

    @Test
    @DisplayName("READ - Debe listar todos los servicios")
    void testListarTodosLosServicios() {
        List<Servicio> servicios = Arrays.asList(servicio);
        List<ServicioResponseDTO> responseDTOs = Arrays.asList(responseDTO);
        when(servicioRepository.findAll()).thenReturn(servicios);
        when(servicioMapper.toResponseDTOList(servicios)).thenReturn(responseDTOs);

        List<ServicioResponseDTO> resultado = servicioService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }
}

