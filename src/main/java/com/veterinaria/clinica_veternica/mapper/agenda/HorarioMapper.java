package com.veterinaria.clinica_veternica.mapper.agenda;

import com.veterinaria.clinica_veternica.domain.agenda.Horario;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.dto.request.agenda.HorarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.HorarioResponseDTO;
import org.mapstruct.*;

import java.time.DayOfWeek;
import java.time.Duration;

import java.util.List;

/**
 * Mapper para convertir entre Horario (Entity) y sus DTOs.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Mapper(componentModel = "spring")
public interface HorarioMapper {

    /**
     * Convierte un HorarioRequestDTO a Horario (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad Horario
     */
    @Mapping(target = "idHorario", ignore = true)
    @Mapping(target = "veterinario", ignore = true)
    @Mapping(target = "diaSemana", source = "diaSemana", qualifiedByName = "stringToDayOfWeek")
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    Horario toEntity(HorarioRequestDTO requestDTO);

    /**
     * Convierte un Horario (Entity) a HorarioResponseDTO.
     *
     * @param horario Entidad
     * @return DTO de response
     */
    @Mapping(target = "diaSemana", source = "diaSemana", qualifiedByName = "dayOfWeekToString")
    @Mapping(target = "veterinario", source = "veterinario", qualifiedByName = "mapVeterinarioSimple")
    @Mapping(target = "duracionHoras", source = "horario", qualifiedByName = "calcularDuracion")
    HorarioResponseDTO toResponseDTO(Horario horario);

    /**
     * Convierte una lista de Horario a lista de HorarioResponseDTO.
     *
     * @param horarios Lista de entidades
     * @return Lista de DTOs
     */
    List<HorarioResponseDTO> toResponseDTOList(List<Horario> horarios);

    /**
     * Actualiza una entidad Horario existente con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param horario Entidad a actualizar
     */
    @Mapping(target = "idHorario", ignore = true)
    @Mapping(target = "veterinario", ignore = true)
    @Mapping(target = "diaSemana", source = "diaSemana", qualifiedByName = "stringToDayOfWeek")
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(HorarioRequestDTO requestDTO, @MappingTarget Horario horario);

    @Named("stringToDayOfWeek")
    default DayOfWeek stringToDayOfWeek(String diaSemana) {
        if (diaSemana == null) return null;
        return DayOfWeek.valueOf(diaSemana.toUpperCase());
    }

    @Named("dayOfWeekToString")
    default String dayOfWeekToString(DayOfWeek diaSemana) {
        if (diaSemana == null) return null;
        return diaSemana.name();
    }

    @Named("mapVeterinarioSimple")
    default HorarioResponseDTO.VeterinarioSimpleDTO mapVeterinarioSimple(Veterinario veterinario) {
        if (veterinario == null) return null;
        return HorarioResponseDTO.VeterinarioSimpleDTO.builder()
                .idPersonal(veterinario.getIdPersonal())
                .nombreCompleto(veterinario.getNombres() + " " + veterinario.getApellidos())
                .especialidad(veterinario.getEspecialidad())
                .build();
    }

    @Named("calcularDuracion")
    default Double calcularDuracion(Horario horario) {
        if (horario == null || horario.getHoraInicio() == null || horario.getHoraFin() == null) {
            return null;
        }
        Duration duration = Duration.between(horario.getHoraInicio(), horario.getHoraFin());
        return duration.toMinutes() / 60.0;
    }
}
