package com.veterinaria.clinica_veternica.patterns.structural.bridge;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Patrón Bridge: ReporteAbstraction (Abstract)
 *
 * Abstracción que define qué reportar, delegando el cómo exportarlo
 * a la implementación concreta (ReporteImplementor).
 *
 * Justificación:
 * - Separa la lógica de negocio del reporte de su formato de salida
 * - Permite cambiar formatos sin modificar la lógica del reporte
 * - Facilita la extensión con nuevos tipos de reportes y formatos
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
public abstract class ReporteAbstraction {

    protected ReporteImplementor implementor;

    protected ReporteAbstraction(ReporteImplementor implementor) {
        this.implementor = implementor;
    }

    /**
     * Genera el reporte usando la implementación específica.
     *
     * @return Contenido del reporte en formato byte array
     */
    public byte[] generar() {
        log.info("Generando reporte con formato: {}", implementor.getExtension());
        Map<String, Object> datos = recopilarDatos();
        String titulo = obtenerTitulo();
        return implementor.generarReporte(datos, titulo);
    }

    /**
     * Recopila los datos necesarios para el reporte.
     * Debe ser implementado por las subclases concretas.
     *
     * @return Map con los datos del reporte
     */
    protected abstract Map<String, Object> recopilarDatos();

    /**
     * Obtiene el título del reporte.
     * Puede ser sobrescrito por las subclases.
     *
     * @return Título del reporte
     */
    protected abstract String obtenerTitulo();

    /**
     * Obtiene el tipo MIME del formato.
     *
     * @return Tipo MIME
     */
    public String getTipoMime() {
        return implementor.getTipoMime();
    }

    /**
     * Obtiene la extensión del archivo.
     *
     * @return Extensión
     */
    public String getExtension() {
        return implementor.getExtension();
    }
}

