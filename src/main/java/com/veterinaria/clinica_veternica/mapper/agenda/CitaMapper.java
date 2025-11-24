package com.veterinaria.clinica_veternica.mapper.agenda;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.dto.request.agenda.CitaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import org.mapstruct.*;


import java.time.LocalDateTime;

import java.util.List;

/**
 * Mapper para convertir entre Cita (Entity) y sus DTOs.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Mapper(componentModel = "spring")
public interface CitaMapper {

    /**
     * Convierte un CitaRequestDTO a Cita (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad Cita
     */
    @Mapping(target = "idCita", ignore = true)
    @Mapping(target = "mascota", ignore = true)
    @Mapping(target = "veterinario", ignore = true)
    @Mapping(target = "servicio", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    Cita toEntity(CitaRequestDTO requestDTO);

    /**
     * Convierte una Cita (Entity) a CitaResponseDTO.
     *
     * @param cita Entidad
     * @return DTO de response
     */
    @Mapping(target = "mascota", source = "mascota", qualifiedByName = "mapMascotaSimple")
    @Mapping(target = "veterinario", source = "veterinario", qualifiedByName = "mapVeterinarioSimple")
    @Mapping(target = "servicio", source = "servicio", qualifiedByName = "mapServicioSimple")
    @Mapping(target = "fechaHora", source = "cita", qualifiedByName = "combinarFechaHora")
    @Mapping(target = "estado", source = "estado", qualifiedByName = "mapEstado")
    CitaResponseDTO toResponseDTO(Cita cita);

    /**
     * Convierte una lista de Cita a lista de CitaResponseDTO.
     *
     * @param citas Lista de entidades
     * @return Lista de DTOs
     */
    List<CitaResponseDTO> toResponseDTOList(List<Cita> citas);

    /**
     * Actualiza una entidad Cita existente con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param cita Entidad a actualizar
     */
    @Mapping(target = "idCita", ignore = true)
    @Mapping(target = "mascota", ignore = true)
    @Mapping(target = "veterinario", ignore = true)
    @Mapping(target = "servicio", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(CitaRequestDTO requestDTO, @MappingTarget Cita cita);

    @Named("mapMascotaSimple")
    default CitaResponseDTO.MascotaSimpleDTO mapMascotaSimple(Mascota mascota) {
        if (mascota == null) return null;
        return CitaResponseDTO.MascotaSimpleDTO.builder()
                .idMascota(mascota.getIdMascota())
                .nombre(mascota.getNombre())
                .especie(mascota.getEspecie() != null ? mascota.getEspecie().getNombre() : null)
                .propietarioNombre(mascota.getPropietario() != null ?
                        mascota.getPropietario().getNombres() + " " + mascota.getPropietario().getApellidos() : null)
                .propietarioTelefono(mascota.getPropietario() != null ? mascota.getPropietario().getTelefono() : null)
                .build();
    }

    @Named("mapVeterinarioSimple")
    default CitaResponseDTO.VeterinarioSimpleDTO mapVeterinarioSimple(Veterinario veterinario) {
        if (veterinario == null) return null;
        return CitaResponseDTO.VeterinarioSimpleDTO.builder()
                .idPersonal(veterinario.getIdPersonal())
                .nombreCompleto(veterinario.getNombres() + " " + veterinario.getApellidos())
                .especialidad(veterinario.getEspecialidad())
                .build();
    }

    @Named("mapServicioSimple")
    default CitaResponseDTO.ServicioSimpleDTO mapServicioSimple(Servicio servicio) {
        if (servicio == null) return null;
        return CitaResponseDTO.ServicioSimpleDTO.builder()
                .idServicio(servicio.getIdServicio())
                .nombre(servicio.getNombre())
                .tipoServicio(servicio.getTipoServicio() != null ? servicio.getTipoServicio().name() : null)
                .precio(servicio.getPrecio())
                .duracionMinutos(servicio.getDuracionEstimadaMinutos())
                .build();
    }

    @Named("combinarFechaHora")
    default LocalDateTime combinarFechaHora(Cita cita) {
        if (cita == null || cita.getFechaCita() == null || cita.getHoraCita() == null) {
            return null;
        }
        return LocalDateTime.of(cita.getFechaCita(), cita.getHoraCita());
    }

    @Named("mapEstado")
    default String mapEstado(com.veterinaria.clinica_veternica.domain.agenda.EstadoCita estado) {
        if (estado == null) return null;
        return estado.name(); // Convierte el enum a String (ej: "CANCELADA", "ATENDIDA", etc.)
    }
}
