package com.veterinaria.clinica_veternica.mapper.usuario;

import com.veterinaria.clinica_veternica.domain.usuario.Recepcionista;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.usuario.RecepcionistaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.RecepcionistaResponseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Recepcionista (Entity) y sus DTOs.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-12
 */
@Mapper(componentModel = "spring", uses = {UsuarioMapper.class})
public interface RecepcionistaMapper {

    /**
     * Convierte un RecepcionistaRequestDTO a Recepcionista (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad Recepcionista
     */
    @Mapping(target = "idPersonal", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Recepcionista toEntity(RecepcionistaRequestDTO requestDTO);

    /**
     * Convierte un Recepcionista (Entity) a RecepcionistaResponseDTO.
     *
     * @param recepcionista Entidad
     * @return DTO de response
     */
    @Mapping(target = "nombreCompleto", source = "recepcionista", qualifiedByName = "getNombreCompletoRec")
    @Mapping(target = "usuario", source = "usuario", qualifiedByName = "mapUsuarioSimple")
    RecepcionistaResponseDTO toResponseDTO(Recepcionista recepcionista);

    /**
     * Convierte una lista de Recepcionista a lista de RecepcionistaResponseDTO.
     *
     * @param recepcionistas Lista de entidades
     * @return Lista de DTOs
     */
    List<RecepcionistaResponseDTO> toResponseDTOList(List<Recepcionista> recepcionistas);

    /**
     * Actualiza una entidad Recepcionista existente con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param recepcionista Entidad a actualizar
     */
    @Mapping(target = "idPersonal", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(RecepcionistaRequestDTO requestDTO, @MappingTarget Recepcionista recepcionista);

    /**
     * Obtiene el nombre completo del recepcionista.
     *
     * @param recepcionista Entidad
     * @return Nombre completo
     */
    @Named("getNombreCompletoRec")
    default String getNombreCompleto(Recepcionista recepcionista) {
        if (recepcionista == null) {
            return null;
        }
        return recepcionista.getNombres() + " " + recepcionista.getApellidos();
    }

    /**
     * Mapea Usuario a UsuarioSimpleDTO.
     *
     * @param usuario Usuario
     * @return UsuarioSimpleDTO
     */
    @Named("mapUsuarioSimple")
    default RecepcionistaResponseDTO.UsuarioSimpleDTO mapUsuarioSimple(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return RecepcionistaResponseDTO.UsuarioSimpleDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .rol(usuario.getRol() != null ? usuario.getRol().name() : null)
                .estado(usuario.getEstado())
                .build();
    }
}

