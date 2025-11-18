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

import java.time.LocalDate;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReportesFacadeController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class ReportesFacadeControllerTest {

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
        responseData = Map.of("totalCitas", 10, "estadisticas", Map.of());
    }

    @Test
    @DisplayName("GET - Debe obtener reporte de citas")
    @WithMockUser(roles = {"ADMIN"})
    void debeObtenerReporteCitas() throws Exception {
        LocalDate fechaInicio = LocalDate.now().minusDays(30);
        LocalDate fechaFin = LocalDate.now();
        when(clinicaFacade.generarReporteCitas(any(LocalDate.class), any(LocalDate.class))).thenReturn(responseData);

        mockMvc.perform(get("/api/facade/reportes/citas")
                        .param("fechaInicio", fechaInicio.toString())
                        .param("fechaFin", fechaFin.toString()))
                .andExpect(status().isOk());

        verify(clinicaFacade).generarReporteCitas(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("GET - Debe obtener reporte de inventario")
    @WithMockUser(roles = {"ADMIN"})
    void debeObtenerReporteInventario() throws Exception {
        when(clinicaFacade.generarReporteInventario()).thenReturn(responseData);

        mockMvc.perform(get("/api/facade/reportes/inventario"))
                .andExpect(status().isOk());

        verify(clinicaFacade).generarReporteInventario();
    }

    @Test
    @DisplayName("GET - Debe obtener reporte de veterinarios")
    @WithMockUser(roles = {"ADMIN"})
    void debeObtenerReporteVeterinarios() throws Exception {
        LocalDate fechaInicio = LocalDate.now().minusDays(30);
        LocalDate fechaFin = LocalDate.now();
        when(clinicaFacade.generarReporteVeterinarios(any(LocalDate.class), any(LocalDate.class))).thenReturn(responseData);

        mockMvc.perform(get("/api/facade/reportes/veterinarios")
                        .param("fechaInicio", fechaInicio.toString())
                        .param("fechaFin", fechaFin.toString()))
                .andExpect(status().isOk());

        verify(clinicaFacade).generarReporteVeterinarios(any(LocalDate.class), any(LocalDate.class));
    }
}

