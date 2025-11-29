package com.veterinaria.clinica_veternica.patterns.behavioral.mediator;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import com.veterinaria.clinica_veternica.patterns.behavioral.observer.CitaSubject;
import com.veterinaria.clinica_veternica.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Patrón Mediator: CitaMediatorImpl
 *
 * Implementación concreta del mediador de citas.
 * Coordina la comunicación entre Veterinario, Mascota, Horario, Servicio y Notificaciones.
 *
 * Justificación:
 * - Centraliza toda la lógica de coordinación de citas
 * - Reduce el acoplamiento entre componentes
 * - Facilita el mantenimiento y extensión
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CitaMediatorImpl implements CitaMediator {

    private static final String MSG_CITA_NO_ENCONTRADA = "Cita no encontrada: ";

    private final CitaRepository citaRepository;
    private final CitaSubject citaSubject;

    @Override
    public Cita crearCita(Cita cita) {
        log.info("Mediador: Coordinando creación de cita");

        // 1. Validar disponibilidad del veterinario
        validarDisponibilidadVeterinario(cita);

        // 2. Validar disponibilidad del horario
        validarDisponibilidadHorario();

        // 3. Validar que el servicio esté disponible
        validarServicioDisponible(cita);

        // 4. Guardar la cita
        Cita citaGuardada = citaRepository.save(cita);
        log.info("Mediador: Cita creada con ID: {}", citaGuardada.getIdCita());

        // 5. Notificar a observadores
        citaSubject.notifyCitaCreated(citaGuardada);

        return citaGuardada;
    }

    @Override
    public void confirmarCita(Long citaId) {
        log.info("Mediador: Coordinando confirmación de cita ID: {}", citaId);

        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new IllegalArgumentException(MSG_CITA_NO_ENCONTRADA + citaId));

        // 1. Cambiar estado
        cita.confirmar();

        // 2. Guardar cambios
        citaRepository.save(cita);

        // 3. Notificar a observadores
        citaSubject.notifyStateChanged(cita, "PROGRAMADA", "CONFIRMADA");

        log.info("Mediador: Cita confirmada exitosamente");
    }

    @Override
    public void cancelarCita(Long citaId, String motivo) {
        log.info("Mediador: Coordinando cancelación de cita ID: {}", citaId);

        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new IllegalArgumentException(MSG_CITA_NO_ENCONTRADA + citaId));

        // 1. Cancelar la cita
        cita.cancelar(motivo, "Sistema");

        // 2. Guardar cambios
        citaRepository.save(cita);

        // 3. Liberar recursos (horario, etc.)
        liberarRecursos();

        // 4. Notificar a observadores
        citaSubject.notifyCitaCancelled(cita, motivo);

        log.info("Mediador: Cita cancelada exitosamente");
    }

    @Override
    public void notificarCambio(Long citaId, String evento) {
        log.debug("Mediador: Notificando cambio en cita ID: {}, evento: {}", citaId, evento);

        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new IllegalArgumentException(MSG_CITA_NO_ENCONTRADA + citaId));

        // Notificar según el tipo de evento
        switch (evento) {
            case "CITA_CREADA" -> citaSubject.notifyCitaCreated(cita);
            case "CITA_CANCELADA" -> citaSubject.notifyCitaCancelled(cita, "Sistema");
            default -> citaSubject.notifyStateChanged(cita, "ANTERIOR", evento);
        }
    }

    private void validarDisponibilidadVeterinario(Cita cita) {
        log.debug("Mediador: Validando disponibilidad del veterinario");
        if (cita.getVeterinario() == null || !cita.getVeterinario().getActivo()) {
            throw new IllegalStateException("El veterinario no está disponible");
        }
    }

    private void validarDisponibilidadHorario() {
        log.debug("Mediador: Validando disponibilidad del horario");
        // La validación completa de horarios se realiza en ValidacionDisponibilidadHandler
        // antes de llegar al mediator. Este método se mantiene por compatibilidad
        // y para futuras validaciones adicionales si fueran necesarias.
    }

    private void validarServicioDisponible(Cita cita) {
        log.debug("Mediador: Validando disponibilidad del servicio");
        if (cita.getServicio() == null || !cita.getServicio().getActivo()) {
            throw new IllegalStateException("El servicio no está disponible");
        }
    }

    private void liberarRecursos() {
        log.debug("Mediador: Liberando recursos de la cita cancelada");
        // Aquí se liberarían horarios, insumos reservados, etc.
    }
}

