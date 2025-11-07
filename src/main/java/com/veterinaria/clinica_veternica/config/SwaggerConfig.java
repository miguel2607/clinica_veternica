package com.veterinaria.clinica_veternica.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para la documentación de la API REST.
 *
 * Esta clase configura la documentación interactiva de la API utilizando OpenAPI 3.0,
 * incluyendo información del proyecto, seguridad JWT y servidores disponibles.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Configuración principal de OpenAPI.
     * Define la información del API, esquemas de seguridad y servidores disponibles.
     *
     * @return Objeto OpenAPI configurado
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "Bearer Authentication";

        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor de Desarrollo"),
                        new Server()
                                .url("https://api.clinica-veterinaria.com")
                                .description("Servidor de Producción")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingresar el token JWT generado al hacer login")
                        )
                );
    }

    /**
     * Información general del API.
     *
     * @return Objeto Info con detalles del proyecto
     */
    private Info apiInfo() {
        return new Info()
                .title("API REST - Sistema de Gestión Veterinaria")
                .description("""
                        API REST para la gestión integral de una clínica veterinaria.

                        **Funcionalidades principales:**
                        - Gestión de usuarios y roles (Veterinarios, Administradores, Recepcionistas)
                        - Registro y gestión de mascotas y propietarios
                        - Sistema de citas y agenda
                        - Historias clínicas digitales
                        - Gestión de inventario de insumos
                        - Facturación y pagos
                        - Sistema de notificaciones y recordatorios

                        **Seguridad:**
                        - Autenticación mediante JWT (JSON Web Tokens)
                        - Control de acceso basado en roles
                        - Encriptación de contraseñas con BCrypt

                        **Patrones de Diseño Implementados:**
                        Singleton, Factory Method, Abstract Factory, Builder, Prototype,
                        Adapter, Bridge, Composite, Decorator, Facade, Proxy,
                        Chain of Responsibility, Command, Observer, Strategy,
                        Template Method, Mediator, Memento, State
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("Equipo de Desarrollo - Clínica Veterinaria")
                        .email("dev@clinica-veterinaria.com")
                        .url("https://clinica-veterinaria.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html"));
    }

}
