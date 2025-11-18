package com.veterinaria.clinica_veternica.util;

import com.veterinaria.clinica_veternica.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BooleanSupplier;

/**
 * Helper Component para validaciones reutilizables.
 * Elimina código duplicado en servicios (Cut & Paste Programming antipattern).
 *
 * <p>Proporciona métodos genéricos para validaciones comunes:</p>
 * <ul>
 *   <li>Validación de unicidad de campos</li>
 *   <li>Sanitización de entradas</li>
 *   <li>Validación de longitud de strings</li>
 *   <li>Establecer valores por defecto</li>
 * </ul>
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-17
 */
@Slf4j
@Component
public class ValidationHelper {

    /**
     * Valida que un campo sea único.
     * Si el campo ya existe, lanza ValidationException.
     *
     * @param existsChecker BooleanSupplier que retorna true si el campo ya existe
     * @param fieldName Nombre del campo para el mensaje de error
     * @param fieldValue Valor del campo para el mensaje de error
     * @throws ValidationException si el campo ya existe
     */
    public void validateUnique(BooleanSupplier existsChecker, String fieldName, Object fieldValue) {
        if (existsChecker.getAsBoolean()) {
            String message = String.format("Ya existe un registro con el %s: %s", fieldName, fieldValue);
            log.warn("Validación de unicidad falló para {}: {}", fieldName, fieldValue);
            throw new ValidationException(message, fieldName, "El campo ya está registrado");
        }
    }

    /**
     * Valida que un campo sea único al actualizar (excluyendo el registro actual).
     *
     * @param currentValue Valor actual del registro
     * @param newValue Nuevo valor a validar
     * @param existsChecker BooleanSupplier que retorna true si el nuevo valor ya existe
     * @param fieldName Nombre del campo para el mensaje de error
     * @throws ValidationException si el campo ya existe en otro registro
     */
    public void validateUniqueOnUpdate(Object currentValue, Object newValue,
                                       BooleanSupplier existsChecker, String fieldName) {
        if (!currentValue.equals(newValue) && existsChecker.getAsBoolean()) {
            String message = String.format("Ya existe otro registro con el %s: %s", fieldName, newValue);
            log.warn("Validación de unicidad en actualización falló para {}: {}", fieldName, newValue);
            throw new ValidationException(message, fieldName, "El campo ya está registrado");
        }
    }

    /**
     * Sanitiza una cadena de entrada removiendo caracteres peligrosos.
     * Previene inyección y otros ataques.
     *
     * @param input Cadena a sanitizar
     * @return Cadena sanitizada
     */
    public String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // Remover caracteres potencialmente peligrosos
        return input.replaceAll("[<>\"';()&+%]", "").trim();
    }

    /**
     * Valida que una cadena no exceda la longitud máxima.
     *
     * @param value Valor a validar
     * @param maxLength Longitud máxima permitida
     * @param fieldName Nombre del campo para el mensaje de error
     * @throws ValidationException si la cadena excede la longitud máxima
     */
    public void validateMaxLength(String value, int maxLength, String fieldName) {
        if (value != null && value.length() > maxLength) {
            String message = String.format("El campo %s excede la longitud máxima de %d caracteres",
                    fieldName, maxLength);
            log.warn("Validación de longitud falló para {}: {} caracteres (máx: {})",
                    fieldName, value.length(), maxLength);
            throw new ValidationException(message, fieldName,
                    String.format("Máximo %d caracteres permitidos", maxLength));
        }
    }

    /**
     * Valida que una cadena no esté vacía.
     *
     * @param value Valor a validar
     * @param fieldName Nombre del campo para el mensaje de error
     * @throws ValidationException si la cadena está vacía o es null
     */
    public void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            String message = String.format("El campo %s no puede estar vacío", fieldName);
            log.warn("Validación de campo vacío falló para: {}", fieldName);
            throw new ValidationException(message, fieldName, "Campo requerido");
        }
    }

    /**
     * Establece un valor por defecto si el valor actual es null.
     *
     * @param currentValue Valor actual
     * @param defaultValue Valor por defecto
     * @param <T> Tipo del valor
     * @return El valor actual si no es null, el valor por defecto en caso contrario
     */
    public <T> T setDefaultIfNull(T currentValue, T defaultValue) {
        return currentValue != null ? currentValue : defaultValue;
    }

    /**
     * Valida y sanitiza una cadena de búsqueda.
     * Combina validaciones de longitud y sanitización.
     *
     * @param searchTerm Término de búsqueda
     * @param maxLength Longitud máxima permitida
     * @return Término de búsqueda sanitizado
     * @throws ValidationException si la validación falla
     */
    public String validateAndSanitizeSearchTerm(String searchTerm, int maxLength) {
        validateNotEmpty(searchTerm, "término de búsqueda");
        validateMaxLength(searchTerm, maxLength, "término de búsqueda");
        return sanitizeInput(searchTerm);
    }

    /**
     * Valida que un documento (tipo + número) sea único.
     * Método específico para validar unicidad de documentos de propietarios.
     *
     * @param existsChecker BooleanSupplier que verifica si el documento ya existe
     * @param tipoDocumento Tipo de documento (DNI, PASAPORTE, etc.)
     * @param numeroDocumento Número del documento
     * @throws ValidationException si el documento ya existe
     */
    public void validateDocumentUnique(BooleanSupplier existsChecker, String tipoDocumento, String numeroDocumento) {
        if (existsChecker.getAsBoolean()) {
            String message = String.format("Ya existe un propietario con el documento %s %s",
                    tipoDocumento, numeroDocumento);
            log.warn("Validación de unicidad de documento falló: {} {}", tipoDocumento, numeroDocumento);
            throw new ValidationException(message, "documento", "El documento ya está registrado");
        }
    }

    /**
     * Valida que una raza pertenezca a una especie específica.
     * Método específico para validar relación Raza-Especie en mascotas.
     *
     * @param razaEspecieId ID de la especie de la raza
     * @param especieId ID de la especie esperada
     * @param razaNombre Nombre de la raza (para mensaje de error)
     * @param especieNombre Nombre de la especie (para mensaje de error)
     * @throws ValidationException si la raza no pertenece a la especie
     */
    public void validateRazaBelongsToSpecies(Long razaEspecieId, Long especieId,
                                             String razaNombre, String especieNombre) {
        if (!razaEspecieId.equals(especieId)) {
            String message = String.format("La raza '%s' no pertenece a la especie '%s'",
                    razaNombre, especieNombre);
            log.warn("Validación de raza-especie falló: Raza {} no pertenece a Especie {}",
                    razaNombre, especieNombre);
            throw new ValidationException(message, "idRaza",
                    "La raza no corresponde a la especie seleccionada");
        }
    }
}
