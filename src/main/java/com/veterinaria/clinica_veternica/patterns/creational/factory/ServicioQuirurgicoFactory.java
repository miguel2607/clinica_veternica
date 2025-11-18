package com.veterinaria.clinica_veternica.patterns.creational.factory;

import com.veterinaria.clinica_veternica.domain.agenda.CategoriaServicio;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Factory concreta para crear servicios quirúrgicos.
 *
 * Los servicios quirúrgicos incluyen cirugías y procedimientos
 * que requieren anestesia y preparación previa.
 *
 * Características típicas:
 * - Duración: 60-240 minutos
 * - Requieren anestesia
 * - Requieren ayuno previo (8-12 horas)
 * - Pueden requerir hospitalización
 * - Precio base alto
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Component
public class ServicioQuirurgicoFactory extends ServicioFactory {

    private static final int DURACION_DEFAULT_MINUTOS = 120;
    private static final int HORAS_AYUNO_DEFAULT = 12;
    private static final BigDecimal PRECIO_BASE_DEFAULT = new BigDecimal("300000");

    @Override
    public Servicio crearServicio(String nombre, String descripcion, BigDecimal precio) {
        return Servicio.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .categoria(CategoriaServicio.QUIRURGICO)
                .tipoServicio(com.veterinaria.clinica_veternica.domain.agenda.TipoServicio.CIRUGIA)
                .precio(precio != null ? precio : PRECIO_BASE_DEFAULT)
                .duracionEstimadaMinutos(DURACION_DEFAULT_MINUTOS)
                .requiereAnestesia(true)
                .requiereAyuno(true)
                .horasAyunoRequeridas(HORAS_AYUNO_DEFAULT)
                .requiereHospitalizacion(true)
                .requiereCuidadosEspeciales(true)
                .activo(true)
                .disponibleEmergencias(false)
                .disponibleDomicilio(false)
                .build();
    }

    @Override
    protected void aplicarConfiguracionesEspecificas(Servicio servicio) {
        // Servicios quirúrgicos siempre requieren anestesia y ayuno
        servicio.setRequiereAnestesia(true);
        servicio.setRequiereAyuno(true);
        servicio.setRequiereHospitalizacion(true);
        servicio.setRequiereCuidadosEspeciales(true);

        // Configurar horas de ayuno si no está configurado
        if (servicio.getHorasAyunoRequeridas() == null || servicio.getHorasAyunoRequeridas() < 8) {
            servicio.setHorasAyunoRequeridas(HORAS_AYUNO_DEFAULT);
        }

        // Asegurar duración mínima para cirugías
        if (servicio.getDuracionEstimadaMinutos() == null || servicio.getDuracionEstimadaMinutos() < 60) {
            servicio.setDuracionEstimadaMinutos(DURACION_DEFAULT_MINUTOS);
        }

        // Los servicios quirúrgicos no están disponibles a domicilio
        servicio.setDisponibleDomicilio(false);
    }

    @Override
    public CategoriaServicio getCategoria() {
        return CategoriaServicio.QUIRURGICO;
    }
}

