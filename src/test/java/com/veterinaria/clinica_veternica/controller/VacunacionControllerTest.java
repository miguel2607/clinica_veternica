package com.veterinaria.clinica_veternica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.clinico.VacunacionRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.VacunacionResponseDTO;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import com.veterinaria.clinica_veternica.service.interfaces.IVacunacionService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = VacunacionController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class VacunacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IVacunacionService vacunacionService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private VacunacionRequestDTO requestDTO;
    private VacunacionResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = VacunacionRequestDTO.builder()
                .idHistoriaClinica(1L)
                .idVeterinario(1L)
                .nombreVacuna("Rabia")
                .laboratorio("Laboratorio XYZ")
                .lote("LOTE123")
                .fechaAplicacion(LocalDate.now())
                .fechaProximaDosis(LocalDate.now().plusMonths(12))
                .viaAdministracion("Intramuscular")
                .observaciones("Vacunación de rutina")
                .build();

        responseDTO = new VacunacionResponseDTO();
        responseDTO.setIdVacunacion(1L);
        responseDTO.setNombreVacuna("Rabia");
        responseDTO.setFechaAplicacion(LocalDate.now());
        responseDTO.setFechaProximaDosis(LocalDate.now().plusMonths(12));
        responseDTO.setLote("LOTE123");
        responseDTO.setObservaciones("Vacunación de rutina");
    }

    @Test
    @DisplayName("CREATE - Debe crear una vacunación exitosamente")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeCrearVacunacionExitosamente() throws Exception {
        when(vacunacionService.crear(eq(1L), any(VacunacionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/vacunaciones")
                        .with(csrf())
                        .param("idHistoriaClinica", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idVacunacion").value(1))
                .andExpect(jsonPath("$.nombreVacuna").value("Rabia"));

        verify(vacunacionService).crear(eq(1L), any(VacunacionRequestDTO.class));
    }

    @Test
    @DisplayName("READ - Debe listar vacunaciones por historia clínica")
    @WithMockUser
    void debeListarVacunacionesPorHistoriaClinica() throws Exception {
        List<VacunacionResponseDTO> vacunaciones = Arrays.asList(responseDTO);
        when(vacunacionService.listarPorHistoriaClinica(1L)).thenReturn(vacunaciones);

        mockMvc.perform(get("/api/vacunaciones/historia-clinica/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idVacunacion").value(1))
                .andExpect(jsonPath("$.length()").value(1));

        verify(vacunacionService).listarPorHistoriaClinica(1L);
    }
}

