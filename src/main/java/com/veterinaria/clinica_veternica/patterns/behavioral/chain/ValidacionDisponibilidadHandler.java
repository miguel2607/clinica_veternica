package com.veterinaria.clinica_veternica.patterns.behavioral.chain;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.Horario;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.repository.CitaRepository;
import com.veterinaria.clinica_veternica.repository.HorarioRepository;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Patrón Chain of Responsibility: ValidacionDisponibilidadHandler
 *
 * Valida que el veterinario esté disponible en la fecha y hora de la cita.
 *
 * Justificación:
 * - Valida disponibilidad después de validar datos básicos
 * - Evita conflictos de horarios
 * - Valida que la hora esté dentro del horario del veterinario
 * - Valida que no haya conflictos con otras citas
 * - Valida el límite de citas simultáneas
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ValidacionDisponibilidadHandler extends ValidacionHandler {

    private final HorarioRepository horarioRepository;
    private final CitaRepository citaRepository;

    @Override
    protected boolean validarEspecifico(Cita cita) throws ValidationException {
        log.debug("Validando disponibilidad del veterinario");

        // Validar que la fecha no sea en el pasado (excepto para emergencias)
        LocalDateTime fechaHoraCita = LocalDateTime.of(cita.getFechaCita(), cita.getHoraCita());
        if (!Constants.isTrue(cita.getEsEmergencia()) && fechaHoraCita.isBefore(LocalDateTime.now())) {
            throw new ValidationException("No se pueden crear citas en el pasado", "fechaCita", "La fecha debe ser futura");
        }

        // Validar que la hora esté dentro del horario del veterinario
        validarHorarioVeterinario(cita);

        // Validar que no haya conflictos con otras citas
        validarConflictosCitas(cita);

        log.debug("Validación de disponibilidad: OK");
        return true;
    }

    /**
     * Valida que la hora de la cita esté dentro del horario laboral del veterinario.
     */
    private void validarHorarioVeterinario(Cita cita) throws ValidationException {
        DayOfWeek diaSemana = cita.getFechaCita().getDayOfWeek();
        List<Horario> horariosDelDia = horarioRepository.findByVeterinario(cita.getVeterinario())
                .stream()
                .filter(h -> h.getDiaSemana().equals(diaSemana) && Boolean.TRUE.equals(h.getActivo()))
                .collect(Collectors.toList());

        if (horariosDelDia.isEmpty()) {
            String nombreDia = diaSemana.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
            throw new ValidationException(
                    "El veterinario no tiene horario disponible para " + nombreDia,
                    "horaCita",
                    "El veterinario no trabaja en ese día"
            );
        }

        // Verificar que la hora esté dentro de algún horario del veterinario
        LocalTime horaCita = cita.getHoraCita();
        Integer duracionMinutos = cita.getDuracionEstimadaMinutos() != null
                ? cita.getDuracionEstimadaMinutos()
                : 30; // Duración por defecto

        boolean dentroDeHorario = false;
        Horario horarioValido = null;

        for (Horario horario : horariosDelDia) {
            LocalTime horaFinCita = horaCita.plusMinutes(duracionMinutos);

            // Verificar que la cita completa (inicio + duración) esté dentro del horario
            if (!horaCita.isBefore(horario.getHoraInicio()) &&
                !horaFinCita.isAfter(horario.getHoraFin())) {
                dentroDeHorario = true;
                horarioValido = horario;
                break;
            }
        }

        if (!dentroDeHorario) {
            String horariosDisponibles = horariosDelDia.stream()
                    .map(h -> h.getHoraInicio() + " - " + h.getHoraFin())
                    .collect(Collectors.joining(", "));

            throw new ValidationException(
                    "La hora " + horaCita + " no está dentro del horario del veterinario. Horarios disponibles: " + horariosDisponibles,
                    "horaCita",
                    "La hora debe estar dentro del horario del veterinario"
            );
        }

        // Validar que la cita esté alineada con los intervalos del horario
        if (horarioValido != null) {
            Integer duracionCitaHorario = horarioValido.getDuracionCitaMinutos();
            if (duracionCitaHorario != null && duracionCitaHorario > 0) {
                validarAlineacionConIntervalo(cita, horarioValido);
            }
        }
    }

    /**
     * Valida que la hora de la cita esté alineada con los intervalos configurados en el horario.
     */
    private void validarAlineacionConIntervalo(Cita cita, Horario horario) throws ValidationException {
        LocalTime horaInicio = horario.getHoraInicio();
        LocalTime horaCita = cita.getHoraCita();
        int duracionMinutos = horario.getDuracionCitaMinutos();

        // Calcular los minutos desde el inicio del horario
        int minutosDesdeInicio = (horaCita.getHour() * 60 + horaCita.getMinute()) -
                                  (horaInicio.getHour() * 60 + horaInicio.getMinute());

        // Verificar que esté alineada con el intervalo
        if (minutosDesdeInicio % duracionMinutos != 0) {
            throw new ValidationException(
                    "La hora de la cita debe estar alineada con intervalos de " + duracionMinutos + " minutos. " +
                    "Hora de inicio del horario: " + horaInicio,
                    "horaCita",
                    "La hora debe coincidir con los intervalos configurados"
            );
        }
    }

    /**
     * Valida que no haya conflictos con otras citas del veterinario.
     */
    private void validarConflictosCitas(Cita cita) throws ValidationException {
        LocalDate fechaCita = cita.getFechaCita();
        LocalTime horaCita = cita.getHoraCita();
        Integer duracionMinutos = cita.getDuracionEstimadaMinutos() != null
                ? cita.getDuracionEstimadaMinutos()
                : 30;

        LocalTime horaFinCita = horaCita.plusMinutes(duracionMinutos);

        // Buscar citas que se solapen con el rango de tiempo
        List<Cita> citasSolapadas = citaRepository.findCitasSolapadas(
                cita.getVeterinario(),
                fechaCita,
                horaCita,
                horaFinCita,
                cita.getIdCita() // null para nuevas citas, ID para actualizaciones
        );

        if (!citasSolapadas.isEmpty()) {
            // Obtener el horario para verificar maxCitasSimultaneas
            DayOfWeek diaSemana = fechaCita.getDayOfWeek();
            List<Horario> horariosDelDia = horarioRepository.findByVeterinario(cita.getVeterinario())
                    .stream()
                    .filter(h -> h.getDiaSemana().equals(diaSemana) && Boolean.TRUE.equals(h.getActivo()))
                    .filter(h -> !horaCita.isBefore(h.getHoraInicio()) && horaCita.isBefore(h.getHoraFin()))
                    .collect(Collectors.toList());

            int maxCitasSimultaneas = horariosDelDia.isEmpty() ? 1 :
                    horariosDelDia.get(0).getMaxCitasSimultaneas() != null ?
                    horariosDelDia.get(0).getMaxCitasSimultaneas() : 1;

            // Si ya hay el máximo de citas simultáneas, rechazar
            if (citasSolapadas.size() >= maxCitasSimultaneas) {
                String citasInfo = citasSolapadas.stream()
                        .map(c -> c.getHoraCita() + " (" + c.getMascota().getNombre() + ")")
                        .collect(Collectors.joining(", "));

                throw new ValidationException(
                        "El veterinario ya tiene " + citasSolapadas.size() + " cita(s) en ese horario. " +
                        "Citas existentes: " + citasInfo + ". Máximo permitido: " + maxCitasSimultaneas,
                        "horaCita",
                        "Ya existe(n) cita(s) en ese horario"
                );
            }
        }
    }
}

