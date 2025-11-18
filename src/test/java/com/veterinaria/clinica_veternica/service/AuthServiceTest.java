package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.usuario.RolUsuario;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.auth.LoginRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.auth.RegisterRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.auth.LoginResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.UsuarioResponseDTO;
import com.veterinaria.clinica_veternica.exception.UnauthorizedException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.usuario.UsuarioMapper;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginRequestDTO loginRequest;
    private RegisterRequestDTO registerRequest;
    private Usuario usuario;
    private UsuarioResponseDTO usuarioResponseDTO;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequestDTO();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setRol("ADMIN");

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

        usuarioResponseDTO = new UsuarioResponseDTO();
        usuarioResponseDTO.setIdUsuario(1L);
        usuarioResponseDTO.setUsername("newuser");
        usuarioResponseDTO.setEmail("newuser@example.com");
        usuarioResponseDTO.setRol("ADMIN");
    }

    @Test
    @DisplayName("LOGIN - Debe autenticar usuario exitosamente")
    void debeAutenticarUsuarioExitosamente() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(jwtProperties.getExpiration()).thenReturn(3600L);

        LoginResponseDTO resultado = authService.login(loginRequest);

        assertNotNull(resultado);
        assertEquals("jwt-token", resultado.getToken());
        assertEquals("Bearer", resultado.getType());
        assertEquals(1L, resultado.getIdUsuario());
        // El usuario solo se guarda si tiene intentos fallidos > 0, en este caso no tiene
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("LOGIN - Debe lanzar excepción con credenciales inválidas")
    void debeLanzarExcepcionCredencialesInvalidas() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        assertThrows(UnauthorizedException.class, () -> authService.login(loginRequest));
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("LOGIN - Debe lanzar excepción cuando usuario está inactivo")
    void debeLanzarExcepcionUsuarioInactivo() {
        usuario.setEstado(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        assertThrows(UnauthorizedException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("LOGIN - Debe lanzar excepción cuando usuario está bloqueado")
    void debeLanzarExcepcionUsuarioBloqueado() {
        usuario.setBloqueado(true);
        usuario.setMotivoBloqueo("Múltiples intentos fallidos");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        assertThrows(UnauthorizedException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("REGISTER - Debe registrar usuario exitosamente")
    void debeRegistrarUsuarioExitosamente() {
        when(usuarioRepository.existsByUsername("newuser")).thenReturn(false);
        when(usuarioRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(usuarioResponseDTO);

        UsuarioResponseDTO resultado = authService.register(registerRequest);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdUsuario());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("REGISTER - Debe lanzar excepción cuando username ya existe")
    void debeLanzarExcepcionUsernameDuplicado() {
        when(usuarioRepository.existsByUsername("newuser")).thenReturn(true);

        assertThrows(ValidationException.class, () -> authService.register(registerRequest));
    }

    @Test
    @DisplayName("REGISTER - Debe lanzar excepción cuando email ya existe")
    void debeLanzarExcepcionEmailDuplicado() {
        when(usuarioRepository.existsByUsername("newuser")).thenReturn(false);
        when(usuarioRepository.existsByEmail("newuser@example.com")).thenReturn(true);

        assertThrows(ValidationException.class, () -> authService.register(registerRequest));
    }
}

