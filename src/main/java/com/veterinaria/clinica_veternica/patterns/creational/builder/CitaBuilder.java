package com.veterinaria.clinica_veternica.patterns.creational.builder;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Patrón Builder: CitaBuilder
 *
 * Construye objetos Cita complejos paso a paso con validaciones
 * y configuraciones específicas según el tipo de cita.
 *
 * Justificación:
 * - Las citas tienen múltiples atributos opcionales
 * - Requieren validaciones complejas (disponibilidad, horarios, etc.)
 * - Diferentes configuraciones según tipo (normal, emergencia, domicilio)
 * - Permite construir citas con diferentes configuraciones de manera clara
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
public class CitaBuilder {

    private Mascota mascota;
    private Veterinario veterinario;
    private Servicio servicio;
    private LocalDate fechaCita;
    private LocalTime horaCita;
    private Integer duracionEstimadaMinutos;
    private String motivoConsulta;
    private String observaciones;
    private Boolean esEmergencia = false;
    private Boolean esDomicilio = false;
    private String direccionDomicilio;
    private BigDecimal precioFinal;

    public CitaBuilder conMascota(Mascota mascota) {
        this.mascota = mascota;
        return this;
    }

    public CitaBuilder conVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
        return this;
    }

    public CitaBuilder conServicio(Servicio servicio) {
        this.servicio = servicio;
        return this;
    }

    public CitaBuilder conFecha(LocalDate fechaCita) {
        this.fechaCita = fechaCita;
        return this;
    }

    public CitaBuilder conHora(LocalTime horaCita) {
        this.horaCita = horaCita;
        return this;
    }

    public CitaBuilder conDuracion(Integer duracionEstimadaMinutos) {
        this.duracionEstimadaMinutos = duracionEstimadaMinutos;
        return this;
    }

    public CitaBuilder conMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
        return this;
    }

    public CitaBuilder conObservaciones(String observaciones) {
        this.observaciones = observaciones;
        return this;
    }

    public CitaBuilder comoEmergencia() {
        this.esEmergencia = true;
        return this;
    }

    public CitaBuilder comoDomicilio(String direccionDomicilio) {
        this.esDomicilio = true;
        this.direccionDomicilio = direccionDomicilio;
        return this;
    }

    public CitaBuilder conPrecioFinal(BigDecimal precioFinal) {
        this.precioFinal = precioFinal;
        return this;
    }

    public Cita build() {
        // Validaciones obligatorias
        if (mascota == null) {
            throw new IllegalStateException("La mascota es obligatoria");
        }
        if (veterinario == null) {
            throw new IllegalStateException("El veterinario es obligatorio");
        }
        if (servicio == null) {
            throw new IllegalStateException("El servicio es obligatorio");
        }
        if (fechaCita == null) {
            throw new IllegalStateException("La fecha de la cita es obligatoria");
        }
        if (horaCita == null) {
            throw new IllegalStateException("La hora de la cita es obligatoria");
        }
        if (motivoConsulta == null || motivoConsulta.isBlank()) {
            throw new IllegalStateException("El motivo de consulta es obligatorio");
        }

        // Usar duración del servicio si no se especifica
        if (duracionEstimadaMinutos == null) {
            duracionEstimadaMinutos = servicio.getDuracionEstimadaMinutos();
        }

        // Calcular precio final si no se especifica
        precioFinal = calcularPrecioFinal();

        // Validar que el servicio esté disponible para el tipo de cita
        validarDisponibilidadServicio();

        return Cita.builder()
                .mascota(mascota)
                .veterinario(veterinario)
                .servicio(servicio)
                .fechaCita(fechaCita)
                .horaCita(horaCita)
                .duracionEstimadaMinutos(duracionEstimadaMinutos)
                .estado(EstadoCita.PROGRAMADA)
                .motivoConsulta(motivoConsulta)
                .observaciones(observaciones)
                .esEmergencia(esEmergencia)
                .esDomicilio(esDomicilio)
                .direccionDomicilio(direccionDomicilio)
                .precioFinal(precioFinal)
                .build();
    }

    /**
     * Calcula el precio final de la cita según el tipo.
     */
    private BigDecimal calcularPrecioFinal() {
        if (precioFinal != null) {
            return precioFinal;
        }

        if (com.veterinaria.clinica_veternica.util.Constants.isTrue(esEmergencia)) {
            return servicio.getPrecioEmergencia();
        }

        if (com.veterinaria.clinica_veternica.util.Constants.isTrue(esDomicilio) && com.veterinaria.clinica_veternica.util.Constants.isTrue(servicio.getDisponibleDomicilio())) {
            return servicio.getPrecioDomicilio();
        }

        return servicio.getPrecio();
    }

    /**
     * Valida que el servicio esté disponible para el tipo de cita solicitado.
     */
    private void validarDisponibilidadServicio() {
        if (com.veterinaria.clinica_veternica.util.Constants.isTrue(esEmergencia) && !com.veterinaria.clinica_veternica.util.Constants.isTrue(servicio.getDisponibleEmergencias())) {
            throw new IllegalStateException("El servicio no está disponible para emergencias");
        }
        if (com.veterinaria.clinica_veternica.util.Constants.isTrue(esDomicilio) && !com.veterinaria.clinica_veternica.util.Constants.isTrue(servicio.getDisponibleDomicilio())) {
            throw new IllegalStateException("El servicio no está disponible a domicilio");
        }
    }
}

