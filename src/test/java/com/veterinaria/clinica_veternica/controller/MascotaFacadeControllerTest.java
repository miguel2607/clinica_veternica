package com.veterinaria.clinica_veternica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.clinico.HistoriaClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.paciente.MascotaRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.paciente.PropietarioRequestDTO;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MascotaFacadeController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class MascotaFacadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    private MascotaFacadeController.RegistroMascotaCompletoRequest registroRequest;
    private Map<String, Object> responseData;

    @BeforeEach
    void setUp() {
        registroRequest = new MascotaFacadeController.RegistroMascotaCompletoRequest();
        
        PropietarioRequestDTO propietarioDTO = PropietarioRequestDTO.builder()
                .documento("12345678")
                .tipoDocumento("CC")
                .nombres("Juan")
                .apellidos("Pérez")
                .email("juan.perez@example.com")
                .telefono("1234567890")
                .direccion("Calle 123")
                .build();
        
        MascotaRequestDTO mascotaDTO = MascotaRequestDTO.builder()
                .nombre("Max")
                .sexo("Macho")
                .fechaNacimiento(LocalDate.now().minusYears(2))
                .color("Negro")
                .idEspecie(1L)
                .idRaza(1L)
                .idPropietario(1L)
                .build();
        
        registroRequest.setPropietario(propietarioDTO);
        registroRequest.setMascota(mascotaDTO);
        registroRequest.setHistoriaClinica(new HistoriaClinicaRequestDTO());

        responseData = Map.of(
                "propietario", Map.of("idPropietario", 1L),
                "mascota", Map.of("idMascota", 1L),
                "historiaClinica", Map.of("idHistoriaClinica", 1L)
        );
    }

    @Test
    @DisplayName("GET - Debe obtener información completa de mascota")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeObtenerInformacionCompletaMascota() throws Exception {
        when(clinicaFacade.obtenerInformacionCompletaMascota(1L)).thenReturn(responseData);

        mockMvc.perform(get("/api/facade/mascotas/1/completa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mascota").exists());

        verify(clinicaFacade).obtenerInformacionCompletaMascota(1L);
    }

    @Test
    @DisplayName("POST - Debe registrar mascota completa")
    @WithMockUser(roles = {"RECEPCIONISTA"})
    void debeRegistrarMascotaCompleta() throws Exception {
        when(clinicaFacade.registrarMascotaCompleta(any(), any(), any())).thenReturn(responseData);

        mockMvc.perform(post("/api/facade/mascotas/registro-completo")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroRequest)))
                .andExpect(status().isCreated());

        verify(clinicaFacade).registrarMascotaCompleta(any(), any(), any());
    }

    @Test
    @DisplayName("GET - Debe buscar mascotas con alertas médicas")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeBuscarMascotasConAlertas() throws Exception {
        Map<String, Object> alertasData = Map.of("mascotas", java.util.List.of());
        when(clinicaFacade.obtenerMascotasConAlertasMedicas()).thenReturn(alertasData);

        mockMvc.perform(get("/api/facade/mascotas/alertas-medicas"))
                .andExpect(status().isOk());

        verify(clinicaFacade).obtenerMascotasConAlertasMedicas();
    }
}

