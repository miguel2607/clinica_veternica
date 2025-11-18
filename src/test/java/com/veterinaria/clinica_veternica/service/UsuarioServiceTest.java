package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.usuario.RolUsuario;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.usuario.UsuarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.UsuarioResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.usuario.UsuarioMapper;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.service.impl.UsuarioServiceImpl;
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
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private UsuarioRequestDTO requestDTO;
    private Usuario usuario;
    private UsuarioResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new UsuarioRequestDTO();
        requestDTO.setUsername("testuser");
        requestDTO.setEmail("test@example.com");
        requestDTO.setPassword("password123");
        requestDTO.setRol("ADMIN");

        usuario = Usuario.builder()
                .idUsuario(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .rol(RolUsuario.ADMIN)
                .estado(true)
                .bloqueado(false)
                .intentosFallidos(0)
                .build();

        responseDTO = new UsuarioResponseDTO();
        responseDTO.setIdUsuario(1L);
        responseDTO.setUsername("testuser");
        responseDTO.setEmail("test@example.com");
        responseDTO.setRol("ADMIN");
        responseDTO.setEstado(true);
    }

    @Test
    @DisplayName("CREATE - Debe crear un usuario exitosamente")
    void testCrearUsuarioExitoso() {
        when(usuarioRepository.existsByUsername("testuser")).thenReturn(false);
        when(usuarioRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(usuarioMapper.toEntity(requestDTO)).thenReturn(usuario);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        UsuarioResponseDTO resultado = usuarioService.crear(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdUsuario());
        assertEquals("testuser", resultado.getUsername());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción cuando username ya existe")
    void testCrearUsuarioUsernameDuplicado() {
        when(usuarioRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(ValidationException.class, () -> usuarioService.crear(requestDTO));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción cuando email ya existe")
    void testCrearUsuarioEmailDuplicado() {
        when(usuarioRepository.existsByUsername("testuser")).thenReturn(false);
        when(usuarioRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(ValidationException.class, () -> usuarioService.crear(requestDTO));
    }

    @Test
    @DisplayName("READ - Debe buscar usuario por ID exitosamente")
    void testBuscarUsuarioPorIdExitoso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        UsuarioResponseDTO resultado = usuarioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdUsuario());
    }

    @Test
    @DisplayName("READ - Debe lanzar excepción cuando usuario no existe")
    void testBuscarUsuarioPorIdNoEncontrado() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.buscarPorId(999L));
    }

    @Test
    @DisplayName("READ - Debe listar todos los usuarios")
    void testListarTodosLosUsuarios() {
        List<Usuario> usuarios = Arrays.asList(usuario);
        List<UsuarioResponseDTO> responseDTOs = Arrays.asList(responseDTO);
        when(usuarioRepository.findAll()).thenReturn(usuarios);
        when(usuarioMapper.toResponseDTOList(usuarios)).thenReturn(responseDTOs);

        List<UsuarioResponseDTO> resultado = usuarioService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }
}

