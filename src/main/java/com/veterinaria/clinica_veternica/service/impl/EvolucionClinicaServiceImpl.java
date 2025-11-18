package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.clinico.EvolucionClinica;
import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.dto.request.clinico.EvolucionClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.EvolucionClinicaResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.mapper.clinico.EvolucionClinicaMapper;
import com.veterinaria.clinica_veternica.repository.EvolucionClinicaRepository;
import com.veterinaria.clinica_veternica.repository.HistoriaClinicaRepository;
import com.veterinaria.clinica_veternica.repository.VeterinarioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IEvolucionClinicaService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio para gestión de Evoluciones Clínicas.
 * Sigue los principios SOLID:
 * - SRP: Responsabilidad única de gestionar evoluciones clínicas
 * - DIP: Depende de abstracciones (repositorios, mappers)
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EvolucionClinicaServiceImpl implements IEvolucionClinicaService {

    private final EvolucionClinicaRepository evolucionClinicaRepository;
    private final HistoriaClinicaRepository historiaClinicaRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final EvolucionClinicaMapper evolucionClinicaMapper;

    @Override
    public EvolucionClinicaResponseDTO crear(Long idHistoriaClinica, EvolucionClinicaRequestDTO requestDTO) {
        log.info("Creando nueva evolución clínica para historia clínica ID: {}", idHistoriaClinica);

        // Validar que la historia clínica existe
        HistoriaClinica historiaClinica = historiaClinicaRepository.findById(idHistoriaClinica)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_HISTORIA_CLINICA, "id", idHistoriaClinica));

        // Validar que el veterinario existe
        Veterinario veterinario = veterinarioRepository.findById(requestDTO.getIdVeterinario())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_VETERINARIO, "id", requestDTO.getIdVeterinario()));

        // Mapear DTO a entidad
        EvolucionClinica evolucion = evolucionClinicaMapper.toEntity(requestDTO);
        
        // Establecer relaciones
        evolucion.setHistoriaClinica(historiaClinica);
        evolucion.setVeterinario(veterinario);
        
        // Combinar motivoConsulta y hallazgosExamen en descripcion
        String descripcion = String.format("Motivo de consulta: %s. Hallazgos del examen: %s",
                requestDTO.getMotivoConsulta(), requestDTO.getHallazgosExamen());
        evolucion.setDescripcion(descripcion);
        
        // Establecer plan de tratamiento si existe
        if (requestDTO.getPlanTratamiento() != null && !requestDTO.getPlanTratamiento().isBlank()) {
            evolucion.setPlan(requestDTO.getPlanTratamiento());
        }
        
        // Establecer fecha de evolución (por defecto ahora)
        if (evolucion.getFechaEvolucion() == null) {
            evolucion.setFechaEvolucion(LocalDateTime.now());
        }
        
        // Establecer estado del paciente por defecto si no se proporciona
        if (evolucion.getEstadoPaciente() == null || evolucion.getEstadoPaciente().isBlank()) {
            evolucion.setEstadoPaciente("ESTABLE");
        }

        EvolucionClinica evolucionGuardada = evolucionClinicaRepository.save(evolucion);
        log.info("Evolución clínica creada exitosamente con ID: {}", evolucionGuardada.getIdEvolucion());
        
        return evolucionClinicaMapper.toResponseDTO(evolucionGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvolucionClinicaResponseDTO> listarPorHistoriaClinica(Long idHistoriaClinica) {
        log.info("Listando evoluciones clínicas para historia clínica ID: {}", idHistoriaClinica);

        HistoriaClinica historiaClinica = historiaClinicaRepository.findById(idHistoriaClinica)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_HISTORIA_CLINICA, "id", idHistoriaClinica));

        List<EvolucionClinica> evoluciones = evolucionClinicaRepository.findByHistoriaOrdenadas(historiaClinica);
        return evolucionClinicaMapper.toResponseDTOList(evoluciones);
    }
}

