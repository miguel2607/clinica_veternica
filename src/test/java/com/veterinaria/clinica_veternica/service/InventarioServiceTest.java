package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.inventario.Inventario;
import com.veterinaria.clinica_veternica.domain.inventario.Insumo;
import com.veterinaria.clinica_veternica.dto.response.inventario.InventarioResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.mapper.inventario.InventarioMapper;
import com.veterinaria.clinica_veternica.patterns.structural.proxy.InventarioProxy;
import com.veterinaria.clinica_veternica.repository.InventarioRepository;
import com.veterinaria.clinica_veternica.repository.InsumoRepository;
import com.veterinaria.clinica_veternica.service.impl.InventarioServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private InventarioMapper inventarioMapper;

    @Mock
    private InventarioProxy inventarioProxy;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    private Inventario inventario;
    private InventarioResponseDTO responseDTO;
    private Insumo insumo;

    @BeforeEach
    void setUp() {
        insumo = Insumo.builder()
                .idInsumo(1L)
                .nombre("Jeringas")
                .codigo("INS001")
                .build();

        inventario = Inventario.builder()
                .idInventario(1L)
                .insumo(insumo)
                .cantidadActual(100)
                .build();

        responseDTO = new InventarioResponseDTO();
        responseDTO.setIdInventario(1L);
        responseDTO.setCantidadActual(100);
    }

    @Test
    @DisplayName("READ - Debe buscar inventario por ID exitosamente")
    void testBuscarInventarioPorIdExitoso() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        when(inventarioMapper.toResponseDTO(inventario)).thenReturn(responseDTO);

        InventarioResponseDTO resultado = inventarioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdInventario());
    }

    @Test
    @DisplayName("READ - Debe lanzar excepción cuando inventario no existe")
    void testBuscarInventarioPorIdNoEncontrado() {
        when(inventarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inventarioService.buscarPorId(999L));
    }

    @Test
    @DisplayName("READ - Debe buscar inventario por insumo exitosamente")
    void testBuscarInventarioPorInsumoExitoso() {
        when(insumoRepository.findById(1L)).thenReturn(Optional.of(insumo));
        // El servicio usa findByInsumoWithFetch
        when(inventarioRepository.findByInsumoWithFetch(insumo)).thenReturn(Optional.of(inventario));
        // El servicio puede llamar a save si crea un nuevo inventario, pero en este caso ya existe
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);
        when(inventarioMapper.toResponseDTO(inventario)).thenReturn(responseDTO);

        InventarioResponseDTO resultado = inventarioService.buscarPorInsumo(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdInventario());
    }

    @Test
    @DisplayName("READ - Debe lanzar excepción cuando insumo no existe")
    void testBuscarInventarioPorInsumoNoExiste() {
        when(insumoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inventarioService.buscarPorInsumo(999L));
    }

    @Test
    @DisplayName("READ - Debe listar todos los inventarios")
    void testListarTodosLosInventarios() {
        List<Insumo> insumos = Arrays.asList(insumo);
        List<Inventario> inventarios = Arrays.asList(inventario);
        List<InventarioResponseDTO> responseDTOs = Arrays.asList(responseDTO);
        
        // El servicio llama a findInsumosActivos() y luego crea/obtiene inventarios
        when(insumoRepository.findInsumosActivos()).thenReturn(insumos);
        // El servicio usa findByInsumoWithFetch dentro de obtenerOcrearInventario
        when(inventarioRepository.findByInsumoWithFetch(insumo)).thenReturn(Optional.of(inventario));
        // El servicio puede llamar a save para sincronizar
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);
        when(inventarioMapper.toResponseDTOList(anyList())).thenReturn(responseDTOs);

        List<InventarioResponseDTO> resultado = inventarioService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }
}

