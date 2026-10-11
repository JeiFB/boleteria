package com.boleteria.service;

import com.boleteria.exception.CompraInvalidaException;
import com.boleteria.model.Asiento;
import com.boleteria.model.Evento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventoServiceTest {

    private EventoService eventoService;

    @BeforeEach
    void preparar() {
        List<Evento> eventos = new ArrayList<>(List.of(
                new Evento("EV001", "Rock Medellin 2026", "2026-10-20", "19:00", "Estadio", "Concierto")));
        List<Asiento> asientos = new ArrayList<>(List.of(
                new Asiento("EV001", "VIP-A-01", "VIP", "A", 1, 350000)));
        eventoService = new EventoService(eventos, asientos);
    }

    @Test
    void buscaEventoExistente() throws CompraInvalidaException {
        assertEquals("EV001", eventoService.buscarEvento("EV001").getIdEvento());
    }

    @Test
    void lanzaExcepcionEventoInexistente() {
        assertThrows(CompraInvalidaException.class, () -> eventoService.buscarEvento("EV999"));
    }
}
