package com.veterinaria.clinica_veternica.mapper.usuario;

import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.dto.request.usuario.VeterinarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.VeterinarioResponseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Veterinario (Entity) y sus DTOs.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Mapper(componentModel = "spring", uses = {UsuarioMapper.class})
public interface VeterinarioMapper {

    /**
     * Convierte un VeterinarioRequestDTO a Veterinario (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad Veterinario
     */
    @Mapping(target = "idPersonal", ignore = true)
    @Mapping(target = "horarios", ignore = true)
    @Mapping(target = "citas", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    Veterinario toEntity(VeterinarioRequestDTO requestDTO);

    /**
     * Convierte un Veterinario (Entity) a VeterinarioResponseDTO.
     *
     * @param veterinario Entidad
     * @return DTO de response
     */
    @Mapping(target = "nombreCompleto", source = "veterinario", qualifiedByName = "getNombreCompletoVet")
    @Mapping(target = "disponible", source = "activo")
    @Mapping(target = "cantidadCitas", source = "citas", qualifiedByName = "contarCitas")
    @Mapping(target = "cantidadHorarios", source = "horarios", qualifiedByName = "contarHorarios")
    @Mapping(target = "usuario", source = "usuario", qualifiedByName = "mapUsuarioSimple")
    VeterinarioResponseDTO toResponseDTO(Veterinario veterinario);

    /**
     * Convierte una lista de Veterinario a lista de VeterinarioResponseDTO.
     *
     * @param veterinarios Lista de entidades
     * @return Lista de DTOs
     */
    List<VeterinarioResponseDTO> toResponseDTOList(List<Veterinario> veterinarios);

    /**
     * Actualiza una entidad Veterinario existente con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param veterinario Entidad a actualizar
     */
    @Mapping(target = "idPersonal", ignore = true)
    @Mapping(target = "horarios", ignore = true)
    @Mapping(target = "citas", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(VeterinarioRequestDTO requestDTO, @MappingTarget Veterinario veterinario);

    /**
     * Obtiene el nombre completo del veterinario.
     *
     * @param veterinario Entidad
     * @return Nombre completo
     */
    @Named("getNombreCompletoVet")
    default String getNombreCompleto(Veterinario veterinario) {
        if (veterinario == null) {
            return null;
        }
        return veterinario.getNombres() + " " + veterinario.getApellidos();
    }

    /**
     * Cuenta la cantidad de citas.
     *
     * @param citas Lista de citas
     * @return Cantidad de citas
     */
    @Named("contarCitas")
    default Integer contarCitas(List<?> citas) {
        return citas != null ? citas.size() : 0;
    }

    /**
     * Cuenta la cantidad de horarios.
     *
     * @param horarios Lista de horarios
     * @return Cantidad de horarios
     */
    @Named("contarHorarios")
    default Integer contarHorarios(List<?> horarios) {
        return horarios != null ? horarios.size() : 0;
    }

    /**
     * Mapea Usuario a UsuarioSimpleDTO.
     *
     * @param usuario Usuario
     * @return UsuarioSimpleDTO
     */
    @Named("mapUsuarioSimple")
    default VeterinarioResponseDTO.UsuarioSimpleDTO mapUsuarioSimple(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return VeterinarioResponseDTO.UsuarioSimpleDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .rol(usuario.getRol() != null ? usuario.getRol().name() : null)
                .estado(usuario.getEstado())
                .build();
    }
}
