package com.veterinaria.clinica_veternica.domain.clinico;

import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa la Historia Clínica de una Mascota.
 *
 * La historia clínica es el expediente médico completo de una mascota,
 * conteniendo todas las evoluciones clínicas, tratamientos, recetas y vacunas
 * aplicadas a lo largo de su vida en la clínica.
 *
 * Es una entidad crítica para:
 * - Registro médico completo
 * - Trazabilidad de atenciones
 * - Continuidad del cuidado
 * - Auditoría y seguimiento
 *
 * Patrón Memento: permite guardar y restaurar estados previos de la historia clínica.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "historias_clinicas",
       indexes = {
           @Index(name = "idx_historia_mascota", columnList = "id_mascota")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_historia_mascota", columnNames = "id_mascota")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"mascota", "evoluciones", "tratamientos", "recetas", "vacunaciones"})
public class HistoriaClinica {

    /**
     * Identificador único de la historia clínica.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistoriaClinica;

    /**
     * Mascota a la que pertenece esta historia clínica.
     * Relación One-to-One con Mascota.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mascota", nullable = false, unique = true)
    @NotNull(message = "La mascota es obligatoria")
    private Mascota mascota;

    /**
     * Número único de historia clínica.
     * Se genera automáticamente al crear la historia.
     */
    @Column(unique = true, length = 20)
    private String numeroHistoria;

    /**
     * Antecedentes médicos relevantes.
     */
    @Size(max = 2000, message = "Los antecedentes no pueden exceder 2000 caracteres")
    @Column(length = 2000)
    private String antecedentesMedicos;

    /**
     * Antecedentes quirúrgicos (cirugías previas).
     */
    @Size(max = 2000, message = "Los antecedentes quirúrgicos no pueden exceder 2000 caracteres")
    @Column(length = 2000)
    private String antecedentesQuirurgicos;

    /**
     * Alergias conocidas o reacciones adversas.
     */
    @Size(max = 1000, message = "Las alergias no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String alergias;

    /**
     * Enfermedades crónicas o condiciones preexistentes.
     */
    @Size(max = 1000, message = "Las enfermedades crónicas no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String enfermedadesCronicas;

    /**
     * Medicamentos actuales que toma regularmente.
     */
    @Size(max = 1000, message = "Los medicamentos actuales no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String medicamentosActuales;


    /**
     * Observaciones generales de la historia clínica.
     * Incluye dieta, tipo de sangre, motivo de archivo, etc.
     */
    @Size(max = 3000, message = "Las observaciones no pueden exceder 3000 caracteres")
    @Column(length = 3000)
    private String observacionesGenerales;

    /**
     * Indica si la historia clínica está activa.
     * Si está inactiva, significa que está archivada.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activa = true;

    /**
     * Evoluciones clínicas asociadas a esta historia.
     * Relación One-to-Many con EvolucionClinica.
     */
    @OneToMany(mappedBy = "historiaClinica", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaEvolucion DESC")
    @Builder.Default
    private List<EvolucionClinica> evoluciones = new ArrayList<>();

    /**
     * Tratamientos asociados a esta historia.
     * Relación One-to-Many con Tratamiento.
     */
    @OneToMany(mappedBy = "historiaClinica", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaInicio DESC")
    @Builder.Default
    private List<Tratamiento> tratamientos = new ArrayList<>();

    /**
     * Recetas médicas asociadas a esta historia.
     * Relación One-to-Many con RecetaMedica.
     */
    @OneToMany(mappedBy = "historiaClinica", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaEmision DESC")
    @Builder.Default
    private List<RecetaMedica> recetas = new ArrayList<>();

    /**
     * Vacunaciones registradas en esta historia.
     * Relación One-to-Many con Vacunacion.
     */
    @OneToMany(mappedBy = "historiaClinica", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaAplicacion DESC")
    @Builder.Default
    private List<Vacunacion> vacunaciones = new ArrayList<>();

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
     * Genera un número de historia clínica único.
     * Formato: HC-{año}-{id_mascota}-{timestamp}
     */
    @PrePersist
    public void generarNumeroHistoria() {
        if (numeroHistoria == null && mascota != null) {
            this.numeroHistoria = String.format("HC-%d-%d-%d",
                java.time.Year.now().getValue(),
                mascota.getIdMascota(),
                System.currentTimeMillis() % 100000);
        }
    }

    /**
     * Agrega una evolución clínica a la historia.
     *
     * @param evolucion Evolución a agregar
     */
    public void agregarEvolucion(EvolucionClinica evolucion) {
        evoluciones.add(evolucion);
        evolucion.setHistoriaClinica(this);
    }

    /**
     * Elimina una evolución clínica de la historia.
     *
     * @param evolucion Evolución a eliminar
     */
    public void eliminarEvolucion(EvolucionClinica evolucion) {
        evoluciones.remove(evolucion);
        evolucion.setHistoriaClinica(null);
    }

    /**
     * Agrega un tratamiento a la historia.
     *
     * @param tratamiento Tratamiento a agregar
     */
    public void agregarTratamiento(Tratamiento tratamiento) {
        tratamientos.add(tratamiento);
        tratamiento.setHistoriaClinica(this);
    }

    /**
     * Elimina un tratamiento de la historia.
     *
     * @param tratamiento Tratamiento a eliminar
     */
    public void eliminarTratamiento(Tratamiento tratamiento) {
        tratamientos.remove(tratamiento);
        tratamiento.setHistoriaClinica(null);
    }

    /**
     * Agrega una receta médica a la historia.
     *
     * @param receta Receta a agregar
     */
    public void agregarReceta(RecetaMedica receta) {
        recetas.add(receta);
        receta.setHistoriaClinica(this);
    }

    /**
     * Elimina una receta médica de la historia.
     *
     * @param receta Receta a eliminar
     */
    public void eliminarReceta(RecetaMedica receta) {
        recetas.remove(receta);
        receta.setHistoriaClinica(null);
    }

    /**
     * Agrega una vacunación a la historia.
     *
     * @param vacunacion Vacunación a agregar
     */
    public void agregarVacunacion(Vacunacion vacunacion) {
        vacunaciones.add(vacunacion);
        vacunacion.setHistoriaClinica(this);
    }

    /**
     * Elimina una vacunación de la historia.
     *
     * @param vacunacion Vacunación a eliminar
     */
    public void eliminarVacunacion(Vacunacion vacunacion) {
        vacunaciones.remove(vacunacion);
        vacunacion.setHistoriaClinica(null);
    }

    /**
     * Obtiene el número total de evoluciones clínicas.
     *
     * @return Cantidad de evoluciones
     */
    public int getCantidadEvoluciones() {
        return evoluciones != null ? evoluciones.size() : 0;
    }

    /**
     * Obtiene el número total de tratamientos.
     *
     * @return Cantidad de tratamientos
     */
    public int getCantidadTratamientos() {
        return tratamientos != null ? tratamientos.size() : 0;
    }

    /**
     * Obtiene el número total de recetas.
     *
     * @return Cantidad de recetas
     */
    public int getCantidadRecetas() {
        return recetas != null ? recetas.size() : 0;
    }

    /**
     * Obtiene el número total de vacunaciones.
     *
     * @return Cantidad de vacunaciones
     */
    public int getCantidadVacunaciones() {
        return vacunaciones != null ? vacunaciones.size() : 0;
    }

    /**
     * Obtiene la última evolución clínica registrada.
     *
     * @return Última evolución o null si no hay evoluciones
     */
    public EvolucionClinica getUltimaEvolucion() {
        if (evoluciones == null || evoluciones.isEmpty()) {
            return null;
        }
        return evoluciones.get(0); // Ya está ordenada por fecha descendente
    }

    /**
     * Obtiene los tratamientos activos (en curso).
     *
     * @return Lista de tratamientos activos
     */
    public List<Tratamiento> getTratamientosActivos() {
        if (tratamientos == null) {
            return new ArrayList<>();
        }
        return tratamientos.stream()
            .filter(Tratamiento::estaActivo)
            .toList();
    }

    /**
     * Archiva la historia clínica.
     *
     * @param motivo Motivo del archivo (se guarda en observaciones)
     */
    public void archivar(String motivo) {
        this.activa = false;
        if (motivo != null && !motivo.isBlank()) {
            String motivoArchivo = "ARCHIVADA: " + motivo + " - " + LocalDateTime.now();
            this.observacionesGenerales = (this.observacionesGenerales != null 
                ? this.observacionesGenerales + "\n" 
                : "") + motivoArchivo;
        }
    }

    /**
     * Reactiva la historia clínica.
     */
    public void reactivar() {
        this.activa = true;
    }

    /**
     * Verifica si está archivada.
     */
    public boolean estaArchivada() {
        return !activa;
    }

    /**
     * Verifica si tiene alergias registradas.
     *
     * @return true si tiene alergias
     */
    public boolean tieneAlergias() {
        return alergias != null && !alergias.isBlank();
    }

    /**
     * Verifica si tiene enfermedades crónicas.
     *
     * @return true si tiene enfermedades crónicas
     */
    public boolean tieneEnfermedadesCronicas() {
        return enfermedadesCronicas != null && !enfermedadesCronicas.isBlank();
    }

    /**
     * Verifica si la historia está completa (tiene al menos una evolución).
     *
     * @return true si está completa
     */
    public boolean estaCompleta() {
        return evoluciones != null && !evoluciones.isEmpty();
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HistoriaClinica that)) return false;
        return idHistoriaClinica != null && idHistoriaClinica.equals(that.idHistoriaClinica);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
