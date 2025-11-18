package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.usuario.Recepcionista;
import com.veterinaria.clinica_veternica.dto.request.usuario.RecepcionistaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.RecepcionistaResponseDTO;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.usuario.RecepcionistaMapper;
import com.veterinaria.clinica_veternica.mapper.usuario.UsuarioMapper;
import com.veterinaria.clinica_veternica.repository.PersonalRepository;
import com.veterinaria.clinica_veternica.repository.RecepcionistaRepository;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.service.impl.RecepcionistaServiceImpl;
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
class RecepcionistaServiceTest {

    @Mock
    private RecepcionistaRepository recepcionistaRepository;

    @Mock
    private PersonalRepository personalRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RecepcionistaMapper recepcionistaMapper;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RecepcionistaServiceImpl recepcionistaService;

    private RecepcionistaRequestDTO requestDTO;
    private Recepcionista recepcionista;
    private RecepcionistaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = RecepcionistaRequestDTO.builder()
                .nombres("Carlos")
                .apellidos("Rodríguez")
                .documento("11223344")
                .correo("carlos@clinica.com")
                .build();

        recepcionista = Recepcionista.builder()
                .idPersonal(1L)
                .nombres("Carlos")
                .apellidos("Rodríguez")
                .documento("11223344")
                .correo("carlos@clinica.com")
                .activo(true)
                .build();

        responseDTO = RecepcionistaResponseDTO.builder()
                .idPersonal(1L)
                .nombres("Carlos")
                .apellidos("Rodríguez")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("CREATE - Debe crear recepcionista exitosamente")
    void testCrearRecepcionistaExitoso() {
        when(personalRepository.existsByDocumento("11223344")).thenReturn(false);
        when(personalRepository.existsByCorreo("carlos@clinica.com")).thenReturn(false);
        when(recepcionistaMapper.toEntity(requestDTO)).thenReturn(recepcionista);
        when(recepcionistaRepository.save(any(Recepcionista.class))).thenReturn(recepcionista);
        when(recepcionistaMapper.toResponseDTO(recepcionista)).thenReturn(responseDTO);

        RecepcionistaResponseDTO resultado = recepcionistaService.crear(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPersonal());
        verify(recepcionistaRepository, times(1)).save(any(Recepcionista.class));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción cuando documento ya existe")
    void testCrearRecepcionistaDocumentoDuplicado() {
        when(personalRepository.existsByDocumento("11223344")).thenReturn(true);

        assertThrows(ValidationException.class, () -> recepcionistaService.crear(requestDTO));
    }

    @Test
    @DisplayName("READ - Debe buscar recepcionista por ID exitosamente")
    void testBuscarRecepcionistaPorIdExitoso() {
        when(recepcionistaRepository.findById(1L)).thenReturn(Optional.of(recepcionista));
        when(recepcionistaMapper.toResponseDTO(recepcionista)).thenReturn(responseDTO);

        RecepcionistaResponseDTO resultado = recepcionistaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPersonal());
    }

    @Test
    @DisplayName("READ - Debe lanzar excepción cuando recepcionista no existe")
    void testBuscarRecepcionistaPorIdNoEncontrado() {
        when(recepcionistaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recepcionistaService.buscarPorId(999L));
    }

    @Test
    @DisplayName("READ - Debe listar todos los recepcionistas")
    void testListarTodosLosRecepcionistas() {
        List<Recepcionista> recepcionistas = Arrays.asList(recepcionista);
        List<RecepcionistaResponseDTO> responseDTOs = Arrays.asList(responseDTO);
        when(recepcionistaRepository.findAll()).thenReturn(recepcionistas);
        when(recepcionistaMapper.toResponseDTOList(recepcionistas)).thenReturn(responseDTOs);

        List<RecepcionistaResponseDTO> resultado = recepcionistaService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("PATCH - Debe lanzar excepción cuando recepcionista ya está activo")
    void testActivarRecepcionistaYaActivo() {
        when(recepcionistaRepository.findById(1L)).thenReturn(Optional.of(recepcionista));

        assertThrows(BusinessException.class, () -> recepcionistaService.activar(1L));
    }
}

