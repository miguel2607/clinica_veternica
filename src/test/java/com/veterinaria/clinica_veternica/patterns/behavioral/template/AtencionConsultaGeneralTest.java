package com.veterinaria.clinica_veternica.patterns.behavioral.template;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AtencionConsultaGeneralTest {

    @Mock
    private Mascota mascota;

    @Mock
    private Veterinario veterinario;

    @Mock
    private Servicio servicio;

    private AtencionConsultaGeneral atencion;
    private Cita cita;

    @BeforeEach
    void setUp() {
        atencion = new AtencionConsultaGeneral();

        cita = Cita.builder()
                .idCita(1L)
                .mascota(mascota)
                .veterinario(veterinario)
                .servicio(servicio)
                .fechaCita(LocalDate.now())
                .horaCita(LocalTime.of(10, 0))
                .motivoConsulta("Consulta de rutina")
                .estado(EstadoCita.CONFIRMADA)
                .build();
    }

    @Test
    @DisplayName("Template - Debe procesar atención completa exitosamente")
    void debeProcesarAtencionCompleta() {
        assertDoesNotThrow(() -> atencion.procesarAtencion(cita));
    }

    @Test
    @DisplayName("Template - Debe lanzar excepción cuando cita no tiene mascota")
    void debeLanzarExcepcionCitaSinMascota() {
        cita.setMascota(null);

        assertThrows(IllegalStateException.class, () -> atencion.procesarAtencion(cita));
    }
}

