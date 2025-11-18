package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.agenda.CategoriaServicio;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import com.veterinaria.clinica_veternica.dto.request.agenda.CitaRequestDTO;
import com.veterinaria.clinica_veternica.service.impl.CitaPriceCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CitaPriceCalculationServiceTest {

    @InjectMocks
    private CitaPriceCalculationService citaPriceCalculationService;

    private Servicio servicio;
    private CitaRequestDTO citaRequestDTO;

    @BeforeEach
    void setUp() {
        servicio = Servicio.builder()
                .idServicio(1L)
                .nombre("Consulta General")
                .precio(BigDecimal.valueOf(50.00))
                .categoria(CategoriaServicio.CLINICO)
                .build();

        citaRequestDTO = new CitaRequestDTO();
        citaRequestDTO.setEsEmergencia(false);
    }

    @Test
    @DisplayName("Debe calcular precio final para cita normal")
    void debeCalcularPrecioFinalCitaNormal() {
        BigDecimal precio = citaPriceCalculationService.calcularPrecioFinal(servicio, citaRequestDTO);

        assertNotNull(precio);
        assertEquals(BigDecimal.valueOf(50.00), precio);
    }

    @Test
    @DisplayName("Debe calcular precio final para cita de emergencia")
    void debeCalcularPrecioFinalCitaEmergencia() {
        citaRequestDTO.setEsEmergencia(true);

        BigDecimal precio = citaPriceCalculationService.calcularPrecioFinal(servicio, citaRequestDTO);

        assertNotNull(precio);
        assertTrue(precio.compareTo(servicio.getPrecio()) > 0, "El precio de emergencia debe ser mayor");
    }
}

