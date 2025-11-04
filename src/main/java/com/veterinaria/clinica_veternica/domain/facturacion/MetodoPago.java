package com.veterinaria.clinica_veternica.domain.facturacion;

/**
 * Enum que representa los métodos de pago aceptados en la clínica.
 *
 * Define las formas de pago disponibles para los propietarios.
 * Cada método puede tener políticas específicas de procesamiento.
 *
 * Métodos:
 * - EFECTIVO: Pago en efectivo
 * - TARJETA: Pago con tarjeta débito/crédito
 * - TRANSFERENCIA: Transferencia bancaria
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
public enum MetodoPago {
    /**
     * Pago en efectivo.
     * Procesamiento inmediato, sin comisiones.
     */
    EFECTIVO("Efectivo", "Pago en efectivo", 0.0),

    /**
     * Pago con tarjeta débito o crédito.
     * Puede tener comisión bancaria.
     */
    TARJETA("Tarjeta", "Pago con tarjeta débito/crédito", 0.03), // 3% comisión

    /**
     * Pago con tarjeta de crédito.
     * Puede tener comisión bancaria.
     */
    TARJETA_CREDITO("Tarjeta de Crédito", "Pago con tarjeta de crédito", 0.03),

    /**
     * Pago con tarjeta de débito.
     * Puede tener comisión menor.
     */
    TARJETA_DEBITO("Tarjeta de Débito", "Pago con tarjeta de débito", 0.02),

    /**
     * Transferencia bancaria.
     * Procesamiento puede tardar 1-2 días hábiles.
     */
    TRANSFERENCIA("Transferencia", "Transferencia bancaria", 0.0);

    private final String displayName;
    private final String description;
    private final double comisionPorcentaje;

    /**
     * Constructor del enum.
     *
     * @param displayName Nombre descriptivo del método
     * @param description Descripción del método
     * @param comisionPorcentaje Porcentaje de comisión (0.0 - 1.0)
     */
    MetodoPago(String displayName, String description, double comisionPorcentaje) {
        this.displayName = displayName;
        this.description = description;
        this.comisionPorcentaje = comisionPorcentaje;
    }

    /**
     * Obtiene el nombre descriptivo del método.
     *
     * @return Nombre del método
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Obtiene la descripción del método.
     *
     * @return Descripción del método
     */
    public String getDescription() {
        return description;
    }

    /**
     * Obtiene el porcentaje de comisión.
     *
     * @return Comisión como decimal (ej: 0.03 = 3%)
     */
    public double getComisionPorcentaje() {
        return comisionPorcentaje;
    }

    /**
     * Verifica si el método de pago es inmediato.
     *
     * @return true si el pago se procesa inmediatamente
     */
    public boolean isInmediato() {
        return this == EFECTIVO || this == TARJETA;
    }

    /**
     * Verifica si el método tiene comisión.
     *
     * @return true si tiene comisión mayor a 0
     */
    public boolean tieneComision() {
        return comisionPorcentaje > 0;
    }

    /**
     * Calcula el monto total incluyendo comisión.
     *
     * @param montoBase Monto base antes de comisión
     * @return Monto total con comisión aplicada
     */
    public double calcularMontoConComision(double montoBase) {
        return montoBase * (1 + comisionPorcentaje);
    }

    /**
     * Calcula el monto de la comisión.
     *
     * @param montoBase Monto base
     * @return Monto de la comisión
     */
    public double calcularComision(double montoBase) {
        return montoBase * comisionPorcentaje;
    }
}
