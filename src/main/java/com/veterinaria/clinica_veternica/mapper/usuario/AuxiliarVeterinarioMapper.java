package com.veterinaria.clinica_veternica.mapper.usuario;

import com.veterinaria.clinica_veternica.domain.usuario.AuxiliarVeterinario;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.usuario.AuxiliarVeterinarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.AuxiliarVeterinarioResponseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre AuxiliarVeterinario (Entity) y sus DTOs.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-12
 */
@Mapper(componentModel = "spring", uses = {UsuarioMapper.class})
public interface AuxiliarVeterinarioMapper {

    /**
     * Convierte un AuxiliarVeterinarioRequestDTO a AuxiliarVeterinario (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad AuxiliarVeterinario
     */
    @Mapping(target = "idPersonal", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    AuxiliarVeterinario toEntity(AuxiliarVeterinarioRequestDTO requestDTO);

    /**
     * Convierte un AuxiliarVeterinario (Entity) a AuxiliarVeterinarioResponseDTO.
     *
     * @param auxiliar Entidad
     * @return DTO de response
     */
    @Mapping(target = "nombreCompleto", source = "auxiliar", qualifiedByName = "getNombreCompletoAux")
    @Mapping(target = "usuario", source = "usuario", qualifiedByName = "mapUsuarioSimple")
    AuxiliarVeterinarioResponseDTO toResponseDTO(AuxiliarVeterinario auxiliar);

    /**
     * Convierte una lista de AuxiliarVeterinario a lista de AuxiliarVeterinarioResponseDTO.
     *
     * @param auxiliares Lista de entidades
     * @return Lista de DTOs
     */
    List<AuxiliarVeterinarioResponseDTO> toResponseDTOList(List<AuxiliarVeterinario> auxiliares);

    /**
     * Actualiza una entidad AuxiliarVeterinario existente con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param auxiliar Entidad a actualizar
     */
    @Mapping(target = "idPersonal", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(AuxiliarVeterinarioRequestDTO requestDTO, @MappingTarget AuxiliarVeterinario auxiliar);

    /**
     * Obtiene el nombre completo del auxiliar veterinario.
     *
     * @param auxiliar Entidad
     * @return Nombre completo
     */
    @Named("getNombreCompletoAux")
    default String getNombreCompleto(AuxiliarVeterinario auxiliar) {
        if (auxiliar == null) {
            return null;
        }
        return auxiliar.getNombres() + " " + auxiliar.getApellidos();
    }

    /**
     * Mapea Usuario a UsuarioSimpleDTO.
     *
     * @param usuario Usuario
     * @return UsuarioSimpleDTO
     */
    @Named("mapUsuarioSimple")
    default AuxiliarVeterinarioResponseDTO.UsuarioSimpleDTO mapUsuarioSimple(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return AuxiliarVeterinarioResponseDTO.UsuarioSimpleDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .rol(usuario.getRol() != null ? usuario.getRol().name() : null)
                .estado(usuario.getEstado())
                .build();
    }
}

