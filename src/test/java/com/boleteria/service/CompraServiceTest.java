package com.boleteria.service;

import com.boleteria.exception.CompraInvalidaException;
import com.boleteria.io.Auditoria;
import com.boleteria.io.ComprasRepository;
import com.boleteria.model.Asiento;
import com.boleteria.model.Compra;
import com.boleteria.model.Comprador;
import com.boleteria.model.Evento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompraServiceTest {

    @TempDir
    Path temp;

    private EventoService eventoService;
    private CompraService service;

    @BeforeEach
    void preparar() {
        List<Evento> eventos = new ArrayList<>(List.of(
                new Evento("EV001", "Rock Medellin 2026", "2026-10-20", "19:00", "Estadio", "Concierto")));
        List<Asiento> asientos = new ArrayList<>(List.of(
                new Asiento("EV001", "VIP-A-01", "VIP", "A", 1, 350000),
                new Asiento("EV001", "VIP-A-02", "VIP", "A", 2, 350000),
                new Asiento("EV001", "GEN-C-01", "General", "C", 1, 150000)));
        eventoService = new EventoService(eventos, asientos);
        ComprasRepository repo = new ComprasRepository(temp.resolve("compras.json").toString());
        Auditoria auditoria = new Auditoria(temp.resolve("auditoria.txt").toString());
        service = new CompraService(eventoService, repo, auditoria, new ArrayList<>());
    }

    @Test
    void calculaSubtotalServicioYTotal() {
        List<Asiento> dos = eventoService.asientosDeEvento("EV001").subList(0, 2);
        double subtotal = service.calcularSubtotal(dos);
        double servicio = service.calcularServicio(subtotal);
        double total = service.calcularTotal(subtotal, servicio);
        assertEquals(700000, subtotal, 0.001);
        assertEquals(70000, servicio, 0.001);
        assertEquals(770000, total, 0.001);
    }

    @Test
    void compraAsientoDisponible() throws Exception {
        Comprador comprador = new Comprador("Laura Gomez", "123", "laura@mail.com");
        Compra compra = service.comprar("EV001", comprador, List.of("VIP-A-01"));
        assertNotNull(compra);
        assertEquals(385000, compra.getTotal(), 0.001);
        assertTrue(eventoService.buscarAsiento("EV001", "VIP-A-01").isVendido());
    }

    @Test
    void rechazaAsientoYaVendido() throws Exception {
        service.comprar("EV001", new Comprador("Laura", "123", "laura@mail.com"), List.of("VIP-A-01"));
        assertThrows(CompraInvalidaException.class,
                () -> service.comprar("EV001", new Comprador("Pedro", "456", "pedro@mail.com"), List.of("VIP-A-01")));
    }

    @Test
    void rechazaCorreoInvalido() {
        assertFalse(service.validarCorreo("correo-malo"));
        assertTrue(service.validarCorreo("laura@mail.com"));
    }

    @Test
    void rechazaCompradorConDatoVacio() {
        Comprador comprador = new Comprador("", "123", "laura@mail.com");
        assertThrows(CompraInvalidaException.class, () -> service.validarComprador(comprador));
    }
}
