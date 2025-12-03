package com.veterinaria.clinica_veternica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.comunicacion.NotificacionRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.comunicacion.NotificacionResponseDTO;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import com.veterinaria.clinica_veternica.service.interfaces.INotificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NotificacionController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "spring.dotenv.enabled=false",
    "JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "JWT_EXPIRATION=86400000"
})
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private INotificacionService notificacionService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private NotificacionRequestDTO requestDTO;
    private NotificacionResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = NotificacionRequestDTO.builder()
                .idUsuario(1L)
                .canal("EMAIL")
                .motivo("Recordatorio de cita")
                .mensaje("Tienes una cita mañana")
                .build();

        responseDTO = NotificacionResponseDTO.builder()
                .idComunicacion(1L)
                .idUsuario(1L)
                .canal("EMAIL")
                .motivo("Recordatorio de cita")
                .mensaje("Tienes una cita mañana")
                .fechaEnvio(LocalDateTime.now())
                .enviada(true)
                .build();
    }

    @Test
    @DisplayName("CREATE - Debe enviar una notificación exitosamente")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeEnviarNotificacionExitosamente() throws Exception {
        when(notificacionService.enviarNotificacion(any(NotificacionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/notificaciones")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idComunicacion").value(1))
                .andExpect(jsonPath("$.canal").value("EMAIL"));

        verify(notificacionService).enviarNotificacion(any(NotificacionRequestDTO.class));
    }

    @Test
    @DisplayName("READ - Debe buscar notificación por ID")
    @WithMockUser
    void debeBuscarNotificacionPorId() throws Exception {
        when(notificacionService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/notificaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idComunicacion").value(1));

        verify(notificacionService).buscarPorId(1L);
    }

    @Test
    @DisplayName("READ - Debe listar todas las notificaciones")
    @WithMockUser
    void debeListarTodasLasNotificaciones() throws Exception {
        List<NotificacionResponseDTO> notificaciones = Arrays.asList(responseDTO);
        when(notificacionService.listarTodas()).thenReturn(notificaciones);

        mockMvc.perform(get("/api/notificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idComunicacion").value(1));

        verify(notificacionService).listarTodas();
    }

    @Test
    @DisplayName("READ - Debe listar notificaciones por usuario")
    @WithMockUser
    void debeListarNotificacionesPorUsuario() throws Exception {
        List<NotificacionResponseDTO> notificaciones = Arrays.asList(responseDTO);
        when(notificacionService.listarPorUsuario(1L)).thenReturn(notificaciones);

        mockMvc.perform(get("/api/notificaciones/usuario/1"))
                .andExpect(status().isOk());

        verify(notificacionService).listarPorUsuario(1L);
    }

    @Test
    @DisplayName("READ - Debe listar notificaciones por canal")
    @WithMockUser
    void debeListarNotificacionesPorCanal() throws Exception {
        List<NotificacionResponseDTO> notificaciones = Arrays.asList(responseDTO);
        when(notificacionService.listarPorCanal("EMAIL")).thenReturn(notificaciones);

        mockMvc.perform(get("/api/notificaciones/canal/EMAIL"))
                .andExpect(status().isOk());

        verify(notificacionService).listarPorCanal("EMAIL");
    }

    @Test
    @DisplayName("READ - Debe listar notificaciones enviadas")
    @WithMockUser
    void debeListarNotificacionesEnviadas() throws Exception {
        List<NotificacionResponseDTO> notificaciones = Arrays.asList(responseDTO);
        when(notificacionService.listarEnviadas()).thenReturn(notificaciones);

        mockMvc.perform(get("/api/notificaciones/enviadas"))
                .andExpect(status().isOk());

        verify(notificacionService).listarEnviadas();
    }

    @Test
    @DisplayName("READ - Debe listar notificaciones pendientes")
    @WithMockUser
    void debeListarNotificacionesPendientes() throws Exception {
        List<NotificacionResponseDTO> notificaciones = Arrays.asList(responseDTO);
        when(notificacionService.listarPendientes()).thenReturn(notificaciones);

        mockMvc.perform(get("/api/notificaciones/pendientes"))
                .andExpect(status().isOk());

        verify(notificacionService).listarPendientes();
    }
}

