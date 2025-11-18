package com.veterinaria.clinica_veternica.service.registry;

import com.veterinaria.clinica_veternica.domain.agenda.CategoriaServicio;
import com.veterinaria.clinica_veternica.patterns.creational.factory.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.Map;

/**
 * Registry para factories de servicios.
 * Elimina el antipatrón de lazy initialization usando @PostConstruct.
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-13
 */
@Component
@RequiredArgsConstructor
public class ServicioFactoryRegistry {

    private final ServicioClinicoFactory servicioClinicoFactory;
    private final ServicioQuirurgicoFactory servicioQuirurgicoFactory;
    private final ServicioEsteticoFactory servicioEsteticoFactory;
    private final ServicioEmergenciaFactory servicioEmergenciaFactory;

    private final Map<CategoriaServicio, ServicioFactory> factories = new EnumMap<>(CategoriaServicio.class);

    @PostConstruct
    public void inicializar() {
        factories.put(CategoriaServicio.CLINICO, servicioClinicoFactory);
        factories.put(CategoriaServicio.QUIRURGICO, servicioQuirurgicoFactory);
        factories.put(CategoriaServicio.ESTETICO, servicioEsteticoFactory);
        factories.put(CategoriaServicio.EMERGENCIA, servicioEmergenciaFactory);
    }

    public ServicioFactory obtenerFactory(CategoriaServicio categoria) {
        ServicioFactory factory = factories.get(categoria);
        if (factory == null) {
            throw new IllegalArgumentException("Categoría de servicio no válida: " + categoria);
        }
        return factory;
    }
}

