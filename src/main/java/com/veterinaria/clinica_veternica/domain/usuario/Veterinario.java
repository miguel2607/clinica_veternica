package com.veterinaria.clinica_veternica.domain.usuario;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.Horario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un Veterinario en la clínica.
 *
 * Veterinario es el profesional médico que atiende a las mascotas.
 * Tiene especialidad, registro profesional y puede atender citas.
 *
 * Responsabilidades:
 * - Atender citas y consultas
 * - Diagnosticar y tratar pacientes
 * - Crear y actualizar historias clínicas
 * - Prescribir tratamientos y medicamentos
 * - Realizar procedimientos veterinarios
 *
 * Relaciones:
 * - Tiene múltiples Horarios de disponibilidad
 * - Atiende múltiples Citas
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "veterinarios",
       indexes = {
           @Index(name = "idx_veterinario_registro", columnList = "registro_profesional"),
           @Index(name = "idx_veterinario_especialidad", columnList = "especialidad")
       })
@DiscriminatorValue("VETERINARIO")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Veterinario extends Personal {

    /**
     * Especialidad del veterinario.
     * Ejemplos: Cirugía, Medicina Interna, Dermatología, etc.
     */
    @NotBlank(message = "La especialidad es obligatoria")
    @Size(max = 100, message = "La especialidad no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String especialidad;

    /**
     * Número de registro profesional.
     * Licencia o matrícula profesional única.
     */
    @NotBlank(message = "El registro profesional es obligatorio")
    @Size(max = 50, message = "El registro profesional no puede exceder 50 caracteres")
    @Column(name = "registro_profesional", nullable = false, unique = true, length = 50)
    private String registroProfesional;

    /**
     * Años de experiencia profesional.
     */
    @Column(name = "anios_experiencia")
    private Integer aniosExperiencia;

    /**
     * Horarios de disponibilidad del veterinario.
     * Un veterinario puede tener múltiples horarios.
     */
    @OneToMany(mappedBy = "veterinario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Horario> horarios = new ArrayList<>();

    /**
     * Citas atendidas o por atender del veterinario.
     */
    @OneToMany(mappedBy = "veterinario", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Cita> citas = new ArrayList<>();

    // ===================================================================
    // MÉTODOS DE NEGOCIO
    // ===================================================================

    /**
     * Obtiene el tipo de personal.
     *
     * @return "Veterinario"
     */
    @Override
    public String getTipoPersonal() {
        return "Veterinario";
    }

    /**
     * Agrega un horario de disponibilidad.
     *
     * @param horario Horario a agregar
     */
    public void agregarHorario(Horario horario) {
        horarios.add(horario);
        horario.setVeterinario(this);
    }

    /**
     * Elimina un horario de disponibilidad.
     *
     * @param horario Horario a eliminar
     */
    public void eliminarHorario(Horario horario) {
        horarios.remove(horario);
        horario.setVeterinario(null);
    }

    /**
     * Agrega una cita al veterinario.
     *
     * @param cita Cita a agregar
     */
    public void agregarCita(Cita cita) {
        citas.add(cita);
        cita.setVeterinario(this);
    }

    /**
     * Verifica si el veterinario puede atender cirugías.
     *
     * @return true si la especialidad incluye cirugía
     */
    public boolean puedeAtenderCirugias() {
        return especialidad != null &&
               (especialidad.toLowerCase().contains("cirugía") ||
                especialidad.toLowerCase().contains("cirugia"));
    }

    /**
     * Obtiene el número total de citas del veterinario.
     *
     * @return Cantidad de citas
     */
    public int getTotalCitas() {
        return citas != null ? citas.size() : 0;
    }

    /**
     * Verifica si el veterinario tiene horarios disponibles.
     *
     * @return true si tiene al menos un horario
     */
    public boolean tieneHorarios() {
        return horarios != null && !horarios.isEmpty();
    }
}
