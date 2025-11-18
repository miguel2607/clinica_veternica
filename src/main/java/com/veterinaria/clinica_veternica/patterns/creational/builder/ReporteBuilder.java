package com.veterinaria.clinica_veternica.patterns.creational.builder;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.patterns.structural.bridge.ReporteCitasAbstraction;
import com.veterinaria.clinica_veternica.patterns.structural.bridge.ReporteExcelImpl;
import com.veterinaria.clinica_veternica.patterns.structural.bridge.ReporteImplementor;
import com.veterinaria.clinica_veternica.patterns.structural.bridge.ReporteJSONImpl;
import com.veterinaria.clinica_veternica.patterns.structural.bridge.ReportePDFImpl;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Patrón Builder: ReporteBuilder
 *
 * Construye reportes complejos paso a paso, permitiendo configurar múltiples
 * filtros, formatos y opciones de manera flexible y validada.
 *
 * PROPÓSITO:
 * - Construcción flexible de reportes con múltiples filtros
 * - Soporte para diferentes formatos de salida (PDF, Excel, JSON)
 * - Validación de parámetros antes de generar el reporte
 * - Reutilización de lógica de construcción
 *
 * CASOS DE USO:
 * 1. Reporte de citas con filtros por fecha, veterinario, estado
 * 2. Reporte de facturación con filtros por rango de fechas, propietario
 * 3. Reporte de inventario con filtros por tipo, stock bajo, proveedor
 * 4. Reporte de historial de mascota con múltiples secciones
 * 5. Reporte consolidado de actividad de la clínica
 *
 * TIPOS DE REPORTE:
 * - CITAS: Reporte de citas con filtros
 * - FACTURACION: Reporte de facturación y pagos
 * - INVENTARIO: Reporte de inventario y movimientos
 * - HISTORIAL: Reporte de historial clínico
 * - CONSOLIDADO: Reporte general de actividad
 *
 * Justificación:
 * - Los reportes tienen múltiples parámetros opcionales
 * - Requieren validaciones complejas
 * - Necesitan soporte para diferentes formatos
 * - Permiten construcción flexible y reutilizable
 *
 * Uso:
 * Reporte reporte = new ReporteBuilder()
 *     .tipoReporte(TipoReporte.CITAS)
 *     .conRangoFechas(LocalDate.now().minusDays(30), LocalDate.now())
 *     .conFiltro("veterinario", idVeterinario)
 *     .conFiltro("estado", "ATENDIDA")
 *     .conFormato(FormatoReporte.PDF)
 *     .conTitulo("Reporte de Citas del Mes")
 *     .incluirGraficos(true)
 *     .build();
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-01-27
 */
@Slf4j
public class ReporteBuilder {

    public enum TipoReporte {
        CITAS,
        FACTURACION,
        INVENTARIO,
        HISTORIAL,
        CONSOLIDADO
    }

    public enum FormatoReporte {
        PDF,
        EXCEL,
        JSON
    }

    private TipoReporte tipoReporte;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private final Map<String, Object> filtros = new HashMap<>();
    private FormatoReporte formato = FormatoReporte.PDF;
    private String titulo;
    private boolean incluirGraficos = false;
    private boolean incluirDetalles = true;
    private boolean incluirResumen = true;
    private List<String> columnas = new ArrayList<>();
    private String ordenamiento;
    private boolean ordenAscendente = true;

    /**
     * Establece el tipo de reporte a generar.
     */
    public ReporteBuilder tipoReporte(TipoReporte tipoReporte) {
        this.tipoReporte = tipoReporte;
        return this;
    }

    /**
     * Establece el rango de fechas para el reporte.
     */
    public ReporteBuilder conRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin");
        }
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        return this;
    }

    /**
     * Establece el rango de fecha y hora para el reporte.
     */
    public ReporteBuilder conRangoFechaHora(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin) {
        if (fechaHoraInicio != null && fechaHoraFin != null && fechaHoraInicio.isAfter(fechaHoraFin)) {
            throw new IllegalArgumentException("La fecha/hora de inicio debe ser anterior a la fecha/hora de fin");
        }
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        return this;
    }

    /**
     * Agrega un filtro personalizado al reporte.
     */
    public ReporteBuilder conFiltro(String nombre, Object valor) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del filtro no puede estar vacío");
        }
        this.filtros.put(nombre, valor);
        return this;
    }

    /**
     * Establece el formato de salida del reporte.
     */
    public ReporteBuilder conFormato(FormatoReporte formato) {
        this.formato = formato;
        return this;
    }

    /**
     * Establece el título del reporte.
     */
    public ReporteBuilder conTitulo(String titulo) {
        this.titulo = titulo;
        return this;
    }

    /**
     * Indica si el reporte debe incluir gráficos.
     */
    public ReporteBuilder incluirGraficos(boolean incluir) {
        this.incluirGraficos = incluir;
        return this;
    }

    /**
     * Indica si el reporte debe incluir detalles.
     */
    public ReporteBuilder incluirDetalles(boolean incluir) {
        this.incluirDetalles = incluir;
        return this;
    }

    /**
     * Indica si el reporte debe incluir resumen.
     */
    public ReporteBuilder incluirResumen(boolean incluir) {
        this.incluirResumen = incluir;
        return this;
    }

    /**
     * Establece las columnas a incluir en el reporte.
     */
    public ReporteBuilder conColumnas(List<String> columnas) {
        this.columnas = columnas != null ? new ArrayList<>(columnas) : new ArrayList<>();
        return this;
    }

    /**
     * Agrega una columna al reporte.
     */
    public ReporteBuilder agregarColumna(String columna) {
        if (columna != null && !columna.isBlank()) {
            this.columnas.add(columna);
        }
        return this;
    }

    /**
     * Establece el ordenamiento del reporte.
     */
    public ReporteBuilder conOrdenamiento(String campo, boolean ascendente) {
        this.ordenamiento = campo;
        this.ordenAscendente = ascendente;
        return this;
    }

    /**
     * Construye el reporte con todas las validaciones y configuraciones.
     *
     * @return Reporte construido
     */
    public Reporte build() {
        // Validaciones obligatorias
        if (tipoReporte == null) {
            throw new IllegalStateException("El tipo de reporte es obligatorio");
        }

        // Generar título si no se proporcionó
        if (titulo == null || titulo.isBlank()) {
            titulo = generarTituloPorDefecto();
        }

        // Obtener implementador según formato
        ReporteImplementor implementor = obtenerImplementor();

        log.info("Construyendo reporte: {} - Formato: {} - Título: {}", 
                tipoReporte, formato, titulo);

        // Construir reporte según tipo
        Reporte reporte = new Reporte();
        reporte.setTipoReporte(tipoReporte);
        reporte.setFechaInicio(fechaInicio);
        reporte.setFechaFin(fechaFin);
        reporte.setFechaHoraInicio(fechaHoraInicio);
        reporte.setFechaHoraFin(fechaHoraFin);
        reporte.setFiltros(new HashMap<>(filtros));
        reporte.setFormato(formato);
        reporte.setTitulo(titulo);
        reporte.setIncluirGraficos(incluirGraficos);
        reporte.setIncluirDetalles(incluirDetalles);
        reporte.setIncluirResumen(incluirResumen);
        reporte.setColumnas(new ArrayList<>(columnas));
        reporte.setOrdenamiento(ordenamiento);
        reporte.setOrdenAscendente(ordenAscendente);
        reporte.setImplementor(implementor);

        log.debug("Reporte construido exitosamente");
        return reporte;
    }

    /**
     * Genera un título por defecto basado en el tipo de reporte y fechas.
     */
    private String generarTituloPorDefecto() {
        StringBuilder tituloBuilder = new StringBuilder();
        tituloBuilder.append("Reporte de ").append(tipoReporte.name());

        if (fechaInicio != null && fechaFin != null) {
            tituloBuilder.append(" (").append(fechaInicio).append(" - ").append(fechaFin).append(")");
        } else if (fechaHoraInicio != null && fechaHoraFin != null) {
            tituloBuilder.append(" (").append(fechaHoraInicio.toLocalDate())
                  .append(" - ").append(fechaHoraFin.toLocalDate()).append(")");
        }

        return tituloBuilder.toString();
    }

    /**
     * Obtiene el implementador según el formato seleccionado.
     */
    private ReporteImplementor obtenerImplementor() {
        return switch (formato) {
            case PDF -> new ReportePDFImpl();
            case EXCEL -> new ReporteExcelImpl();
            case JSON -> new ReporteJSONImpl();
        };
    }

    /**
     * Clase que representa un reporte construido.
     */
    @lombok.Data
    public static class Reporte {
        private TipoReporte tipoReporte;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private LocalDateTime fechaHoraInicio;
        private LocalDateTime fechaHoraFin;
        private Map<String, Object> filtros;
        private FormatoReporte formato;
        private String titulo;
        private boolean incluirGraficos;
        private boolean incluirDetalles;
        private boolean incluirResumen;
        private List<String> columnas;
        private String ordenamiento;
        private boolean ordenAscendente;
        private ReporteImplementor implementor;

        /**
         * Genera el reporte usando el implementador configurado.
         */
        public byte[] generar() {
            if (implementor == null) {
                throw new IllegalStateException("No se ha configurado un implementador para el reporte");
            }
            
            // Convertir el reporte a un Map para el implementador
            Map<String, Object> datos = new java.util.HashMap<>();
            datos.put("tipoReporte", tipoReporte != null ? tipoReporte.name() : null);
            datos.put("fechaInicio", fechaInicio);
            datos.put("fechaFin", fechaFin);
            datos.put("fechaHoraInicio", fechaHoraInicio);
            datos.put("fechaHoraFin", fechaHoraFin);
            datos.put("filtros", filtros);
            datos.put("incluirGraficos", incluirGraficos);
            datos.put("incluirDetalles", incluirDetalles);
            datos.put("incluirResumen", incluirResumen);
            datos.put("columnas", columnas);
            datos.put("ordenamiento", ordenamiento);
            datos.put("ordenAscendente", ordenAscendente);
            
            return implementor.generarReporte(datos, titulo != null ? titulo : "Reporte");
        }
    }
}

