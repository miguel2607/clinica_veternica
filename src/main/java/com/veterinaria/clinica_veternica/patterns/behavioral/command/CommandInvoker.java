package com.veterinaria.clinica_veternica.patterns.behavioral.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Patrón Command: CommandInvoker
 *
 * Invocador que ejecuta comandos y mantiene un historial para deshacer.
 *
 * Justificación:
 * - Centraliza la ejecución de comandos
 * - Mantiene historial para operaciones undo
 * - Facilita auditoría y logging de operaciones
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class CommandInvoker {

    private final Deque<Command> historialComandos = new ArrayDeque<>();

    /**
     * Ejecuta un comando y lo agrega al historial.
     *
     * @param comando Comando a ejecutar
     * @throws RuntimeException Si la ejecución falla
     */
    public void ejecutarComando(Command comando) throws RuntimeException {
        log.info("Ejecutando comando: {}", comando.getDescripcion());
        comando.ejecutar();
        historialComandos.push(comando);
        log.info("Comando ejecutado exitosamente. Historial: {} comandos", historialComandos.size());
    }

    /**
     * Deshace el último comando ejecutado.
     *
     * @throws RuntimeException Si la reversión falla
     */
    public void deshacerUltimoComando() throws RuntimeException {
        if (historialComandos.isEmpty()) {
            log.warn("No hay comandos para deshacer");
            return;
        }

        Command ultimoComando = historialComandos.pop();
        log.info("Deshaciendo comando: {}", ultimoComando.getDescripcion());
        ultimoComando.deshacer();
        log.info("Comando deshecho exitosamente. Historial: {} comandos", historialComandos.size());
    }

    /**
     * Obtiene el tamaño del historial de comandos.
     *
     * @return Número de comandos en el historial
     */
    public int getTamanioHistorial() {
        return historialComandos.size();
    }

    /**
     * Limpia el historial de comandos.
     */
    public void limpiarHistorial() {
        log.info("Limpiando historial de comandos");
        historialComandos.clear();
    }
}

