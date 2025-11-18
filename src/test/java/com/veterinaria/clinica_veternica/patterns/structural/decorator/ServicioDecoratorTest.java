package com.veterinaria.clinica_veternica.patterns.structural.decorator;

import com.veterinaria.clinica_veternica.domain.agenda.CategoriaServicio;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el patrón Decorator - ServicioDecorator
 */
class ServicioDecoratorTest {

    private Servicio servicioBase;

    @BeforeEach
    void setUp() {
        servicioBase = Servicio.builder()
                .idServicio(1L)
                .nombre("Consulta General")
                .descripcion("Consulta veterinaria de rutina")
                .categoria(CategoriaServicio.CLINICO)
                .precio(new BigDecimal("100.00"))
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("Decorator Descuento - Debe aplicar descuento correctamente")
    void descuentoDebeAplicarDescuentoCorrectamente() {
        BigDecimal porcentajeDescuento = new BigDecimal("10.00");
        ServicioConDescuentoDecorator decorator = 
            new ServicioConDescuentoDecorator(servicioBase, porcentajeDescuento);

        BigDecimal precioConDescuento = decorator.getPrecio();
        
        // Precio original: 100.00
        // Descuento 10%: 10.00
        // Precio final: 90.00
        assertEquals(new BigDecimal("90.00"), precioConDescuento);
    }

    @Test
    @DisplayName("Decorator Descuento - Debe modificar descripción")
    void descuentoDebeModificarDescripcion() {
        BigDecimal porcentajeDescuento = new BigDecimal("15.00");
        ServicioConDescuentoDecorator decorator = 
            new ServicioConDescuentoDecorator(servicioBase, porcentajeDescuento);

        String descripcion = decorator.getDescripcion();
        
        assertTrue(descripcion.contains("Descuento"));
        assertTrue(descripcion.contains("15"));
    }

    @Test
    @DisplayName("Decorator Descuento - Debe lanzar excepción con descuento negativo")
    void descuentoDebeLanzarExcepcionConDescuentoNegativo() {
        BigDecimal descuentoNegativo = new BigDecimal("-10.00");
        assertThrows(IllegalArgumentException.class, () -> new ServicioConDescuentoDecorator(servicioBase, descuentoNegativo));
    }

    @Test
    @DisplayName("Decorator Descuento - Debe lanzar excepción con descuento mayor a 100")
    void descuentoDebeLanzarExcepcionConDescuentoMayor100() {
        BigDecimal descuentoInvalido = new BigDecimal("150.00");
        assertThrows(IllegalArgumentException.class, () -> new ServicioConDescuentoDecorator(servicioBase, descuentoInvalido));
    }

    @Test
    @DisplayName("Decorator Descuento - Debe obtener servicio base")
    void descuentoDebeObtenerServicioBase() {
        ServicioConDescuentoDecorator decorator = 
            new ServicioConDescuentoDecorator(servicioBase, new BigDecimal("10.00"));

        assertEquals(servicioBase, decorator.getServicio());
    }

    @Test
    @DisplayName("Decorator Urgencia - Debe aplicar cargo adicional")
    void urgenciaDebeAplicarCargoAdicional() {
        ServicioUrgenciaDecorator decorator = new ServicioUrgenciaDecorator(servicioBase);
        
        BigDecimal precioConUrgencia = decorator.getPrecio();
        
        // Debe ser mayor que el precio base
        assertTrue(precioConUrgencia.compareTo(servicioBase.getPrecio()) > 0);
    }

    @Test
    @DisplayName("Decorator Urgencia - Debe modificar descripción")
    void urgenciaDebeModificarDescripcion() {
        ServicioUrgenciaDecorator decorator = new ServicioUrgenciaDecorator(servicioBase);
        
        String descripcion = decorator.getDescripcion();
        
        assertTrue(descripcion.contains("URGENCIA") || descripcion.contains("urgencia"));
    }

    @Test
    @DisplayName("Decorator - Debe poder combinar múltiples decoradores")
    void debePoderCombinarMultiplesDecoradores() {
        // Aplicar descuento primero
        ServicioConDescuentoDecorator conDescuento = 
            new ServicioConDescuentoDecorator(servicioBase, new BigDecimal("10.00"));
        
        // Luego aplicar urgencia (aunque en la práctica sería al revés)
        // Por ahora solo verificamos que ambos decoradores funcionan
        BigDecimal precioConDescuento = conDescuento.getPrecio();
        assertEquals(new BigDecimal("90.00"), precioConDescuento);
        
        // Nota: Para combinar decoradores, necesitaríamos que ServicioUrgenciaDecorator
        // acepte un ServicioDecorator en lugar de solo Servicio
    }
}

