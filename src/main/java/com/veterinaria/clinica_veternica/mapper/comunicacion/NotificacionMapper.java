package com.veterinaria.clinica_veternica.mapper.comunicacion;

import com.veterinaria.clinica_veternica.domain.comunicacion.Notificacion;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.comunicacion.NotificacionRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.comunicacion.NotificacionResponseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Notificacion (Entity) y sus DTOs.
 * MapStruct genera automáticamente la implementación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Mapper(componentModel = "spring")
public interface NotificacionMapper {

    /**
     * Convierte un NotificacionRequestDTO a Notificacion (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad Notificacion
     */
    @Mapping(target = "idNotificacion", ignore = true)
    @Mapping(target = "enviada", ignore = true)
    @Mapping(target = "fechaEnvio", ignore = true)
    @Mapping(target = "leida", ignore = true)
    @Mapping(target = "fechaLectura", ignore = true)
    @Mapping(target = "intentosEnvio", ignore = true)
    @Mapping(target = "mensajeError", ignore = true)
    @Mapping(target = "idExterno", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    Notificacion toEntity(NotificacionRequestDTO requestDTO);

    /**
     * Convierte una Notificacion (Entity) a NotificacionResponseDTO.
     *
     * @param notificacion Entidad
     * @return DTO de response
     */
    @Mapping(target = "idUsuario", source = "usuario.idUsuario")
    @Mapping(target = "seAgotaronIntentos", expression = "java(notificacion.seAgotaronIntentos())")
    @Mapping(target = "puedeReintentar", expression = "java(notificacion.puedeReintentar())")
    @Mapping(target = "esEmail", expression = "java(notificacion.esEmail())")
    @Mapping(target = "esSMS", expression = "java(notificacion.esSMS())")
    @Mapping(target = "esWhatsApp", expression = "java(notificacion.esWhatsApp())")
    @Mapping(target = "esPush", expression = "java(notificacion.esPush())")
    @Mapping(target = "esUrgente", expression = "java(notificacion.esUrgente())")
    @Mapping(target = "esAltaPrioridad", expression = "java(notificacion.esAltaPrioridad())")
    @Mapping(target = "minutosDesdeCreacion", expression = "java(notificacion.getMinutosDesdeCreacion())")
    @Mapping(target = "estaPendiente", expression = "java(notificacion.estaPendiente())")
    @Mapping(target = "resumen", expression = "java(notificacion.getResumen())")
    NotificacionResponseDTO toResponseDTO(Notificacion notificacion);

    /**
     * Convierte una lista de Notificacion a lista de NotificacionResponseDTO.
     *
     * @param notificaciones Lista de entidades
     * @return Lista de DTOs
     */
    List<NotificacionResponseDTO> toResponseDTOList(List<Notificacion> notificaciones);

    /**
     * Mapea el ID de usuario a la entidad Usuario.
     *
     * @param idUsuario ID del usuario
     * @return Entidad Usuario con solo el ID establecido
     */
    default Usuario mapIdToUsuario(Long idUsuario) {
        if (idUsuario == null) {
            return null;
        }
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        return usuario;
    }
}
