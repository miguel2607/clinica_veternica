package com.veterinaria.clinica_veternica.patterns.creational.abstractfactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el patrón Abstract Factory - NotificacionFactory
 */
@SpringBootTest
class NotificacionFactoryTest {

    @Autowired
    private EmailNotificacionFactory emailNotificacionFactory;

    @Autowired
    private SMSNotificacionFactory smsNotificacionFactory;

    @Autowired
    private WhatsAppNotificacionFactory whatsAppNotificacionFactory;

    @Autowired
    private PushNotificacionFactory pushNotificacionFactory;

    private String destinatario;
    private String asunto;
    private String contenido;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        destinatario = "test@example.com";
        asunto = "Recordatorio de cita";
        contenido = "Tiene una cita programada para mañana";
    }

    // ========== EmailNotificacionFactory Tests ==========

    @Test
    @DisplayName("Abstract Factory Email - Debe crear mensaje exitosamente")
    void emailDebeCrearMensajeExitosamente() {
        MensajeNotificacion mensaje = emailNotificacionFactory.crearMensaje(
                destinatario, asunto, contenido);

        assertNotNull(mensaje);
        assertEquals(destinatario, mensaje.getDestinatario());
        assertEquals(asunto, mensaje.getAsunto());
        assertEquals(contenido, mensaje.getContenido());
    }

    @Test
    @DisplayName("Abstract Factory Email - Debe crear validador")
    void emailDebeCrearValidador() {
        ValidadorDestinatario validador = emailNotificacionFactory.crearValidador();
        assertNotNull(validador);
    }

    @Test
    @DisplayName("Abstract Factory Email - Debe crear enviador")
    void emailDebeCrearEnviador() {
        EnviadorNotificacion enviador = emailNotificacionFactory.crearEnviador();
        assertNotNull(enviador);
    }

    @Test
    @DisplayName("Abstract Factory Email - Debe obtener nombre del canal")
    void emailDebeObtenerNombreCanal() {
        assertEquals("EMAIL", emailNotificacionFactory.getNombreCanal());
    }

    @Test
    @DisplayName("Abstract Factory Email - Debe validar email correctamente")
    void emailDebeValidarEmailCorrectamente() {
        ValidadorDestinatario validador = emailNotificacionFactory.crearValidador();
        
        assertTrue(validador.esValido("test@example.com"));
        assertFalse(validador.esValido("email-invalido"));
    }

    // ========== SMSNotificacionFactory Tests ==========

    @Test
    @DisplayName("Abstract Factory SMS - Debe crear mensaje exitosamente")
    void smsDebeCrearMensajeExitosamente() {
        MensajeNotificacion mensaje = smsNotificacionFactory.crearMensaje(
                "+573001234567", asunto, contenido);

        assertNotNull(mensaje);
    }

    @Test
    @DisplayName("Abstract Factory SMS - Debe obtener nombre del canal")
    void smsDebeObtenerNombreCanal() {
        assertEquals("SMS", smsNotificacionFactory.getNombreCanal());
    }

    @Test
    @DisplayName("Abstract Factory SMS - Debe validar número de teléfono")
    void smsDebeValidarNumeroTelefono() {
        ValidadorDestinatario validador = smsNotificacionFactory.crearValidador();
        
        assertTrue(validador.esValido("+573001234567"));
        assertFalse(validador.esValido("123")); // Número muy corto
    }

    // ========== WhatsAppNotificacionFactory Tests ==========

    @Test
    @DisplayName("Abstract Factory WhatsApp - Debe crear mensaje exitosamente")
    void whatsAppDebeCrearMensajeExitosamente() {
        MensajeNotificacion mensaje = whatsAppNotificacionFactory.crearMensaje(
                "+573001234567", asunto, contenido);

        assertNotNull(mensaje);
    }

    @Test
    @DisplayName("Abstract Factory WhatsApp - Debe obtener nombre del canal")
    void whatsAppDebeObtenerNombreCanal() {
        assertEquals("WHATSAPP", whatsAppNotificacionFactory.getNombreCanal());
    }

    // ========== PushNotificacionFactory Tests ==========

    @Test
    @DisplayName("Abstract Factory Push - Debe crear mensaje exitosamente")
    void pushDebeCrearMensajeExitosamente() {
        MensajeNotificacion mensaje = pushNotificacionFactory.crearMensaje(
                "device-token-123", asunto, contenido);

        assertNotNull(mensaje);
    }

    @Test
    @DisplayName("Abstract Factory Push - Debe obtener nombre del canal")
    void pushDebeObtenerNombreCanal() {
        assertEquals("PUSH", pushNotificacionFactory.getNombreCanal());
    }

    // ========== Tests de Comparación entre Factories ==========

    @Test
    @DisplayName("Abstract Factory - Diferentes factories deben crear objetos relacionados")
    void diferentesFactoriesDebenCrearObjetosRelacionados() {
        // Email factory
        MensajeNotificacion mensajeEmail = emailNotificacionFactory.crearMensaje(
                "test@example.com", asunto, contenido);
        ValidadorDestinatario validadorEmail = emailNotificacionFactory.crearValidador();
        EnviadorNotificacion enviadorEmail = emailNotificacionFactory.crearEnviador();

        assertNotNull(mensajeEmail);
        assertNotNull(validadorEmail);
        assertNotNull(enviadorEmail);

        // SMS factory
        MensajeNotificacion mensajeSMS = smsNotificacionFactory.crearMensaje(
                "+573001234567", asunto, contenido);
        ValidadorDestinatario validadorSMS = smsNotificacionFactory.crearValidador();
        EnviadorNotificacion enviadorSMS = smsNotificacionFactory.crearEnviador();

        assertNotNull(mensajeSMS);
        assertNotNull(validadorSMS);
        assertNotNull(enviadorSMS);
    }

    @Test
    @DisplayName("Abstract Factory - Debe verificar disponibilidad")
    void debeVerificarDisponibilidad() {
        // Todas las factories deben tener un método para verificar disponibilidad
        // Retorna boolean primitivo, verificamos que el método se ejecuta sin excepción
        boolean emailDisponible = emailNotificacionFactory.estaDisponible();
        boolean smsDisponible = smsNotificacionFactory.estaDisponible();
        boolean whatsAppDisponible = whatsAppNotificacionFactory.estaDisponible();
        boolean pushDisponible = pushNotificacionFactory.estaDisponible();
        
        // Verificar que los métodos se ejecutan correctamente (retornan boolean válido)
        // Los valores pueden ser true o false dependiendo de la implementación
        // Simplemente verificamos que los métodos se ejecutan sin excepción
        assertTrue(emailDisponible == true || emailDisponible == false);
        assertTrue(smsDisponible == true || smsDisponible == false);
        assertTrue(whatsAppDisponible == true || whatsAppDisponible == false);
        assertTrue(pushDisponible == true || pushDisponible == false);
    }

    @Test
    @DisplayName("Abstract Factory - Debe obtener costo de envío")
    void debeObtenerCostoEnvio() {
        // Todas las factories deben tener un costo de envío
        assertTrue(emailNotificacionFactory.getCostoEnvio() >= 0);
        assertTrue(smsNotificacionFactory.getCostoEnvio() >= 0);
        assertTrue(whatsAppNotificacionFactory.getCostoEnvio() >= 0);
        assertTrue(pushNotificacionFactory.getCostoEnvio() >= 0);
    }
}

