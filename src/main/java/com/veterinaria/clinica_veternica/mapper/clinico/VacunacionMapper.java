package com.veterinaria.clinica_veternica.mapper.clinico;

import com.veterinaria.clinica_veternica.domain.clinico.Vacunacion;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.dto.request.clinico.VacunacionRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.VacunacionResponseDTO;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Mapper(componentModel = "spring")
public interface VacunacionMapper {

    @Mapping(target = "idVacunacion", ignore = true)
    @Mapping(target = "historiaClinica", ignore = true)
    @Mapping(target = "veterinario", ignore = true)
    @Mapping(target = "insumo", ignore = true)
    @Mapping(target = "mascota", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    Vacunacion toEntity(VacunacionRequestDTO requestDTO);

    @Mapping(target = "veterinario", source = "veterinario", qualifiedByName = "mapVeterinarioSimple")
    @Mapping(target = "vencida", source = "vacunacion", qualifiedByName = "calcularVencida")
    @Mapping(target = "diasHastaProximaDosis", source = "vacunacion", qualifiedByName = "calcularDiasHastaDosis")
    VacunacionResponseDTO toResponseDTO(Vacunacion vacunacion);

    List<VacunacionResponseDTO> toResponseDTOList(List<Vacunacion> vacunaciones);

    @Named("mapVeterinarioSimple")
    default VacunacionResponseDTO.VeterinarioSimpleDTO mapVeterinarioSimple(Veterinario veterinario) {
        if (veterinario == null) return null;
        return VacunacionResponseDTO.VeterinarioSimpleDTO.builder()
                .idPersonal(veterinario.getIdPersonal())
                .nombreCompleto(veterinario.getNombres() + " " + veterinario.getApellidos())
                .build();
    }

    @Named("calcularVencida")
    default Boolean calcularVencida(Vacunacion vacunacion) {
        if (vacunacion == null || vacunacion.getFechaProximaDosis() == null) {
            return false;
        }
        return vacunacion.getFechaProximaDosis().isBefore(LocalDate.now());
    }

    @Named("calcularDiasHastaDosis")
    default Integer calcularDiasHastaDosis(Vacunacion vacunacion) {
        if (vacunacion == null || vacunacion.getFechaProximaDosis() == null) {
            return null;
        }
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), vacunacion.getFechaProximaDosis());
    }
}
