package com.veterinaria.clinica_veternica.domain.paciente;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una Mascota (paciente veterinario).
 *
 * Almacena información completa sobre el animal, incluyendo datos físicos,
 * médicos, propietario, historial de citas y atenciones.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "mascotas",
       indexes = {
           @Index(name = "idx_mascota_nombre", columnList = "nombre"),
           @Index(name = "idx_mascota_propietario", columnList = "id_propietario"),
           @Index(name = "idx_mascota_especie", columnList = "id_especie"),
           @Index(name = "idx_mascota_raza", columnList = "id_raza"),
           @Index(name = "idx_mascota_activo", columnList = "activo")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"propietario", "especie", "raza", "citas", "historiaClinica"})
public class Mascota {

    /**
     * Identificador único de la mascota.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMascota;

    /**
     * Nombre de la mascota.
     */
    @NotBlank(message = "El nombre de la mascota es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    /**
     * Sexo de la mascota (Macho, Hembra).
     */
    @NotBlank(message = "El sexo es obligatorio")
    @Pattern(regexp = "^(Macho|Hembra)$", message = "El sexo debe ser 'Macho' o 'Hembra'")
    @Column(nullable = false, length = 10)
    private String sexo;

    /**
     * Fecha de nacimiento de la mascota.
     */
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @Column
    private LocalDate fechaNacimiento;

    /**
     * Color o colores del pelaje/plumaje/escamas.
     */
    @Size(max = 100, message = "El color no puede exceder 100 caracteres")
    @Column(length = 100)
    private String color;

    /**
     * Peso actual en kilogramos.
     */
    @Positive(message = "El peso debe ser un valor positivo")
    @Column
    private Double peso;

    /**
     * Número de microchip (identificación única internacional).
     */
    @Size(max = 20, message = "El número de microchip no puede exceder 20 caracteres")
    @Column(length = 20, unique = true)
    private String numeroMicrochip;

    /**
     * Indica si la mascota está esterilizada/castrada.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean esterilizado = false;

    /**
     * Observaciones generales sobre el comportamiento o características especiales.
     * Incluye alergias, enfermedades crónicas, información de fallecimiento, etc.
     */
    @Size(max = 2000, message = "Las observaciones no pueden exceder 2000 caracteres")
    @Column(length = 2000)
    private String observaciones;

    /**
     * URL de la foto de la mascota.
     */
    @Size(max = 255, message = "La URL de la foto no puede exceder 255 caracteres")
    @Column(length = 255) // Explicit length for clarity
    private String fotoUrl;

    /**
     * Indica si la mascota está activa en el sistema.
     * Mascotas inactivas son aquellas fallecidas o transferidas a otro propietario.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    /**
     * Propietario de la mascota.
     * Relación Many-to-One con Propietario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_propietario", nullable = false)
    @NotNull(message = "El propietario es obligatorio")
    private Propietario propietario;

    /**
     * Especie de la mascota.
     * Relación Many-to-One con Especie.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_especie", nullable = false)
    @NotNull(message = "La especie es obligatoria")
    private Especie especie;

    /**
     * Raza de la mascota.
     * Relación Many-to-One con Raza.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_raza")
    private Raza raza;

    /**
     * Citas asociadas a la mascota.
     * Relación One-to-Many con Cita.
     */
    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Cita> citas = new ArrayList<>();

    /**
     * Historia clínica de la mascota.
     * Relación One-to-One con HistoriaClinica.
     */
    @OneToOne(mappedBy = "mascota", cascade = CascadeType.ALL, orphanRemoval = true)
    private HistoriaClinica historiaClinica;

    /**
     * Fecha y hora de creación del registro.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de última modificación.
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime fechaModificacion;

    // ===================================================================
    // MÉTODOS DE NEGOCIO
    // ===================================================================

    /**
     * Calcula la edad de la mascota en años, meses y días.
     *
     * @return Periodo con la edad, o null si no hay fecha de nacimiento
     */
    public Period getEdad() {
        if (fechaNacimiento == null) {
            return null;
        }
        LocalDate fechaReferencia = LocalDate.now();
        return Period.between(fechaNacimiento, fechaReferencia);
    }

    /**
     * Obtiene la edad en años completos.
     *
     * @return Edad en años
     */
    public Integer getEdadEnAnios() {
        Period edad = getEdad();
        return edad != null ? edad.getYears() : null;
    }

    /**
     * Obtiene la edad en formato legible (ej: "3 años, 5 meses").
     *
     * @return Edad en formato texto
     */
    public String getEdadFormateada() {
        Period edad = getEdad();
        if (edad == null) {
            return "Edad desconocida";
        }

        StringBuilder sb = new StringBuilder();
        appendAnios(sb, edad);
        appendMeses(sb, edad);
        appendDias(sb, edad);

        return !sb.isEmpty() ? sb.toString() : "Recién nacido";
    }

    private void appendAnios(StringBuilder sb, Period edad) {
        if (edad.getYears() > 0) {
            sb.append(edad.getYears()).append(" año").append(edad.getYears() > 1 ? "s" : "");
        }
    }

    private void appendMeses(StringBuilder sb, Period edad) {
        if (edad.getMonths() > 0) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(edad.getMonths()).append(" mes").append(edad.getMonths() > 1 ? "es" : "");
        }
    }

    private void appendDias(StringBuilder sb, Period edad) {
        if (edad.getYears() == 0 && edad.getDays() > 0) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(edad.getDays()).append(" día").append(edad.getDays() > 1 ? "s" : "");
        }
    }

    /**
     * Verifica si la mascota es cachorro/cría (menos de 1 año).
     *
     * @return true si es cachorro/cría
     */
    public boolean esCachorro() {
        Integer edad = getEdadEnAnios();
        return edad != null && edad < 1;
    }

    /**
     * Verifica si la mascota es adulta (entre 1 y 7 años).
     *
     * @return true si es adulta
     */
    public boolean esAdulta() {
        Integer edad = getEdadEnAnios();
        return edad != null && edad >= 1 && edad <= 7;
    }

    /**
     * Verifica si la mascota es geriátrica (más de 7 años).
     *
     * @return true si es geriátrica
     */
    public boolean esGeriatrica() {
        Integer edad = getEdadEnAnios();
        return edad != null && edad > 7;
    }

    /**
     * Agrega una cita a la mascota.
     *
     * @param cita Cita a agregar
     */
    public void agregarCita(Cita cita) {
        citas.add(cita);
        cita.setMascota(this);
    }

    /**
     * Elimina una cita de la mascota.
     *
     * @param cita Cita a eliminar
     */
    public void eliminarCita(Cita cita) {
        citas.remove(cita);
        cita.setMascota(null);
    }

    /**
     * Obtiene el número de citas de la mascota.
     *
     * @return Cantidad de citas
     */
    public int getCantidadCitas() {
        return citas != null ? citas.size() : 0;
    }

    /**
     * Establece la historia clínica de la mascota.
     *
     * @param historiaClinica Historia clínica a establecer
     */
    public void setHistoriaClinica(HistoriaClinica historiaClinica) {
        this.historiaClinica = historiaClinica;
        if (historiaClinica != null) {
            historiaClinica.setMascota(this);
        }
    }

    /**
     * Activa la mascota.
     */
    public void activar() {
        this.activo = true;
    }

    /**
     * Desactiva la mascota.
     */
    public void desactivar() {
        this.activo = false;
    }

    /**
     * Registra el fallecimiento de la mascota.
     *
     * @param fecha Fecha de fallecimiento
     * @param causa Causa de fallecimiento
     */
    public void registrarFallecimiento(LocalDate fecha, String causa) {
        this.activo = false;
        String infoFallecimiento = String.format("FALLECIMIENTO: Fecha: %s, Causa: %s", fecha, causa);
        this.observaciones = (this.observaciones != null 
            ? this.observaciones + "\n" 
            : "") + infoFallecimiento;
    }

    /**
     * Marca la mascota como esterilizada.
     */
    public void marcarComoEsterilizado() {
        this.esterilizado = true;
    }

    /**
     * Obtiene el nombre completo con especie y raza.
     *
     * @return Nombre completo
     */
    public String getNombreCompletoConEspecie() {
        StringBuilder sb = new StringBuilder(nombre);
        if (especie != null) {
            sb.append(" (").append(especie.getNombre());
            if (raza != null) {
                sb.append(" - ").append(raza.getNombre());
            }
            sb.append(")");
        }
        return sb.toString();
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mascota mascota)) return false;
        return idMascota != null && idMascota.equals(mascota.idMascota);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
