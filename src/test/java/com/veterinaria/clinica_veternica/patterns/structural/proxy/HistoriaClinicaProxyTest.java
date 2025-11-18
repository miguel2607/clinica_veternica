package com.veterinaria.clinica_veternica.patterns.structural.proxy;

import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
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

/**
 * Tests para el patrón Proxy - HistoriaClinicaProxy
 */
@ExtendWith(MockitoExtension.class)
class HistoriaClinicaProxyTest {

    @Mock
    private AuditLogger auditLogger;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private HistoriaClinicaProxy historiaClinicaProxy;

    private HistoriaClinica historiaClinica;

    @BeforeEach
    void setUp() {
        historiaClinica = HistoriaClinica.builder()
                .idHistoriaClinica(1L)
                .numeroHistoria("HC-001")
                .build();

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Debe permitir lectura a usuario ADMIN")
    void debePermitirLecturaAUsuarioAdmin() {
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(Constants.ROLE_ADMIN_STRING)
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("admin");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        boolean tienePermiso = historiaClinicaProxy.tienePermisoLectura(historiaClinica);

        assertTrue(tienePermiso);
        verify(auditLogger, times(1)).logAccess(
                Constants.ENTIDAD_HISTORIA_CLINICA_SIN_ESPACIO,
                1L,
                "admin"
        );
    }

    @Test
    @DisplayName("Debe permitir lectura a usuario VETERINARIO")
    void debePermitirLecturaAUsuarioVeterinario() {
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(Constants.ROLE_VETERINARIO_STRING)
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("veterinario");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        boolean tienePermiso = historiaClinicaProxy.tienePermisoLectura(historiaClinica);

        assertTrue(tienePermiso);
        verify(auditLogger, times(1)).logAccess(anyString(), eq(1L), eq("veterinario"));
    }

    @Test
    @DisplayName("Debe denegar lectura a usuario sin permisos")
    void debeDenegarLecturaAUsuarioSinPermisos() {
        Collection<GrantedAuthority> authorities = Collections.emptyList();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("usuario");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        boolean tienePermiso = historiaClinicaProxy.tienePermisoLectura(historiaClinica);

        assertFalse(tienePermiso);
        verify(auditLogger, times(1)).logUnauthorizedAccess(
                anyString(), eq(1L), eq("usuario"), anyString()
        );
    }

    @Test
    @DisplayName("Debe denegar acceso si no está autenticado")
    void debeDenegarAccesoSiNoEstaAutenticado() {
        when(securityContext.getAuthentication()).thenReturn(null);

        boolean tienePermiso = historiaClinicaProxy.tienePermisoLectura(historiaClinica);

        assertFalse(tienePermiso);
        verify(auditLogger, never()).logAccess(anyString(), anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe permitir escritura solo a ADMIN y VETERINARIO")
    void debePermitirEscrituraSoloAAdminYVeterinario() {
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(Constants.ROLE_VETERINARIO_STRING)
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("veterinario");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        boolean tienePermiso = historiaClinicaProxy.tienePermisoEscritura(historiaClinica);

        assertTrue(tienePermiso);
    }

    @Test
    @DisplayName("Debe denegar escritura a RECEPCIONISTA")
    void debeDenegarEscrituraARecepcionista() {
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_RECEPCIONISTA")
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("recepcionista");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        boolean tienePermiso = historiaClinicaProxy.tienePermisoEscritura(historiaClinica);

        assertFalse(tienePermiso);
        verify(auditLogger, times(1)).logUnauthorizedAccess(
                anyString(), eq(1L), eq("recepcionista"), anyString()
        );
    }

    @Test
    @DisplayName("Debe registrar acceso a historia clínica")
    void debeRegistrarAccesoAHistoriaClinica() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");

        historiaClinicaProxy.registrarAcceso(historiaClinica, "READ");

        verify(auditLogger, times(1)).log(
                eq("READ"),
                eq(Constants.ENTIDAD_HISTORIA_CLINICA_SIN_ESPACIO),
                eq(1L),
                eq("admin"),
                anyString()
        );
    }

    @Test
    @DisplayName("Debe registrar modificación de historia clínica")
    void debeRegistrarModificacionDeHistoriaClinica() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("veterinario");

        String cambios = "Actualización de alergias";
        historiaClinicaProxy.registrarModificacion(historiaClinica, cambios);

        verify(auditLogger, times(1)).logUpdate(
                Constants.ENTIDAD_HISTORIA_CLINICA_SIN_ESPACIO,
                1L,
                "veterinario",
                cambios
        );
    }

    @Test
    @DisplayName("Debe permitir acceso a ADMIN sin validar relación")
    void debePermitirAccesoAAdminSinValidarRelacion() {
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(Constants.ROLE_ADMIN_STRING)
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("admin");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        boolean puedeAcceder = historiaClinicaProxy.puedeAccederAHistoria(historiaClinica, null);

        assertTrue(puedeAcceder);
        verify(auditLogger, times(1)).logAccess(anyString(), eq(1L), eq("admin"));
    }
}

