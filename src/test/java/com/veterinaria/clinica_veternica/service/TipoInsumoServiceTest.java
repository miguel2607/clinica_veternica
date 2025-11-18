package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.inventario.Insumo;
import com.veterinaria.clinica_veternica.domain.inventario.TipoInsumo;
import com.veterinaria.clinica_veternica.dto.request.inventario.TipoInsumoRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.TipoInsumoResponseDTO;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.inventario.TipoInsumoMapper;
import com.veterinaria.clinica_veternica.repository.TipoInsumoRepository;
import com.veterinaria.clinica_veternica.service.impl.TipoInsumoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoInsumoServiceTest {

    @Mock
    private TipoInsumoRepository tipoInsumoRepository;

    @Mock
    private TipoInsumoMapper tipoInsumoMapper;

    @InjectMocks
    private TipoInsumoServiceImpl tipoInsumoService;

    private TipoInsumoRequestDTO requestDTO;
    private TipoInsumo tipoInsumo;
    private TipoInsumoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new TipoInsumoRequestDTO();
        requestDTO.setNombre("Material Médico");
        requestDTO.setDescripcion("Material médico descartable");

        tipoInsumo = TipoInsumo.builder()
                .idTipoInsumo(1L)
                .nombre("Material Médico")
                .descripcion("Material médico descartable")
                .activo(true)
                .build();

        responseDTO = new TipoInsumoResponseDTO();
        responseDTO.setIdTipoInsumo(1L);
        responseDTO.setNombre("Material Médico");
        responseDTO.setActivo(true);
    }

    @Test
    @DisplayName("CREATE - Debe crear tipo de insumo exitosamente")
    void testCrearTipoInsumoExitoso() {
        when(tipoInsumoRepository.existsByNombre("Material Médico")).thenReturn(false);
        when(tipoInsumoMapper.toEntity(requestDTO)).thenReturn(tipoInsumo);
        when(tipoInsumoRepository.save(any(TipoInsumo.class))).thenReturn(tipoInsumo);
        when(tipoInsumoMapper.toResponseDTO(tipoInsumo)).thenReturn(responseDTO);

        TipoInsumoResponseDTO resultado = tipoInsumoService.crear(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdTipoInsumo());
        verify(tipoInsumoRepository, times(1)).save(any(TipoInsumo.class));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción cuando nombre ya existe")
    void testCrearTipoInsumoNombreDuplicado() {
        when(tipoInsumoRepository.existsByNombre("Material Médico")).thenReturn(true);

        assertThrows(ValidationException.class, () -> tipoInsumoService.crear(requestDTO));
    }

    @Test
    @DisplayName("READ - Debe buscar tipo de insumo por ID exitosamente")
    void testBuscarTipoInsumoPorIdExitoso() {
        when(tipoInsumoRepository.findById(1L)).thenReturn(Optional.of(tipoInsumo));
        when(tipoInsumoMapper.toResponseDTO(tipoInsumo)).thenReturn(responseDTO);

        TipoInsumoResponseDTO resultado = tipoInsumoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdTipoInsumo());
    }

    @Test
    @DisplayName("READ - Debe lanzar excepción cuando tipo de insumo no existe")
    void testBuscarTipoInsumoPorIdNoEncontrado() {
        when(tipoInsumoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tipoInsumoService.buscarPorId(999L));
    }

    @Test
    @DisplayName("DELETE - Debe lanzar excepción cuando tiene insumos asociados")
    void testEliminarTipoInsumoConInsumos() {
        // Agregar insumos a la lista para simular que tiene insumos asociados
        Insumo insumo1 = Insumo.builder().idInsumo(1L).build();
        Insumo insumo2 = Insumo.builder().idInsumo(2L).build();
        tipoInsumo.getInsumos().add(insumo1);
        tipoInsumo.getInsumos().add(insumo2);
        when(tipoInsumoRepository.findById(1L)).thenReturn(Optional.of(tipoInsumo));

        assertThrows(BusinessException.class, () -> tipoInsumoService.eliminar(1L));
    }

    @Test
    @DisplayName("PATCH - Debe activar tipo de insumo exitosamente")
    void testActivarTipoInsumo() {
        tipoInsumo.setActivo(false);
        when(tipoInsumoRepository.findById(1L)).thenReturn(Optional.of(tipoInsumo));
        when(tipoInsumoRepository.save(any(TipoInsumo.class))).thenReturn(tipoInsumo);
        when(tipoInsumoMapper.toResponseDTO(tipoInsumo)).thenReturn(responseDTO);

        TipoInsumoResponseDTO resultado = tipoInsumoService.activar(1L);

        assertNotNull(resultado);
        assertTrue(tipoInsumo.getActivo());
    }
}

