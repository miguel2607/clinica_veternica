package com.veterinaria.clinica_veternica.patterns.structural.bridge;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Patrón Bridge: ReporteJSONImpl
 *
 * Implementación concreta para generar reportes en formato JSON.
 *
 * Justificación:
 * - Implementa la interfaz ReporteImplementor para formato JSON
 * - Útil para APIs y sistemas que consumen datos estructurados
 * - Facilita la integración con otros sistemas
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class ReporteJSONImpl implements ReporteImplementor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] generarReporte(Map<String, Object> datos, String titulo) {
        log.info("Generando reporte JSON: {}", titulo);

        try {
            Map<String, Object> reporte = new HashMap<>();
            reporte.put("titulo", titulo);
            reporte.put("fechaGeneracion", java.time.LocalDateTime.now().toString());
            reporte.put("datos", datos);

            byte[] jsonBytes = objectMapper.writeValueAsBytes(reporte);
            log.debug("Reporte JSON generado con {} bytes", jsonBytes.length);
            return jsonBytes;
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("Error al serializar reporte JSON: {}", e.getMessage(), e);
            throw new IllegalStateException("Error al generar reporte JSON", e);
        } catch (IllegalArgumentException e) {
            log.error("Error de validación al generar reporte JSON: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String getTipoMime() {
        return "application/json";
    }

    @Override
    public String getExtension() {
        return "json";
    }
}

