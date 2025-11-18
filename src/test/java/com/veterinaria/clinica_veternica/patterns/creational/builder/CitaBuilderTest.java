package com.veterinaria.clinica_veternica.patterns.creational.builder;

import com.veterinaria.clinica_veternica.domain.agenda.CategoriaServicio;
import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el patrón Builder - CitaBuilder
 */
class CitaBuilderTest {

    private Mascota mascota;
    private Veterinario veterinario;
    private Servicio servicio;
    private CitaBuilder builder;

    @BeforeEach
    void setUp() {
        mascota = Mascota.builder()
                .idMascota(1L)
                .nombre("Max")
                .build();

        veterinario = Veterinario.builder()
                .idPersonal(1L)
                .nombres("Dr. Carlos")
                .apellidos("García")
                .build();

        servicio = Servicio.builder()
                .idServicio(1L)
                .nombre("Consulta General")
                .categoria(CategoriaServicio.CLINICO)
                .precio(new BigDecimal("100.00"))
                .duracionEstimadaMinutos(30)
                .disponibleEmergencias(true)
                .disponibleDomicilio(true)
                .build();

        builder = new CitaBuilder();
    }

    @Test
    @DisplayName("Builder - Debe construir cita básica exitosamente")
    void debeConstruirCitaBasica() {
        Cita cita = builder
                .conMascota(mascota)
                .conVeterinario(veterinario)
                .conServicio(servicio)
                .conFecha(LocalDate.now().plusDays(1))
                .conHora(LocalTime.of(10, 0))
                .conMotivoConsulta("Consulta de rutina")
                .build();

        assertNotNull(cita);
        assertEquals(mascota, cita.getMascota());
        assertEquals(veterinario, cita.getVeterinario());
        assertEquals(servicio, cita.getServicio());
        assertEquals(EstadoCita.PROGRAMADA, cita.getEstado());
        assertEquals("Consulta de rutina", cita.getMotivoConsulta());
        assertEquals(new BigDecimal("100.00"), cita.getPrecioFinal());
    }

    @Test
    @DisplayName("Builder - Debe usar duración del servicio si no se especifica")
    void debeUsarDuracionDelServicio() {
        Cita cita = builder
                .conMascota(mascota)
                .conVeterinario(veterinario)
                .conServicio(servicio)
                .conFecha(LocalDate.now().plusDays(1))
                .conHora(LocalTime.of(10, 0))
                .conMotivoConsulta("Consulta")
                .build();

        assertEquals(30, cita.getDuracionEstimadaMinutos());
    }

    @Test
    @DisplayName("Builder - Debe usar duración personalizada si se especifica")
    void debeUsarDuracionPersonalizada() {
        Cita cita = builder
                .conMascota(mascota)
                .conVeterinario(veterinario)
                .conServicio(servicio)
                .conFecha(LocalDate.now().plusDays(1))
                .conHora(LocalTime.of(10, 0))
                .conMotivoConsulta("Consulta")
                .conDuracion(45)
                .build();

        assertEquals(45, cita.getDuracionEstimadaMinutos());
    }

    @Test
    @DisplayName("Builder - Debe construir cita de emergencia")
    void debeConstruirCitaEmergencia() {
        Cita cita = builder
                .conMascota(mascota)
                .conVeterinario(veterinario)
                .conServicio(servicio)
                .conFecha(LocalDate.now())
                .conHora(LocalTime.of(10, 0))
                .conMotivoConsulta("Emergencia")
                .comoEmergencia()
                .build();

        assertTrue(cita.getEsEmergencia());
        // El precio final se calcula basado en el precio del servicio
        assertNotNull(cita.getPrecioFinal());
    }

    @Test
    @DisplayName("Builder - Debe construir cita a domicilio")
    void debeConstruirCitaDomicilio() {
        String direccion = "Calle 123 #45-67";
        Cita cita = builder
                .conMascota(mascota)
                .conVeterinario(veterinario)
                .conServicio(servicio)
                .conFecha(LocalDate.now().plusDays(1))
                .conHora(LocalTime.of(10, 0))
                .conMotivoConsulta("Consulta a domicilio")
                .comoDomicilio(direccion)
                .build();

        assertTrue(cita.getEsDomicilio());
        assertEquals(direccion, cita.getDireccionDomicilio());
        // El precio final se calcula basado en el precio del servicio
        assertNotNull(cita.getPrecioFinal());
    }

    @Test
    @DisplayName("Builder - Debe establecer observaciones")
    void debeEstablecerObservaciones() {
        String observaciones = "Mascota nerviosa, requiere manejo especial";
        Cita cita = builder
                .conMascota(mascota)
                .conVeterinario(veterinario)
                .conServicio(servicio)
                .conFecha(LocalDate.now().plusDays(1))
                .conHora(LocalTime.of(10, 0))
                .conMotivoConsulta("Consulta")
                .conObservaciones(observaciones)
                .build();

        assertEquals(observaciones, cita.getObservaciones());
    }

    @Test
    @DisplayName("Builder - Debe establecer precio final personalizado")
    void debeEstablecerPrecioFinalPersonalizado() {
        BigDecimal precioPersonalizado = new BigDecimal("200.00");
        Cita cita = builder
                .conMascota(mascota)
                .conVeterinario(veterinario)
                .conServicio(servicio)
                .conFecha(LocalDate.now().plusDays(1))
                .conHora(LocalTime.of(10, 0))
                .conMotivoConsulta("Consulta")
                .conPrecioFinal(precioPersonalizado)
                .build();

        assertEquals(precioPersonalizado, cita.getPrecioFinal());
    }

    @Test
    @DisplayName("Builder - Debe lanzar excepción sin mascota")
    void debeLanzarExcepcionSinMascota() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime hora = LocalTime.of(10, 0);
        CitaBuilder builderIncompleto = builder
                .conVeterinario(veterinario)
                .conServicio(servicio)
                .conFecha(fecha)
                .conHora(hora)
                .conMotivoConsulta("Consulta");
        assertThrows(IllegalStateException.class, builderIncompleto::build);
    }

    @Test
    @DisplayName("Builder - Debe lanzar excepción sin veterinario")
    void debeLanzarExcepcionSinVeterinario() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime hora = LocalTime.of(10, 0);
        CitaBuilder builderIncompleto = builder
                .conMascota(mascota)
                .conServicio(servicio)
                .conFecha(fecha)
                .conHora(hora)
                .conMotivoConsulta("Consulta");
        assertThrows(IllegalStateException.class, builderIncompleto::build);
    }

    @Test
    @DisplayName("Builder - Debe lanzar excepción sin servicio")
    void debeLanzarExcepcionSinServicio() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime hora = LocalTime.of(10, 0);
        CitaBuilder builderIncompleto = builder
                .conMascota(mascota)
                .conVeterinario(veterinario)
                .conFecha(fecha)
                .conHora(hora)
                .conMotivoConsulta("Consulta");
        assertThrows(IllegalStateException.class, builderIncompleto::build);
    }

    @Test
    @DisplayName("Builder - Debe lanzar excepción sin fecha")
    void debeLanzarExcepcionSinFecha() {
        LocalTime hora = LocalTime.of(10, 0);
        CitaBuilder builderIncompleto = builder
                .conMascota(mascota)
                .conVeterinario(veterinario)
                .conServicio(servicio)
                .conHora(hora)
                .conMotivoConsulta("Consulta");
        assertThrows(IllegalStateException.class, builderIncompleto::build);
    }

    @Test
    @DisplayName("Builder - Debe lanzar excepción sin hora")
    void debeLanzarExcepcionSinHora() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        CitaBuilder builderIncompleto = builder
                .conMascota(mascota)
                .conVeterinario(veterinario)
                .conServicio(servicio)
                .conFecha(fecha)
                .conMotivoConsulta("Consulta");
        assertThrows(IllegalStateException.class, builderIncompleto::build);
    }

    @Test
    @DisplayName("Builder - Debe lanzar excepción sin motivo de consulta")
    void debeLanzarExcepcionSinMotivoConsulta() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime hora = LocalTime.of(10, 0);
        CitaBuilder builderIncompleto = builder
                .conMascota(mascota)
                .conVeterinario(veterinario)
                .conServicio(servicio)
                .conFecha(fecha)
                .conHora(hora);
        assertThrows(IllegalStateException.class, builderIncompleto::build);
    }

    @Test
    @DisplayName("Builder - Debe lanzar excepción con motivo de consulta vacío")
    void debeLanzarExcepcionConMotivoConsultaVacio() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime hora = LocalTime.of(10, 0);
        CitaBuilder builderIncompleto = builder
                .conMascota(mascota)
                .conVeterinario(veterinario)
                .conServicio(servicio)
                .conFecha(fecha)
                .conHora(hora)
                .conMotivoConsulta("");
        assertThrows(IllegalStateException.class, builderIncompleto::build);
    }

    @Test
    @DisplayName("Builder - Debe lanzar excepción si servicio no está disponible para emergencias")
    void debeLanzarExcepcionSiServicioNoDisponibleEmergencias() {
        servicio.setDisponibleEmergencias(false);
        LocalDate fecha = LocalDate.now();
        LocalTime hora = LocalTime.of(10, 0);
        CitaBuilder builderIncompleto = builder
                .conMascota(mascota)
                .conVeterinario(veterinario)
                .conServicio(servicio)
                .conFecha(fecha)
                .conHora(hora)
                .conMotivoConsulta("Emergencia")
                .comoEmergencia();
        assertThrows(IllegalStateException.class, builderIncompleto::build);
    }

    @Test
    @DisplayName("Builder - Debe lanzar excepción si servicio no está disponible a domicilio")
    void debeLanzarExcepcionSiServicioNoDisponibleDomicilio() {
        servicio.setDisponibleDomicilio(false);
        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime hora = LocalTime.of(10, 0);
        CitaBuilder builderIncompleto = builder
                .conMascota(mascota)
                .conVeterinario(veterinario)
                .conServicio(servicio)
                .conFecha(fecha)
                .conHora(hora)
                .conMotivoConsulta("Consulta")
                .comoDomicilio("Dirección");
        assertThrows(IllegalStateException.class, builderIncompleto::build);
    }
}

