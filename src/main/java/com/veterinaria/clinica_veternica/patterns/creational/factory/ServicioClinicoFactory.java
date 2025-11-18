package com.veterinaria.clinica_veternica.patterns.creational.factory;

import com.veterinaria.clinica_veternica.domain.agenda.CategoriaServicio;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Factory concreta para crear servicios clínicos.
 *
 * Los servicios clínicos incluyen consultas, diagnósticos,
 * vacunaciones y tratamientos no quirúrgicos.
 *
 * Características típicas:
 * - Duración: 15-30 minutos
 * - No requieren anestesia
 * - No requieren ayuno
 * - Precio base moderado
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Component
public class ServicioClinicoFactory extends ServicioFactory {

    private static final int DURACION_DEFAULT_MINUTOS = 30;
    private static final BigDecimal PRECIO_BASE_DEFAULT = new BigDecimal("50000");

    @Override
    public Servicio crearServicio(String nombre, String descripcion, BigDecimal precio) {
        return Servicio.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .categoria(CategoriaServicio.CLINICO)
                .tipoServicio(com.veterinaria.clinica_veternica.domain.agenda.TipoServicio.CONSULTA_GENERAL)
                .precio(precio != null ? precio : PRECIO_BASE_DEFAULT)
                .duracionEstimadaMinutos(DURACION_DEFAULT_MINUTOS)
                .requiereAnestesia(false)
                .requiereAyuno(false)
                .requiereHospitalizacion(false)
                .requiereCuidadosEspeciales(false)
                .activo(true)
                .disponibleEmergencias(true)
                .disponibleDomicilio(true)
                .build();
    }

    @Override
    protected void aplicarConfiguracionesEspecificas(Servicio servicio) {
        // Servicios clínicos no requieren preparación especial
        servicio.setRequiereAnestesia(false);
        servicio.setRequiereAyuno(false);
        servicio.setRequiereHospitalizacion(false);
        servicio.setDisponibleEmergencias(true);
        servicio.setDisponibleDomicilio(true);

        // Asegurar duración mínima si no está configurada
        if (servicio.getDuracionEstimadaMinutos() == null || servicio.getDuracionEstimadaMinutos() < 15) {
            servicio.setDuracionEstimadaMinutos(DURACION_DEFAULT_MINUTOS);
        }
    }

    @Override
    public CategoriaServicio getCategoria() {
        return CategoriaServicio.CLINICO;
    }
}

