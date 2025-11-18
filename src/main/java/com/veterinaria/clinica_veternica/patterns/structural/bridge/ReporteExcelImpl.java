package com.veterinaria.clinica_veternica.patterns.structural.bridge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Patrón Bridge: ReporteExcelImpl
 *
 * Implementación concreta para generar reportes en formato Excel.
 *
 * Justificación:
 * - Implementa la interfaz ReporteImplementor para formato Excel
 * - En producción, usaría Apache POI para generar archivos Excel reales
 * - Aquí se simula la generación de Excel
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class ReporteExcelImpl implements ReporteImplementor {

    @Override
    public byte[] generarReporte(Map<String, Object> datos, String titulo) {
        log.info("Generando reporte Excel: {}", titulo);

        // Simulación de generación de Excel
        // En producción, aquí se usaría Apache POI para generar el Excel real
        StringBuilder contenido = new StringBuilder();
        contenido.append(titulo).append("\n");
        contenido.append("Columna1\tColumna2\tColumna3\n");

        int fila = 1;
        for (Map.Entry<String, Object> entry : datos.entrySet()) {
            contenido.append(entry.getKey()).append("\t")
                    .append(entry.getValue()).append("\t")
                    .append("Fila ").append(fila++).append("\n");
        }

        log.debug("Reporte Excel generado con {} campos", datos.size());
        return contenido.toString().getBytes();
    }

    @Override
    public String getTipoMime() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    @Override
    public String getExtension() {
        return "xlsx";
    }
}

