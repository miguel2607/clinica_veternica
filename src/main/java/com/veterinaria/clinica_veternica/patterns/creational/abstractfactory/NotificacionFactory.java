package com.veterinaria.clinica_veternica.patterns.creational.abstractfactory;

/**
 * Patrón Abstract Factory: NotificacionFactory
 *
 * Define la interfaz para crear familias de objetos relacionados
 * (notificaciones por diferentes canales) sin especificar sus clases concretas.
 *
 * Justificación:
 * - El sistema necesita enviar notificaciones por múltiples canales
 * - Cada canal tiene su propia implementación y configuración
 * - Permite agregar nuevos canales sin modificar código existente
 * - Encapsula la creación de objetos relacionados (mensaje, validador, enviador)
 *
 * Uso:
 * - Crear notificaciones para diferentes canales (EMAIL, SMS, WhatsApp, Push)
 * - Enviar recordatorios de citas
 * - Alertas de inventario
 * - Notificaciones de pagos y facturas
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
public interface NotificacionFactory {

    /**
     * Crea un mensaje de notificación para el canal específico.
     *
     * @param destinatario Destinatario de la notificación
     * @param asunto Asunto del mensaje
     * @param contenido Contenido del mensaje
     * @return Mensaje creado
     */
    MensajeNotificacion crearMensaje(String destinatario, String asunto, String contenido);

    /**
     * Crea un validador para validar el formato del destinatario.
     *
     * @return Validador específico del canal
     */
    ValidadorDestinatario crearValidador();

    /**
     * Crea un enviador para enviar la notificación.
     *
     * @return Enviador específico del canal
     */
    EnviadorNotificacion crearEnviador();

    /**
     * Obtiene el nombre del canal de notificación.
     *
     * @return Nombre del canal (EMAIL, SMS, WHATSAPP, PUSH)
     */
    String getNombreCanal();

    /**
     * Verifica si el canal está disponible/activo.
     *
     * @return true si está disponible
     */
    boolean estaDisponible();

    /**
     * Obtiene el costo estimado de envío por notificación.
     *
     * @return Costo en la moneda local
     */
    double getCostoEnvio();
}
