package com.veterinaria.clinica_veternica.mapper.usuario;

import com.veterinaria.clinica_veternica.domain.usuario.RolUsuario;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.usuario.UsuarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.UsuarioResponseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Usuario (Entity) y sus DTOs.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    /**
     * Convierte un UsuarioRequestDTO a Usuario (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad Usuario
     */
    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "rol", source = "rol", qualifiedByName = "stringToRol")
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "ultimoLogin", ignore = true)
    @Mapping(target = "intentosFallidos", ignore = true)
    @Mapping(target = "bloqueado", ignore = true)
    Usuario toEntity(UsuarioRequestDTO requestDTO);

    /**
     * Convierte un Usuario (Entity) a UsuarioResponseDTO.
     *
     * @param usuario Entidad
     * @return DTO de response
     */
    @Mapping(target = "rol", source = "rol", qualifiedByName = "rolToString")
    UsuarioResponseDTO toResponseDTO(Usuario usuario);

    /**
     * Convierte una lista de Usuario a lista de UsuarioResponseDTO.
     *
     * @param usuarios Lista de entidades
     * @return Lista de DTOs
     */
    List<UsuarioResponseDTO> toResponseDTOList(List<Usuario> usuarios);

    /**
     * Actualiza una entidad Usuario existente con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param usuario Entidad a actualizar
     */
    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "rol", source = "rol", qualifiedByName = "stringToRol")
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "ultimoLogin", ignore = true)
    @Mapping(target = "intentosFallidos", ignore = true)
    @Mapping(target = "bloqueado", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(UsuarioRequestDTO requestDTO, @MappingTarget Usuario usuario);

    /**
     * Convierte String a RolUsuario enum.
     *
     * @param rol Rol como string
     * @return Enum RolUsuario
     */
    @Named("stringToRol")
    default RolUsuario stringToRol(String rol) {
        if (rol == null) {
            return null;
        }
        return RolUsuario.valueOf(rol.toUpperCase());
    }

    /**
     * Convierte RolUsuario enum a String.
     *
     * @param rol Enum RolUsuario
     * @return Rol como string
     */
    @Named("rolToString")
    default String rolToString(RolUsuario rol) {
        if (rol == null) {
            return null;
        }
        return rol.name();
    }
}
