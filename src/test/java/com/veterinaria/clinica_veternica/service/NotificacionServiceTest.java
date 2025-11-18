package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.comunicacion.Comunicacion;
import com.veterinaria.clinica_veternica.domain.usuario.RolUsuario;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.comunicacion.NotificacionRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.comunicacion.NotificacionResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.*;
import com.veterinaria.clinica_veternica.repository.ComunicacionRepository;
import com.veterinaria.clinica_veternica.repository.PersonalRepository;
import com.veterinaria.clinica_veternica.repository.PropietarioRepository;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.service.impl.NotificacionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private ComunicacionRepository comunicacionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PersonalRepository personalRepository;

    @Mock
    private PropietarioRepository propietarioRepository;

    @Mock
    private EmailNotificacionFactory emailFactory;

    @Mock
    private SMSNotificacionFactory smsFactory;

    @Mock
    private WhatsAppNotificacionFactory whatsAppFactory;

    @Mock
    private PushNotificacionFactory pushFactory;

    @Mock
    private EnviadorNotificacion enviador;

    @Mock
    private ValidadorDestinatario validador;

    @Mock
    private MensajeNotificacion mensajeNotificacion;

    @InjectMocks
    private NotificacionServiceImpl notificacionService;

    private NotificacionRequestDTO requestDTO;
    private Usuario usuario;
    private Comunicacion comunicacion;

    @BeforeEach
    void setUp() {
        requestDTO = NotificacionRequestDTO.builder()
                .idUsuario(1L)
                .canal("EMAIL")
                .motivo("Recordatorio de cita")
                .mensaje("Tienes una cita mañana")
                .build();

        usuario = Usuario.builder()
                .idUsuario(1L)
                .username("testuser")
                .email("test@example.com")
                .rol(RolUsuario.ADMIN)
                .build();

        comunicacion = Comunicacion.builder()
                .idComunicacion(1L)
                .canal("EMAIL")
                .destinatarioEmail("test@example.com")
                .asunto("Recordatorio de cita")
                .mensaje("Tienes una cita mañana")
                .enviada(true)
                .fechaEnvio(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("CREATE - Debe enviar notificación exitosamente")
    void testEnviarNotificacionExitoso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(emailFactory.crearValidador()).thenReturn(validador);
        when(validador.esValido(anyString())).thenReturn(true);
        when(validador.normalizar(anyString())).thenReturn("test@example.com");
        when(emailFactory.crearMensaje(anyString(), anyString(), anyString())).thenReturn(mensajeNotificacion);
        when(comunicacionRepository.save(any(Comunicacion.class))).thenReturn(comunicacion);
        when(emailFactory.crearEnviador()).thenReturn(enviador);
        when(enviador.enviar(any(MensajeNotificacion.class))).thenReturn(true);
        when(enviador.getIdExterno()).thenReturn("EMAIL-123");

        NotificacionResponseDTO resultado = notificacionService.enviarNotificacion(requestDTO);

        assertNotNull(resultado);
        verify(comunicacionRepository, atLeastOnce()).save(any(Comunicacion.class));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción cuando usuario no existe")
    void testEnviarNotificacionUsuarioNoExiste() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        requestDTO.setIdUsuario(999L);
        assertThrows(ResourceNotFoundException.class, () -> notificacionService.enviarNotificacion(requestDTO));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción cuando canal no es válido")
    void testEnviarNotificacionCanalInvalido() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        requestDTO.setCanal("INVALIDO");
        assertThrows(ValidationException.class, () -> notificacionService.enviarNotificacion(requestDTO));
    }

    @Test
    @DisplayName("READ - Debe buscar notificación por ID exitosamente")
    void testBuscarNotificacionPorIdExitoso() {
        when(comunicacionRepository.findById(1L)).thenReturn(Optional.of(comunicacion));
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));

        NotificacionResponseDTO resultado = notificacionService.buscarPorId(1L);

        assertNotNull(resultado);
    }

    @Test
    @DisplayName("READ - Debe lanzar excepción cuando notificación no existe")
    void testBuscarNotificacionPorIdNoEncontrado() {
        when(comunicacionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificacionService.buscarPorId(999L));
    }

    @Test
    @DisplayName("READ - Debe listar todas las notificaciones")
    void testListarTodasLasNotificaciones() {
        List<Comunicacion> comunicaciones = Arrays.asList(comunicacion);
        when(comunicacionRepository.findByTipo(anyString())).thenReturn(comunicaciones);
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));

        List<NotificacionResponseDTO> resultado = notificacionService.listarTodas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }
}

