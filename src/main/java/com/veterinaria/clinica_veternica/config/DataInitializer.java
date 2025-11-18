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
            // Obtener valores de properties o usar valores por defecto solo para desarrollo
            final String USERNAME = environment.getProperty("app.admin.username", "admin");
            final String EMAIL = environment.getProperty("app.admin.email", "admin@veterinaria.com");
            // En producción, la contraseña debe venir de variables de entorno
            // NOSONAR: Valor por defecto solo para desarrollo, en producción usar ADMIN_PASSWORD
            final String RAW_PASS = environment.getProperty("app.admin.password", 
                environment.getProperty("ADMIN_PASSWORD", "Admin123!")); // NOSONAR

            Usuario u = usuarioRepository.findByUsername(USERNAME).orElse(null);

            if (u == null) {
                u = new Usuario();
                u.setUsername(USERNAME);
                u.setEmail(EMAIL);
                u.setPassword(passwordEncoder.encode(RAW_PASS));
                u.setRol(RolUsuario.ADMIN);
                u.setEstado(true);
                u.setBloqueado(false);
                u.setIntentosFallidos(0);
                usuarioRepository.save(u);
                log.info("✅ Usuario admin creado: {}", USERNAME);
            } else {
                // Solo actualizar contraseña si se proporciona una nueva
                String newPassword = environment.getProperty("app.admin.password", 
                    environment.getProperty("ADMIN_PASSWORD"));
                if (newPassword != null && !newPassword.isEmpty()) {
                    u.setPassword(passwordEncoder.encode(newPassword));
                }
                u.setBloqueado(false);
                u.setIntentosFallidos(0);
                u.setEstado(true);
                u.setRol(RolUsuario.ADMIN);
                usuarioRepository.save(u);
                log.info("✅ Usuario admin actualizado y desbloqueado: {}", USERNAME);
            }
        };
    }
}
