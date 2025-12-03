package com.veterinaria.clinica_veternica.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Configuración de prueba para JavaMailSender.
 * Proporciona un mock de JavaMailSender para los tests.
 */
@TestConfiguration
public class TestMailConfig {

    @Bean
    @Primary
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("localhost");
        mailSender.setPort(587);
        mailSender.setUsername("test@test.com");
        mailSender.setPassword("test");
        return mailSender;
    }
}

