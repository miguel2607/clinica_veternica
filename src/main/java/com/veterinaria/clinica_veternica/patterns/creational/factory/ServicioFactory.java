package com.veterinaria.clinica_veternica.patterns.creational.factory;

import com.veterinaria.clinica_veternica.domain.agenda.CategoriaServicio;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;

import java.math.BigDecimal;

/**
 * Patrón Factory Method: ServicioFactory (Abstract Factory)
 *
 * Define la interfaz para crear objetos Servicio sin especificar
 * las clases concretas que se crearán. Cada factory concreta crea
 * servicios con características específicas según su categoría.
 *
 * Justificación:
 * - Cada categoría de servicio tiene características específicas
 *   (precio base, duración, requisitos, etc.)
 * - Encapsula la lógica de creación compleja
 * - Facilita agregar nuevos tipos de servicios sin modificar código existente
 * - Permite validaciones específicas por categoría
 *
 * Uso:
 * - Crear servicios estándar por categoría
 * - Aplicar precios y configuraciones predeterminadas
 * - Validar requisitos específicos de cada categoría
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
public abstract class ServicioFactory {

    /**
     * Método factory abstracto para crear un servicio.
     *
     * @param nombre Nombre del servicio
     * @param descripcion Descripción del servicio
     * @param precio Precio base del servicio
     * @return Servicio creado con características específicas de la categoría
     */
    public abstract Servicio crearServicio(String nombre, String descripcion, BigDecimal precio);

    /**
     * Método template que define el proceso de creación.
     * Las subclases pueden sobrescribir pasos específicos.
     *
     * @param nombre Nombre del servicio
     * @param descripcion Descripción del servicio
     * @param precio Precio base
     * @return Servicio completamente configurado
     */
    public Servicio crearServicioCompleto(String nombre, String descripcion, BigDecimal precio) {
        Servicio servicio = crearServicio(nombre, descripcion, precio);
        aplicarConfiguracionesEspecificas(servicio);
        validarServicio(servicio);
        return servicio;
    }

    /**
     * Aplica configuraciones específicas según la categoría.
     * Las subclases deben implementar este método.
     *
     * @param servicio Servicio a configurar
     */
    protected abstract void aplicarConfiguracionesEspecificas(Servicio servicio);

    /**
     * Valida que el servicio cumpla con los requisitos de su categoría.
     *
     * @param servicio Servicio a validar
     */
    protected void validarServicio(Servicio servicio) {
        if (servicio.getNombre() == null || servicio.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del servicio no puede estar vacío");
        }
        if (servicio.getPrecio() == null || servicio.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }
        if (servicio.getDuracionEstimadaMinutos() == null || servicio.getDuracionEstimadaMinutos() < 5) {
            throw new IllegalArgumentException("La duración mínima es 5 minutos");
        }
    }

    /**
     * Obtiene la categoría de servicio que esta factory crea.
     *
     * @return Categoría de servicio
     */
    public abstract CategoriaServicio getCategoria();
}

