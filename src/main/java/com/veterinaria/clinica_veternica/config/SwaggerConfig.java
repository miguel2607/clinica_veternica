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
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración consolidada de OpenAPI/Swagger para la documentación de la API REST.
 *
 * Esta clase configura la documentación interactiva de la API utilizando OpenAPI 3.0,
 * incluyendo:
 * - Información del proyecto, seguridad JWT y servidores disponibles
 * - Configuración de SpringDoc para escanear controladores
 * - Customizadores para limpiar referencias rotas y esquemas problemáticos
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
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
        final String securitySchemeName = "bearerAuth";

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
                                        .description("""
                                                Autenticación mediante JWT (JSON Web Token).
                                                
                                                **Instrucciones:**
                                                1. Primero, realiza login en el endpoint `/api/auth/login`
                                                2. Copia el token JWT del campo `token` en la respuesta
                                                3. Haz clic en el botón "Authorize" (🔒) arriba a la derecha
                                                4. Pega el token en el campo (sin la palabra "Bearer")
                                                5. Haz clic en "Authorize" y luego en "Close"
                                                
                                                Ahora podrás probar todos los endpoints protegidos.
                                                """)
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
                        Singleton, Factory Method, Abstract Factory, Builder,
                        Adapter, Bridge, Decorator, Facade, Proxy,
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

    /**
     * Configura el grupo de API para SpringDoc.
     * Escanea solo los controladores de la aplicación.
     *
     * @return GroupedOpenApi configurado
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("clinica-veterinaria")
                .pathsToMatch("/api/**")
                .packagesToScan("com.veterinaria.clinica_veternica.controller")
                .build();
    }

    /**
     * Customizador que limpia referencias rotas y esquemas problemáticos.
     * Por ahora, SpringDoc maneja esto automáticamente, pero se puede extender
     * para limpiar esquemas específicos si es necesario.
     *
     * @return OpenApiCustomizer configurado
     */
    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            // Limpiar referencias rotas si existen
            if (openApi.getComponents() != null && openApi.getComponents().getSchemas() != null) {
                // Remover esquemas problemáticos si es necesario
                // Por ahora, dejamos que SpringDoc maneje esto automáticamente
            }
        };
    }

}
