package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.agenda.CategoriaServicio;
import com.veterinaria.clinica_veternica.domain.agenda.TipoServicio;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import com.veterinaria.clinica_veternica.dto.request.agenda.ServicioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.ServicioResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.agenda.ServicioMapper;
import com.veterinaria.clinica_veternica.patterns.creational.factory.ServicioFactory;
import com.veterinaria.clinica_veternica.repository.ServicioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IServicioService;
import com.veterinaria.clinica_veternica.service.registry.ServicioFactoryRegistry;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementación del servicio para gestión de Servicios.
 * Utiliza el patrón Factory Method para crear servicios según su categoría.
 * Elimina antipatrón de lazy initialization usando ServicioFactoryRegistry.
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ServicioServiceImpl implements IServicioService {

    private final ServicioRepository servicioRepository;
    private final ServicioMapper servicioMapper;
    private final ServicioFactoryRegistry factoryRegistry;

    @Override
    public ServicioResponseDTO crear(ServicioRequestDTO requestDTO) {
        log.info("Creando nuevo servicio: {}", requestDTO.getNombre());

        // Validar que no exista un servicio con el mismo nombre
        if (servicioRepository.existsByNombre(requestDTO.getNombre())) {
            throw new ValidationException(
                    "Ya existe un servicio con el nombre: " + requestDTO.getNombre(),
                    "nombre",
                    "El nombre del servicio ya está registrado"
            );
        }

        Servicio servicio = servicioMapper.toEntity(requestDTO);

        if (servicio.getActivo() == null) {
            servicio.setActivo(true);
        }

        // Si la categoría es EMERGENCIA o el tipo es EMERGENCIA, habilitar disponibilidad para emergencias
        if (servicio.getCategoria() == CategoriaServicio.EMERGENCIA ||
            servicio.getTipoServicio() == TipoServicio.EMERGENCIA) {
            servicio.setDisponibleEmergencias(true);
            log.info("Servicio de emergencia detectado, habilitando disponibilidad para emergencias");
        }

        Servicio servicioGuardado = servicioRepository.save(servicio);
        log.info("Servicio creado exitosamente con ID: {}", servicioGuardado.getIdServicio());
        return servicioMapper.toResponseDTO(servicioGuardado);
    }

    @Override
    public ServicioResponseDTO crearConFactory(String nombre, String descripcion, BigDecimal precio, String categoria) {
        log.info("Creando servicio con Factory: {} - Categoría: {}", nombre, categoria);

        CategoriaServicio categoriaEnum = CategoriaServicio.valueOf(categoria.toUpperCase());
        ServicioFactory factory = factoryRegistry.obtenerFactory(categoriaEnum);

        Servicio servicio = factory.crearServicioCompleto(nombre, descripcion, precio);
        servicio.setActivo(true);

        Servicio servicioGuardado = servicioRepository.save(servicio);
        log.info("Servicio creado con Factory exitosamente con ID: {}", servicioGuardado.getIdServicio());
        return servicioMapper.toResponseDTO(servicioGuardado);
    }

    @Override
    public ServicioResponseDTO actualizar(Long id, ServicioRequestDTO requestDTO) {
        log.info("Actualizando servicio ID: {}", id);

        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_SERVICIO, "id", id));

        // Validar nombre único si cambió
        if (requestDTO.getNombre() != null 
                && !requestDTO.getNombre().equals(servicio.getNombre())
                && servicioRepository.existsByNombre(requestDTO.getNombre())) {
            throw new ValidationException(
                    "Ya existe otro servicio con el nombre: " + requestDTO.getNombre(),
                    "nombre",
                    "El nombre del servicio ya está registrado"
            );
        }

        servicioMapper.updateEntityFromDTO(requestDTO, servicio);
        
        // Si la categoría es EMERGENCIA o el tipo es EMERGENCIA, habilitar disponibilidad para emergencias
        if (servicio.getCategoria() == CategoriaServicio.EMERGENCIA ||
            servicio.getTipoServicio() == TipoServicio.EMERGENCIA) {
            servicio.setDisponibleEmergencias(true);
            log.info("Servicio de emergencia detectado, habilitando disponibilidad para emergencias");
        }
        
        Servicio servicioActualizado = servicioRepository.save(servicio);
        log.info("Servicio actualizado exitosamente");
        return servicioMapper.toResponseDTO(servicioActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public ServicioResponseDTO buscarPorId(Long id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_SERVICIO, "id", id));
        return servicioMapper.toResponseDTO(servicio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioResponseDTO> listarTodos() {
        List<Servicio> servicios = servicioRepository.findAll();
        return servicioMapper.toResponseDTOList(servicios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioResponseDTO> listarActivos() {
        List<Servicio> servicios = servicioRepository.findServiciosActivos();
        return servicioMapper.toResponseDTOList(servicios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioResponseDTO> listarPorTipo(String tipoServicio) {
        List<Servicio> servicios = servicioRepository.findServiciosActivosPorTipo(tipoServicio);
        return servicioMapper.toResponseDTOList(servicios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioResponseDTO> listarPorCategoria(String categoria) {
        CategoriaServicio categoriaEnum = CategoriaServicio.valueOf(categoria.toUpperCase());
        List<Servicio> servicios = servicioRepository.findAll().stream()
                .filter(s -> s.getCategoria() == categoriaEnum && s.getActivo() != null && s.getActivo())
                .toList();
        return servicioMapper.toResponseDTOList(servicios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioResponseDTO> listarPorRangoPrecio(BigDecimal min, BigDecimal max) {
        List<Servicio> servicios = servicioRepository.findServiciosPorRangoPrecio(min, max);
        return servicioMapper.toResponseDTOList(servicios);
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando servicio ID: {}", id);
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_SERVICIO, "id", id));

        // Verificar si tiene citas asociadas
        if (servicio.getCantidadCitas() > 0) {
            throw new ValidationException(
                    "No se puede eliminar el servicio porque tiene " + servicio.getCantidadCitas() + " cita(s) asociada(s)",
                    "servicio",
                    "El servicio tiene citas asociadas"
            );
        }

        servicio.setActivo(false);
        servicioRepository.save(servicio);
        log.info("Servicio desactivado exitosamente");
    }

    @Override
    public ServicioResponseDTO activar(Long id) {
        log.info("Activando servicio ID: {}", id);
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_SERVICIO, "id", id));

        servicio.activar();
        Servicio servicioActivado = servicioRepository.save(servicio);
        return servicioMapper.toResponseDTO(servicioActivado);
    }

    @Override
    public ServicioResponseDTO desactivar(Long id) {
        log.info("Desactivando servicio ID: {}", id);
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_SERVICIO, "id", id));

        servicio.desactivar();
        Servicio servicioDesactivado = servicioRepository.save(servicio);
        return servicioMapper.toResponseDTO(servicioDesactivado);
    }
}

