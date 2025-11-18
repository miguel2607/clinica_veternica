package com.veterinaria.clinica_veternica.patterns.behavioral.command;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Patrón Command: CrearCitaCommand
 *
 * Comando para crear una nueva cita.
 * Encapsula la operación de creación y permite deshacerla.
 *
 * Justificación:
 * - Encapsula la creación de citas como objeto
 * - Permite deshacer la creación si es necesario
 * - Facilita auditoría y logging
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CrearCitaCommand implements Command {

    private final CitaRepository citaRepository;
    private Cita cita;
    private Cita citaCreada;

    public CrearCitaCommand setCita(Cita cita) {
        this.cita = cita;
        return this;
    }

    @Override
    public void ejecutar() throws ValidationException, BusinessException, IllegalStateException {
        if (cita == null) {
            throw new IllegalStateException("La cita no puede ser nula");
        }

        log.info("Ejecutando comando: Crear cita para mascota {}", cita.getMascota().getNombre());
        citaCreada = citaRepository.save(cita);
        log.info("Cita creada exitosamente con ID: {}", citaCreada.getIdCita());
    }

    @Override
    public void deshacer() throws ValidationException, BusinessException, IllegalStateException {
        if (citaCreada == null) {
            throw new IllegalStateException("No hay cita creada para deshacer");
        }

        log.info("Deshaciendo comando: Eliminando cita ID: {}", citaCreada.getIdCita());
        citaRepository.delete(citaCreada);
        citaCreada = null;
        log.info("Cita eliminada exitosamente");
    }

    @Override
    public String getDescripcion() {
        return "Crear cita para " + (cita != null ? cita.getMascota().getNombre() : "N/A");
    }
}

