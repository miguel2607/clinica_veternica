package com.veterinaria.clinica_veternica.util;

import java.util.Arrays;

/**
 * Utilidad para parsear nombres completos y extraer nombres y apellidos.
 * Elimina código duplicado en múltiples servicios (DRY principle).
 *
 * @author Sistema de Gestión Clínica Veterinaria
 * @version 1.0
 */
public final class NameParser {

    private NameParser() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad y no puede ser instanciada");
    }

    /**
     * Extrae nombres y apellidos de un nombre completo.
     *
     * Lógica:

     * - Si tiene 2 partes: primera es nombres, segunda es apellidos
     * - Si tiene 3+ partes: primeras 2 son nombres, resto son apellidos
     *
     * @param nombreCompleto El nombre completo a parsear
     * @return NameParts con nombres y apellidos separados
     */
    public static NameParts extractNamesAndLastNames(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            return new NameParts("", "");
        }

        String[] partes = nombreCompleto.trim().split("\\s+");


        if (partes.length == 1) {
            return new NameParts(partes[0], "");
        }

        // 2 partes: primera nombres, segunda apellidos
        if (partes.length == 2) {
            return new NameParts(partes[0], partes[1]);
        }

        // 3 o más partes: primeras 2 son nombres, resto apellidos
        int mitad = Math.min(2, partes.length / 2);
        String nombres = String.join(" ", Arrays.copyOfRange(partes, 0, mitad));
        String apellidos = String.join(" ", Arrays.copyOfRange(partes, mitad, partes.length));

        return new NameParts(nombres, apellidos);
    }

    /**
     * Record para encapsular el resultado del parseo de nombres.
     */
    public record NameParts(String nombres, String apellidos) {
        /**
         * Constructor que valida que no sean null.
         */
        public NameParts {
            if (nombres == null) {
                nombres = "";
            }
            if (apellidos == null) {
                apellidos = "";
            }
        }
    }
}
