package com.veterinaria.clinica_veternica.patterns.creational.abstractfactory;

/**
 * Producto abstracto: Validador de Destinatario
 *
 * Interfaz para validar el formato del destinatario según el canal.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
public interface ValidadorDestinatario {

    /**
     * Valida el formato del destinatario.
     *
     * @param destinatario Destinatario a validar
     * @return true si el formato es válido
     */
    boolean esValido(String destinatario);

    /**
     * Normaliza el formato del destinatario.
     *
     * @param destinatario Destinatario a normalizar
     * @return Destinatario normalizado
     */
    String normalizar(String destinatario);

    /**
     * Obtiene el mensaje de error si la validación falla.
     *
     * @return Mensaje de error
     */
    String getMensajeError();
}
