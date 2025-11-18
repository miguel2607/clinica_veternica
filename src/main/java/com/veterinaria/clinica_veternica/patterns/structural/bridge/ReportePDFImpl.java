package com.veterinaria.clinica_veternica.patterns.structural.bridge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Patrón Bridge: ReportePDFImpl
 *
 * Implementación concreta para generar reportes en formato PDF.
 *
 * Justificación:
 * - Implementa la interfaz ReporteImplementor para formato PDF
 * - En producción, usaría una librería como Apache PDFBox o iText
 * - Aquí se simula la generación de PDF
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class ReportePDFImpl implements ReporteImplementor {

    @Override
    public byte[] generarReporte(Map<String, Object> datos, String titulo) {
        log.info("Generando reporte PDF: {}", titulo);

        // Simulación de generación de PDF
        // En producción, aquí se usaría Apache PDFBox o iText para generar el PDF real
        StringBuilder contenido = new StringBuilder();
        contenido.append("=== ").append(titulo).append(" ===\n\n");

        datos.forEach((key, value) -> contenido.append(key).append(": ").append(value).append("\n"));

        log.debug("Reporte PDF generado con {} campos", datos.size());
        return contenido.toString().getBytes();
    }

    @Override
    public String getTipoMime() {
        return "application/pdf";
    }

    @Override
    public String getExtension() {
        return "pdf";
    }
}

