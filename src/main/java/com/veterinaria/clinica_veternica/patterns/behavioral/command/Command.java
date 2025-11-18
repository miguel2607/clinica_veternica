package com.veterinaria.clinica_veternica.patterns.behavioral.command;

import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ValidationException;

/**
 * Patrón Command: Command (Interface)
 *
 * Define la interfaz para encapsular una solicitud como un objeto.
 * Permite parametrizar clientes con diferentes solicitudes, encolar solicitudes,
 * y soportar operaciones reversibles (undo).
 *
 * Justificación:
 * - Encapsula operaciones como objetos
 * - Permite deshacer operaciones (undo)
 * - Facilita auditoría y logging
 * - Permite encolar y programar comandos
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
public interface Command {

    /**
     * Ejecuta el comando.
     *
     * @throws ValidationException Si la validación falla
     * @throws BusinessException Si hay un error de negocio
     * @throws IllegalStateException Si el estado del comando no permite la ejecución
     */
    void ejecutar() throws ValidationException, BusinessException, IllegalStateException;

    /**
     * Deshace el comando (si es reversible).
     *
     * @throws ValidationException Si la validación falla
     * @throws BusinessException Si hay un error de negocio
     * @throws IllegalStateException Si el estado del comando no permite la reversión
     */
    void deshacer() throws ValidationException, BusinessException, IllegalStateException;

    /**
     * Obtiene la descripción del comando.
     *
     * @return Descripción del comando
     */
    String getDescripcion();
}

