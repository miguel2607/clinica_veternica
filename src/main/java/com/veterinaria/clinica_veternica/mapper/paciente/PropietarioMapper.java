package com.veterinaria.clinica_veternica.mapper.paciente;

import com.veterinaria.clinica_veternica.domain.paciente.Propietario;
import com.veterinaria.clinica_veternica.dto.request.paciente.PropietarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.PropietarioResponseDTO;
import org.mapstruct.*;



import java.util.List;

/**
 * Mapper para convertir entre Propietario (Entity) y sus DTOs.
 * MapStruct genera automáticamente la implementación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Mapper(componentModel = "spring")
public interface PropietarioMapper {

    /**
     * Convierte un PropietarioRequestDTO a Propietario (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad Propietario
     */
    @Mapping(target = "idPropietario", ignore = true)
    @Mapping(target = "mascotas", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    Propietario toEntity(PropietarioRequestDTO requestDTO);

    /**
     * Convierte un Propietario (Entity) a PropietarioResponseDTO.
     *
     * @param propietario Entidad
     * @return DTO de response
     */
    @Mapping(target = "nombreCompleto", expression = "java(getNombreCompleto(propietario))")
    @Mapping(target = "edad", ignore = true)
    @Mapping(target = "direccionCompleta", expression = "java(getDireccionCompleta(propietario))")
    @Mapping(target = "cantidadMascotas", expression = "java(propietario.getCantidadMascotas())")
    PropietarioResponseDTO toResponseDTO(Propietario propietario);

    /**
     * Convierte una lista de Propietario a lista de PropietarioResponseDTO.
     *
     * @param propietarios Lista de entidades
     * @return Lista de DTOs
     */
    List<PropietarioResponseDTO> toResponseDTOList(List<Propietario> propietarios);

    /**
     * Actualiza una entidad Propietario existente con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param propietario Entidad a actualizar
     */
    @Mapping(target = "idPropietario", ignore = true)
    @Mapping(target = "mascotas", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(PropietarioRequestDTO requestDTO, @MappingTarget Propietario propietario);

    /**
     * Obtiene el nombre completo del propietario.
     *
     * @param propietario Entidad
     * @return Nombre completo
     */
    default String getNombreCompleto(Propietario propietario) {
        return propietario.getNombres() + " " + propietario.getApellidos();
    }

    /**
     * Obtiene la dirección completa formateada.
     * La dirección ya incluye ciudad y código postal si están disponibles.
     *
     * @param propietario Entidad
     * @return Dirección completa
     */
    default String getDireccionCompleta(Propietario propietario) {
        if (propietario.getDireccion() != null && !propietario.getDireccion().isBlank()) {
            return propietario.getDireccion();
        }
        return null;
    }
}
