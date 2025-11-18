package com.veterinaria.clinica_veternica.patterns.creational.factory;

import com.veterinaria.clinica_veternica.domain.agenda.CategoriaServicio;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Factory concreta para crear servicios de emergencia.
 *
 * Los servicios de emergencia son atención urgente que requiere
 * prioridad y disponibilidad inmediata.
 *
 * Características típicas:
 * - Duración: Variable (15-120 minutos)
 * - Pueden requerir anestesia (depende del caso)
 * - No requieren ayuno previo (urgencia)
 * - Precio con recargo de emergencia (50% adicional)
 * - Disponibles 24/7
 * - No disponibles a domicilio (requieren instalaciones)
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Component
public class ServicioEmergenciaFactory extends ServicioFactory {

    private static final int DURACION_DEFAULT_MINUTOS = 60;
    private static final BigDecimal PRECIO_BASE_DEFAULT = new BigDecimal("100000");
    private static final double RECARGO_EMERGENCIA = 1.5; // 50% adicional

    @Override
    public Servicio crearServicio(String nombre, String descripcion, BigDecimal precio) {
        // Aplicar recargo de emergencia al precio base
        BigDecimal precioConRecargo = precio != null 
            ? precio.multiply(BigDecimal.valueOf(RECARGO_EMERGENCIA))
            : PRECIO_BASE_DEFAULT.multiply(BigDecimal.valueOf(RECARGO_EMERGENCIA));

        return Servicio.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .categoria(CategoriaServicio.EMERGENCIA)
                .tipoServicio(com.veterinaria.clinica_veternica.domain.agenda.TipoServicio.CONSULTA_GENERAL)
                .precio(precioConRecargo)
                .duracionEstimadaMinutos(DURACION_DEFAULT_MINUTOS)
                .requiereAnestesia(false) // Se determina según el caso
                .requiereAyuno(false) // No hay tiempo para ayuno en emergencias
                .requiereHospitalizacion(true) // Las emergencias pueden requerir observación
                .requiereCuidadosEspeciales(true)
                .activo(true)
                .disponibleEmergencias(true)
                .disponibleDomicilio(false) // Requieren instalaciones de la clínica
                .build();
    }

    @Override
    protected void aplicarConfiguracionesEspecificas(Servicio servicio) {
        // Servicios de emergencia siempre están disponibles
        servicio.setDisponibleEmergencias(true);
        servicio.setActivo(true);

        // No disponibles a domicilio (requieren equipamiento de la clínica)
        servicio.setDisponibleDomicilio(false);

        // Pueden requerir hospitalización para observación
        servicio.setRequiereHospitalizacion(true);
        servicio.setRequiereCuidadosEspeciales(true);

        // No requieren ayuno previo (son emergencias)
        servicio.setRequiereAyuno(false);

        // Aplicar recargo de emergencia si no se aplicó en la creación
        if (servicio.getPrecio() != null && 
            servicio.getPrecio().compareTo(PRECIO_BASE_DEFAULT) < 0) {
            servicio.setPrecio(servicio.getPrecio().multiply(BigDecimal.valueOf(RECARGO_EMERGENCIA)));
        }
    }

    @Override
    public CategoriaServicio getCategoria() {
        return CategoriaServicio.EMERGENCIA;
    }
}

