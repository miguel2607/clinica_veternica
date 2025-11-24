package com.veterinaria.clinica_veternica.config;

import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.domain.usuario.RolUsuario; // <-- AJUSTA si tu enum está en otro paquete
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initAdmin(UsuarioRepository usuarioRepository, 
                                org.springframework.core.env.Environment environment) {
        return args -> {
            try {
                log.info("🔄 Iniciando creación/verificación de usuario administrador...");
                
                // Obtener valores de properties o usar valores por defecto solo para desarrollo
                final String USERNAME = environment.getProperty("app.admin.username", "admin");
                final String EMAIL = environment.getProperty("app.admin.email", "admin@veterinaria.com");

                /*
                 * IMPORTANTE: En producción, la contraseña DEBE venir de variables de entorno.
                 * El valor por defecto "Admin123!" es SOLO para entornos de desarrollo local.
                 * Configura la variable ADMIN_PASSWORD en producción para sobrescribir este valor.
                 */
                final String RAW_PASS = environment.getProperty("app.admin.password",
                    environment.getProperty("ADMIN_PASSWORD", "Admin123!"));

                // Verificar que el repositorio esté disponible
                Usuario u = usuarioRepository.findByUsername(USERNAME).orElse(null);

                if (u == null) {
                    log.info("📝 Usuario admin no existe, creando nuevo usuario...");
                    u = new Usuario();
                    u.setUsername(USERNAME);
                    u.setEmail(EMAIL);
                    u.setPassword(passwordEncoder.encode(RAW_PASS));
                    u.setRol(RolUsuario.ADMIN);
                    u.setEstado(true);
                    u.setBloqueado(false);
                    u.setIntentosFallidos(0);
                    usuarioRepository.save(u);
                    log.info("✅ Usuario admin creado exitosamente: {} (email: {})", USERNAME, EMAIL);
                } else {
                    log.info("👤 Usuario admin ya existe, verificando estado...");
                    // Solo actualizar contraseña si se proporciona una nueva
                    String newPassword = environment.getProperty("app.admin.password", 
                        environment.getProperty("ADMIN_PASSWORD"));
                    if (newPassword != null && !newPassword.isEmpty() && !newPassword.equals("Admin123!")) {
                        u.setPassword(passwordEncoder.encode(newPassword));
                        log.info("🔑 Contraseña del admin actualizada");
                    }
                    u.setBloqueado(false);
                    u.setIntentosFallidos(0);
                    u.setEstado(true);
                    u.setRol(RolUsuario.ADMIN);
                    usuarioRepository.save(u);
                    log.info("✅ Usuario admin verificado y actualizado: {} (email: {})", USERNAME, EMAIL);
                }
            } catch (Exception e) {
                log.error("❌ ERROR al inicializar usuario administrador: {}", e.getMessage(), e);
                log.error("⚠️  Verifica que:");
                log.error("   1. MySQL esté corriendo");
                log.error("   2. La base de datos 'clinica_veterinaria_dev' exista");
                log.error("   3. Las tablas se hayan creado correctamente");
                log.error("   4. Las credenciales de conexión sean correctas");
                // No lanzamos la excepción para que la aplicación pueda continuar
            }
        };
    }
}
