package com.veterinaria.clinica_veternica.patterns.structural.decorator;

import com.veterinaria.clinica_veternica.domain.agenda.CategoriaServicio;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ServicioConDescuentoDecoratorTest {

    private Servicio servicio;
    private ServicioConDescuentoDecorator decorator;

    @BeforeEach
    void setUp() {
        servicio = Servicio.builder()
                .idServicio(1L)
                .nombre("Consulta General")
                .precio(BigDecimal.valueOf(100.00))
                .categoria(CategoriaServicio.CLINICO)
                .build();
    }

    @Test
    @DisplayName("Decorator - Debe aplicar descuento correctamente")
    void debeAplicarDescuentoCorrectamente() {
        BigDecimal porcentajeDescuento = BigDecimal.valueOf(20); // 20%
        decorator = new ServicioConDescuentoDecorator(servicio, porcentajeDescuento);

        BigDecimal precioConDescuento = decorator.getPrecio();

        assertEquals(0, BigDecimal.valueOf(80.00).compareTo(precioConDescuento), 
                "El precio con descuento debe ser 80.00");
    }

    @Test
    @DisplayName("Decorator - Debe lanzar excepción con descuento negativo")
    void debeLanzarExcepcionDescuentoNegativo() {
        BigDecimal porcentajeDescuento = BigDecimal.valueOf(-10);

        assertThrows(IllegalArgumentException.class, 
                () -> new ServicioConDescuentoDecorator(servicio, porcentajeDescuento));
    }

    @Test
    @DisplayName("Decorator - Debe lanzar excepción con descuento mayor a 100")
    void debeLanzarExcepcionDescuentoMayor100() {
        BigDecimal porcentajeDescuento = BigDecimal.valueOf(150);

        assertThrows(IllegalArgumentException.class, 
                () -> new ServicioConDescuentoDecorator(servicio, porcentajeDescuento));
    }

    @Test
    @DisplayName("Decorator - Debe retornar descripción con descuento")
    void debeRetornarDescripcionConDescuento() {
        BigDecimal porcentajeDescuento = BigDecimal.valueOf(20);
        decorator = new ServicioConDescuentoDecorator(servicio, porcentajeDescuento);

        String descripcion = decorator.getDescripcion();

        assertNotNull(descripcion);
        assertTrue(descripcion.contains("Descuento"));
    }
}

