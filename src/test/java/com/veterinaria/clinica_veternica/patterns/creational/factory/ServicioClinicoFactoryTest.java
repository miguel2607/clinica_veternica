package com.veterinaria.clinica_veternica.patterns.creational.factory;

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
class ServicioClinicoFactoryTest {

    private ServicioClinicoFactory factory;

    @BeforeEach
    void setUp() {
        factory = new ServicioClinicoFactory();
    }

    @Test
    @DisplayName("Factory - Debe crear servicio clínico correctamente")
    void debeCrearServicioClinico() {
        String nombre = "Consulta General";
        String descripcion = "Consulta veterinaria general";
        BigDecimal precio = BigDecimal.valueOf(50.00);

        Servicio servicio = factory.crearServicioCompleto(nombre, descripcion, precio);

        assertNotNull(servicio);
        assertEquals(nombre, servicio.getNombre());
        assertEquals(descripcion, servicio.getDescripcion());
        assertEquals(precio, servicio.getPrecio());
        assertEquals(CategoriaServicio.CLINICO, servicio.getCategoria());
    }

    @Test
    @DisplayName("Factory - Debe retornar categoría correcta")
    void debeRetornarCategoriaCorrecta() {
        assertEquals(CategoriaServicio.CLINICO, factory.getCategoria());
    }
}

