package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.patterns.structural.facade.ClinicaFacade;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NotificacionesFacadeController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class NotificacionesFacadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClinicaFacade clinicaFacade;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Map<String, Object> responseData;

    @BeforeEach
    void setUp() {
        responseData = Map.of("notificacionesEnviadas", 5);
    }

    @Test
    @DisplayName("POST - Debe enviar recordatorios de citas")
    @WithMockUser(roles = {"RECEPCIONISTA"})
    void debeEnviarRecordatoriosCitas() throws Exception {
        when(clinicaFacade.enviarRecordatoriosCitasProximas(24)).thenReturn(responseData);

        mockMvc.perform(post("/api/facade/notificaciones/recordatorios-citas")
                        .with(csrf())
                        .param("horasAnticipacion", "24"))
                .andExpect(status().isOk());

        verify(clinicaFacade).enviarRecordatoriosCitasProximas(24);
    }

    @Test
    @DisplayName("POST - Debe notificar stock bajo")
    @WithMockUser(roles = {"ADMIN"})
    void debeNotificarStockBajo() throws Exception {
        when(clinicaFacade.notificarStockBajo()).thenReturn(responseData);

        mockMvc.perform(post("/api/facade/notificaciones/stock-bajo")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(clinicaFacade).notificarStockBajo();
    }
}

