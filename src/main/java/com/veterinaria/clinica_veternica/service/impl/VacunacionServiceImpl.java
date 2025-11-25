package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import com.veterinaria.clinica_veternica.domain.clinico.Vacunacion;
import com.veterinaria.clinica_veternica.domain.inventario.Insumo;
import com.veterinaria.clinica_veternica.domain.inventario.Inventario;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.dto.request.clinico.VacunacionRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.VacunacionResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.clinico.VacunacionMapper;
import com.veterinaria.clinica_veternica.repository.HistoriaClinicaRepository;
import com.veterinaria.clinica_veternica.repository.InsumoRepository;
import com.veterinaria.clinica_veternica.repository.InventarioRepository;
import com.veterinaria.clinica_veternica.repository.VacunacionRepository;
import com.veterinaria.clinica_veternica.repository.VeterinarioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IInsumoService;
import com.veterinaria.clinica_veternica.service.interfaces.IVacunacionService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementación del servicio para gestión de Vacunaciones.
 * Sigue los principios SOLID:
 * - SRP: Responsabilidad única de gestionar vacunaciones
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
public class VacunacionServiceImpl implements IVacunacionService {

    private final VacunacionRepository vacunacionRepository;
    private final HistoriaClinicaRepository historiaClinicaRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final InsumoRepository insumoRepository;
    private final InventarioRepository inventarioRepository;
    private final IInsumoService insumoService;
    private final VacunacionMapper vacunacionMapper;

    @Override
    public VacunacionResponseDTO crear(Long idHistoriaClinica, VacunacionRequestDTO requestDTO) {
        log.info("Creando nueva vacunación para historia clínica ID: {}", idHistoriaClinica);

        // Validar que la historia clínica existe
        HistoriaClinica historiaClinica = historiaClinicaRepository.findById(idHistoriaClinica)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_HISTORIA_CLINICA, "id", idHistoriaClinica));

        // Validar que el veterinario existe
        Veterinario veterinario = veterinarioRepository.findById(requestDTO.getIdVeterinario())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_VETERINARIO, "id", requestDTO.getIdVeterinario()));

        // Mapear DTO a entidad
        Vacunacion vacunacion = vacunacionMapper.toEntity(requestDTO);
        
        // Establecer relaciones
        vacunacion.setHistoriaClinica(historiaClinica);
        vacunacion.setMascota(historiaClinica.getMascota());
        vacunacion.setVeterinario(veterinario);
        
        // Establecer tipo de vacuna por defecto si no se proporciona
        if (vacunacion.getTipoVacuna() == null || vacunacion.getTipoVacuna().isBlank()) {
            String tipoVacuna = inferirTipoVacuna(requestDTO.getNombreVacuna());
            vacunacion.setTipoVacuna(tipoVacuna);
            log.debug("Tipo de vacuna inferido: {} para vacuna: {}", tipoVacuna, requestDTO.getNombreVacuna());
        }
        
        // Establecer enfermedades prevenidas por defecto si no se proporciona
        if (vacunacion.getEnfermedadesPrevenidas() == null || vacunacion.getEnfermedadesPrevenidas().isBlank()) {
            String enfermedades = obtenerEnfermedadesPorTipoVacuna(requestDTO.getNombreVacuna());
            vacunacion.setEnfermedadesPrevenidas(enfermedades);
        }
        
        // Establecer vía de administración por defecto si no se proporciona
        if (vacunacion.getViaAdministracion() == null || vacunacion.getViaAdministracion().isBlank()) {
            vacunacion.setViaAdministracion("SUBCUTANEA");
            log.debug("Vía de administración establecida por defecto: SUBCUTANEA");
        }
        
        // Establecer sitio de aplicación si no se proporciona
        if (vacunacion.getSitioAplicacion() == null || vacunacion.getSitioAplicacion().isBlank()) {
            vacunacion.setSitioAplicacion("Cuello");
        }
        
        // Establecer esquema completo si no se proporciona
        if (vacunacion.getEsquemaCompleto() == null) {
            vacunacion.setEsquemaCompleto(!Boolean.FALSE.equals(requestDTO.getEsquemaCompleto()));
        }
        
        // Establecer número de dosis si no se proporciona
        if (vacunacion.getNumeroDosis() == null) {
            vacunacion.setNumeroDosis(1);
        }
        
        // Establecer total de dosis en esquema si no se proporciona
        if (vacunacion.getTotalDosisEsquema() == null) {
            vacunacion.setTotalDosisEsquema(1);
        }

        // Manejar insumo si se proporciona el ID
        Insumo insumo = prepararInsumoParaVacunacion(requestDTO);
        if (insumo != null) {
            consumirInsumo(insumo, requestDTO);
            vacunacion.setInsumo(insumo);
        }

        Vacunacion vacunacionGuardada = vacunacionRepository.save(vacunacion);
        log.info("Vacunación creada exitosamente con ID: {}", vacunacionGuardada.getIdVacunacion());
        
        return vacunacionMapper.toResponseDTO(vacunacionGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VacunacionResponseDTO> listarPorHistoriaClinica(Long idHistoriaClinica) {
        log.info("Listando vacunaciones para historia clínica ID: {}", idHistoriaClinica);

        HistoriaClinica historiaClinica = historiaClinicaRepository.findById(idHistoriaClinica)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_HISTORIA_CLINICA, "id", idHistoriaClinica));

        List<Vacunacion> vacunaciones = vacunacionRepository.findByHistoriaClinica(historiaClinica);
        return vacunacionMapper.toResponseDTOList(vacunaciones);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VacunacionResponseDTO> listarTodas() {
        log.info("Listando todas las vacunaciones");
        List<Vacunacion> vacunaciones = vacunacionRepository.findAll();
        return vacunacionMapper.toResponseDTOList(vacunaciones);
    }

    private Insumo prepararInsumoParaVacunacion(VacunacionRequestDTO requestDTO) {
        if (requestDTO.getIdInsumo() != null) {
            return insumoRepository.findById(requestDTO.getIdInsumo())
                    .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_INSUMO, "id", requestDTO.getIdInsumo()));
        }
        return null;
    }

    private void consumirInsumo(Insumo insumo, VacunacionRequestDTO requestDTO) {
        if (insumo.getActivo() == null || !insumo.getActivo()) {
            throw new ValidationException(
                    "El insumo " + insumo.getNombre() + " no está activo",
                    "idInsumo",
                    "El insumo debe estar activo para ser utilizado"
            );
        }

        int cantidadUsada = (requestDTO.getCantidadUsada() != null && requestDTO.getCantidadUsada() > 0)
                ? requestDTO.getCantidadUsada()
                : 1;

        if (insumo.getCantidadStock() == null || insumo.getCantidadStock() < cantidadUsada) {
            throw new ValidationException(
                    "Stock insuficiente para el insumo " + insumo.getNombre() +
                            ". Stock disponible: " + (insumo.getCantidadStock() != null ? insumo.getCantidadStock() : 0) +
                            ", cantidad requerida: " + cantidadUsada,
                    "idInsumo",
                    "Stock insuficiente"
            );
        }

        // Decrementar stock del insumo
        insumo.decrementarStock(cantidadUsada);

        if (insumo.getCantidadStock() == 0) {
            insumo.marcarComoAgotado();
        } else if (insumo.esStockBajo()) {
            insumo.marcarComoDisponible();
        }

        insumoRepository.save(insumo);
        log.info("Insumo ID: {} asociado a vacunación. Stock decrementado en {}. Stock restante: {}",
                insumo.getIdInsumo(), cantidadUsada, insumo.getCantidadStock());

        // Actualizar el inventario consolidado
        actualizarInventario(insumo, cantidadUsada);
    }

    /**
     * Actualiza el registro de inventario cuando se consume un insumo.
     * Si no existe un registro de inventario para el insumo, lo crea.
     *
     * @param insumo Insumo consumido
     * @param cantidadUsada Cantidad consumida
     */
    private void actualizarInventario(Insumo insumo, int cantidadUsada) {
        Inventario inventario = inventarioRepository.findByInsumo(insumo)
                .orElse(null);

        if (inventario == null) {
            // Crear nuevo registro de inventario si no existe
            inventario = Inventario.builder()
                    .insumo(insumo)
                    .cantidadActual(insumo.getCantidadStock())
                    .totalSalidas(cantidadUsada)
                    .build();
            log.info("Registro de inventario creado para insumo ID: {}", insumo.getIdInsumo());
        }

        // Calcular el valor de la salida (precio de compra * cantidad)
        BigDecimal valorSalida = null;
        if (insumo.getPrecioCompra() != null) {
            valorSalida = insumo.getPrecioCompra().multiply(BigDecimal.valueOf(cantidadUsada));
        }

        // Registrar la salida en el inventario
        inventario.registrarSalida(cantidadUsada, valorSalida);
        
        // Sincronizar cantidad actual con el stock del insumo
        inventario.setCantidadActual(insumo.getCantidadStock());

        inventarioRepository.save(inventario);
        log.info("Inventario actualizado para insumo ID: {}. Salida registrada: {} unidades. Stock actual: {}",
                insumo.getIdInsumo(), cantidadUsada, inventario.getCantidadActual());
    }

    /**
     * Infiere el tipo de vacuna basándose en el nombre.
     * Valores posibles: VIRAL, BACTERIANA, POLIVALENTE, ANTIRRABICA, OTRA
     */
    private String inferirTipoVacuna(String nombreVacuna) {
        if (nombreVacuna == null || nombreVacuna.isBlank()) {
            return "OTRA";
        }
        
        String nombreLower = nombreVacuna.toLowerCase();
        if (nombreLower.contains("antirrábica") || nombreLower.contains("antirrabica") || nombreLower.contains("rabia")) {
            return "ANTIRRABICA";
        } else if (nombreLower.contains("polivalente") || nombreLower.contains("múltiple") || nombreLower.contains("multiple")) {
            return "POLIVALENTE";
        } else if (nombreLower.contains("leptospirosis") || nombreLower.contains("bordetella")) {
            return "BACTERIANA";
        } else if (nombreLower.contains("moquillo") || nombreLower.contains("parvovirus") || 
                   nombreLower.contains("hepatitis") || nombreLower.contains("parainfluenza")) {
            return "VIRAL";
        } else {
            return "OTRA";
        }
    }

    /**
     * Obtiene las enfermedades prevenidas según el tipo de vacuna.
     */
    private String obtenerEnfermedadesPorTipoVacuna(String nombreVacuna) {
        if (nombreVacuna == null || nombreVacuna.isBlank()) {
            return "Enfermedades prevenidas según tipo de vacuna";
        }
        
        String nombreLower = nombreVacuna.toLowerCase();
        if (nombreLower.contains("antirrábica") || nombreLower.contains("antirrabica")) {
            return "Rabia";
        } else if (nombreLower.contains("polivalente") || nombreLower.contains("múltiple")) {
            return "Moquillo, Parvovirus, Hepatitis, Parainfluenza";
        } else if (nombreLower.contains("leptospirosis")) {
            return "Leptospirosis";
        } else if (nombreLower.contains("hepatitis")) {
            return "Hepatitis canina";
        } else {
            return "Enfermedades prevenidas según tipo de vacuna";
        }
    }
}

