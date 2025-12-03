package com.veterinaria.clinica_veternica.patterns.creational.factory;

import com.veterinaria.clinica_veternica.domain.agenda.CategoriaServicio;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.veterinaria.clinica_veternica.config.TestMailConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el patrón Factory Method - ServicioFactory
 */
@SpringBootTest
@Import(TestMailConfig.class)
@TestPropertySource(properties = {
    "spring.dotenv.enabled=false",
    "JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "JWT_EXPIRATION=86400000"
})
class ServicioFactoryTest {

    @Autowired
    private ServicioClinicoFactory servicioClinicoFactory;

    @Autowired
    private ServicioQuirurgicoFactory servicioQuirurgicoFactory;

    @Autowired
    private ServicioEsteticoFactory servicioEsteticoFactory;

    @Autowired
    private ServicioEmergenciaFactory servicioEmergenciaFactory;

    private String nombreServicio;
    private String descripcionServicio;
    private BigDecimal precioServicio;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        nombreServicio = "Consulta General";
        descripcionServicio = "Consulta veterinaria de rutina";
        precioServicio = new BigDecimal("50000");
    }

    // ========== ServicioClinicoFactory Tests ==========

    @Test
    @DisplayName("Factory Clínico - Debe crear servicio clínico exitosamente")
    void clinicoDebeCrearServicioExitosamente() {
        Servicio servicio = servicioClinicoFactory.crearServicioCompleto(
                nombreServicio, descripcionServicio, precioServicio);

        assertNotNull(servicio);
        assertEquals(nombreServicio, servicio.getNombre());
        assertEquals(descripcionServicio, servicio.getDescripcion());
        assertEquals(precioServicio, servicio.getPrecio());
        assertEquals(CategoriaServicio.CLINICO, servicio.getCategoria());
    }

    @Test
    @DisplayName("Factory Clínico - Debe aplicar configuraciones específicas")
    void clinicoDebeAplicarConfiguracionesEspecificas() {
        Servicio servicio = servicioClinicoFactory.crearServicioCompleto(
                nombreServicio, descripcionServicio, precioServicio);

        assertFalse(servicio.getRequiereAnestesia());
        assertFalse(servicio.getRequiereAyuno());
        assertFalse(servicio.getRequiereHospitalizacion());
        assertTrue(servicio.getDisponibleEmergencias());
        assertTrue(servicio.getDisponibleDomicilio());
        assertNotNull(servicio.getDuracionEstimadaMinutos());
        assertTrue(servicio.getDuracionEstimadaMinutos() >= 15);
    }

    @Test
    @DisplayName("Factory Clínico - Debe obtener categoría correcta")
    void clinicoDebeObtenerCategoriaCorrecta() {
        assertEquals(CategoriaServicio.CLINICO, servicioClinicoFactory.getCategoria());
    }

    // ========== ServicioQuirurgicoFactory Tests ==========

    @Test
    @DisplayName("Factory Quirúrgico - Debe crear servicio quirúrgico exitosamente")
    void quirurgicoDebeCrearServicioExitosamente() {
        Servicio servicio = servicioQuirurgicoFactory.crearServicioCompleto(
                "Cirugía de esterilización", "Cirugía para esterilizar mascota", new BigDecimal("300000"));

        assertNotNull(servicio);
        assertEquals(CategoriaServicio.QUIRURGICO, servicio.getCategoria());
    }

    @Test
    @DisplayName("Factory Quirúrgico - Debe aplicar configuraciones específicas")
    void quirurgicoDebeAplicarConfiguracionesEspecificas() {
        Servicio servicio = servicioQuirurgicoFactory.crearServicioCompleto(
                "Cirugía", "Descripción", new BigDecimal("300000"));

        assertTrue(servicio.getRequiereAnestesia());
        assertTrue(servicio.getRequiereAyuno());
        assertTrue(servicio.getRequiereHospitalizacion());
    }

    @Test
    @DisplayName("Factory Quirúrgico - Debe obtener categoría correcta")
    void quirurgicoDebeObtenerCategoriaCorrecta() {
        assertEquals(CategoriaServicio.QUIRURGICO, servicioQuirurgicoFactory.getCategoria());
    }

    // ========== ServicioEsteticoFactory Tests ==========

    @Test
    @DisplayName("Factory Estético - Debe crear servicio estético exitosamente")
    void esteticoDebeCrearServicioExitosamente() {
        Servicio servicio = servicioEsteticoFactory.crearServicioCompleto(
                "Baño y peluquería", "Servicio de baño y corte de pelo", new BigDecimal("80000"));

        assertNotNull(servicio);
        assertEquals(CategoriaServicio.ESTETICO, servicio.getCategoria());
    }

    @Test
    @DisplayName("Factory Estético - Debe obtener categoría correcta")
    void esteticoDebeObtenerCategoriaCorrecta() {
        assertEquals(CategoriaServicio.ESTETICO, servicioEsteticoFactory.getCategoria());
    }

    // ========== ServicioEmergenciaFactory Tests ==========

    @Test
    @DisplayName("Factory Emergencia - Debe crear servicio de emergencia exitosamente")
    void emergenciaDebeCrearServicioExitosamente() {
        Servicio servicio = servicioEmergenciaFactory.crearServicioCompleto(
                "Atención de emergencia", "Atención veterinaria de urgencia", new BigDecimal("150000"));

        assertNotNull(servicio);
        assertEquals(CategoriaServicio.EMERGENCIA, servicio.getCategoria());
    }

    @Test
    @DisplayName("Factory Emergencia - Debe aplicar configuraciones específicas")
    void emergenciaDebeAplicarConfiguracionesEspecificas() {
        Servicio servicio = servicioEmergenciaFactory.crearServicioCompleto(
                "Emergencia", "Descripción", new BigDecimal("150000"));

        assertTrue(servicio.getDisponibleEmergencias());
        assertTrue(servicio.getActivo());
    }

    @Test
    @DisplayName("Factory Emergencia - Debe obtener categoría correcta")
    void emergenciaDebeObtenerCategoriaCorrecta() {
        assertEquals(CategoriaServicio.EMERGENCIA, servicioEmergenciaFactory.getCategoria());
    }

    // ========== Tests de Validación ==========

    @Test
    @DisplayName("Factory - Debe lanzar excepción con nombre vacío")
    void debeLanzarExcepcionConNombreVacio() {
        assertThrows(IllegalArgumentException.class, () -> {
            servicioClinicoFactory.crearServicioCompleto("", descripcionServicio, precioServicio);
        });
    }

    @Test
    @DisplayName("Factory - Debe lanzar excepción con precio cero")
    void debeLanzarExcepcionConPrecioCero() {
        assertThrows(IllegalArgumentException.class, () -> servicioClinicoFactory.crearServicioCompleto(nombreServicio, descripcionServicio, BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Factory - Debe lanzar excepción con precio negativo")
    void debeLanzarExcepcionConPrecioNegativo() {
        BigDecimal precioNegativo = new BigDecimal("-100");
        assertThrows(IllegalArgumentException.class, () -> servicioClinicoFactory.crearServicioCompleto(nombreServicio, descripcionServicio, precioNegativo));
    }

    @Test
    @DisplayName("Factory - Debe usar precio por defecto si no se proporciona")
    void debeUsarPrecioPorDefectoSiNoSeProporciona() {
        Servicio servicio = servicioClinicoFactory.crearServicio(nombreServicio, descripcionServicio, null);
        
        assertNotNull(servicio.getPrecio());
        assertTrue(servicio.getPrecio().compareTo(BigDecimal.ZERO) > 0);
    }

    // ========== Tests de Comparación entre Factories ==========

    @Test
    @DisplayName("Factory - Diferentes factories deben crear servicios con diferentes categorías")
    void diferentesFactoriesDebenCrearServiciosConDiferentesCategorias() {
        Servicio clinico = servicioClinicoFactory.crearServicioCompleto(nombreServicio, descripcionServicio, precioServicio);
        Servicio quirurgico = servicioQuirurgicoFactory.crearServicioCompleto("Cirugía", "Desc", precioServicio);
        Servicio estetico = servicioEsteticoFactory.crearServicioCompleto("Baño", "Desc", precioServicio);
        Servicio emergencia = servicioEmergenciaFactory.crearServicioCompleto("Emergencia", "Desc", precioServicio);

        assertEquals(CategoriaServicio.CLINICO, clinico.getCategoria());
        assertEquals(CategoriaServicio.QUIRURGICO, quirurgico.getCategoria());
        assertEquals(CategoriaServicio.ESTETICO, estetico.getCategoria());
        assertEquals(CategoriaServicio.EMERGENCIA, emergencia.getCategoria());
    }
}

