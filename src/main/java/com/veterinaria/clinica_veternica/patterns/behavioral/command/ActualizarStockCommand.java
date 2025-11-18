package com.veterinaria.clinica_veternica.patterns.behavioral.command;

import com.veterinaria.clinica_veternica.domain.inventario.Inventario;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Patrón Command: ActualizarStockCommand
 *
 * Comando para actualizar el stock de inventario.
 * Encapsula la operación de actualización y permite deshacerla.
 *
 * Justificación:
 * - Encapsula la actualización de stock como objeto
 * - Permite deshacer la actualización si es necesario
 * - Facilita auditoría y logging de cambios de inventario
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ActualizarStockCommand implements Command {

    private final InventarioRepository inventarioRepository;
    private Long inventarioId;
    private Integer cantidadAnterior;
    private Integer cantidadNueva;

    public ActualizarStockCommand setInventarioId(Long inventarioId) {
        this.inventarioId = inventarioId;
        return this;
    }

    public ActualizarStockCommand setCantidadNueva(Integer cantidadNueva) {
        this.cantidadNueva = cantidadNueva;
        return this;
    }

    @Override
    public void ejecutar() throws ValidationException, BusinessException, IllegalStateException {
        if (inventarioId == null) {
            throw new IllegalStateException("El ID del inventario no puede ser nulo");
        }

        Inventario inventario = inventarioRepository.findById(inventarioId)
                .orElseThrow(() -> new ValidationException("Inventario no encontrado: " + inventarioId, "inventarioId", "Inventario no existe"));

        log.info("Ejecutando comando: Actualizar stock de inventario ID: {} de {} a {}", 
                inventarioId, inventario.getCantidadActual(), cantidadNueva);

        cantidadAnterior = inventario.getCantidadActual();
        inventario.setCantidadActual(cantidadNueva);
        inventarioRepository.save(inventario);

        log.info("Stock actualizado exitosamente");
    }

    @Override
    public void deshacer() throws ValidationException, BusinessException, IllegalStateException {
        if (inventarioId == null || cantidadAnterior == null) {
            throw new IllegalStateException("No hay actualización de stock para deshacer");
        }

        Inventario inventario = inventarioRepository.findById(inventarioId)
                .orElseThrow(() -> new ValidationException("Inventario no encontrado: " + inventarioId, "inventarioId", "Inventario no existe"));

        log.info("Deshaciendo comando: Restaurando stock de inventario ID: {} a {}", 
                inventarioId, cantidadAnterior);

        inventario.setCantidadActual(cantidadAnterior);
        inventarioRepository.save(inventario);

        log.info("Actualización de stock deshecha exitosamente");
    }

    @Override
    public String getDescripcion() {
        return "Actualizar stock de inventario ID: " + (inventarioId != null ? inventarioId : "N/A");
    }
}

