package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.usuario.AuxiliarVeterinario;
import com.veterinaria.clinica_veternica.dto.request.usuario.AuxiliarVeterinarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.AuxiliarVeterinarioResponseDTO;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.usuario.AuxiliarVeterinarioMapper;
import com.veterinaria.clinica_veternica.mapper.usuario.UsuarioMapper;
import com.veterinaria.clinica_veternica.repository.AuxiliarVeterinarioRepository;
import com.veterinaria.clinica_veternica.repository.PersonalRepository;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.service.impl.AuxiliarVeterinarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuxiliarVeterinarioServiceTest {

    @Mock
    private AuxiliarVeterinarioRepository auxiliarVeterinarioRepository;

    @Mock
    private PersonalRepository personalRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AuxiliarVeterinarioMapper auxiliarVeterinarioMapper;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuxiliarVeterinarioServiceImpl auxiliarVeterinarioService;

    private AuxiliarVeterinarioRequestDTO requestDTO;
    private AuxiliarVeterinario auxiliar;
    private AuxiliarVeterinarioResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = AuxiliarVeterinarioRequestDTO.builder()
                .nombres("María")
                .apellidos("González")
                .documento("87654321")
                .correo("maria@clinica.com")
                .build();

        auxiliar = AuxiliarVeterinario.builder()
                .idPersonal(1L)
                .nombres("María")
                .apellidos("González")
                .documento("87654321")
                .correo("maria@clinica.com")
                .activo(true)
                .build();

        responseDTO = AuxiliarVeterinarioResponseDTO.builder()
                .idPersonal(1L)
                .nombres("María")
                .apellidos("González")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("CREATE - Debe crear auxiliar veterinario exitosamente")
    void testCrearAuxiliarExitoso() {
        when(personalRepository.existsByDocumento("87654321")).thenReturn(false);
        when(personalRepository.existsByCorreo("maria@clinica.com")).thenReturn(false);
        when(auxiliarVeterinarioMapper.toEntity(requestDTO)).thenReturn(auxiliar);
        when(auxiliarVeterinarioRepository.save(any(AuxiliarVeterinario.class))).thenReturn(auxiliar);
        when(auxiliarVeterinarioMapper.toResponseDTO(auxiliar)).thenReturn(responseDTO);

        AuxiliarVeterinarioResponseDTO resultado = auxiliarVeterinarioService.crear(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPersonal());
        verify(auxiliarVeterinarioRepository, times(1)).save(any(AuxiliarVeterinario.class));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción cuando documento ya existe")
    void testCrearAuxiliarDocumentoDuplicado() {
        when(personalRepository.existsByDocumento("87654321")).thenReturn(true);

        assertThrows(ValidationException.class, () -> auxiliarVeterinarioService.crear(requestDTO));
    }

    @Test
    @DisplayName("READ - Debe buscar auxiliar por ID exitosamente")
    void testBuscarAuxiliarPorIdExitoso() {
        when(auxiliarVeterinarioRepository.findById(1L)).thenReturn(Optional.of(auxiliar));
        when(auxiliarVeterinarioMapper.toResponseDTO(auxiliar)).thenReturn(responseDTO);

        AuxiliarVeterinarioResponseDTO resultado = auxiliarVeterinarioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPersonal());
    }

    @Test
    @DisplayName("READ - Debe lanzar excepción cuando auxiliar no existe")
    void testBuscarAuxiliarPorIdNoEncontrado() {
        when(auxiliarVeterinarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> auxiliarVeterinarioService.buscarPorId(999L));
    }

    @Test
    @DisplayName("READ - Debe listar todos los auxiliares")
    void testListarTodosLosAuxiliares() {
        List<AuxiliarVeterinario> auxiliares = Arrays.asList(auxiliar);
        List<AuxiliarVeterinarioResponseDTO> responseDTOs = Arrays.asList(responseDTO);
        when(auxiliarVeterinarioRepository.findAll()).thenReturn(auxiliares);
        when(auxiliarVeterinarioMapper.toResponseDTOList(auxiliares)).thenReturn(responseDTOs);

        List<AuxiliarVeterinarioResponseDTO> resultado = auxiliarVeterinarioService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("PATCH - Debe lanzar excepción cuando auxiliar ya está activo")
    void testActivarAuxiliarYaActivo() {
        when(auxiliarVeterinarioRepository.findById(1L)).thenReturn(Optional.of(auxiliar));

        assertThrows(BusinessException.class, () -> auxiliarVeterinarioService.activar(1L));
    }
}

