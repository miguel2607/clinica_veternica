package com.veterinaria.clinica_veternica.mapper.usuario;

import com.veterinaria.clinica_veternica.domain.usuario.*;
import com.veterinaria.clinica_veternica.dto.request.usuario.PersonalRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.PersonalResponseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Personal (Entity) y sus DTOs.
 * Aplica a Administrador, Recepcionista, Auxiliar Veterinario.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Mapper(componentModel = "spring", uses = {UsuarioMapper.class})
public interface PersonalMapper {

    /**
     * Convierte un PersonalRequestDTO a Administrador (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad Administrador
     */
    @Mapping(target = "idPersonal", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    Administrador toAdministrador(PersonalRequestDTO requestDTO);

    /**
     * Convierte un PersonalRequestDTO a Recepcionista (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad Recepcionista
     */
    @Mapping(target = "idPersonal", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    Recepcionista toRecepcionista(PersonalRequestDTO requestDTO);

    /**
     * Convierte un PersonalRequestDTO a AuxiliarVeterinario (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad AuxiliarVeterinario
     */
    @Mapping(target = "idPersonal", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    AuxiliarVeterinario toAuxiliarVeterinario(PersonalRequestDTO requestDTO);

    /**
     * Convierte un Personal (Entity) a PersonalResponseDTO.
     *
     * @param personal Entidad
     * @return DTO de response
     */
    @Mapping(target = "nombreCompleto", source = "personal", qualifiedByName = "getNombreCompletoPersonal")
    @Mapping(target = "tipoPersonal", source = "personal", qualifiedByName = "getTipoPersonal")
    @Mapping(target = "usuario", source = "usuario", qualifiedByName = "mapUsuarioSimple")
    PersonalResponseDTO toResponseDTO(Personal personal);

    /**
     * Convierte una lista de Personal a lista de PersonalResponseDTO.
     *
     * @param personalList Lista de entidades
     * @return Lista de DTOs
     */
    List<PersonalResponseDTO> toResponseDTOList(List<? extends Personal> personalList);

    /**
     * Actualiza una entidad Administrador con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param administrador Entidad a actualizar
     */
    @Mapping(target = "idPersonal", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAdministrador(PersonalRequestDTO requestDTO, @MappingTarget Administrador administrador);

    /**
     * Actualiza una entidad Recepcionista con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param recepcionista Entidad a actualizar
     */
    @Mapping(target = "idPersonal", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRecepcionista(PersonalRequestDTO requestDTO, @MappingTarget Recepcionista recepcionista);

    /**
     * Actualiza una entidad AuxiliarVeterinario con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param auxiliar Entidad a actualizar
     */
    @Mapping(target = "idPersonal", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAuxiliarVeterinario(PersonalRequestDTO requestDTO, @MappingTarget AuxiliarVeterinario auxiliar);

    /**
     * Obtiene el nombre completo del personal.
     *
     * @param personal Entidad
     * @return Nombre completo
     */
    @Named("getNombreCompletoPersonal")
    default String getNombreCompleto(Personal personal) {
        if (personal == null) {
            return null;
        }
        return personal.getNombres() + " " + personal.getApellidos();
    }

    /**
     * Obtiene el tipo de personal.
     *
     * @param personal Entidad
     * @return Tipo de personal
     */
    @Named("getTipoPersonal")
    default String getTipoPersonal(Personal personal) {
        if (personal == null) {
            return null;
        }
        if (personal instanceof Administrador) {
            return "ADMINISTRADOR";
        } else if (personal instanceof Recepcionista) {
            return "RECEPCIONISTA";
        } else if (personal instanceof AuxiliarVeterinario) {
            return "AUXILIAR_VETERINARIO";
        } else if (personal instanceof Veterinario) {
            return "VETERINARIO";
        }
        return "DESCONOCIDO";
    }

    /**
     * Mapea Usuario a UsuarioSimpleDTO.
     *
     * @param usuario Usuario
     * @return UsuarioSimpleDTO
     */
    @Named("mapUsuarioSimple")
    default PersonalResponseDTO.UsuarioSimpleDTO mapUsuarioSimple(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return PersonalResponseDTO.UsuarioSimpleDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .rol(usuario.getRol() != null ? usuario.getRol().name() : null)
                .estado(usuario.getEstado())
                .build();
    }
}
