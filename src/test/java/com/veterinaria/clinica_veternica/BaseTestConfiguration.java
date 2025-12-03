package com.veterinaria.clinica_veternica;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;

/**
 * Configuración base para todos los tests.
 * Deshabilita dotenv y proporciona valores por defecto para las variables de entorno.
 */
@TestPropertySource(properties = {
    "spring.dotenv.enabled=false",
    "JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "JWT_EXPIRATION=86400000"
})
@TestConfiguration
public class BaseTestConfiguration {
    // Esta clase solo sirve para centralizar la configuración de @TestPropertySource
}

