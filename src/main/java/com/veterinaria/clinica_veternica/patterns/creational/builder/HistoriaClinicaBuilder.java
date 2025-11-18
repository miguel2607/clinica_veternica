package com.veterinaria.clinica_veternica.patterns.creational.builder;

import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;

/**
 * Patrón Builder: HistoriaClinicaBuilder
 *
 * Construye objetos HistoriaClinica complejos paso a paso,
 * permitiendo agregar evoluciones, tratamientos, recetas y vacunaciones
 * de manera estructurada.
 *
 * Justificación:
 * - Las historias clínicas tienen múltiples componentes relacionados
 * - Requieren agregar información de manera incremental
 * - Necesitan validaciones específicas
 * - Permite construir historias completas de manera clara y organizada
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
public class HistoriaClinicaBuilder {

    private Mascota mascota;
    private String antecedentesMedicos;
    private String antecedentesQuirurgicos;
    private String alergias;
    private String enfermedadesCronicas;
    private String medicamentosActuales;
    private String observacionesGenerales;

    public HistoriaClinicaBuilder conMascota(Mascota mascota) {
        this.mascota = mascota;
        return this;
    }

    public HistoriaClinicaBuilder conAntecedentesMedicos(String antecedentesMedicos) {
        this.antecedentesMedicos = antecedentesMedicos;
        return this;
    }

    public HistoriaClinicaBuilder conAntecedentesQuirurgicos(String antecedentesQuirurgicos) {
        this.antecedentesQuirurgicos = antecedentesQuirurgicos;
        return this;
    }

    public HistoriaClinicaBuilder conAlergias(String alergias) {
        this.alergias = alergias;
        return this;
    }

    public HistoriaClinicaBuilder conEnfermedadesCronicas(String enfermedadesCronicas) {
        this.enfermedadesCronicas = enfermedadesCronicas;
        return this;
    }

    public HistoriaClinicaBuilder conMedicamentosActuales(String medicamentosActuales) {
        this.medicamentosActuales = medicamentosActuales;
        return this;
    }

    public HistoriaClinicaBuilder conObservacionesGenerales(String observacionesGenerales) {
        this.observacionesGenerales = observacionesGenerales;
        return this;
    }

    public HistoriaClinica build() {
        // Validación obligatoria
        if (mascota == null) {
            throw new IllegalStateException("La mascota es obligatoria para crear una historia clínica");
        }

        return HistoriaClinica.builder()
                .mascota(mascota)
                .antecedentesMedicos(antecedentesMedicos)
                .antecedentesQuirurgicos(antecedentesQuirurgicos)
                .alergias(alergias)
                .enfermedadesCronicas(enfermedadesCronicas)
                .medicamentosActuales(medicamentosActuales)
                .observacionesGenerales(observacionesGenerales)
                .activa(true)
                .build();
    }
}

