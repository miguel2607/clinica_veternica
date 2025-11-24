package com.veterinaria.clinica_veternica.patterns.structural.facade;

import com.veterinaria.clinica_veternica.dto.response.facade.BusquedaGlobalDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.PropietarioResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.VeterinarioResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.IMascotaService;
import com.veterinaria.clinica_veternica.service.interfaces.IPropietarioService;
import com.veterinaria.clinica_veternica.service.interfaces.IVeterinarioService;
import com.veterinaria.clinica_veternica.util.ValidationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusquedaFacadeServiceTest {

    @Mock
    private IMascotaService mascotaService;

    @Mock
    private IPropietarioService propietarioService;

    @Mock
    private IVeterinarioService veterinarioService;

    @Mock
    private ValidationHelper validationHelper;

    @InjectMocks
    private BusquedaFacadeService busquedaFacadeService;

    @BeforeEach
    void setUp() {
        when(validationHelper.validateAndSanitizeSearchTerm(anyString(), anyInt()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("Debe realizar búsqueda global con resultados")
    void debeRealizarBusquedaGlobal() {
        String termino = "test";
        List<MascotaResponseDTO> mascotas = List.of(MascotaResponseDTO.builder().idMascota(1L).nombre("Test").build());
        List<PropietarioResponseDTO> propietarios = List.of(PropietarioResponseDTO.builder().idPropietario(1L).build());
        List<VeterinarioResponseDTO> veterinarios = List.of(VeterinarioResponseDTO.builder().idPersonal(1L).build());

        when(mascotaService.buscarPorNombre(termino)).thenReturn(mascotas);
        when(propietarioService.buscarPorNombre(termino)).thenReturn(propietarios);
        when(veterinarioService.buscarPorNombre(termino)).thenReturn(veterinarios);

        BusquedaGlobalDTO result = busquedaFacadeService.busquedaGlobal(termino);

        assertNotNull(result);
        assertEquals(3, result.getTotalResultados());
        assertEquals(1, result.getTotalMascotas());
        assertEquals(1, result.getTotalPropietarios());
        assertEquals(1, result.getTotalVeterinarios());
        verify(mascotaService).buscarPorNombre(termino);
        verify(propietarioService).buscarPorNombre(termino);
        verify(veterinarioService).buscarPorNombre(termino);
    }

    @Test
    @DisplayName("Debe realizar búsqueda global sin resultados")
    void debeRealizarBusquedaGlobalSinResultados() {
        String termino = "noexiste";

        when(mascotaService.buscarPorNombre(termino)).thenReturn(Collections.emptyList());
        when(propietarioService.buscarPorNombre(termino)).thenReturn(Collections.emptyList());
        when(veterinarioService.buscarPorNombre(termino)).thenReturn(Collections.emptyList());

        BusquedaGlobalDTO result = busquedaFacadeService.busquedaGlobal(termino);

        assertNotNull(result);
        assertEquals(0, result.getTotalResultados());
        assertEquals(0, result.getTotalMascotas());
    }
}
