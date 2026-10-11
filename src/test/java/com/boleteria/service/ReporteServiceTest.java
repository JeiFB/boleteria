package com.boleteria.service;

import com.boleteria.model.Asiento;
import com.boleteria.model.Evento;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReporteServiceTest {

    @Test
    void calculaPorcentajeDeOcupacion() {
        ReporteService reporteService = new ReporteService(
                new EventoService(new ArrayList<Evento>(), new ArrayList<Asiento>()), new ArrayList<>());
        assertEquals(11.111, reporteService.calcularOcupacion(2, 18), 0.01);
        assertEquals(50.0, reporteService.calcularOcupacion(9, 18), 0.001);
        assertEquals(0.0, reporteService.calcularOcupacion(5, 0), 0.001);
    }
}
