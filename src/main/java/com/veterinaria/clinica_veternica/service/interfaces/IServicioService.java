package com.veterinaria.clinica_veternica.service.interfaces;

/**
 * Interfaz compuesta del servicio para gestión de Servicios.
 * Extiende las interfaces segregadas para mantener compatibilidad.
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-06
 */
public interface IServicioService extends IServicioCreationService, IServicioQueryService, IServicioManagementService {
}

