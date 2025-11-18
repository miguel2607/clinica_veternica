package com.veterinaria.clinica_veternica.mapper.usuario;

import com.veterinaria.clinica_veternica.domain.usuario.Administrador;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.usuario.AdministradorRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.AdministradorResponseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Administrador (Entity) y sus DTOs.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-12
 */
@Mapper(componentModel = "spring", uses = {UsuarioMapper.class})
public interface AdministradorMapper {

    /**
     * Convierte un AdministradorRequestDTO a Administrador (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad Administrador
     */
    @Mapping(target = "idPersonal", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Administrador toEntity(AdministradorRequestDTO requestDTO);

    /**
     * Convierte un Administrador (Entity) a AdministradorResponseDTO.
     *
     * @param administrador Entidad
     * @return DTO de response
     */
    @Mapping(target = "nombreCompleto", source = "administrador", qualifiedByName = "getNombreCompletoAdmin")
    @Mapping(target = "usuario", source = "usuario", qualifiedByName = "mapUsuarioSimple")
    AdministradorResponseDTO toResponseDTO(Administrador administrador);

    /**
     * Convierte una lista de Administrador a lista de AdministradorResponseDTO.
     *
     * @param administradores Lista de entidades
     * @return Lista de DTOs
     */
    List<AdministradorResponseDTO> toResponseDTOList(List<Administrador> administradores);

    /**
     * Actualiza una entidad Administrador existente con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param administrador Entidad a actualizar
     */
    @Mapping(target = "idPersonal", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(AdministradorRequestDTO requestDTO, @MappingTarget Administrador administrador);

    /**
     * Obtiene el nombre completo del administrador.
     *
     * @param administrador Entidad
     * @return Nombre completo
     */
    @Named("getNombreCompletoAdmin")
    default String getNombreCompleto(Administrador administrador) {
        if (administrador == null) {
            return null;
        }
        return administrador.getNombres() + " " + administrador.getApellidos();
    }

    /**
     * Mapea Usuario a UsuarioSimpleDTO.
     *
     * @param usuario Usuario
     * @return UsuarioSimpleDTO
     */
    @Named("mapUsuarioSimple")
    default AdministradorResponseDTO.UsuarioSimpleDTO mapUsuarioSimple(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return AdministradorResponseDTO.UsuarioSimpleDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .rol(usuario.getRol() != null ? usuario.getRol().name() : null)
                .estado(usuario.getEstado())
                .build();
    }
}

