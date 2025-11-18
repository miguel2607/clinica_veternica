package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.request.comunicacion.NotificacionRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.comunicacion.NotificacionResponseDTO;

import java.util.List;

/**
 * Interfaz del servicio para gestión de notificaciones.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-01-XX
 */
public interface INotificacionService {

    /**
     * Envía una notificación a un usuario por el canal especificado.
     * NOTA: En producción, esto se integraría con servicios reales de notificación.
     *
     * @param requestDTO Datos de la notificación
     * @return Notificación enviada
     */
    NotificacionResponseDTO enviarNotificacion(NotificacionRequestDTO requestDTO);

    /**
     * Busca una notificación por ID.
     *
     * @param id ID de la notificación
     * @return Notificación encontrada
     */
    NotificacionResponseDTO buscarPorId(Long id);

    /**
     * Lista todas las notificaciones.
     *
     * @return Lista de notificaciones
     */
    List<NotificacionResponseDTO> listarTodas();

    /**
     * Lista notificaciones por usuario.
     *
     * @param idUsuario ID del usuario
     * @return Lista de notificaciones
     */
    List<NotificacionResponseDTO> listarPorUsuario(Long idUsuario);

    /**
     * Lista notificaciones por canal.
     *
     * @param canal Canal (EMAIL, SMS, WHATSAPP, PUSH)
     * @return Lista de notificaciones
     */
    List<NotificacionResponseDTO> listarPorCanal(String canal);

    /**
     * Lista notificaciones enviadas.
     *
     * @return Lista de notificaciones enviadas
     */
    List<NotificacionResponseDTO> listarEnviadas();

    /**
     * Lista notificaciones pendientes.
     *
     * @return Lista de notificaciones pendientes
     */
    List<NotificacionResponseDTO> listarPendientes();
}

