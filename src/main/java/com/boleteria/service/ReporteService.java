package com.boleteria.service;

import com.boleteria.model.Compra;
import com.boleteria.model.Evento;
import com.boleteria.model.ReporteEvento;

import java.util.ArrayList;
import java.util.List;

/** Calcula ingresos, boletas vendidas y porcentaje de ocupacion por evento. */
public class ReporteService {

    private final EventoService eventoService;
    private final List<Compra> compras;

    public ReporteService(EventoService eventoService, List<Compra> compras) {
        this.eventoService = eventoService;
        this.compras = compras;
    }

    public List<ReporteEvento> generarReporte() {
        List<ReporteEvento> reporte = new ArrayList<>();
        for (Evento evento : eventoService.listarEventos()) {
            int totalAsientos = eventoService.asientosDeEvento(evento.getIdEvento()).size();
            int vendidas = 0;
            double ingresos = 0;
            for (Compra compra : compras) {
                if (compra.getIdEvento().equalsIgnoreCase(evento.getIdEvento())) {
                    vendidas += compra.getAsientos().size();
                    ingresos += compra.getTotal();
                }
            }
            double ocupacion = calcularOcupacion(vendidas, totalAsientos);
            reporte.add(new ReporteEvento(evento.getIdEvento(), evento.getNombre(),
                    vendidas, totalAsientos, ingresos, ocupacion));
        }
        return reporte;
    }

    public double calcularOcupacion(int vendidas, int totalAsientos) {
        if (totalAsientos == 0) {
            return 0;
        }
        return vendidas * 100.0 / totalAsientos;
    }
}
