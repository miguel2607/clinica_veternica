package com.veterinaria.clinica_veternica.patterns.structural.proxy;

import com.veterinaria.clinica_veternica.domain.inventario.Inventario;
import com.veterinaria.clinica_veternica.patterns.creational.singleton.AuditLogger;
import com.veterinaria.clinica_veternica.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyLong;

import org.mockito.ArgumentCaptor;

/**
 * Tests para el patrón Proxy - InventarioProxy
 */
@ExtendWith(MockitoExtension.class)
class InventarioProxyTest {

    @Mock
    private AuditLogger auditLogger;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private InventarioProxy inventarioProxy;

    private Inventario inventario;

    @BeforeEach
    void setUp() {
        inventario = Inventario.builder()
                .idInventario(1L)
                .cantidadActual(100)
                .build();

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Debe permitir modificar inventario a usuario ADMIN")
    void debePermitirModificarInventarioAUsuarioAdmin() {
        // Arrange
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(Constants.ROLE_ADMIN_STRING)
        );
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(authentication.getName()).thenReturn("admin");
        lenient().when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Act
        boolean tienePermiso = inventarioProxy.tienePermisoModificar();

        // Assert
        assertTrue(tienePermiso);
    }

    @Test
    @DisplayName("Debe permitir modificar inventario a usuario VETERINARIO")
    void debePermitirModificarInventarioAUsuarioVeterinario() {
        // Arrange
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(Constants.ROLE_VETERINARIO_STRING)
        );
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(authentication.getName()).thenReturn("veterinario");
        lenient().when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Act
        boolean tienePermiso = inventarioProxy.tienePermisoModificar();

        // Assert
        assertTrue(tienePermiso);
    }

    @Test
    @DisplayName("Debe denegar modificación a usuario RECEPCIONISTA")
    void debeDenegarModificacionAUsuarioRecepcionista() {
        // Arrange
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_RECEPCIONISTA")
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("recepcionista");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Act
        boolean tienePermiso = inventarioProxy.tienePermisoModificar();

        // Assert
        assertFalse(tienePermiso);
        assertUnauthorizedAccessLog("recepcionista", "Intento de modificación sin permisos");
    }

    @Test
    @DisplayName("Debe denegar modificación a usuario sin permisos")
    void debeDenegarModificacionAUsuarioSinPermisos() {
        // Arrange
        Collection<GrantedAuthority> authorities = Collections.emptyList();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("usuario");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Act
        boolean tienePermiso = inventarioProxy.tienePermisoModificar();

        // Assert
        assertFalse(tienePermiso);
        assertUnauthorizedAccessLog("usuario", null);
    }

    @Test
    @DisplayName("Debe denegar acceso si no está autenticado")
    void debeDenegarAccesoSiNoEstaAutenticado() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        boolean tienePermiso = inventarioProxy.tienePermisoModificar();

        // Assert
        assertFalse(tienePermiso);
        verify(auditLogger, never()).logUnauthorizedAccess(anyString(), any(), anyString(), anyString());
    }

    @Test
    @DisplayName("Debe denegar acceso si la autenticación no está autenticada")
    void debeDenegarAccesoSiAutenticacionNoEstaAutenticada() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act
        boolean tienePermiso = inventarioProxy.tienePermisoModificar();

        // Assert
        assertFalse(tienePermiso);
    }

    @Test
    @DisplayName("Debe registrar modificación con usuario autenticado")
    void debeRegistrarModificacionConUsuarioAutenticado() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");

        String accion = "ACTUALIZAR_STOCK";
        String detalles = "Actualización de stock: 100 -> 80";

        // Act
        inventarioProxy.registrarModificacion(inventario, accion, detalles);

        // Assert
        assertAuditLogEquals(accion, "admin", detalles);
    }

    @Test
    @DisplayName("Debe registrar modificación con usuario SISTEMA si no hay autenticación")
    void debeRegistrarModificacionConUsuarioSistema() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        String accion = "ACTUALIZAR_STOCK_AUTOMATICO";
        String detalles = "Actualización automática de stock";

        // Act
        inventarioProxy.registrarModificacion(inventario, accion, detalles);

        // Assert
        assertAuditLogEquals(accion, "SISTEMA", detalles);
    }

    @Test
    @DisplayName("Debe registrar modificación de incremento de stock")
    void debeRegistrarModificacionIncrementoStock() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("veterinario");

        String accion = "INCREMENTAR_STOCK";
        String detalles = "Ingreso de insumos: +50 unidades";

        // Act
        inventarioProxy.registrarModificacion(inventario, accion, detalles);

        // Assert
        assertAuditLogContains(accion, "veterinario", "Ingreso de insumos");
    }

    @Test
    @DisplayName("Debe registrar modificación de decremento de stock")
    void debeRegistrarModificacionDecrementoStock() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("veterinario");

        String accion = "DECREMENTAR_STOCK";
        String detalles = "Uso de insumos en cita: -20 unidades";

        // Act
        inventarioProxy.registrarModificacion(inventario, accion, detalles);

        // Assert
        assertAuditLogContains(accion, "veterinario", "Uso de insumos");
    }

    @Test
    @DisplayName("Debe permitir múltiples verificaciones de permisos sin afectar auditoría")
    void debePermitirMultiplesVerificacionesDePermisos() {
        // Arrange
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(Constants.ROLE_ADMIN_STRING)
        );
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(authentication.getName()).thenReturn("admin");
        lenient().when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Act
        boolean permiso1 = inventarioProxy.tienePermisoModificar();
        boolean permiso2 = inventarioProxy.tienePermisoModificar();
        boolean permiso3 = inventarioProxy.tienePermisoModificar();

        // Assert
        assertTrue(permiso1);
        assertTrue(permiso2);
        assertTrue(permiso3);
        // No debe registrar acceso no autorizado
        verify(auditLogger, never()).logUnauthorizedAccess(anyString(), any(), anyString(), anyString());
    }

    @Test
    @DisplayName("Debe manejar múltiples roles correctamente")
    void debeManejarMultiplesRolesCorrectamente() {
        // Arrange
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(Constants.ROLE_VETERINARIO_STRING),
                new SimpleGrantedAuthority("ROLE_RECEPCIONISTA")
        );
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(authentication.getName()).thenReturn("usuario-multiple");
        lenient().when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Act
        boolean tienePermiso = inventarioProxy.tienePermisoModificar();

        // Assert
        assertTrue(tienePermiso); // Debe permitir porque tiene rol VETERINARIO
    }

    @Test
    @DisplayName("Debe registrar correctamente cuando hay múltiples modificaciones seguidas")
    void debeRegistrarCorrectamenteMuchasModificacionesSeguidas() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");

        // Act
        inventarioProxy.registrarModificacion(inventario, "ACCION_1", "Detalle 1");
        inventarioProxy.registrarModificacion(inventario, "ACCION_2", "Detalle 2");
        inventarioProxy.registrarModificacion(inventario, "ACCION_3", "Detalle 3");

        // Assert
        verify(auditLogger, times(3)).log(anyString(), anyString(), anyLong(), anyString(), anyString());
    }

    private void assertUnauthorizedAccessLog(String expectedUsuario, String expectedDetalleFragment) {
        ArgumentCaptor<String> entidadCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> usuarioCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> detalleCaptor = ArgumentCaptor.forClass(String.class);

        verify(auditLogger, times(1)).logUnauthorizedAccess(
                entidadCaptor.capture(),
                idCaptor.capture(),
                usuarioCaptor.capture(),
                detalleCaptor.capture()
        );

        assertEquals("Inventario", entidadCaptor.getValue());
        assertNull(idCaptor.getValue());
        assertEquals(expectedUsuario, usuarioCaptor.getValue());
        if (expectedDetalleFragment != null) {
            assertTrue(detalleCaptor.getValue().contains(expectedDetalleFragment));
        }
    }

    private void assertAuditLogEquals(String expectedAccion, String expectedUsuario, String expectedDetalles) {
        ArgumentCaptor<String> accionCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> entidadCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> usuarioCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> detalleCaptor = ArgumentCaptor.forClass(String.class);

        verify(auditLogger, times(1)).log(
                accionCaptor.capture(),
                entidadCaptor.capture(),
                idCaptor.capture(),
                usuarioCaptor.capture(),
                detalleCaptor.capture()
        );

        assertEquals(expectedAccion, accionCaptor.getValue());
        assertEquals("Inventario", entidadCaptor.getValue());
        assertEquals(1L, idCaptor.getValue());
        assertEquals(expectedUsuario, usuarioCaptor.getValue());
        assertEquals(expectedDetalles, detalleCaptor.getValue());
    }

    private void assertAuditLogContains(String expectedAccion, String expectedUsuario, String textoEsperado) {
        ArgumentCaptor<String> accionCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> entidadCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> usuarioCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> detalleCaptor = ArgumentCaptor.forClass(String.class);

        verify(auditLogger, times(1)).log(
                accionCaptor.capture(),
                entidadCaptor.capture(),
                idCaptor.capture(),
                usuarioCaptor.capture(),
                detalleCaptor.capture()
        );

        assertEquals(expectedAccion, accionCaptor.getValue());
        assertEquals("Inventario", entidadCaptor.getValue());
        assertEquals(1L, idCaptor.getValue());
        assertEquals(expectedUsuario, usuarioCaptor.getValue());
        assertTrue(detalleCaptor.getValue().contains(textoEsperado));
    }
}
