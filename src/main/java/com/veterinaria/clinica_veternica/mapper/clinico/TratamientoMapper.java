package com.veterinaria.clinica_veternica.mapper.clinico;

import com.veterinaria.clinica_veternica.domain.clinico.Tratamiento;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.dto.request.clinico.TratamientoRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.TratamientoResponseDTO;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TratamientoMapper {

    @Mapping(target = "idTratamiento", ignore = true)
    @Mapping(target = "historiaClinica", ignore = true)
    @Mapping(target = "veterinario", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    Tratamiento toEntity(TratamientoRequestDTO requestDTO);

    @Mapping(target = "veterinario", source = "veterinario", qualifiedByName = "mapVeterinarioSimple")
    @Mapping(target = "diasTratamiento", source = "tratamiento", qualifiedByName = "calcularDiasTratamiento")
    @Mapping(target = "diasRestantes", source = "tratamiento", qualifiedByName = "calcularDiasRestantes")
    TratamientoResponseDTO toResponseDTO(Tratamiento tratamiento);

    List<TratamientoResponseDTO> toResponseDTOList(List<Tratamiento> tratamientos);

    @Named("mapVeterinarioSimple")
    default TratamientoResponseDTO.VeterinarioSimpleDTO mapVeterinarioSimple(Veterinario veterinario) {
        if (veterinario == null) return null;
        return TratamientoResponseDTO.VeterinarioSimpleDTO.builder()
                .idPersonal(veterinario.getIdPersonal())
                .nombreCompleto(veterinario.getNombres() + " " + veterinario.getApellidos())
                .especialidad(veterinario.getEspecialidad())
                .build();
    }

    @Named("calcularDiasTratamiento")
    default Integer calcularDiasTratamiento(Tratamiento tratamiento) {
        if (tratamiento == null || tratamiento.getFechaInicio() == null || tratamiento.getFechaFin() == null) {
            return null;
        }
        return (int) ChronoUnit.DAYS.between(tratamiento.getFechaInicio(), tratamiento.getFechaFin());
    }

    @Named("calcularDiasRestantes")
    default Integer calcularDiasRestantes(Tratamiento tratamiento) {
        if (tratamiento == null || tratamiento.getFechaFin() == null) {
            return null;
        }
        long dias = ChronoUnit.DAYS.between(LocalDate.now(), tratamiento.getFechaFin());
        return dias > 0 ? (int) dias : 0;
    }
}
