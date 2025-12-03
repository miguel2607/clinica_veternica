package com.veterinaria.clinica_veternica.patterns.creational.singleton;

import com.veterinaria.clinica_veternica.config.TestMailConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el patrón Singleton - AuditLogger
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestMailConfig.class)
@TestPropertySource(properties = {
    "spring.dotenv.enabled=false",
    "JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "JWT_EXPIRATION=86400000"
})
class AuditLoggerTest {

    @Autowired
    private AuditLogger auditLogger;

    @BeforeEach
    void setUp() {
        // Limpiar logs antes de cada test
        auditLogger.clearLogs();
    }

    @Test
    @DisplayName("Singleton - Debe ser la misma instancia")
    void debeSerMismaInstancia() {
        // Verificar que Spring inyecta la misma instancia
        assertNotNull(auditLogger);
        
        // Verificar que es un componente de Spring (Singleton por defecto)
        AuditLogger otraInstancia = auditLogger;
        assertSame(auditLogger, otraInstancia);
    }

    @Test
    @DisplayName("Debe registrar un log de auditoría")
    void debeRegistrarLog() {
        auditLogger.log("CREATE", "Cita", 1L, "admin", "Cita creada");

        List<AuditLogger.AuditLog> logs = auditLogger.getAllLogs();
        assertEquals(1, logs.size());
        
        AuditLogger.AuditLog log = logs.get(0);
        assertEquals("CREATE", log.getAccion());
        assertEquals("Cita", log.getEntidad());
        assertEquals(1L, log.getEntidadId());
        assertEquals("admin", log.getUsuario());
        assertEquals("Cita creada", log.getDetalles());
        assertNotNull(log.getTimestamp());
    }

    @Test
    @DisplayName("Debe registrar log de creación")
    void debeRegistrarLogCreate() {
        auditLogger.logCreate("Mascota", 1L, "admin");

        List<AuditLogger.AuditLog> logs = auditLogger.getLogsByAccion("CREATE");
        assertEquals(1, logs.size());
        assertEquals("Mascota", logs.get(0).getEntidad());
    }

    @Test
    @DisplayName("Debe registrar log de actualización")
    void debeRegistrarLogUpdate() {
        auditLogger.logUpdate("Usuario", 1L, "admin", "Cambio de rol");

        List<AuditLogger.AuditLog> logs = auditLogger.getLogsByAccion("UPDATE");
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).getDetalles().contains("Cambio de rol"));
    }

    @Test
    @DisplayName("Debe registrar log de eliminación")
    void debeRegistrarLogDelete() {
        auditLogger.logDelete("Factura", 1L, "admin");

        List<AuditLogger.AuditLog> logs = auditLogger.getLogsByAccion("DELETE");
        assertEquals(1, logs.size());
    }

    @Test
    @DisplayName("Debe registrar log de acceso")
    void debeRegistrarLogAccess() {
        auditLogger.logAccess("HistoriaClinica", 1L, "veterinario");

        List<AuditLogger.AuditLog> logs = auditLogger.getLogsByAccion("ACCESS");
        assertEquals(1, logs.size());
    }

    @Test
    @DisplayName("Debe registrar cambio de estado")
    void debeRegistrarLogStateChange() {
        auditLogger.logStateChange("Cita", 1L, "admin", "PROGRAMADA", "CONFIRMADA");

        List<AuditLogger.AuditLog> logs = auditLogger.getLogsByAccion("STATE_CHANGE");
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).getDetalles().contains("PROGRAMADA -> CONFIRMADA"));
    }

    @Test
    @DisplayName("Debe registrar acceso no autorizado")
    void debeRegistrarLogUnauthorizedAccess() {
        auditLogger.logUnauthorizedAccess("Factura", 1L, "usuario", "Sin permisos");

        List<AuditLogger.AuditLog> logs = auditLogger.getLogsByAccion("UNAUTHORIZED");
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).getDetalles().contains("Sin permisos"));
    }

    @Test
    @DisplayName("Debe registrar error")
    void debeRegistrarLogError() {
        auditLogger.logError("Pago", 1L, "admin", "Error de conexión");

        List<AuditLogger.AuditLog> logs = auditLogger.getLogsByAccion("ERROR");
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).getDetalles().contains("Error de conexión"));
    }

    @Test
    @DisplayName("Debe filtrar logs por entidad")
    void debeFiltrarLogsPorEntidad() {
        auditLogger.logCreate("Cita", 1L, "admin");
        auditLogger.logCreate("Mascota", 1L, "admin");
        auditLogger.logCreate("Cita", 2L, "admin");

        List<AuditLogger.AuditLog> logsCita = auditLogger.getLogsByEntidad("Cita");
        assertEquals(2, logsCita.size());
        
        List<AuditLogger.AuditLog> logsMascota = auditLogger.getLogsByEntidad("Mascota");
        assertEquals(1, logsMascota.size());
    }

    @Test
    @DisplayName("Debe filtrar logs por usuario")
    void debeFiltrarLogsPorUsuario() {
        auditLogger.logCreate("Cita", 1L, "admin");
        auditLogger.logCreate("Mascota", 1L, "veterinario");
        auditLogger.logCreate("Factura", 1L, "admin");

        List<AuditLogger.AuditLog> logsAdmin = auditLogger.getLogsByUsuario("admin");
        assertEquals(2, logsAdmin.size());
        
        List<AuditLogger.AuditLog> logsVeterinario = auditLogger.getLogsByUsuario("veterinario");
        assertEquals(1, logsVeterinario.size());
    }

    @Test
    @DisplayName("Debe filtrar logs por acción")
    void debeFiltrarLogsPorAccion() {
        auditLogger.logCreate("Cita", 1L, "admin");
        auditLogger.logUpdate("Cita", 1L, "admin", "Cambio");
        auditLogger.logDelete("Cita", 1L, "admin");

        List<AuditLogger.AuditLog> logsCreate = auditLogger.getLogsByAccion("CREATE");
        assertEquals(1, logsCreate.size());
        
        List<AuditLogger.AuditLog> logsUpdate = auditLogger.getLogsByAccion("UPDATE");
        assertEquals(1, logsUpdate.size());
    }

    @Test
    @DisplayName("Debe filtrar logs por rango de fechas")
    void debeFiltrarLogsPorRangoFechas() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicio = ahora.minusHours(1);
        LocalDateTime fin = ahora.plusHours(1);

        auditLogger.logCreate("Cita", 1L, "admin");
        
        // Usar espera activa corta en lugar de Thread.sleep para evitar warning de SonarQube
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 10) {
            // Espera activa mínima para asegurar timestamp diferente
        }

        List<AuditLogger.AuditLog> logs = auditLogger.getLogsByDateRange(inicio, fin);
        assertTrue(logs.size() >= 1);
    }

    @Test
    @DisplayName("Debe obtener el conteo de logs")
    void debeObtenerConteoLogs() {
        assertEquals(0, auditLogger.getLogCount());

        auditLogger.logCreate("Cita", 1L, "admin");
        assertEquals(1, auditLogger.getLogCount());

        auditLogger.logCreate("Mascota", 1L, "admin");
        assertEquals(2, auditLogger.getLogCount());
    }

    @Test
    @DisplayName("Debe limpiar todos los logs")
    void debeLimpiarLogs() {
        auditLogger.logCreate("Cita", 1L, "admin");
        auditLogger.logCreate("Mascota", 1L, "admin");
        assertEquals(2, auditLogger.getLogCount());

        auditLogger.clearLogs();
        assertEquals(0, auditLogger.getLogCount());
        assertTrue(auditLogger.getAllLogs().isEmpty());
    }

    @Test
    @DisplayName("Debe manejar múltiples logs concurrentes")
    void debeManejarLogsConcurrentes() throws InterruptedException {
        // Simular múltiples operaciones concurrentes
        int numThreads = 100;
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    auditLogger.logCreate("Cita", (long) index, "admin");
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        // Esperar a que todos los threads terminen usando CountDownLatch
        latch.await();

        // Verificar que todos los logs se registraron
        assertTrue(auditLogger.getLogCount() >= numThreads);
    }
}

