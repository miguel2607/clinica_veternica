package com.veterinaria.clinica_veternica.service.interfaces;

/**
 * Interfaz compuesta del servicio para gestión de Citas.
 * Extiende las interfaces segregadas para mantener compatibilidad.
 * Utiliza múltiples patrones: Mediator, Chain of Responsibility, Command, State, Template Method.
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-06
 */
public interface ICitaService extends ICitaCreationService, ICitaQueryService, ICitaStateService {
}

