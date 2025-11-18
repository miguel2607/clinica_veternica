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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = VeterinarioFacadeController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class VeterinarioFacadeControllerTest {

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
        responseData = Map.of(
                "veterinario", Map.of("idPersonal", 1L),
                "citas", java.util.List.of(),
                "estadisticas", Map.of()
        );
    }

    @Test
    @DisplayName("GET - Debe obtener información completa de veterinario")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeObtenerInformacionCompletaVeterinario() throws Exception {
        when(clinicaFacade.obtenerInformacionCompletaVeterinario(1L)).thenReturn(responseData);

        mockMvc.perform(get("/api/facade/veterinarios/1/completo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.veterinario").exists());

        verify(clinicaFacade).obtenerInformacionCompletaVeterinario(1L);
    }
}

