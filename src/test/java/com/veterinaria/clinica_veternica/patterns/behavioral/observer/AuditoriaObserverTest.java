package com.veterinaria.clinica_veternica.patterns.behavioral.observer;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.patterns.creational.singleton.AuditLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests para el patrón Observer - AuditoriaObserver
 */
@ExtendWith(MockitoExtension.class)
class AuditoriaObserverTest {

    @Mock
    private AuditLogger auditLogger;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuditoriaObserver auditoriaObserver;

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
                .nombres("Dr. Juan")
                .apellidos("García")
                .especialidad("Medicina General")
                .documento("123456789")
                .correo("dr.garcia@clinica.com")
                .telefono("1234567890")
                .build();

        cita = Cita.builder()
                .idCita(1L)
                .mascota(mascota)
                .veterinario(veterinario)
                .fechaCita(LocalDate.now())
                .horaCita(LocalTime.of(10, 0))
                .build();

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Debe registrar cambio de estado de cita con usuario autenticado")
    void debeRegistrarCambioDeEstadoDeCitaConUsuarioAutenticado() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("admin");

        String estadoAnterior = "PROGRAMADA";
        String estadoNuevo = "CONFIRMADA";

        // Act
        auditoriaObserver.onCitaStateChanged(cita, estadoAnterior, estadoNuevo);

        // Assert
        verify(auditLogger, times(1)).logStateChange(
                "Cita",
                1L,
                "admin",
                estadoAnterior,
                estadoNuevo
        );
    }

    @Test
    @DisplayName("Debe usar SISTEMA cuando no hay usuario autenticado")
    void debeUsarSistemaCuandoNoHayUsuarioAutenticado() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        String estadoAnterior = "PROGRAMADA";
        String estadoNuevo = "CONFIRMADA";

        // Act
        auditoriaObserver.onCitaStateChanged(cita, estadoAnterior, estadoNuevo);

        // Assert
        verify(auditLogger, times(1)).logStateChange(
                "Cita",
                1L,
                "SISTEMA",
                estadoAnterior,
                estadoNuevo
        );
    }

    @Test
    @DisplayName("Debe usar SISTEMA cuando usuario es anonymousUser")
    void debeUsarSistemaCuandoUsuarioEsAnonymousUser() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("anonymousUser");

        String estadoAnterior = "PROGRAMADA";
        String estadoNuevo = "CONFIRMADA";

        // Act
        auditoriaObserver.onCitaStateChanged(cita, estadoAnterior, estadoNuevo);

        // Assert
        verify(auditLogger, times(1)).logStateChange(
                "Cita",
                1L,
                "SISTEMA",
                estadoAnterior,
                estadoNuevo
        );
    }

    @Test
    @DisplayName("Debe registrar creación de cita con detalles completos")
    void debeRegistrarCreacionDeCitaConDetallesCompletos() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("recepcionista");

        // Act
        auditoriaObserver.onCitaCreated(cita);

        // Assert
        verify(auditLogger, times(1)).logCreate(
                "Cita",
                1L,
                "recepcionista"
        );

        verify(auditLogger, times(1)).log(
                eq("CREATE"),
                eq("Cita"),
                eq(1L),
                eq("recepcionista"),
                argThat(msg -> msg.contains("Mascota: Max"))
        );
    }

    @Test
    @DisplayName("Debe registrar cancelación de cita con motivo")
    void debeRegistrarCancelacionDeCitaConMotivo() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("admin");

        String motivo = "Cliente canceló por enfermedad";

        // Act
        auditoriaObserver.onCitaCancelled(cita, motivo);

        // Assert
        verify(auditLogger, times(1)).log(
                eq("CANCEL"),
                eq("Cita"),
                eq(1L),
                eq("admin"),
                argThat(msg -> msg.contains(motivo))
        );
    }

    @Test
    @DisplayName("Debe registrar creación genérica de entidad")
    void debeRegistrarCreacionGenericaDeEntidad() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("veterinario");

        // Act
        auditoriaObserver.registrarCreacion("Mascota", 1L, "Nueva mascota: Max");

        // Assert
        verify(auditLogger, times(1)).logCreate(
                "Mascota",
                1L,
                "veterinario"
        );

        verify(auditLogger, times(1)).log(
                "CREATE",
                "Mascota",
                1L,
                "veterinario",
                "Nueva mascota: Max"
        );
    }

    @Test
    @DisplayName("No debe registrar detalles si son nulos o vacíos")
    void noDebeRegistrarDetallesSiSonNulosOVacios() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("admin");

        // Act
        auditoriaObserver.registrarCreacion("Mascota", 1L, null);

        // Assert
        verify(auditLogger, times(1)).logCreate(anyString(), anyLong(), anyString());
        verify(auditLogger, never()).log(anyString(), anyString(), anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("Debe registrar actualización de entidad con cambios")
    void debeRegistrarActualizacionDeEntidadConCambios() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("veterinario");

        String cambios = "Nombre: Max -> Maximus, Peso: 10kg -> 12kg";

        // Act
        auditoriaObserver.registrarActualizacion("Mascota", 1L, cambios);

        // Assert
        verify(auditLogger, times(1)).logUpdate(
                "Mascota",
                1L,
                "veterinario",
                cambios
        );
    }

    @Test
    @DisplayName("Debe registrar eliminación de entidad con motivo")
    void debeRegistrarEliminacionDeEntidadConMotivo() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("admin");

        String motivo = "Mascota fallecida";

        // Act
        auditoriaObserver.registrarEliminacion("Mascota", 1L, motivo);

        // Assert
        verify(auditLogger, times(1)).logDelete(
                "Mascota",
                1L,
                "admin"
        );

        verify(auditLogger, times(1)).log(
                eq("DELETE"),
                eq("Mascota"),
                eq(1L),
                eq("admin"),
                argThat(msg -> msg.contains(motivo))
        );
    }

    @Test
    @DisplayName("Debe registrar acceso a información sensible")
    void debeRegistrarAccesoAInformacionSensible() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("veterinario");

        // Act
        auditoriaObserver.registrarAccesoSensible("HistoriaClinica", 1L, "READ");

        // Assert
        verify(auditLogger, times(1)).logAccess(
                "HistoriaClinica",
                1L,
                "veterinario"
        );

        verify(auditLogger, times(1)).log(
                eq("ACCESS"),
                eq("HistoriaClinica"),
                eq(1L),
                eq("veterinario"),
                argThat(msg -> msg.contains("READ"))
        );
    }

    @Test
    @DisplayName("Debe registrar acceso no autorizado")
    void debeRegistrarAccesoNoAutorizado() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("usuario");

        String motivo = "Usuario no tiene permisos para ver historias clínicas";

        // Act
        auditoriaObserver.registrarAccesoNoAutorizado("HistoriaClinica", 1L, motivo);

        // Assert
        verify(auditLogger, times(1)).logUnauthorizedAccess(
                "HistoriaClinica",
                1L,
                "usuario",
                motivo
        );
    }

    @Test
    @DisplayName("Debe registrar operación financiera con monto")
    void debeRegistrarOperacionFinancieraConMonto() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("admin");

        BigDecimal monto = new BigDecimal("150.50");
        String detalles = "Pago de consulta veterinaria";

        // Act
        auditoriaObserver.registrarOperacionFinanciera("PAGO", 1L, monto, detalles);

        // Assert
        verify(auditLogger, times(1)).log(
                eq("PAGO"),
                eq("OperacionFinanciera"),
                eq(1L),
                eq("admin"),
                argThat(msg -> msg.contains("150.50"))
        );
    }

    @Test
    @DisplayName("Debe registrar operación financiera con monto nulo")
    void debeRegistrarOperacionFinancieraConMontoNulo() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("admin");

        // Act
        auditoriaObserver.registrarOperacionFinanciera("AJUSTE", 1L, null, "Ajuste manual");

        // Assert
        verify(auditLogger, times(1)).log(
                eq("AJUSTE"),
                eq("OperacionFinanciera"),
                eq(1L),
                eq("admin"),
                argThat(msg -> msg.contains("N/A"))
        );
    }

    @Test
    @DisplayName("Debe registrar múltiples operaciones correctamente")
    void debeRegistrarMultiplesOperacionesCorrectamente() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("admin");

        // Act
        auditoriaObserver.registrarCreacion("Mascota", 1L, "Mascota 1");
        auditoriaObserver.registrarActualizacion("Mascota", 1L, "Cambio 1");
        auditoriaObserver.registrarEliminacion("Mascota", 1L, "Eliminado");

        // Assert
        verify(auditLogger, times(1)).logCreate(anyString(), anyLong(), anyString());
        verify(auditLogger, times(1)).logUpdate(anyString(), anyLong(), anyString(), anyString());
        verify(auditLogger, times(1)).logDelete(anyString(), anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe manejar authentication no autenticada")
    void debeManejarAuthenticationNoAutenticada() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act
        auditoriaObserver.registrarCreacion("Mascota", 1L, "Test");

        // Assert
        verify(auditLogger, times(1)).logCreate(
                "Mascota",
                1L,
                "SISTEMA"
        );
    }

    @Test
    @DisplayName("Debe registrar diferentes tipos de acceso sensible")
    void debeRegistrarDiferentesTiposDeAccesoSensible() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("veterinario");

        // Act
        auditoriaObserver.registrarAccesoSensible("HistoriaClinica", 1L, "READ");
        auditoriaObserver.registrarAccesoSensible("HistoriaClinica", 1L, "EXPORT");
        auditoriaObserver.registrarAccesoSensible("HistoriaClinica", 1L, "PRINT");

        // Assert
        verify(auditLogger, times(3)).logAccess(anyString(), anyLong(), anyString());
        verify(auditLogger, times(3)).log(
                eq("ACCESS"),
                eq("HistoriaClinica"),
                eq(1L),
                eq("veterinario"),
                anyString()
        );
    }

    @Test
    @DisplayName("Debe incluir detalles en operación financiera")
    void debeIncluirDetallesEnOperacionFinanciera() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("admin");

        BigDecimal monto = new BigDecimal("250.00");
        String detalles = "Reembolso por cancelación de cirugía";

        // Act
        auditoriaObserver.registrarOperacionFinanciera("REEMBOLSO", 1L, monto, detalles);

        // Assert
        verify(auditLogger, times(1)).log(
                eq("REEMBOLSO"),
                eq("OperacionFinanciera"),
                eq(1L),
                eq("admin"),
                argThat((String arg) ->
                        arg.contains("REEMBOLSO") &&
                        arg.contains("250.00") &&
                        arg.contains("Reembolso por cancelación de cirugía")
                )
        );
    }
}
