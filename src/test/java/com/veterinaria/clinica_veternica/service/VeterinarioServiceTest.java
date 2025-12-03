package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.dto.request.usuario.VeterinarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.VeterinarioResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.usuario.VeterinarioMapper;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.repository.VeterinarioRepository;
import com.veterinaria.clinica_veternica.service.impl.VeterinarioServiceImpl;
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
class VeterinarioServiceTest {

    @Mock
    private VeterinarioRepository veterinarioRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private VeterinarioMapper veterinarioMapper;

    @InjectMocks
    private VeterinarioServiceImpl veterinarioService;

    private VeterinarioRequestDTO requestDTO;
    private Veterinario veterinario;
    private VeterinarioResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = VeterinarioRequestDTO.builder()
                .nombres("Juan")
                .apellidos("Pérez")
                .correo("juan.perez@clinica.com")
                .telefono("123456789")
                .documento("12345678")
                .registroProfesional("VET001")
                .especialidad("Cirugía")
                .build();

        veterinario = Veterinario.builder()
                .idPersonal(1L)
                .nombres("Juan")
                .apellidos("Pérez")
                .correo("juan.perez@clinica.com")
                .registroProfesional("VET001")
                .especialidad("Cirugía")
                .activo(true)
                .build();

        responseDTO = VeterinarioResponseDTO.builder()
                .idPersonal(1L)
                .nombres("Juan")
                .apellidos("Pérez")
                .correo("juan.perez@clinica.com")
                .registroProfesional("VET001")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("CREATE - Debe crear veterinario exitosamente")
    void testCrearVeterinarioExitoso() {
        when(veterinarioRepository.existsByRegistroProfesional("VET001")).thenReturn(false);
        when(veterinarioMapper.toEntity(requestDTO)).thenReturn(veterinario);
        when(veterinarioRepository.save(any(Veterinario.class))).thenReturn(veterinario);
        when(veterinarioMapper.toResponseDTO(veterinario)).thenReturn(responseDTO);

        VeterinarioResponseDTO resultado = veterinarioService.crear(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPersonal());
        verify(veterinarioRepository, times(1)).save(any(Veterinario.class));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción cuando registro profesional ya existe")
    void testCrearVeterinarioRegistroDuplicado() {
        when(veterinarioRepository.existsByRegistroProfesional("VET001")).thenReturn(true);

        assertThrows(ValidationException.class, () -> veterinarioService.crear(requestDTO));
    }

    @Test
    @DisplayName("READ - Debe buscar veterinario por ID exitosamente")
    void testBuscarVeterinarioPorIdExitoso() {
        when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
        when(veterinarioMapper.toResponseDTO(veterinario)).thenReturn(responseDTO);

        VeterinarioResponseDTO resultado = veterinarioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPersonal());
    }

    @Test
    @DisplayName("READ - Debe lanzar excepción cuando veterinario no existe")
    void testBuscarVeterinarioPorIdNoEncontrado() {
        when(veterinarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> veterinarioService.buscarPorId(999L));
    }

    @Test
    @DisplayName("READ - Debe listar todos los veterinarios")
    void testListarTodosLosVeterinarios() {
        List<Veterinario> veterinarios = Arrays.asList(veterinario);
        List<VeterinarioResponseDTO> responseDTOs = Arrays.asList(responseDTO);
        // El servicio usa findAllWithUsuario() en lugar de findAll()
        when(veterinarioRepository.findAllWithUsuario()).thenReturn(veterinarios);
        when(veterinarioMapper.toResponseDTOList(veterinarios)).thenReturn(responseDTOs);

        List<VeterinarioResponseDTO> resultado = veterinarioService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }
}

