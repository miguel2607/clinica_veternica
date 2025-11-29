package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.inventario.Inventario;
import com.veterinaria.clinica_veternica.domain.inventario.Insumo;
import com.veterinaria.clinica_veternica.dto.response.inventario.InventarioResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;

import com.veterinaria.clinica_veternica.mapper.inventario.InventarioMapper;
import com.veterinaria.clinica_veternica.patterns.structural.proxy.InventarioProxy;
import com.veterinaria.clinica_veternica.repository.InventarioRepository;
import com.veterinaria.clinica_veternica.repository.InsumoRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IInventarioService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementación del servicio para gestión de Inventario.
 * Utiliza el patrón Proxy para control de acceso y auditoría.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventarioServiceImpl implements IInventarioService {

    private final InventarioRepository inventarioRepository;
    private final InsumoRepository insumoRepository;
    private final InventarioMapper inventarioMapper;
    private final InventarioProxy inventarioProxy;

    @Override
    @Transactional(readOnly = true)
    public InventarioResponseDTO buscarPorId(Long id) {
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_INVENTARIO, "id", id));
        return inventarioMapper.toResponseDTO(inventario);
    }

    @Override
    @Transactional
    public InventarioResponseDTO buscarPorInsumo(Long idInsumo) {
        Insumo insumo = insumoRepository.findById(idInsumo)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_INSUMO, "id", idInsumo));

        Inventario inventario = obtenerOcrearInventario(insumo);
        return inventarioMapper.toResponseDTO(inventario);
    }

    @Override
    @Transactional
    public List<InventarioResponseDTO> listarTodos() {
        // Obtener todos los insumos activos
        List<Insumo> insumos = insumoRepository.findInsumosActivos();
        
        // Para cada insumo, asegurar que tenga un registro de inventario y sincronizar stock
        List<Inventario> inventarios = insumos.stream()
                .map(this::obtenerOcrearInventario)
                .toList();
        
        // Asegurar que todos los insumos estén cargados antes de mapear
        inventarios.forEach(inv -> {
            if (inv.getInsumo() != null) {
                // Forzar la carga del insumo accediendo a una propiedad
                inv.getInsumo().getNombre();
            }
        });
        
        return inventarioMapper.toResponseDTOList(inventarios);
    }

    /**
     * Obtiene el registro de inventario para un insumo, o lo crea si no existe.
     * Sincroniza la cantidad actual del inventario con el stock del insumo.
     *
     * @param insumo Insumo para el cual obtener o crear el inventario
     * @return Registro de inventario sincronizado
     */
    private Inventario obtenerOcrearInventario(Insumo insumo) {
        // Intentar obtener el inventario con el insumo cargado
        Inventario inventario = inventarioRepository.findByInsumoWithFetch(insumo)
                .orElse(null);

        if (inventario == null) {
            inventario = crearNuevoInventario(insumo);
        } else {
            inventario = sincronizarInventarioExistente(inventario, insumo);
        }
        
        // Asegurar que el insumo esté cargado y asociado (por si acaso)
        if (inventario.getInsumo() == null) {
            inventario.setInsumo(insumo);
        } else {
            // Forzar la carga del insumo accediendo a una propiedad
            inventario.getInsumo().getNombre();
        }

        return inventario;
    }

    /**
     * Crea un nuevo registro de inventario para un insumo.
     *
     * @param insumo Insumo para el cual crear el inventario
     * @return Registro de inventario creado
     */
    private Inventario crearNuevoInventario(Insumo insumo) {
        Integer stockInicial = insumo.getCantidadStock() != null ? insumo.getCantidadStock() : 0;
        
        Inventario inventario = Inventario.builder()
                .insumo(insumo)
                .cantidadActual(stockInicial)
                .totalEntradas(0)
                .totalSalidas(0)
                .valorEntradas(BigDecimal.ZERO)
                .valorSalidas(BigDecimal.ZERO)
                .requiereReorden(false)
                .build();
        
        calcularValorTotal(inventario, insumo, stockInicial);
        
        inventario = inventarioRepository.save(inventario);
        // Asegurar que el insumo esté asociado después de guardar
        inventario.setInsumo(insumo);
        log.debug("Registro de inventario creado para insumo ID: {} - {}", insumo.getIdInsumo(), insumo.getNombre());
        
        return inventario;
    }

    /**
     * Sincroniza un inventario existente con el stock del insumo.
     *
     * @param inventario Inventario existente
     * @param insumo Insumo relacionado
     * @return Inventario sincronizado
     */
    private Inventario sincronizarInventarioExistente(Inventario inventario, Insumo insumo) {
        Integer stockInsumo = insumo.getCantidadStock() != null ? insumo.getCantidadStock() : 0;
        
        if (!stockInsumo.equals(inventario.getCantidadActual())) {
            log.debug("Sincronizando stock del inventario para insumo ID: {}. Stock insumo: {}, Stock inventario: {}",
                    insumo.getIdInsumo(), stockInsumo, inventario.getCantidadActual());
            
            inventario.setCantidadActual(stockInsumo);
            calcularValorTotal(inventario, insumo, stockInsumo);
            actualizarIndicadorReorden(inventario, insumo, stockInsumo);
            
            inventario = inventarioRepository.save(inventario);
        }
        
        // Asegurar que el insumo esté asociado
        inventario.setInsumo(insumo);
        
        return inventario;
    }

    /**
     * Calcula y establece el valor total del inventario.
     *
     * @param inventario Inventario a actualizar
     * @param insumo Insumo relacionado
     * @param cantidad Cantidad de stock
     */
    private void calcularValorTotal(Inventario inventario, Insumo insumo, Integer cantidad) {
        if (insumo.getPrecioCompra() != null && cantidad != null) {
            BigDecimal valorTotal = insumo.getPrecioCompra().multiply(BigDecimal.valueOf(cantidad));
            inventario.setValorTotal(valorTotal);
        }
    }

    /**
     * Actualiza el indicador de reorden del inventario.
     *
     * @param inventario Inventario a actualizar
     * @param insumo Insumo relacionado
     * @param stockActual Stock actual del insumo
     */
    private void actualizarIndicadorReorden(Inventario inventario, Insumo insumo, Integer stockActual) {
        if (insumo.getStockMinimo() != null) {
            inventario.setRequiereReorden(stockActual <= insumo.getStockMinimo());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> listarConStockBajo() {
        List<Inventario> inventarios = inventarioRepository.findInventariosConStockBajo();
        return inventarioMapper.toResponseDTOList(inventarios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> listarAgotados() {
        List<Inventario> inventarios = inventarioRepository.findInventariosAgotados();
        return inventarioMapper.toResponseDTOList(inventarios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> listarOrdenadosPorValor() {
        List<Inventario> inventarios = inventarioRepository.findInventariosOrdenadosPorValor();
        return inventarioMapper.toResponseDTOList(inventarios);
    }
}

