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

        // Mapear DTO a entidad (solo campos básicos)
        EvolucionClinica evolucion = evolucionClinicaMapper.toEntity(requestDTO);

        // Establecer relaciones
        evolucion.setHistoriaClinica(historiaClinica);
        evolucion.setVeterinario(veterinario);

        // Guardar motivoConsulta y hallazgosExamen por separado
        if (requestDTO.getMotivoConsulta() != null && !requestDTO.getMotivoConsulta().isBlank()) {
            evolucion.setMotivoConsulta(requestDTO.getMotivoConsulta().trim());
        }
        
        if (requestDTO.getHallazgosExamen() != null && !requestDTO.getHallazgosExamen().isBlank()) {
            evolucion.setHallazgosExamen(requestDTO.getHallazgosExamen().trim());
        }

        // Combinar motivoConsulta y hallazgosExamen en descripcion para compatibilidad
        String motivo = requestDTO.getMotivoConsulta() != null ? requestDTO.getMotivoConsulta() : "";
        String hallazgos = requestDTO.getHallazgosExamen() != null ? requestDTO.getHallazgosExamen() : "";
        String descripcion = String.format("Motivo de consulta: %s. Hallazgos del examen: %s", motivo, hallazgos);
        evolucion.setDescripcion(descripcion);

        // Establecer tipo de evolución
        evolucion.setTipoEvolucion(requestDTO.getTipoEvolucion());

        // Establecer plan de tratamiento si existe
        if (requestDTO.getPlanTratamiento() != null && !requestDTO.getPlanTratamiento().isBlank()) {
            evolucion.setPlan(requestDTO.getPlanTratamiento());
        }

        // Establecer observaciones si existen
        if (requestDTO.getObservaciones() != null && !requestDTO.getObservaciones().isBlank()) {
            evolucion.setObservaciones(requestDTO.getObservaciones());
        }

        // Establecer diagnóstico si existe
        if (requestDTO.getDiagnostico() != null && !requestDTO.getDiagnostico().isBlank()) {
            evolucion.setDiagnostico(requestDTO.getDiagnostico());
        }

        // Establecer signos vitales solo si tienen valores válidos
        if (requestDTO.getPeso() != null && requestDTO.getPeso() > 0) {
            evolucion.setPeso(requestDTO.getPeso());
        }

        if (requestDTO.getTemperatura() != null && requestDTO.getTemperatura() >= 35.0) {
            evolucion.setTemperatura(requestDTO.getTemperatura());
        }

        if (requestDTO.getFrecuenciaCardiaca() != null && requestDTO.getFrecuenciaCardiaca() >= 20) {
            evolucion.setFrecuenciaCardiaca(requestDTO.getFrecuenciaCardiaca());
        }

        if (requestDTO.getFrecuenciaRespiratoria() != null && requestDTO.getFrecuenciaRespiratoria() >= 5) {
            evolucion.setFrecuenciaRespiratoria(requestDTO.getFrecuenciaRespiratoria());
        }

        // Establecer fecha de evolución (por defecto ahora)
        evolucion.setFechaEvolucion(LocalDateTime.now());

        // Establecer estado del paciente por defecto
        evolucion.setEstadoPaciente("ESTABLE");

        // Establecer valores por defecto
        evolucion.setActivo(true);
        evolucion.setEsAlta(false);

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

