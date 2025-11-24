package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.agenda.Horario;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.dto.request.agenda.HorarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.HorarioResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.agenda.HorarioMapper;
import com.veterinaria.clinica_veternica.repository.HorarioRepository;
import com.veterinaria.clinica_veternica.repository.VeterinarioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IHorarioService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;

/**
 * Implementación del servicio para gestión de Horarios.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HorarioServiceImpl implements IHorarioService {

    private final HorarioRepository horarioRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final HorarioMapper horarioMapper;

    @Override
    public HorarioResponseDTO crear(HorarioRequestDTO requestDTO) {
        log.info("Creando nuevo horario para veterinario ID: {}", requestDTO.getIdVeterinario());

        // Validar que el veterinario existe
        Veterinario veterinario = veterinarioRepository.findById(requestDTO.getIdVeterinario())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_VETERINARIO, "id", requestDTO.getIdVeterinario()));

        // Validar que no haya traslape con otros horarios del mismo veterinario
        validarTraslapeHorarios(veterinario, requestDTO);

        Horario horario = horarioMapper.toEntity(requestDTO);
        horario.setVeterinario(veterinario);

        if (horario.getActivo() == null) {
            horario.setActivo(true);
        }

        // Establecer valores por defecto si no están presentes
        if (horario.getDuracionCitaMinutos() == null) {
            horario.setDuracionCitaMinutos(30); // Valor por defecto: 30 minutos
        }

        if (horario.getMaxCitasSimultaneas() == null) {
            horario.setMaxCitasSimultaneas(1); // Valor por defecto: 1 cita simultánea
        }

        Horario horarioGuardado = horarioRepository.save(horario);
        log.info("Horario creado exitosamente con ID: {}", horarioGuardado.getIdHorario());
        return horarioMapper.toResponseDTO(horarioGuardado);
    }

    @Override
    public HorarioResponseDTO actualizar(Long id, HorarioRequestDTO requestDTO) {
        log.info("Actualizando horario ID: {}", id);

        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_HORARIO, "id", id));

        // Validar traslape si cambió el día u horario
        if (requestDTO.getDiaSemana() != null || requestDTO.getHoraInicio() != null || requestDTO.getHoraFin() != null) {
            validarTraslapeHorarios(horario.getVeterinario(), requestDTO, id);
        }

        horarioMapper.updateEntityFromDTO(requestDTO, horario);
        Horario horarioActualizado = horarioRepository.save(horario);
        log.info("Horario actualizado exitosamente");
        return horarioMapper.toResponseDTO(horarioActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public HorarioResponseDTO buscarPorId(Long id) {
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_HORARIO, "id", id));
        return horarioMapper.toResponseDTO(horario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponseDTO> listarTodos() {
        List<Horario> horarios = horarioRepository.findAll();
        return horarioMapper.toResponseDTOList(horarios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponseDTO> listarActivos() {
        List<Horario> horarios = horarioRepository.findByActivo(true);
        return horarioMapper.toResponseDTOList(horarios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponseDTO> listarPorVeterinario(Long idVeterinario) {
        Veterinario veterinario = veterinarioRepository.findById(idVeterinario)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_VETERINARIO, "id", idVeterinario));

        List<Horario> horarios = horarioRepository.findHorariosActivosPorVeterinario(veterinario);
        return horarioMapper.toResponseDTOList(horarios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponseDTO> listarPorDiaSemana(DayOfWeek diaSemana) {
        List<Horario> horarios = horarioRepository.findHorariosActivosPorDia(diaSemana.toString());
        return horarioMapper.toResponseDTOList(horarios);
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando horario ID: {}", id);
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_HORARIO, "id", id));

        horario.setActivo(false);
        horarioRepository.save(horario);
        log.info("Horario desactivado exitosamente");
    }

    @Override
    public HorarioResponseDTO activar(Long id) {
        log.info("Activando horario ID: {}", id);
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_HORARIO, "id", id));

        horario.activar();
        Horario horarioActivado = horarioRepository.save(horario);
        return horarioMapper.toResponseDTO(horarioActivado);
    }

    @Override
    public HorarioResponseDTO desactivar(Long id) {
        log.info("Desactivando horario ID: {}", id);
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_HORARIO, "id", id));

        horario.desactivar();
        Horario horarioDesactivado = horarioRepository.save(horario);
        return horarioMapper.toResponseDTO(horarioDesactivado);
    }

    private void validarTraslapeHorarios(Veterinario veterinario, HorarioRequestDTO requestDTO) {
        validarTraslapeHorarios(veterinario, requestDTO, null);
    }

    private void validarTraslapeHorarios(Veterinario veterinario, HorarioRequestDTO requestDTO, Long idExcluir) {
        List<Horario> horariosExistentes = horarioRepository.findByVeterinario(veterinario);

        for (Horario horarioExistente : horariosExistentes) {
            if (debeExcluirHorario(horarioExistente, idExcluir)) {
                continue;
            }

            // Crear un horario temporal para validar traslape
            Horario horarioTemporal = Horario.builder()
                    .diaSemana(requestDTO.getDiaSemana() != null 
                            ? DayOfWeek.valueOf(requestDTO.getDiaSemana()) 
                            : horarioExistente.getDiaSemana())
                    .horaInicio(requestDTO.getHoraInicio() != null 
                            ? requestDTO.getHoraInicio() 
                            : horarioExistente.getHoraInicio())
                    .horaFin(requestDTO.getHoraFin() != null 
                            ? requestDTO.getHoraFin() 
                            : horarioExistente.getHoraFin())
                    .build();

            if (horarioTemporal.seTraslapa(horarioExistente)) {
                throw new ValidationException(
                        "El horario se traslapa con otro horario existente: " + horarioExistente.getDescripcion(),
                        "horario",
                        "No se pueden tener horarios traslapados para el mismo veterinario"
                );
            }
        }
    }

    private boolean debeExcluirHorario(Horario horarioExistente, Long idExcluir) {
        if (idExcluir != null && horarioExistente.getIdHorario().equals(idExcluir)) {
            return true; // Excluir el horario que se está actualizando
        }
        return horarioExistente.getActivo() == null || !horarioExistente.getActivo(); // Ignorar horarios inactivos
    }
}

