package com.veterinaria.clinica_veternica.patterns.creational.abstractfactory;

import com.veterinaria.clinica_veternica.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmailNotificacionFactoryTest {

    @Mock
    private EmailService emailService;

    private EmailNotificacionFactory factory;

    @BeforeEach
    void setUp() {
        factory = new EmailNotificacionFactory(emailService);
    }

    @Test
    @DisplayName("Abstract Factory - Debe crear enviador de email")
    void debeCrearEnviadorEmail() {
        EnviadorNotificacion enviador = factory.crearEnviador();

        assertNotNull(enviador);
    }

    @Test
    @DisplayName("Abstract Factory - Debe crear mensaje de email")
    void debeCrearMensajeEmail() {
        String titulo = "Test";
        String contenido = "Contenido de prueba";
        String destinatario = "test@example.com";

        MensajeNotificacion mensaje = factory.crearMensaje(destinatario, titulo, contenido);

        assertNotNull(mensaje);
        assertEquals(titulo, mensaje.getAsunto());
        assertEquals(contenido, mensaje.getContenido());
        assertEquals(destinatario, mensaje.getDestinatario());
    }

    @Test
    @DisplayName("Abstract Factory - Debe crear validador de email")
    void debeCrearValidadorEmail() {
        ValidadorDestinatario validador = factory.crearValidador();

        assertNotNull(validador);
    }
}

