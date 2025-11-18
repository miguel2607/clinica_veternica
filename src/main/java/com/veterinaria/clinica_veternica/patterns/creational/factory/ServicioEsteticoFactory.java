package com.veterinaria.clinica_veternica.patterns.creational.factory;

import com.veterinaria.clinica_veternica.domain.agenda.CategoriaServicio;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Factory concreta para crear servicios estéticos.
 *
 * Los servicios estéticos incluyen baño, peluquería, corte de uñas
 * y otros servicios de cuidado e higiene.
 *
 * Características típicas:
 * - Duración: 30-90 minutos
 * - No requieren anestesia
 * - No requieren ayuno
 * - Precio base bajo-moderado
 * - Disponibles a domicilio
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Component
public class ServicioEsteticoFactory extends ServicioFactory {

    private static final int DURACION_DEFAULT_MINUTOS = 45;
    private static final BigDecimal PRECIO_BASE_DEFAULT = new BigDecimal("30000");
    private static final BigDecimal COSTO_DOMICILIO_DEFAULT = new BigDecimal("15000");

    @Override
    public Servicio crearServicio(String nombre, String descripcion, BigDecimal precio) {
        return Servicio.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .categoria(CategoriaServicio.ESTETICO)
                .tipoServicio(com.veterinaria.clinica_veternica.domain.agenda.TipoServicio.BANO)
                .precio(precio != null ? precio : PRECIO_BASE_DEFAULT)
                .duracionEstimadaMinutos(DURACION_DEFAULT_MINUTOS)
                .requiereAnestesia(false)
                .requiereAyuno(false)
                .requiereHospitalizacion(false)
                .requiereCuidadosEspeciales(false)
                .activo(true)
                .disponibleEmergencias(false)
                .disponibleDomicilio(true)
                .costoAdicionalDomicilio(COSTO_DOMICILIO_DEFAULT)
                .build();
    }

    @Override
    protected void aplicarConfiguracionesEspecificas(Servicio servicio) {
        // Servicios estéticos no requieren preparación médica
        servicio.setRequiereAnestesia(false);
        servicio.setRequiereAyuno(false);
        servicio.setRequiereHospitalizacion(false);
        servicio.setRequiereCuidadosEspeciales(false);

        // Disponibles a domicilio con costo adicional
        servicio.setDisponibleDomicilio(true);
        if (servicio.getCostoAdicionalDomicilio() == null) {
            servicio.setCostoAdicionalDomicilio(COSTO_DOMICILIO_DEFAULT);
        }

        // No disponibles para emergencias
        servicio.setDisponibleEmergencias(false);
    }

    @Override
    public CategoriaServicio getCategoria() {
        return CategoriaServicio.ESTETICO;
    }
}

