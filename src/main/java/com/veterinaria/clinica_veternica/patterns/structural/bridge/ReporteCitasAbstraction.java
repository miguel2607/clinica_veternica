package com.veterinaria.clinica_veternica.patterns.structural.bridge;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Patrón Bridge: ReporteCitasAbstraction
 *
 * Abstracción concreta para reportes de citas.
 * Define qué datos recopilar, delegando el formato a ReporteImplementor.
 *
 * Justificación:
 * - Separa la lógica de recopilación de datos del formato de salida
 * - Permite generar el mismo reporte en PDF, Excel o JSON
 * - Facilita agregar nuevos formatos sin modificar la lógica
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
public class ReporteCitasAbstraction extends ReporteAbstraction {

    private final CitaRepository citaRepository;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public ReporteCitasAbstraction(ReporteImplementor implementor, CitaRepository citaRepository) {
        super(implementor);
        this.citaRepository = citaRepository;
    }

    public ReporteCitasAbstraction setRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        return this;
    }

    @Override
    protected Map<String, Object> recopilarDatos() {
        log.debug("Recopilando datos de citas desde {} hasta {}", fechaInicio, fechaFin);

        List<Cita> citas;
        if (fechaInicio != null && fechaFin != null) {
            java.time.LocalDateTime inicio = fechaInicio.atStartOfDay();
            java.time.LocalDateTime fin = fechaFin.atTime(23, 59, 59);
            citas = citaRepository.findCitasEnRango(inicio, fin);
        } else {
            citas = citaRepository.findAll();
        }

        Map<String, Object> datos = new HashMap<>();
        datos.put("totalCitas", citas.size());
        datos.put("fechaInicio", fechaInicio != null ? fechaInicio.toString() : "N/A");
        datos.put("fechaFin", fechaFin != null ? fechaFin.toString() : "N/A");
        datos.put("citas", citas.stream()
                .map(cita -> Map.of(
                        "id", cita.getIdCita(),
                        "mascota", cita.getMascota().getNombre(),
                        "veterinario", cita.getVeterinario().getNombreCompleto(),
                        "fecha", cita.getFechaCita().toString(),
                        "hora", cita.getHoraCita().toString(),
                        "estado", cita.getEstado().toString()
                ))
                .toList());

        return datos;
    }

    @Override
    protected String obtenerTitulo() {
        return "Reporte de Citas" + 
               (fechaInicio != null && fechaFin != null 
                   ? " (" + fechaInicio + " - " + fechaFin + ")" 
                   : "");
    }
}

