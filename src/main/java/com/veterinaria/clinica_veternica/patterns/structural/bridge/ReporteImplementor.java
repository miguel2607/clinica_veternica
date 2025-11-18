package com.veterinaria.clinica_veternica.patterns.structural.bridge;

import java.util.Map;

/**
 * Patrón Bridge: ReporteImplementor (Interface)
 *
 * Define la implementación para generar reportes en diferentes formatos.
 * Separa la abstracción (tipo de reporte) de su implementación (formato de salida).
 *
 * Justificación:
 * - Permite cambiar el formato de salida sin modificar la lógica del reporte
 * - Facilita agregar nuevos formatos (PDF, Excel, JSON, CSV, etc.)
 * - Separa responsabilidades: qué reportar vs cómo exportarlo
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
public interface ReporteImplementor {

    /**
     * Genera el reporte en el formato específico.
     *
     * @param datos Datos del reporte
     * @param titulo Título del reporte
     * @return Contenido del reporte en formato byte array
     */
    byte[] generarReporte(Map<String, Object> datos, String titulo);

    /**
     * Obtiene el tipo MIME del formato.
     *
     * @return Tipo MIME (ej: "application/pdf", "application/vnd.ms-excel")
     */
    String getTipoMime();

    /**
     * Obtiene la extensión del archivo.
     *
     * @return Extensión (ej: "pdf", "xlsx", "json")
     */
    String getExtension();
}

