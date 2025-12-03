package com.veterinaria.clinica_veternica.patterns.behavioral.template;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.veterinaria.clinica_veternica.config.TestMailConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el patrón Template Method - AtencionTemplate
 */
@SpringBootTest
@Import(TestMailConfig.class)
@TestPropertySource(properties = {
    "spring.dotenv.enabled=false",
    "JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "JWT_EXPIRATION=86400000"
})
class AtencionTemplateTest {

    @Autowired
    private AtencionConsultaGeneral atencionConsultaGeneral;

    @Autowired
    private AtencionEmergencia atencionEmergencia;

    @Autowired
    private AtencionCirugia atencionCirugia;

    private Cita cita;
    private Mascota mascota;
    private Veterinario veterinario;

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

        cita = Cita.builder()
                .idCita(1L)
                .mascota(mascota)
                .veterinario(veterinario)
                .fechaCita(LocalDate.now())
                .horaCita(LocalTime.of(10, 0))
                .motivoConsulta("Consulta de rutina")
                .estado(EstadoCita.CONFIRMADA)
                .build();
    }

    @Test
    @DisplayName("Template Consulta General - Debe procesar atención exitosamente")
    void consultaGeneralDebeProcesarAtencionExitosamente() {
        assertDoesNotThrow(() -> {
            atencionConsultaGeneral.procesarAtencion(cita);
        });

        // Verificar que la cita fue marcada como atendida
        assertEquals(EstadoCita.ATENDIDA, cita.getEstado());
    }

    @Test
    @DisplayName("Template Consulta General - Debe validar pre-atención")
    void consultaGeneralDebeValidarPreAtencion() {
        cita.setMascota(null);

        // validarPreAtencion debe lanzar IllegalStateException cuando la mascota es null
        assertThrows(IllegalStateException.class, () -> {
            atencionConsultaGeneral.procesarAtencion(cita);
        });
    }

    @Test
    @DisplayName("Template Emergencia - Debe procesar atención de emergencia")
    void emergenciaDebeProcesarAtencion() {
        assertDoesNotThrow(() -> {
            atencionEmergencia.procesarAtencion(cita);
        });

        assertEquals(EstadoCita.ATENDIDA, cita.getEstado());
    }

    @Test
    @DisplayName("Template Cirugía - Debe procesar atención quirúrgica")
    void cirugiaDebeProcesarAtencion() {
        assertDoesNotThrow(() -> {
            atencionCirugia.procesarAtencion(cita);
        });

        assertEquals(EstadoCita.ATENDIDA, cita.getEstado());
    }

    @Test
    @DisplayName("Template - Debe seguir el mismo flujo para todos los tipos")
    void debeSeguirMismoFlujoParaTodosLosTipos() {
        // Todos los tipos de atención deben seguir el mismo flujo:
        // 1. Preparar
        // 2. Validar
        // 3. Ejecutar
        // 4. Finalizar
        // 5. Registrar

        assertDoesNotThrow(() -> {
            atencionConsultaGeneral.procesarAtencion(cita);
        });

        // Resetear cita
        cita.setEstado(EstadoCita.CONFIRMADA);
        assertDoesNotThrow(() -> {
            atencionEmergencia.procesarAtencion(cita);
        });

        // Resetear cita
        cita.setEstado(EstadoCita.CONFIRMADA);
        assertDoesNotThrow(() -> {
            atencionCirugia.procesarAtencion(cita);
        });
    }
}

