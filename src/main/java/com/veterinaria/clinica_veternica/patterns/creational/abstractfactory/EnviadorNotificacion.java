package com.veterinaria.clinica_veternica.patterns.creational.abstractfactory;

/**
 * Producto abstracto: Enviador de Notificación
 *
 * Interfaz para enviar notificaciones por un canal específico.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
public interface EnviadorNotificacion {

    /**
     * Envía una notificación.
     *
     * @param mensaje Mensaje a enviar
     * @return true si se envió exitosamente
     */
    boolean enviar(MensajeNotificacion mensaje);

    /**
     * Obtiene el estado del último envío.
     *
     * @return Estado del envío
     */
    String getEstadoEnvio();

    /**
     * Obtiene el ID externo generado por el proveedor.
     *
     * @return ID externo
     */
    String getIdExterno();

    /**
     * Obtiene el mensaje de error si el envío falló.
     *
     * @return Mensaje de error
     */
    String getMensajeError();
}
