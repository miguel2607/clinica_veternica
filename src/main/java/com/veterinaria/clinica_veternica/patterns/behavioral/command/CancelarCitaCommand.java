package com.veterinaria.clinica_veternica.patterns.behavioral.command;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Patrón Command: CancelarCitaCommand
 *
 * Comando para cancelar una cita existente.
 * Encapsula la operación de cancelación y permite deshacerla.
 *
 * Justificación:
 * - Encapsula la cancelación de citas como objeto
 * - Permite deshacer la cancelación si es necesario
 * - Facilita auditoría y logging
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CancelarCitaCommand implements Command {

    private final CitaRepository citaRepository;
    private Long citaId;
    private Cita citaOriginal;
    private EstadoCita estadoAnterior;
    private String motivoCancelacion;
    private String usuarioCancelacion;

    public CancelarCitaCommand setCitaId(Long citaId) {
        this.citaId = citaId;
        return this;
    }

    public CancelarCitaCommand setMotivo(String motivo, String usuario) {
        this.motivoCancelacion = motivo;
        this.usuarioCancelacion = usuario;
        return this;
    }

    @Override
    public void ejecutar() throws ValidationException, BusinessException, IllegalStateException {
        if (citaId == null) {
            throw new IllegalStateException("El ID de la cita no puede ser nulo");
        }

        citaOriginal = citaRepository.findById(citaId)
                .orElseThrow(() -> new ValidationException("Cita no encontrada: " + citaId, "citaId", "Cita no existe"));

        log.info("Ejecutando comando: Cancelar cita ID: {}", citaId);
        estadoAnterior = citaOriginal.getEstado();
        citaOriginal.cancelar(motivoCancelacion != null ? motivoCancelacion : "Sin motivo",
                usuarioCancelacion != null ? usuarioCancelacion : "Sistema");
        citaRepository.save(citaOriginal);
        log.info("Cita cancelada exitosamente");
    }

    @Override
    public void deshacer() throws ValidationException, BusinessException, IllegalStateException {
        if (citaOriginal == null) {
            throw new IllegalStateException("No hay cita cancelada para deshacer");
        }

        log.info("Deshaciendo comando: Restaurando cita ID: {} al estado {}", citaOriginal.getIdCita(), estadoAnterior);
        citaOriginal.setEstado(estadoAnterior);
        citaOriginal.setFechaCancelacion(null);
        citaOriginal.setMotivoCancelacion(null);
        citaOriginal.setCanceladaPor(null);
        citaRepository.save(citaOriginal);
        log.info("Cancelación de cita deshecha exitosamente");
    }

    @Override
    public String getDescripcion() {
        return "Cancelar cita ID: " + (citaId != null ? citaId : "N/A");
    }
}

