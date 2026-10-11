package com.boleteria.service;

import com.boleteria.exception.CompraInvalidaException;
import com.boleteria.model.Asiento;
import com.boleteria.model.Compra;
import com.boleteria.model.Evento;

import java.util.ArrayList;
import java.util.List;

/** Consultas sobre eventos y asientos y su disponibilidad. */
public class EventoService {

    private final List<Evento> eventos;
    private final List<Asiento> asientos;

    public EventoService(List<Evento> eventos, List<Asiento> asientos) {
        this.eventos = eventos;
        this.asientos = asientos;
    }

    public List<Evento> listarEventos() {
        return eventos;
    }

    public Evento buscarEvento(String idEvento) throws CompraInvalidaException {
        for (Evento evento : eventos) {
            if (evento.getIdEvento().equalsIgnoreCase(idEvento)) {
                return evento;
            }
        }
        throw new CompraInvalidaException("El evento '" + idEvento + "' no existe.");
    }

    public boolean existeEvento(String idEvento) {
        for (Evento evento : eventos) {
            if (evento.getIdEvento().equalsIgnoreCase(idEvento)) {
                return true;
            }
        }
        return false;
    }

    public List<Asiento> asientosDeEvento(String idEvento) {
        List<Asiento> resultado = new ArrayList<>();
        for (Asiento asiento : asientos) {
            if (asiento.getIdEvento().equalsIgnoreCase(idEvento)) {
                resultado.add(asiento);
            }
        }
        return resultado;
    }

    public Asiento buscarAsiento(String idEvento, String codigo) {
        for (Asiento asiento : asientos) {
            if (asiento.getIdEvento().equalsIgnoreCase(idEvento)
                    && asiento.getCodigo().equalsIgnoreCase(codigo)) {
                return asiento;
            }
        }
        return null;
    }

    // Marca como vendidos los asientos que aparecen en compras ya registradas.
    public void marcarVendidos(List<Compra> compras) {
        for (Compra compra : compras) {
            for (String codigo : compra.getAsientos()) {
                Asiento asiento = buscarAsiento(compra.getIdEvento(), codigo);
                if (asiento != null) {
                    asiento.setVendido(true);
                }
            }
        }
    }
}
