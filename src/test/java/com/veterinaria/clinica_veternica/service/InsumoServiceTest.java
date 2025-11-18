package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.inventario.EstadoInsumo;
import com.veterinaria.clinica_veternica.domain.inventario.Insumo;
import com.veterinaria.clinica_veternica.domain.inventario.TipoInsumo;
import com.veterinaria.clinica_veternica.dto.request.inventario.InsumoRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.InsumoResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.inventario.InsumoMapper;
import com.veterinaria.clinica_veternica.repository.InsumoRepository;
import com.veterinaria.clinica_veternica.repository.TipoInsumoRepository;
import com.veterinaria.clinica_veternica.service.impl.InsumoServiceImpl;
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
class InsumoServiceTest {

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private TipoInsumoRepository tipoInsumoRepository;

    @Mock
    private InsumoMapper insumoMapper;

    @InjectMocks
    private InsumoServiceImpl insumoService;

    private InsumoRequestDTO requestDTO;
    private Insumo insumo;
    private InsumoResponseDTO responseDTO;
    private TipoInsumo tipoInsumo;

    @BeforeEach
    void setUp() {
        tipoInsumo = TipoInsumo.builder()
                .idTipoInsumo(1L)
                .nombre("Material Médico")
                .activo(true)
                .build();

        requestDTO = InsumoRequestDTO.builder()
                .nombre("Jeringas")
                .codigo("INS001")
                .cantidadStock(100)
                .stockMinimo(10)
                .precioCompra(BigDecimal.valueOf(5.50))
                .idTipoInsumo(1L)
                .unidadMedida("Unidad")
                .estado(EstadoInsumo.DISPONIBLE)
                .build();

        insumo = Insumo.builder()
                .idInsumo(1L)
                .nombre("Jeringas")
                .codigo("INS001")
                .cantidadStock(100)
                .stockMinimo(10)
                .precioCompra(BigDecimal.valueOf(5.50))
                .tipoInsumo(tipoInsumo)
                .activo(true)
                .build();

        responseDTO = InsumoResponseDTO.builder()
                .idInsumo(1L)
                .nombre("Jeringas")
                .codigo("INS001")
                .cantidadStock(100)
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("CREATE - Debe crear un insumo exitosamente")
    void testCrearInsumoExitoso() {
        when(insumoRepository.existsByCodigo("INS001")).thenReturn(false);
        when(tipoInsumoRepository.findById(1L)).thenReturn(Optional.of(tipoInsumo));
        when(insumoMapper.toEntity(requestDTO)).thenReturn(insumo);
        when(insumoRepository.save(any(Insumo.class))).thenReturn(insumo);
        when(insumoMapper.toResponseDTO(insumo)).thenReturn(responseDTO);

        InsumoResponseDTO resultado = insumoService.crear(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdInsumo());
        assertEquals("Jeringas", resultado.getNombre());
        verify(insumoRepository, times(1)).save(any(Insumo.class));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción cuando código ya existe")
    void testCrearInsumoCodigoDuplicado() {
        when(insumoRepository.existsByCodigo("INS001")).thenReturn(true);

        assertThrows(ValidationException.class, () -> insumoService.crear(requestDTO));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción cuando tipo de insumo no existe")
    void testCrearInsumoTipoNoExiste() {
        when(insumoRepository.existsByCodigo("INS001")).thenReturn(false);
        when(tipoInsumoRepository.findById(999L)).thenReturn(Optional.empty());

        requestDTO.setIdTipoInsumo(999L);
        assertThrows(ResourceNotFoundException.class, () -> insumoService.crear(requestDTO));
    }

    @Test
    @DisplayName("READ - Debe buscar insumo por ID exitosamente")
    void testBuscarInsumoPorIdExitoso() {
        when(insumoRepository.findById(1L)).thenReturn(Optional.of(insumo));
        when(insumoMapper.toResponseDTO(insumo)).thenReturn(responseDTO);

        InsumoResponseDTO resultado = insumoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdInsumo());
    }

    @Test
    @DisplayName("READ - Debe lanzar excepción cuando insumo no existe")
    void testBuscarInsumoPorIdNoEncontrado() {
        when(insumoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> insumoService.buscarPorId(999L));
    }

    @Test
    @DisplayName("READ - Debe listar todos los insumos")
    void testListarTodosLosInsumos() {
        List<Insumo> insumos = Arrays.asList(insumo);
        List<InsumoResponseDTO> responseDTOs = Arrays.asList(responseDTO);
        when(insumoRepository.findAll()).thenReturn(insumos);
        when(insumoMapper.toResponseDTOList(insumos)).thenReturn(responseDTOs);

        List<InsumoResponseDTO> resultado = insumoService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }
}

