package com.veterinaria.clinica_veternica.patterns.creational.builder;

import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el patrón Builder - HistoriaClinicaBuilder
 */
class HistoriaClinicaBuilderTest {

    private Mascota mascota;
    private HistoriaClinicaBuilder builder;

    @BeforeEach
    void setUp() {
        mascota = Mascota.builder()
                .idMascota(1L)
                .nombre("Max")
                .build();

        builder = new HistoriaClinicaBuilder();
    }

    @Test
    @DisplayName("Builder - Debe construir historia clínica básica exitosamente")
    void debeConstruirHistoriaClinicaBasica() {
        HistoriaClinica historia = builder
                .conMascota(mascota)
                .build();

        assertNotNull(historia);
        assertEquals(mascota, historia.getMascota());
        assertTrue(historia.getActiva());
    }

    @Test
    @DisplayName("Builder - Debe establecer antecedentes médicos")
    void debeEstablecerAntecedentesMedicos() {
        String antecedentes = "Diabetes tipo 1, diagnosticada en 2020";
        HistoriaClinica historia = builder
                .conMascota(mascota)
                .conAntecedentesMedicos(antecedentes)
                .build();

        assertEquals(antecedentes, historia.getAntecedentesMedicos());
    }

    @Test
    @DisplayName("Builder - Debe establecer antecedentes quirúrgicos")
    void debeEstablecerAntecedentesQuirurgicos() {
        String antecedentes = "Esterilización realizada en 2019";
        HistoriaClinica historia = builder
                .conMascota(mascota)
                .conAntecedentesQuirurgicos(antecedentes)
                .build();

        assertEquals(antecedentes, historia.getAntecedentesQuirurgicos());
    }

    @Test
    @DisplayName("Builder - Debe establecer alergias")
    void debeEstablecerAlergias() {
        String alergias = "Alérgico a penicilina y algunos alimentos";
        HistoriaClinica historia = builder
                .conMascota(mascota)
                .conAlergias(alergias)
                .build();

        assertEquals(alergias, historia.getAlergias());
    }

    @Test
    @DisplayName("Builder - Debe establecer enfermedades crónicas")
    void debeEstablecerEnfermedadesCronicas() {
        String enfermedades = "Artritis crónica, requiere medicación continua";
        HistoriaClinica historia = builder
                .conMascota(mascota)
                .conEnfermedadesCronicas(enfermedades)
                .build();

        assertEquals(enfermedades, historia.getEnfermedadesCronicas());
    }

    @Test
    @DisplayName("Builder - Debe establecer medicamentos actuales")
    void debeEstablecerMedicamentosActuales() {
        String medicamentos = "Insulina 2 veces al día, Metformina 1 vez al día";
        HistoriaClinica historia = builder
                .conMascota(mascota)
                .conMedicamentosActuales(medicamentos)
                .build();

        assertEquals(medicamentos, historia.getMedicamentosActuales());
    }

    @Test
    @DisplayName("Builder - Debe establecer observaciones generales")
    void debeEstablecerObservacionesGenerales() {
        String observaciones = "Mascota tranquila, responde bien al tratamiento";
        HistoriaClinica historia = builder
                .conMascota(mascota)
                .conObservacionesGenerales(observaciones)
                .build();

        assertEquals(observaciones, historia.getObservacionesGenerales());
    }

    @Test
    @DisplayName("Builder - Debe construir historia clínica completa")
    void debeConstruirHistoriaClinicaCompleta() {
        HistoriaClinica historia = builder
                .conMascota(mascota)
                .conAntecedentesMedicos("Diabetes tipo 1")
                .conAntecedentesQuirurgicos("Esterilización")
                .conAlergias("Penicilina")
                .conEnfermedadesCronicas("Artritis")
                .conMedicamentosActuales("Insulina")
                .conObservacionesGenerales("Mascota saludable")
                .build();

        assertNotNull(historia);
        assertEquals(mascota, historia.getMascota());
        assertEquals("Diabetes tipo 1", historia.getAntecedentesMedicos());
        assertEquals("Esterilización", historia.getAntecedentesQuirurgicos());
        assertEquals("Penicilina", historia.getAlergias());
        assertEquals("Artritis", historia.getEnfermedadesCronicas());
        assertEquals("Insulina", historia.getMedicamentosActuales());
        assertEquals("Mascota saludable", historia.getObservacionesGenerales());
        assertTrue(historia.getActiva());
    }

    @Test
    @DisplayName("Builder - Debe lanzar excepción sin mascota")
    void debeLanzarExcepcionSinMascota() {
        HistoriaClinicaBuilder builderIncompleto = builder
                .conAntecedentesMedicos("Algunos antecedentes");
        assertThrows(IllegalStateException.class, builderIncompleto::build);
    }

    @Test
    @DisplayName("Builder - Debe permitir valores nulos en campos opcionales")
    void debePermitirValoresNulosEnCamposOpcionales() {
        HistoriaClinica historia = builder
                .conMascota(mascota)
                .build();

        assertNotNull(historia);
        assertNull(historia.getAntecedentesMedicos());
        assertNull(historia.getAntecedentesQuirurgicos());
        assertNull(historia.getAlergias());
        assertNull(historia.getEnfermedadesCronicas());
        assertNull(historia.getMedicamentosActuales());
        assertNull(historia.getObservacionesGenerales());
    }
}

