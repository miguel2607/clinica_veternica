package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.paciente.Propietario;
import com.veterinaria.clinica_veternica.dto.request.paciente.PropietarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.PropietarioResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;

import com.veterinaria.clinica_veternica.mapper.paciente.PropietarioMapper;
import com.veterinaria.clinica_veternica.repository.PropietarioRepository;
import com.veterinaria.clinica_veternica.service.impl.PropietarioServiceImpl;
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

/**
 * Tests básicos CRUD para PropietarioService
 */
@ExtendWith(MockitoExtension.class)
class PropietarioServiceTest {

    @Mock
    private PropietarioRepository propietarioRepository;

    @Mock
    private PropietarioMapper propietarioMapper;

    @InjectMocks
    private PropietarioServiceImpl propietarioService;

    private PropietarioRequestDTO requestDTO;
    private Propietario propietario;
    private PropietarioResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = PropietarioRequestDTO.builder()
                .tipoDocumento("CC")
                .documento("1234567890")
                .nombres("Juan")
                .apellidos("Pérez")
                .email("juan@example.com")
                .telefono("3001234567")
                .activo(true)
                .build();

        propietario = Propietario.builder()
                .idPropietario(1L)
                .tipoDocumento("CC")
                .documento("1234567890")
                .nombres("Juan")
                .apellidos("Pérez")
                .email("juan@example.com")
                .telefono("3001234567")
                .activo(true)
                .build();

        responseDTO = PropietarioResponseDTO.builder()
                .idPropietario(1L)
                .nombres("Juan")
                .apellidos("Pérez")
                .email("juan@example.com")
                .build();
    }

    @Test
    @DisplayName("CREATE - Debe crear un propietario exitosamente")
    void testCrearPropietarioExitoso() {
        when(propietarioRepository.existsByTipoDocumentoAndNumeroDocumento("CC", "1234567890"))
                .thenReturn(false);
        when(propietarioRepository.existsByEmail("juan@example.com")).thenReturn(false);
        when(propietarioMapper.toEntity(requestDTO)).thenReturn(propietario);
        when(propietarioRepository.save(any(Propietario.class))).thenReturn(propietario);
        when(propietarioMapper.toResponseDTO(propietario)).thenReturn(responseDTO);

        PropietarioResponseDTO resultado = propietarioService.crear(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPropietario());
        verify(propietarioRepository, times(1)).save(any(Propietario.class));
    }

    @Test
    @DisplayName("READ - Debe buscar propietario por ID exitosamente")
    void testBuscarPropietarioPorIdExitoso() {
        when(propietarioRepository.findById(1L)).thenReturn(Optional.of(propietario));
        when(propietarioMapper.toResponseDTO(propietario)).thenReturn(responseDTO);

        PropietarioResponseDTO resultado = propietarioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPropietario());
    }

    @Test
    @DisplayName("READ - Debe lanzar excepción cuando propietario no existe")
    void testBuscarPropietarioPorIdNoEncontrado() {
        when(propietarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> propietarioService.buscarPorId(999L));
    }

    @Test
    @DisplayName("UPDATE - Debe actualizar un propietario exitosamente")
    void testActualizarPropietarioExitoso() {
        when(propietarioRepository.findById(1L)).thenReturn(Optional.of(propietario));
        when(propietarioRepository.save(any(Propietario.class))).thenReturn(propietario);
        when(propietarioMapper.toResponseDTO(propietario)).thenReturn(responseDTO);

        PropietarioResponseDTO resultado = propietarioService.actualizar(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPropietario());
        verify(propietarioRepository, times(1)).save(any(Propietario.class));
    }

    @Test
    @DisplayName("DELETE - Debe eliminar un propietario exitosamente (soft delete)")
    void testEliminarPropietarioExitoso() {
        propietario.setMascotas(null);
        when(propietarioRepository.findById(1L)).thenReturn(Optional.of(propietario));
        when(propietarioRepository.save(any(Propietario.class))).thenReturn(propietario);

        assertDoesNotThrow(() -> propietarioService.eliminar(1L));
        verify(propietarioRepository, times(1)).save(any(Propietario.class));
        assertFalse(propietario.getActivo());
    }
}
