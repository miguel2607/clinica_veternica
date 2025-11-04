package com.veterinaria.clinica_veternica.domain.inventario;

/**
 * Enum que representa los estados de disponibilidad de un insumo en el inventario.
 *
 * Define el estado actual de stock de cada insumo médico o producto.
 * Se utiliza para gestionar alertas y control de inventario.
 *
 * Estados:
 * - DISPONIBLE: Insumo disponible en stock
 * - AGOTADO: Insumo sin stock (cantidad = 0)
 * - EN_PEDIDO: Insumo ordenado a proveedor, en espera de entrega
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
public enum EstadoInsumo {
    /**
     * Insumo disponible en stock.
     * Stock actual mayor o igual al stock mínimo.
     */
    DISPONIBLE("Disponible", "El insumo está disponible en stock"),

    /**
     * Insumo agotado.
     * Stock actual es cero o por debajo del stock mínimo.
     */
    AGOTADO("Agotado", "El insumo está agotado y requiere reabastecimiento urgente"),

    /**
     * Insumo en pedido.
     * Se ha realizado pedido al proveedor y está en tránsito.
     */
    EN_PEDIDO("En Pedido", "El insumo ha sido pedido al proveedor");

    private final String displayName;
    private final String description;

    /**
     * Constructor del enum.
     *
     * @param displayName Nombre descriptivo del estado
     * @param description Descripción del estado
     */
    EstadoInsumo(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Obtiene el nombre descriptivo del estado.
     *
     * @return Nombre del estado
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Obtiene la descripción del estado.
     *
     * @return Descripción del estado
     */
    public String getDescription() {
        return description;
    }

    /**
     * Verifica si el insumo puede ser usado en servicios.
     *
     * @return true si el estado es DISPONIBLE
     */
    public boolean isUsable() {
        return this == DISPONIBLE;
    }

    /**
     * Verifica si se debe generar alerta de reabastecimiento.
     *
     * @return true si el estado es AGOTADO
     */
    public boolean requiresAlert() {
        return this == AGOTADO;
    }

    /**
     * Determina el estado del insumo basado en stock actual y mínimo.
     *
     * @param stockActual Stock actual del insumo
     * @param stockMinimo Stock mínimo configurado
     * @return Estado calculado del insumo
     */
    public static EstadoInsumo determinarEstado(int stockActual, int stockMinimo) {
        if (stockActual <= 0) {
            return AGOTADO;
        } else if (stockActual < stockMinimo) {
            return AGOTADO; // También agotado si está por debajo del mínimo
        } else {
            return DISPONIBLE;
        }
    }
}
