package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.response.facade.DashboardResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.EstadisticasGeneralesDTO;
import com.veterinaria.clinica_veternica.patterns.structural.facade.DashboardFacadeService;
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
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DashboardFacadeController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "spring.dotenv.enabled=false",
    "JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "JWT_EXPIRATION=86400000"
})
class DashboardFacadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardFacadeService dashboardFacadeService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private DashboardResponseDTO dashboardData;
    private EstadisticasGeneralesDTO estadisticasData;

    @BeforeEach
    void setUp() {
        dashboardData = DashboardResponseDTO.builder()
                .citasHoy(Collections.emptyList())
                .totalCitasHoy(0)
                .citasProgramadas(Collections.emptyList())
                .totalCitasProgramadas(0)
                .stockBajo(Collections.emptyList())
                .totalStockBajo(0)
                .notificacionesRecientes(Collections.emptyList())
                .totalNotificacionesRecientes(0)
                .build();

        estadisticasData = EstadisticasGeneralesDTO.builder()
                .totalMascotas(100L)
                .totalPropietarios(80L)
                .totalVeterinarios(15L)
                .totalCitasProgramadas(25L)
                .totalCitasHoy(5L)
                .insumosStockBajo(3)
                .build();
    }

    @Test
    @DisplayName("GET - Debe obtener dashboard completo")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeObtenerDashboard() throws Exception {
        dashboardData.setTotalCitasHoy(5);
        when(dashboardFacadeService.obtenerDashboard()).thenReturn(dashboardData);

        mockMvc.perform(get("/api/facade/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.citasHoy").isArray())
                .andExpect(jsonPath("$.totalCitasHoy").value(5));

        verify(dashboardFacadeService).obtenerDashboard();
    }

    @Test
    @DisplayName("GET - Debe obtener estadísticas generales")
    @WithMockUser(roles = {"ADMIN"})
    void debeObtenerEstadisticas() throws Exception {
        when(dashboardFacadeService.obtenerEstadisticasGenerales()).thenReturn(estadisticasData);

        mockMvc.perform(get("/api/facade/dashboard/estadisticas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMascotas").value(100));

        verify(dashboardFacadeService).obtenerEstadisticasGenerales();
    }
}

