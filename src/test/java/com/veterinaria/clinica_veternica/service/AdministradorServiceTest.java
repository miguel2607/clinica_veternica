package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.usuario.Administrador;
import com.veterinaria.clinica_veternica.dto.request.usuario.AdministradorRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.AdministradorResponseDTO;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.usuario.AdministradorMapper;
import com.veterinaria.clinica_veternica.mapper.usuario.UsuarioMapper;
import com.veterinaria.clinica_veternica.repository.AdministradorRepository;
import com.veterinaria.clinica_veternica.repository.PersonalRepository;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.service.impl.AdministradorServiceImpl;
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
class AdministradorServiceTest {

    @Mock
    private AdministradorRepository administradorRepository;

    @Mock
    private PersonalRepository personalRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AdministradorMapper administradorMapper;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdministradorServiceImpl administradorService;

    private AdministradorRequestDTO requestDTO;
    private Administrador administrador;
    private AdministradorResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new AdministradorRequestDTO();
        requestDTO.setNombres("Admin");
        requestDTO.setApellidos("Sistema");
        requestDTO.setDocumento("12345678");
        requestDTO.setCorreo("admin@clinica.com");

        administrador = Administrador.builder()
                .idPersonal(1L)
                .nombres("Admin")
                .apellidos("Sistema")
                .documento("12345678")
                .correo("admin@clinica.com")
                .activo(true)
                .build();

        responseDTO = AdministradorResponseDTO.builder()
                .idPersonal(1L)
                .nombres("Admin")
                .apellidos("Sistema")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("CREATE - Debe crear administrador exitosamente")
    void testCrearAdministradorExitoso() {
        when(personalRepository.existsByDocumento("12345678")).thenReturn(false);
        when(personalRepository.existsByCorreo("admin@clinica.com")).thenReturn(false);
        when(administradorMapper.toEntity(requestDTO)).thenReturn(administrador);
        when(administradorRepository.save(any(Administrador.class))).thenReturn(administrador);
        when(administradorMapper.toResponseDTO(administrador)).thenReturn(responseDTO);

        AdministradorResponseDTO resultado = administradorService.crear(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPersonal());
        verify(administradorRepository, times(1)).save(any(Administrador.class));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción cuando documento ya existe")
    void testCrearAdministradorDocumentoDuplicado() {
        when(personalRepository.existsByDocumento("12345678")).thenReturn(true);

        assertThrows(ValidationException.class, () -> administradorService.crear(requestDTO));
    }

    @Test
    @DisplayName("READ - Debe buscar administrador por ID exitosamente")
    void testBuscarAdministradorPorIdExitoso() {
        when(administradorRepository.findById(1L)).thenReturn(Optional.of(administrador));
        when(administradorMapper.toResponseDTO(administrador)).thenReturn(responseDTO);

        AdministradorResponseDTO resultado = administradorService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPersonal());
    }

    @Test
    @DisplayName("READ - Debe lanzar excepción cuando administrador no existe")
    void testBuscarAdministradorPorIdNoEncontrado() {
        when(administradorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> administradorService.buscarPorId(999L));
    }

    @Test
    @DisplayName("READ - Debe listar todos los administradores")
    void testListarTodosLosAdministradores() {
        List<Administrador> administradores = Arrays.asList(administrador);
        List<AdministradorResponseDTO> responseDTOs = Arrays.asList(responseDTO);
        when(administradorRepository.findAll()).thenReturn(administradores);
        when(administradorMapper.toResponseDTOList(administradores)).thenReturn(responseDTOs);

        List<AdministradorResponseDTO> resultado = administradorService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("PATCH - Debe lanzar excepción cuando administrador ya está activo")
    void testActivarAdministradorYaActivo() {
        when(administradorRepository.findById(1L)).thenReturn(Optional.of(administrador));

        assertThrows(BusinessException.class, () -> administradorService.activar(1L));
    }
}

