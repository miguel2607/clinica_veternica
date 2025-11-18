package com.veterinaria.clinica_veternica.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

    // Opcional: proveedor de auditor (quién creó/modificó). Aquí fijo "system".
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("system");
    }
}
